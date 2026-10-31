// Nathflix — Copyright (C) 2026 lavagnou. Based on dx.bi, Copyright (C) 2026 dx.bi. GNU General Public License, version 3 or later (see LICENSE).
//
// Performance bench: loads web.stremio.com in Chromium with a slowed-down processor, runs Nathflix in it
// the way the apps do, and prints what it measured as JSON (the median of the runs). Run it before and
// after a change and compare. See "Performance bench" in tools/README.md.
//
//   npx --yes -p playwright@1 node tools/perf-bench.mjs [--tv | --pc | --phone] [--runs 5] [--throttle 6]
//       [--plugin nathflix.plugin.js] [--out result.json] [--settle 15] [--idle 30] [--settings '{"k":v}']
//       [--storage '{"localStorageKey":"value"}' | @file.json] [--shots dir] [--compare refdir] [--no-measure]
//       [--dwell "#/route,#/other"] [--loss-test] [--vs other.plugin.js] [--login] [--trace] [--descend 10]
//
// No Stremio account: the guest mode is allowed with nathflix.allowGuest, like the CI. Never set
// nathflix.lang here: a language change reloads the page (langWatch).
import { execFileSync } from "node:child_process";
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { createRequire } from "node:module";
import { delimiter, join, resolve } from "node:path";

const argv = process.argv.slice(2);
const flag = (name) => argv.includes(`--${name}`);
const opt = (name, fallback) => {
  const i = argv.indexOf(`--${name}`);
  return i >= 0 && i + 1 < argv.length ? argv[i + 1] : fallback;
};
const mode = flag("pc") ? "pc" : flag("phone") ? "phone" : "tv";
const runs = Math.max(1, parseInt(opt("runs", "5"), 10));
const throttle = Math.max(1, Number(opt("throttle", "6")));
const settleMs = Number(opt("settle", "15")) * 1000;
const idleMs = Number(opt("idle", "30")) * 1000;
const pluginPath = resolve(opt("plugin", "nathflix.plugin.js"));
const outFile = opt("out", "");
const shotsDir = opt("shots", "");
const compareDir = opt("compare", "");
const benchSettings = JSON.parse(opt("settings", "{}"));
// (or "@file.json": a real export is too long for a command line)
const storageArg = opt("storage", "{}");
const benchStorage = JSON.parse(storageArg.startsWith("@") ? readFileSync(storageArg.slice(1), "utf8") : storageArg);
// after the walk, rest on these routes too (a title's page, the calendar…) and count what happens there
const dwellRoutes = opt("dwell", "").split(",").filter(Boolean);
// changes the profile and closes the page 200 ms later, three ways: is the new profile still there?
const lossTest = flag("loss-test");
// a second build, measured in the same browser, runs alternating with the first: { a, b } in the report
const vsPath = opt("vs", "") ? resolve(opt("vs", "")) : "";
// a visible window to sign in to Stremio first (by hand, in that window); the session lives in memory only
const login = flag("login");
// a browser trace of the keys and the walk (first run of each build): where the main thread's time goes
const traceOn = flag("trace");
// after the keys, Down this many times down Home's rows, 700 ms apart: the row reached each time
const descendN = Math.max(0, parseInt(opt("descend", "0"), 10) || 0);
if (login && lossTest) throw new Error("--loss-test adds profiles: not on a real account (--login)");
// signed in, "Who's watching?" would cover Home at every load
if (login && !("whoIsWatching" in benchSettings)) benchSettings.whoIsWatching = false;

const source = readFileSync(pluginPath, "utf8");
const version = (source.match(/@version\s+(\S+)/) || [])[1] || "?";
let commit = "?";
try { commit = execFileSync("git", ["rev-parse", "--short", "HEAD"], { encoding: "utf8" }).trim() + (execFileSync("git", ["status", "--porcelain", "nathflix.plugin.js"], { encoding: "utf8" }).trim() ? "+" : ""); } catch (_) { /* not a checkout */ }

// Playwright isn't a dependency of the repository: it comes from npx (the folder of its node_modules/.bin
// is on the PATH), or from a global install
async function playwright() {
  try { return await import("playwright"); } catch (_) { /* not installed here */ }
  for (const dir of (process.env.PATH || "").split(delimiter)) {
    if (!/node_modules[\\/]\.bin$/.test(dir)) continue;
    try { return createRequire(join(dir, "..", "bench.js"))("playwright"); } catch (_) { /* another folder */ }
  }
  throw new Error("Playwright not found: run with npx --yes -p playwright@1 node tools/perf-bench.mjs …");
}

const DESKTOP_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36";
const DEVICES = {
  // Nathflix TV: 1280 px CSS wide, drawn at 1920×1080
  tv: { userAgent: `${DESKTOP_UA} nathflix-tv/${version}`, viewport: { width: 1280, height: 720 }, deviceScaleFactor: 1.5 },
  // Stremio Enhanced and the desktop app
  pc: { userAgent: DESKTOP_UA, viewport: { width: 1600, height: 900 }, deviceScaleFactor: 1 },
  phone: { userAgent: `${DESKTOP_UA} nathflix-mobile/${version}`, viewport: { width: 412, height: 915 }, deviceScaleFactor: 2, isMobile: true, hasTouch: true },
};

const ORIGIN = "https://web.stremio.com";
const PLUGIN_URL = `${ORIGIN}/__nathflix/plugin.js`;
const READY = '[class*="meta-item-container"]';
const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// The plugin inside the same StremioEnhancedAPI wrapper as the desktop app's modController.loadPlugin,
// with its settings kept in memory
const wrap = (source) => `(function () {
  const saved = (window.__bench && window.__bench.settings) || {};
  const StremioEnhancedAPI = {
    logger: { info: (m) => console.log("[bench]", m), warn: (m) => console.warn("[bench]", m), error: (m) => console.error("[bench]", m) },
    getSetting: (key) => Promise.resolve(key in saved ? saved[key] : null),
    saveSetting: (key, value) => { saved[key] = value; return Promise.resolve(); },
    registerSettings: () => {},
    onSettingsSaved: () => {},
    showAlert: () => {},
    showPrompt: () => Promise.resolve(null),
  };
  try {
${source}
  } catch (err) {
    console.error("[Nathflix] plugin crashed", err);
  }
})();
`;
const bodies = [wrap(source), ...(vsPath ? [wrap(readFileSync(vsPath, "utf8"))] : [])];
let serving = 0;

// Runs in every document before Stremio's scripts: the guest flag, the counters, and the plugin added
// again after each load (a reload would drop a plugin added by hand)
function initScript(cfg) {
  try {
    localStorage.setItem("nathflix.allowGuest", "1");
    for (const [k, v] of Object.entries(cfg.storage)) localStorage.setItem(k, v);
  } catch (_) { /* no storage */ }
  const bench = (window.__bench = { settings: cfg.settings, longTasks: [], events: [], presses: [], readyAt: 0, pluginLoadMs: 0 });
  try {
    new PerformanceObserver((list) => {
      for (const e of list.getEntries()) bench.longTasks.push({ at: e.startTime, ms: e.duration });
    }).observe({ type: "longtask", buffered: true });
    new PerformanceObserver((list) => {
      for (const e of list.getEntries()) bench.events.push({ name: e.name, at: e.startTime, ms: e.duration });
    }).observe({ type: "event", durationThreshold: 16, buffered: true });
  } catch (_) { /* old browser */ }
  window.addEventListener("keydown", (e) => bench.presses.push(e.timeStamp), { capture: true, passive: true });
  const ready = setInterval(() => {
    if (document.querySelector(cfg.ready)) {
      bench.readyAt = performance.now();
      clearInterval(ready);
    }
  }, 50);
  const inject = () => {
    if (document.getElementById("nathflix.plugin.js")) return;
    const script = document.createElement("script");
    script.id = "nathflix.plugin.js";
    script.src = cfg.pluginUrl;
    const started = performance.now();
    script.onload = () => { bench.pluginLoadMs = performance.now() - started; };
    document.body.append(script);
  };
  if (document.readyState === "complete") inject();
  else window.addEventListener("load", inject, { once: true });
}

const median = (xs) => {
  const v = xs.filter((x) => typeof x === "number" && !Number.isNaN(x)).sort((a, b) => a - b);
  if (!v.length) return 0;
  const m = Math.floor(v.length / 2);
  return v.length % 2 ? v[m] : (v[m - 1] + v[m]) / 2;
};
const round = (x) => Math.round(x * 10) / 10;

// Counts between two perf() snapshots: { sync: { calls, ms }, getState: { model: n }, saveLocal, dispatch, filterMs }
function counters(a, b) {
  const getState = {};
  for (const [k, n] of Object.entries(b.counts)) {
    if (k.startsWith("getState:")) getState[k.slice(9)] = n - (a.counts[k] || 0);
  }
  return {
    sync: { calls: (b.counts.sync || 0) - (a.counts.sync || 0), ms: round((b.ms.sync || 0) - (a.ms.sync || 0)) },
    getState,
    saveLocal: (b.counts.saveLocal || 0) - (a.counts.saveLocal || 0),
    dispatch: (b.counts.dispatch || 0) - (a.counts.dispatch || 0),
    // time in the per-viewer library filter (only with two profiles or more)
    filterMs: round((b.ms.filter || 0) - (a.ms.filter || 0)),
    // a build that times each step of sync() ("sync:<step>" in perf().ms): ms by step
    syncSteps: Object.fromEntries(Object.keys(b.ms).filter((k) => k.startsWith("sync:")).map((k) => [k.slice(5), round(b.ms[k] - (a.ms[k] || 0))])),
  };
}

async function openPage(ctx, errors, slow) {
  const page = await ctx.newPage();
  page.on("console", (m) => {
    const text = m.text();
    if (/^\[Nathflix\]/.test(text) && (m.type() === "error" || /failed|crashed/i.test(text))) errors.push(text.slice(0, 300));
  });
  page.on("pageerror", (err) => {
    if (String(err.stack || "").includes("__nathflix/plugin.js")) errors.push(`pageerror: ${String(err.message).slice(0, 300)}`);
  });
  if (slow) {
    const cdp = await ctx.newCDPSession(page);
    await cdp.send("Emulation.setCPUThrottlingRate", { rate: throttle });
  }
  return page;
}

async function waitReady(page) {
  await page.waitForFunction(() => window.__bench && window.__bench.readyAt && window.__nathflix && window.__bench.pluginLoadMs, null, { timeout: 180000, polling: 500 });
}

// Stremio's own announcements (a featured catalog…) open over Home once: closed
async function closeModals(page) {
  for (let i = 0; i < 3; i++) {
    const closed = await page.evaluate(() => {
      const x = document.querySelector('[class*="modal-container"] [class*="close-button"], [class*="modal-dialog-container"] [class*="close-button"]');
      if (x) x.click();
      return !!x;
    });
    if (!closed) return;
    await sleep(500);
  }
}

// A profile change, then the page closed 200 ms later: when it's written down, the next start has it.
// "unload": closed normally (beforeunload, pagehide); "hidden": the app goes to the background first
// (what Android does before it may close an app); "abrupt": the renderer crashes (no event at all)
async function loss() {
  const out = {};
  for (const way of ["unload", "hidden", "abrupt"]) {
    const ctx = await newContext(benchSettings);
    const errors = [];
    let page = await openPage(ctx, errors, false);
    await page.goto(`${ORIGIN}/#/`, { waitUntil: "load", timeout: 180000 });
    await waitReady(page);
    const id = await page.evaluate(async () => {
      window.__nathflix.viewers.add("Bench");
      const v = window.__nathflix.viewers.list();
      return v.list[v.list.length - 1].id;
    });
    // the new profile is written down for sure, then the change that's tested
    await sleep(3000);
    await page.evaluate((x) => window.__nathflix.viewers.select(x), id);
    await sleep(200);
    if (way === "hidden") {
      await page.evaluate(() => {
        Object.defineProperty(document, "hidden", { configurable: true, get: () => true });
        Object.defineProperty(document, "visibilityState", { configurable: true, get: () => "hidden" });
        document.dispatchEvent(new Event("visibilitychange"));
      });
    }
    if (way === "abrupt") {
      // (closing a page still sends pagehide: a crashed renderer sends nothing)
      const cdp = await ctx.newCDPSession(page);
      // (never answers: the page is gone)
      cdp.send("Page.crash").catch(() => {});
      await sleep(1000);
    }
    await page.close({ runBeforeUnload: way === "unload" }).catch(() => { /* crashed */ });
    if (way === "unload") await sleep(500);
    page = await openPage(ctx, errors, false);
    await page.goto(`${ORIGIN}/#/`, { waitUntil: "load", timeout: 180000 });
    await waitReady(page);
    const current = await page.evaluate(() => window.__nathflix.viewers.current().id);
    out[way] = current === id ? "kept" : "lost";
    await ctx.close();
  }
  return out;
}

// The trace's main-thread time by kind of work, and script time by script. Durations include what
// they call (a script that forces a layout counts it too); style and layout are counted on their own
const TRACED = { UpdateLayoutTree: "style", Layout: "layout", Paint: "paint", PrePaint: "prepaint", Layerize: "layerize", ParseHTML: "parseHTML", ParseAuthorStyleSheet: "parseCSS", MinorGC: "gc", MajorGC: "gc" };
function traceSummary(buffer) {
  const events = JSON.parse(buffer.toString("utf8")).traceEvents || [];
  const threads = new Map();
  for (const e of events) if (e.ph === "M" && e.name === "thread_name") threads.set(`${e.pid}:${e.tid}`, e.args && e.args.name);
  const main = (e) => threads.get(`${e.pid}:${e.tid}`) === "CrRendererMain";
  const work = {};
  const scripts = {};
  // only the outermost script event of each stack (nested calls would count twice), named after the
  // first script it runs (an event or a timer names none itself)
  const SCRIPT = new Set(["FunctionCall", "EvaluateScript", "TimerFire", "EventDispatch", "FireAnimationFrame", "RunMicrotasks", "FireIdleCallback"]);
  const urlOf = (e) => {
    const d = (e.args && e.args.data) || {};
    const url = String(d.url || d.scriptName || (d.stackTrace && d.stackTrace[0] && d.stackTrace[0].url) || "");
    if (!url) return "";
    return url.includes("__nathflix") ? "nathflix" : url.replace(/^https?:\/\/[^/]+/, "").replace(/[?#].*$/, "").slice(-60);
  };
  let current = null;
  let scriptEnd = 0;
  const flush = () => { if (current) scripts[current.name || current.kind] = (scripts[current.name || current.kind] || 0) + current.ms; };
  const sorted = events.filter((e) => e.ph === "X" && typeof e.dur === "number" && main(e)).sort((a, b) => a.ts - b.ts);
  for (const e of sorted) {
    const kind = TRACED[e.name];
    if (kind) work[kind] = (work[kind] || 0) + e.dur / 1000;
    if (!SCRIPT.has(e.name)) continue;
    if (e.ts >= scriptEnd) {
      flush();
      current = { kind: e.name, ms: e.dur / 1000, name: urlOf(e) };
      scriptEnd = e.ts + e.dur;
    } else if (current && !current.name) current.name = urlOf(e);
  }
  flush();
  const rounded = (o) => Object.fromEntries(Object.entries(o).map(([k, v]) => [k, Math.round(v)]).filter(([, v]) => v).sort((a, b) => b[1] - a[1]).slice(0, 12));
  return { work: rounded(work), scripts: rounded(scripts) };
}

async function measure(ctx, trace = false) {
  const errors = [];
  const page = await openPage(ctx, errors, true);
  await page.goto(`${ORIGIN}/#/`, { waitUntil: "load", timeout: 180000 });
  await waitReady(page);
  await closeModals(page);
  const load = await page.evaluate(() => ({ pageReadyMs: window.__bench.readyAt, pluginLoadMs: window.__bench.pluginLoadMs }));
  await sleep(settleMs);

  // At rest on Home
  const before = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now() }));
  await sleep(idleMs);
  const after = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now(), longTasks: window.__bench.longTasks, elements: document.getElementsByTagName("*").length }));
  const idleTasks = after.longTasks.filter((t) => t.at >= before.at && t.at < after.at);

  if (trace) await browser.startTracing(page, { categories: ["devtools.timeline", "disabled-by-default-devtools.timeline", "v8"] });

  // 20 key presses, 400 ms apart
  const keys = [...Array(2).fill("ArrowDown"), ...Array(10).fill("ArrowRight"), ...Array(4).fill("ArrowLeft"), ...Array(2).fill("ArrowUp"), ...Array(2).fill("ArrowDown")];
  await page.evaluate(() => { window.__bench.presses = []; });
  for (const key of keys) {
    await page.keyboard.press(key);
    await sleep(400);
  }
  await sleep(1000);
  // each press: the longest event the browser timed for it (under 16 ms isn't reported: counted as 0)
  const keyMs = await page.evaluate(() => window.__bench.presses.map((at) => {
    const own = window.__bench.events.filter((e) => /^key(down|up)$/.test(e.name) && e.at >= at - 1 && e.at - at < 350);
    return own.reduce((m, e) => Math.max(m, e.ms), 0);
  }));
  const sorted = [...keyMs].sort((a, b) => a - b);

  // Down Home's rows: where the selection is after each press (its row among Home's rows, in screen
  // order), how long each press took, and how big Home has grown by the end
  let descend = null;
  if (descendN) {
    await page.evaluate(() => { window.__bench.presses = []; });
    const rows = [];
    for (let i = 0; i < descendN; i++) {
      await page.keyboard.press("ArrowDown");
      await sleep(700);
      rows.push(await page.evaluate(() => {
        const row = document.activeElement && document.activeElement.closest && document.activeElement.closest('[class*="meta-row-container"]');
        const all = [...document.querySelectorAll('[class*="board-content-"]:not([class*="board-content-container"]) [class*="meta-row-container"]')]
          .sort((a, b) => a.getBoundingClientRect().top - b.getBoundingClientRect().top);
        return row ? all.indexOf(row) : -1;
      }));
    }
    await sleep(1000);
    const ms = await page.evaluate(() => window.__bench.presses.map((at) => {
      const own = window.__bench.events.filter((e) => /^key(down|up)$/.test(e.name) && e.at >= at - 1 && e.at - at < 650);
      return Math.round(own.reduce((m, e) => Math.max(m, e.ms), 0));
    }));
    const stalls = rows.filter((r, i) => i && r === rows[i - 1]).length;
    descend = { rows, stalls, keys: { median: Math.round(median(ms)), worst: Math.max(0, ...ms) }, elements: await page.evaluate(() => document.getElementsByTagName("*").length) };
  }

  // The control walk: every main screen, a title's page and back, without an error from Nathflix
  for (const route of ["#/discover", "#/library", "#/calendar", "#/settings", "#/"]) {
    await page.evaluate((r) => { location.hash = r; }, route);
    await sleep(4000);
  }
  const detail = await page.evaluate(() => {
    const a = document.querySelector('#app a[href*="#/detail/"]');
    return a ? a.getAttribute("href").replace(/^.*#/, "#") : "";
  });
  if (detail) {
    await page.evaluate((r) => { location.hash = r; }, detail);
    await sleep(5000);
    await page.evaluate(() => { location.hash = "#/"; });
    await sleep(3000);
  } else errors.push("control walk: no title on Home");
  // the whole visit, from the plugin's start: load, rest, keys and walk
  const end = await page.evaluate(() => ({ perf: window.__nathflix.perf(), longTasks: window.__bench.longTasks }));
  const traced = trace ? traceSummary(await browser.stopTracing()) : null;
  const tasks = (list) => ({ count: list.length, totalMs: Math.round(list.reduce((a, t) => a + t.ms, 0)), maxMs: Math.round(list.reduce((a, t) => Math.max(a, t.ms), 0)) });
  // Two profiles or more (--storage): to another profile and back, as at "Who's watching?" (never on
  // a real account: the profiles are synced to it)
  let switching = null;
  const who = login ? null : await page.evaluate(() => {
    const v = window.__nathflix.viewers && window.__nathflix.viewers.list();
    return v && v.list.length > 1 ? { ids: v.list.map((x) => x.id), current: v.current } : null;
  });
  if (who) {
    const from = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now() }));
    for (const id of [who.ids.find((x) => x !== who.current), who.current]) {
      await page.evaluate((x) => window.__nathflix.viewers.select(x), id);
      await sleep(3000);
    }
    const to = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now(), longTasks: window.__bench.longTasks }));
    switching = { ...counters(from.perf, to.perf), longTasks: tasks(to.longTasks.filter((t) => t.at >= from.at && t.at < to.at)) };
  }

  // At rest on other routes: counted from 5 s after arriving, for as long as the rest on Home
  const dwell = {};
  for (const route of dwellRoutes) {
    await page.evaluate((r) => { location.hash = r; }, route);
    await sleep(5000);
    const from = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now() }));
    await sleep(idleMs);
    const to = await page.evaluate(() => ({ perf: window.__nathflix.perf(), at: performance.now(), longTasks: window.__bench.longTasks }));
    dwell[route] = { ...counters(from.perf, to.perf), longTasks: tasks(to.longTasks.filter((t) => t.at >= from.at && t.at < to.at)) };
  }
  await page.close();

  return {
    pageReadyMs: Math.round(load.pageReadyMs),
    pluginLoadMs: Math.round(load.pluginLoadMs),
    ...counters(before.perf, after.perf),
    longTasks: tasks(idleTasks),
    elements: after.elements,
    keys: { median: Math.round(median(keyMs)), p90: Math.round(sorted[Math.min(sorted.length - 1, Math.floor(sorted.length * 0.9))] || 0), worst: Math.round(sorted[sorted.length - 1] || 0), pressed: keyMs.length },
    session: { ...counters({ counts: {}, ms: {} }, end.perf), longTasks: tasks(end.longTasks) },
    switching,
    descend,
    traced,
    dwell,
    errors,
  };
}

function summarise(results) {
  const pick = (f) => median(results.map(f));
  const models = new Set(results.flatMap((r) => Object.keys(r.getState)));
  return {
    mode,
    version,
    commit,
    runs: results.length,
    throttle,
    pageReadyMs: pick((r) => r.pageReadyMs),
    pluginLoadMs: pick((r) => r.pluginLoadMs),
    sync: { calls: pick((r) => r.sync.calls), ms: round(pick((r) => r.sync.ms)) },
    getState: Object.fromEntries([...models].sort().map((m) => [m, pick((r) => r.getState[m] || 0)])),
    saveLocal: pick((r) => r.saveLocal),
    dispatch: pick((r) => r.dispatch),
    filterMs: round(pick((r) => r.filterMs)),
    longTasks: { count: pick((r) => r.longTasks.count), totalMs: pick((r) => r.longTasks.totalMs), maxMs: pick((r) => r.longTasks.maxMs) },
    elements: pick((r) => r.elements),
    keys: { median: pick((r) => r.keys.median), p90: pick((r) => r.keys.p90), worst: pick((r) => r.keys.worst) },
    // the whole visit: load, rest, keys, then the control walk
    session: {
      sync: { calls: pick((r) => r.session.sync.calls), ms: round(pick((r) => r.session.sync.ms)) },
      getState: Object.fromEntries([...new Set(results.flatMap((r) => Object.keys(r.session.getState)))].sort().map((m) => [m, pick((r) => r.session.getState[m] || 0)])),
      saveLocal: pick((r) => r.session.saveLocal),
      dispatch: pick((r) => r.session.dispatch),
      filterMs: round(pick((r) => r.session.filterMs)),
      syncSteps: Object.fromEntries([...new Set(results.flatMap((r) => Object.keys(r.session.syncSteps)))].map((k) => [k, round(pick((r) => r.session.syncSteps[k] || 0))]).filter(([, ms]) => ms).sort((x, y) => y[1] - x[1])),
      longTasks: { count: pick((r) => r.session.longTasks.count), totalMs: pick((r) => r.session.longTasks.totalMs), maxMs: pick((r) => r.session.longTasks.maxMs) },
    },
    ...(results[0].traced ? { traced: results[0].traced } : {}),
    ...(results[0].descend ? {
      descend: {
        stalls: pick((r) => r.descend.stalls),
        keys: { median: pick((r) => r.descend.keys.median), worst: pick((r) => r.descend.keys.worst) },
        elements: pick((r) => r.descend.elements),
        rows: results.map((r) => r.descend.rows.join(" ")),
      },
    } : {}),
    ...(results[0].switching ? {
      switching: {
        sync: { calls: pick((r) => r.switching.sync.calls), ms: round(pick((r) => r.switching.sync.ms)) },
        saveLocal: pick((r) => r.switching.saveLocal),
        filterMs: round(pick((r) => r.switching.filterMs)),
        longTasks: { count: pick((r) => r.switching.longTasks.count), totalMs: pick((r) => r.switching.longTasks.totalMs), maxMs: pick((r) => r.switching.longTasks.maxMs) },
      },
    } : {}),
    ...(dwellRoutes.length ? {
      dwell: Object.fromEntries(dwellRoutes.map((route) => {
        const at = (r) => r.dwell[route];
        return [route, {
          sync: { calls: pick((r) => at(r).sync.calls), ms: round(pick((r) => at(r).sync.ms)) },
          getState: Object.fromEntries([...new Set(results.flatMap((r) => Object.keys(at(r).getState)))].sort().map((m) => [m, pick((r) => at(r).getState[m] || 0)])),
          saveLocal: pick((r) => at(r).saveLocal),
          dispatch: pick((r) => at(r).dispatch),
          filterMs: round(pick((r) => at(r).filterMs)),
          longTasks: { count: pick((r) => at(r).longTasks.count), totalMs: pick((r) => at(r).longTasks.totalMs), maxMs: pick((r) => at(r).longTasks.maxMs) },
        }];
      })),
    } : {}),
    errors: [...new Set(results.flatMap((r) => r.errors))],
    perRun: results.map((r) => ({ pageReadyMs: r.pageReadyMs, pluginLoadMs: r.pluginLoadMs, idleSyncMs: r.sync.ms, sessionSyncMs: r.session.sync.ms, sessionLongTasksMs: r.session.longTasks.totalMs, keysMedian: r.keys.median, elements: r.elements })),
  };
}

// Screenshots of the main screens, pictures and clocks hidden, the billboard held still: the same build
// gives the same pixels
const SCREENS = { home: "#/", discover: "#/discover", detail: "#/detail/movie/tt0111161/tt0111161", settings: "#/settings" };
// (pictures made invisible, not covered: a cover over the billboard would hide everything on it)
const HIDE = `img, video, iframe { visibility: hidden !important; }
[style*="background-image"], [style*="url("], [class*="background-image-layer"] * { background-image: none !important; }
.dx-ends, .dx-perf { visibility: hidden !important; }`;

async function shots(ctx, dir) {
  mkdirSync(dir, { recursive: true });
  const errors = [];
  const page = await openPage(ctx, errors, false);
  await page.goto(`${ORIGIN}/#/`, { waitUntil: "load", timeout: 120000 });
  await waitReady(page);
  await page.addStyleTag({ content: HIDE });
  for (const [name, route] of Object.entries(SCREENS)) {
    await page.evaluate((r) => { location.hash = r; }, route);
    await sleep(8000);
    await closeModals(page);
    await page.evaluate(() => { window.scrollTo(0, 0); document.activeElement && document.activeElement.blur && document.activeElement.blur(); });
    await page.mouse.move(0, 0);
    await sleep(500);
    await page.screenshot({ path: join(dir, `${mode}-${name}.png`), animations: "disabled", caret: "hide" });
  }
  await page.close();
  return errors;
}

// Pixel by pixel, in the browser (no image library needed)
async function compare(browser, dir, refDir) {
  const page = await browser.newPage();
  const out = {};
  for (const name of Object.keys(SCREENS)) {
    const file = `${mode}-${name}.png`;
    if (!existsSync(join(refDir, file)) || !existsSync(join(dir, file))) {
      out[name] = "missing";
      continue;
    }
    const a = "data:image/png;base64," + readFileSync(join(refDir, file)).toString("base64");
    const b = "data:image/png;base64," + readFileSync(join(dir, file)).toString("base64");
    out[name] = await page.evaluate(async ([a, b]) => {
      const load = (src) => new Promise((ok, ko) => { const i = new Image(); i.onload = () => ok(i); i.onerror = ko; i.src = src; });
      const [ia, ib] = await Promise.all([load(a), load(b)]);
      if (ia.width !== ib.width || ia.height !== ib.height) return `size ${ia.width}x${ia.height} → ${ib.width}x${ib.height}`;
      const px = (img) => {
        const c = document.createElement("canvas");
        c.width = img.width;
        c.height = img.height;
        const g = c.getContext("2d");
        g.drawImage(img, 0, 0);
        return g.getImageData(0, 0, c.width, c.height).data;
      };
      const da = px(ia);
      const db = px(ib);
      let diff = 0;
      const box = [Infinity, Infinity, -1, -1];
      for (let i = 0; i < da.length; i += 4) {
        if (Math.abs(da[i] - db[i]) + Math.abs(da[i + 1] - db[i + 1]) + Math.abs(da[i + 2] - db[i + 2]) <= 24) continue;
        diff++;
        const x = (i / 4) % ia.width;
        const y = Math.floor(i / 4 / ia.width);
        box[0] = Math.min(box[0], x);
        box[1] = Math.min(box[1], y);
        box[2] = Math.max(box[2], x);
        box[3] = Math.max(box[3], y);
      }
      // how many pixels differ, and where (x, y to x, y in screenshot pixels)
      return diff ? `${diff} px in ${box.join(",")}` : 0;
    }, [a, b]);
  }
  await page.close();
  return out;
}

const { chromium } = await playwright();
let browser;
try {
  browser = await chromium.launch({ headless: !login });
} catch (err) {
  // no Playwright Chromium downloaded: the installed Chrome will do
  browser = await chromium.launch({ channel: "chrome", headless: !login });
}

// --login: the person signs in to Stremio in the window; the bench only checks that someone is signed in
// (never reads the session key), then measures in that same context
async function signIn(ctx) {
  const page = await openPage(ctx, [], false);
  await page.goto(`${ORIGIN}/#/intro`, { waitUntil: "load", timeout: 180000 });
  console.error("Sign in to Stremio in the window that opened (and pick a profile without a code if Nathflix asks). The bench goes on by itself.");
  const signedIn = () => page.evaluate(() => {
    try {
      const p = JSON.parse(localStorage.getItem("profile") || "null");
      return !!(p && p.auth && p.auth.key);
    } catch (_) { return false; }
  }).catch(() => false);
  const until = Date.now() + 15 * 60000;
  while (!(await signedIn())) {
    if (Date.now() > until) throw new Error("nobody signed in within 15 minutes");
    await sleep(2000);
  }
  console.error("Signed in: measuring. Leave the window alone until the bench closes it.");
  // the library, Continue Watching and the profiles come in
  await sleep(15000);
  await page.close();
}

let signedInCtx = null;
async function newContext(settings) {
  // signed in once: every measure and the screenshots then share that session
  if (login && signedInCtx) return signedInCtx;
  const ctx = await browser.newContext(DEVICES[mode]);
  await ctx.route(PLUGIN_URL, (route) => route.fulfill({ status: 200, contentType: "application/javascript; charset=utf-8", body: bodies[serving] }));
  await ctx.addInitScript(initScript, { settings, storage: benchStorage, ready: READY, pluginUrl: PLUGIN_URL });
  if (login) {
    await signIn(ctx);
    signedInCtx = ctx;
  }
  // One load to warm the HTTP cache and Nathflix's own caches: the runs measure a warm start
  const warm = await openPage(ctx, [], false);
  await warm.goto(`${ORIGIN}/#/`, { waitUntil: "load", timeout: 180000 });
  await waitReady(warm);
  await sleep(10000);
  await closeModals(warm);
  await warm.close();
  return ctx;
}

const report = {};
try {
  if (!flag("no-measure")) {
    const ctx = await newContext(benchSettings);
    // with --vs, each run measures both builds, one after the other, in turn first
    const results = bodies.map(() => []);
    let retries = 0;
    for (let i = 0, k = 0; i < runs; ) {
      const which = bodies.length > 1 ? (i % 2 ? 1 - k : k) : 0;
      serving = which;
      let r;
      try {
        r = await measure(ctx, traceOn && !results[which].length);
      } catch (err) {
        // the page reloaded under the bench (it happens now and then while Stremio starts): that run
        // is taken again, twice at most, and the report says so
        if (retries >= 2 || !/context was destroyed|navigat/i.test(String(err && err.message))) throw err;
        retries++;
        for (const p of ctx.pages()) await p.close().catch(() => {});
        console.error(`run ${i + 1}/${runs}: the page reloaded, run taken again`);
        continue;
      }
      console.error(`run ${i + 1}/${runs}${bodies.length > 1 ? ` (${"ab"[which]})` : ""}: ready ${r.pageReadyMs} ms, sync ${r.sync.calls}× ${r.sync.ms} ms, long tasks ${r.longTasks.totalMs} ms, keys ${r.keys.median} ms${r.errors.length ? `, ${r.errors.length} errors` : ""}`);
      results[which].push(r);
      if (bodies.length > 1 && k === 0) k = 1;
      else { k = 0; i++; }
      // what's measured so far, in case the bench is stopped before the end
      if (outFile) {
        const partial = bodies.length > 1 ? { a: results[0].length ? summarise(results[0]) : null, b: results[1].length ? summarise(results[1]) : null } : summarise(results[0]);
        writeFileSync(outFile, JSON.stringify({ partial: true, runsDone: i, ...partial }, null, 2) + "\n");
      }
    }
    if (bodies.length > 1) {
      const a = summarise(results[0]), b = summarise(results[1]);
      Object.assign(report, { mode, version, commit, runs, throttle, signedIn: login, retries, a: { plugin: pluginPath, ...a }, b: { plugin: vsPath, ...b }, errors: [...new Set([...a.errors, ...b.errors])] });
    } else Object.assign(report, summarise(results[0]), { signedIn: login, retries });
    serving = 0;
    if (!login) await ctx.close();
  }
  if (shotsDir) {
    // the billboard holds still for pictures (its slide would change between two builds)
    const ctx = await newContext({ ...benchSettings, billboardInterval: "0" });
    const shotErrors = await shots(ctx, shotsDir);
    report.shots = shotsDir;
    if (shotErrors.length) report.errors = [...(report.errors || []), ...shotErrors];
    if (compareDir) report.compare = await compare(browser, shotsDir, compareDir);
  }
  if (lossTest) report.loss = await loss();
} finally {
  await browser.close();
}

const json = JSON.stringify(report, null, 2);
if (outFile) writeFileSync(outFile, json + "\n");
console.log(json);
if (report.errors && report.errors.length) process.exitCode = 1;
