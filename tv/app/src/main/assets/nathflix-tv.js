// Nathflix TV — Copyright (C) 2026 lavagnou. Based on dx.bi, Copyright (C) 2026 dx.bi. GNU General Public License, version 3 or later (see LICENSE).
// Nathflix TV: runs in web.stremio.com before the page's own scripts. It stands in for Stremio Enhanced
// (the plugin API Nathflix expects), loads the built-in Nathflix plugin, fits the page to a TV, and hands
// every stream to the app's native player, keeping Stremio's watch progress in step.
(function () {
  "use strict";
  if (window.__nathflixTv) return;
  // Every call to the app carries this launch's token (the app writes it in here): the app's
  // bridge is visible to every frame, embedded trailers included, but only this page knows it
  const TOKEN = "__NATHFLIX_TV_TOKEN__";
  const bridge = window.NathflixTV;
  if (!bridge) return;
  const native = {
    pluginSource: () => bridge.pluginSource(TOKEN),
    getSettings: (plugin) => bridge.getSettings(TOKEN, plugin),
    saveSetting: (plugin, key, valueJson) => bridge.saveSetting(TOKEN, plugin, key, valueJson),
    play: (json) => bridge.play(TOKEN, json),
    activePlayback: () => bridge.activePlayback(TOKEN),
    takePendingClose: () => bridge.takePendingClose(TOKEN),
    openExternal: (url) => bridge.openExternal(TOKEN, url),
    findServers: () => bridge.findServers(TOKEN),
    torrentUrl: (json) => (bridge.torrentUrl ? bridge.torrentUrl(TOKEN, json) : ""),
    deviceInfo: () => { try { return JSON.parse(bridge.deviceInfo ? bridge.deviceInfo(TOKEN) : "{}"); } catch (_) { return {}; } },
    setLanguage: (lang) => { if (bridge.setLanguage) bridge.setLanguage(TOKEN, lang); },
  };

  // Nathflix speaks French or English, as Stremio's interface does: read the way the plugin reads it
  // (its last look at Stremio's core, else Stremio's saved profile). A change of language reloads the
  // page (the plugin sees to it), so this is read once. The app's own screens follow it too.
  const LANG = (() => {
    try {
      const profile = JSON.parse(localStorage.getItem("profile") || "null");
      const code = localStorage.getItem("nathflix.lang") || (profile && profile.settings && profile.settings.interfaceLanguage) || "";
      return /^(fr|fre|fra)(\b|-|_)/i.test(code) ? "fr" : "en";
    } catch (_) { return "en"; }
  })();
  try { native.setLanguage(LANG); } catch (_) { /* older app */ }
  const FR = LANG === "fr" ? {
    "this TV": "ce téléviseur",
    "this phone": "ce téléphone",
    "this tablet": "cette tablette",
    "On": "Activé",
    "Off": "Désactivé",
    "Set": "Défini",
    "Not set": "Non défini",
    "Done": "Terminé",
    "OK": "OK",
    "Cancel": "Annuler",
    "Save": "Enregistrer",
    "Close": "Fermer",
    "Try again": "Réessayer",
    "Nathflix settings": "Réglages Nathflix",
    "Look, Home rows, previews, trailers, reviews, sounds and more": "Apparence, rangées de l'accueil, aperçus, bandes-annonces, critiques, sons et plus",
    "Open": "Ouvrir",
    "Streaming server for torrents": "Serveur de streaming pour les torrents",
    "Torrents play through Nathflix or Stremio running on a computer on your network, or on {device} if you switch on Play torrents on {device} (Nathflix settings). Debrid streams don't need either": "Les torrents passent par Nathflix ou Stremio ouvert sur un ordinateur de votre réseau, ou par {device} si vous activez Lire les torrents sur {device} (Réglages Nathflix). Les flux debrid n'ont besoin ni de l'un ni de l'autre",
    "Find": "Chercher",
    "Report a problem": "Signaler un problème",
    "Sends Nathflix's developer the details that help fix it (no passwords, keys or stream links)": "Envoie au développeur de Nathflix les détails utiles pour le corriger (aucun mot de passe, clé ou lien de flux)",
    "Report": "Signaler",
    "{device} (Stremio's app)": "{device} (app Stremio)",
    "Looking for Nathflix or Stremio on your network…": "Recherche de Nathflix ou Stremio sur votre réseau…",
    "No streaming server found": "Aucun serveur de streaming trouvé",
    "Open Nathflix or Stremio on a computer connected to the same network as {device}, leave it running, then try again.\n\nDebrid streams play without a streaming server.": "Ouvrez Nathflix ou Stremio sur un ordinateur connecté au même réseau que {device}, laissez-le ouvert, puis réessayez.\n\nLes flux debrid se lisent sans serveur de streaming.",
    "Streaming server found": "Serveur de streaming trouvé",
    "Streaming servers found": "Serveurs de streaming trouvés",
    "Torrents will play through the one you pick. Keep it running while you watch.": "Les torrents passeront par celui que vous choisissez. Laissez-le ouvert pendant que vous regardez.",
    "Using the streaming server at {server}": "Serveur de streaming utilisé : {server}",
    "Couldn't change the streaming server. Try again from Settings.": "Impossible de changer de serveur de streaming. Réessayez depuis les Paramètres.",
    "Nathflix hasn't finished loading yet. Try again in a moment.": "Nathflix n'a pas fini de charger. Réessayez dans un instant.",
    "What went wrong, and what were you doing? Nathflix adds its version, the model of {device}, your settings, addon names and recent errors. No passwords, keys or stream links are sent.": "Qu'est-ce qui n'a pas marché, et que faisiez-vous ? Nathflix ajoute sa version, le modèle de {device}, vos réglages, le nom de vos addons et les erreurs récentes. Aucun mot de passe, clé ou lien de flux n'est envoyé.",
    "Send report": "Envoyer",
    "Sending…": "Envoi…",
    "Report sent": "Signalement envoyé",
    "Thanks! Your report code is {code}.\nMention it when you ask for help so it can be matched to you.": "Merci ! Votre code de signalement est {code}.\nDonnez-le quand vous demandez de l'aide, pour qu'on puisse le retrouver.",
    "Couldn't send the report": "Impossible d'envoyer le signalement",
    "Try again in a moment.": "Réessayez dans un instant.",
    "This stream needs a streaming server": "Ce flux a besoin d'un serveur de streaming",
    "It's a torrent, and {device} has no streaming server. Use the one in Nathflix or Stremio on a computer on your network, or let {device} download it itself (it connects to other people sharing the file).\n\nDebrid streams play without either. To always play torrents on {device}: Nathflix settings → Play torrents on {device}.": "C'est un torrent, et {device} n'a pas de serveur de streaming. Utilisez celui de Nathflix ou de Stremio sur un ordinateur de votre réseau, ou laissez {device} le télécharger lui-même (il se connecte à d'autres personnes qui partagent le fichier).\n\nLes flux debrid se lisent sans l'un ni l'autre. Pour toujours lire les torrents sur {device} : Réglages Nathflix → Lire les torrents sur {device}.",
    "Find one on my network": "En chercher un sur mon réseau",
    "Play it on {device}": "Le lire sur {device}",
    "Couldn't start the torrent. Try another stream.": "Impossible de lancer le torrent. Essayez un autre flux.",
    "This stream couldn't play: {error}. Try another one.": "Ce flux n'a pas pu être lu : {error}. Essayez-en un autre.",
    "S{s} E{e}": "S{s} É{e}",
  } : {};
  // t("Done") → "Terminé"; t("Play it on {device}", { device }) fills in {device}
  function t(text, vars) {
    let out = FR[text] || text;
    if (vars) out = out.replace(/\{(\w+)\}/g, (m, key) => (key in vars ? String(vars[key]) : m));
    return out;
  }

  const PLUGIN = "nathflix.plugin.js";
  const tv = (window.__nathflixTv = {});
  // problem reports (the plugin's `report`) add these
  tv.deviceInfo = () => native.deviceInfo();
  // the app plays the intro film itself (Intro.kt): the plugin's splash stays out of the way
  tv.nativeIntro = true;

  // ---------------------------------------------------------------------------
  // Fit the page to the screen. A TV: 1280 CSS pixels wide (a 10-foot size on any screen). A phone or
  // tablet: the screen's own width, like any app (Nathflix's mobile layout takes it from there).
  // ---------------------------------------------------------------------------
  const PHONE = (() => { try { return !!bridge.isPhone(); } catch (_) { return false; } })();
  // "tv", "phone" or "tablet" (older apps only knew phone or not), and its name in messages
  const KIND = (() => { try { return (bridge.deviceKind && bridge.deviceKind()) || (PHONE ? "phone" : "tv"); } catch (_) { return PHONE ? "phone" : "tv"; } })();
  const NOUN = KIND === "tv" ? "TV" : KIND;
  // "this phone" / "ce téléphone"…, and with a capital at the start of a sentence
  const THIS = t(`this ${NOUN}`);
  const THIS_CAP = THIS.charAt(0).toUpperCase() + THIS.slice(1);
  tv.deviceKind = KIND;
  const VIEWPORT = PHONE ? "width=device-width, initial-scale=1, user-scalable=no" : "width=1280, user-scalable=no";
  function fitViewport() {
    let meta = document.querySelector('meta[name="viewport"]');
    if (!meta) {
      if (!document.head) return;
      meta = document.createElement("meta");
      meta.name = "viewport";
      document.head.append(meta);
    }
    if (meta.content !== VIEWPORT) meta.content = VIEWPORT;
  }
  // Watched only until <head> exists, then only <head> itself: a whole-page watcher ran on every change
  // the page made, for as long as it was open
  const viewportWatch = new MutationObserver(() => {
    fitViewport();
    if (!document.head) return;
    viewportWatch.disconnect();
    new MutationObserver(fitViewport).observe(document.head, { childList: true });
  });
  if (document.head) new MutationObserver(fitViewport).observe(document.head, { childList: true });
  // (an older WebView can run this before the page has any element at all)
  else if (document.documentElement) viewportWatch.observe(document.documentElement, { childList: true, subtree: true });
  else document.addEventListener("readystatechange", fitViewport, { once: true });
  fitViewport();

  // ---------------------------------------------------------------------------
  // What Stremio Enhanced provides and Nathflix relies on
  // ---------------------------------------------------------------------------

  // Stremio's router changes the address with pushState, which fires no event: Nathflix listens for hashchange
  for (const method of ["pushState", "replaceState"]) {
    const original = history[method];
    history[method] = function (...args) {
      const result = original.apply(this, args);
      window.dispatchEvent(new Event("hashchange"));
      return result;
    };
  }

  // Stremio drops window.core on some screens: keep the first working copy
  const permanent = (window.__permanentCore = { getState: null, dispatch: null });
  let services;
  Object.defineProperty(window, "services", {
    configurable: true,
    get() { return services; },
    set(value) {
      services = value;
      const core = value && value.core;
      if (core && core.getState && !permanent.getState) permanent.getState = core.getState.bind(core);
      if (core && core.dispatch && !permanent.dispatch) permanent.dispatch = core.dispatch.bind(core);
    },
  });
  const core = () => {
    const live = window.core || (services && services.core);
    return {
      getState: (live && live.getState && live.getState.bind(live)) || permanent.getState,
      dispatch: (live && live.dispatch && live.dispatch.bind(live)) || permanent.dispatch,
    };
  };

  const schemas = {};
  const savedListeners = {};

  function readSettings(plugin) {
    try { return JSON.parse(native.getSettings(plugin) || "{}") || {}; } catch (_) { return {}; }
  }

  const api = {
    info: (plugin, message) => console.log(`[${plugin}]`, message),
    warn: (plugin, message) => console.warn(`[${plugin}]`, message),
    error: (plugin, message) => console.error(`[${plugin}]`, message),
    getSetting: async (plugin, key) => {
      const value = readSettings(plugin)[key];
      return value === undefined ? null : value;
    },
    getSettings: async (plugin) => readSettings(plugin),
    saveSetting: async (plugin, key, value) => {
      native.saveSetting(plugin, key, JSON.stringify(value === undefined ? null : value));
      const all = readSettings(plugin);
      for (const callback of savedListeners[plugin] || []) {
        try { callback(all); } catch (err) { console.error(err); }
      }
      return true;
    },
    // a promise, like Stremio Enhanced's (Nathflix chains on it: without one it stopped before loading
    // the TV's saved settings, so changes in Nathflix settings never took effect)
    registerSettings: async (plugin, schema) => { schemas[plugin] = schema; },
    onSettingsSaved: (plugin, callback) => { (savedListeners[plugin] = savedListeners[plugin] || []).push(callback); },
    showAlert: (type, title, message, buttons) => dialog({ title, message, buttons: buttons && buttons.length ? buttons : [t("OK")] }),
    showPrompt: (plugin, title, message, defaultValue) => dialog({ title, message, buttons: [t("OK"), t("Cancel")], input: defaultValue == null ? "" : String(defaultValue) })
      .then((r) => (r && r.button === 0 ? r.value : null)),
    // Nathflix's "Open in mpv / VLC" goes to the app's own player here
    launchExternalPlayer: async (player, url) => {
      native.play(JSON.stringify({ url, title: document.title || "Nathflix", hash: location.hash }));
      return { success: true };
    },
    getExternalPlayerPaths: async () => ({ vlc: null, mpv: null }),
    applyTheme: () => {},
    getGpuRenderer: async () => "auto",
    setGpuRenderer: async () => {},
  };
  window.StremioEnhancedAPI = api;

  // ---------------------------------------------------------------------------
  // Dialogs (alerts, prompts, settings): plain page elements, so the remote's arrows and OK work
  // ---------------------------------------------------------------------------
  const STYLE = `
    .dxtv-modal { position: fixed; inset: 0; z-index: 2147483600; display: flex; align-items: center; justify-content: center; background: rgba(0,0,0,.72); font-family: Inter, system-ui, sans-serif; }
    .dxtv-sheet { width: min(640px, 86vw); max-height: 84vh; overflow: auto; padding: 28px 30px; border-radius: 14px; background: #1c1c1c; color: #fff; box-shadow: 0 24px 80px rgba(0,0,0,.6); }
    .dxtv-sheet h2 { margin: 0 0 10px; font-size: 24px; font-weight: 700; }
    .dxtv-sheet p { margin: 0 0 18px; color: #c8c8c8; font-size: 16px; line-height: 1.5; white-space: pre-line; }
    .dxtv-sheet input[type=text] { width: 100%; box-sizing: border-box; margin: 0 0 18px; padding: 12px 14px; border: 2px solid #444; border-radius: 8px; background: #111; color: #fff; font-size: 17px; }
    .dxtv-sheet input[type=text]:focus { border-color: #fff; outline: none; }
    .dxtv-buttons { display: flex; flex-wrap: wrap; gap: 12px; justify-content: flex-end; }
    .dxtv-btn { padding: 11px 22px; border: 0; border-radius: 8px; background: rgba(255,255,255,.14); color: #fff; font: 600 16px Inter, system-ui, sans-serif; cursor: pointer; transition: transform .15s, background-color .15s; }
    .dxtv-btn:focus { outline: none; background: #fff; color: #000; transform: scale(1.06); }
    .dxtv-row { display: flex; align-items: center; justify-content: space-between; gap: 18px; width: 100%; margin: 0 0 8px; padding: 14px 16px; border: 0; border-radius: 10px; background: rgba(255,255,255,.06); color: #fff; text-align: left; font: 500 16px Inter, system-ui, sans-serif; cursor: pointer; }
    .dxtv-group { margin: 22px 0 8px; font-size: 13px; font-weight: 700; letter-spacing: .08em; text-transform: uppercase; color: #8d8d96; }
    .dxtv-sheet h2 + .dxtv-group { margin-top: 6px; }
    .dxtv-row:focus { outline: none; background: rgba(255,255,255,.18); transform: scale(1.015); }
    .dxtv-row small { display: block; margin-top: 4px; color: #9a9a9a; font-size: 13px; line-height: 1.4; }
    .dxtv-row b { flex: none; color: #e50914; font-weight: 700; }
    .dxtv-settings-entry { margin: 0 0 24px; }
    /* No streaming server runs on the TV itself (debrid streams don't need one): no nagging bar */
    [class*="board-warning-container"] { display: none !important; }
    .dxtv-toast { position: fixed; left: 50%; bottom: 48px; z-index: 2147483600; transform: translateX(-50%); padding: 14px 22px; border-radius: 10px; background: rgba(20,20,20,.94); color: #fff; font: 500 16px Inter, system-ui, sans-serif; box-shadow: 0 10px 40px rgba(0,0,0,.5); }
    /* Phones and tablets (touch): what was tapped last doesn't stay lit up, and sheets fit the screen */
    .dxtv-touch .dxtv-btn:focus:not(:focus-visible) { background: rgba(255,255,255,.14); color: #fff; transform: none; }
    .dxtv-touch .dxtv-row:focus:not(:focus-visible) { background: rgba(255,255,255,.06); transform: none; }
    .dxtv-touch .dxtv-sheet { width: calc(100vw - 24px); max-height: calc(100vh - 24px); padding: 22px 18px; box-sizing: border-box; }
    .dxtv-touch .dxtv-row { min-height: 52px; }
    .dxtv-touch .dxtv-btn { min-height: 44px; }
    .dxtv-touch .dxtv-buttons { position: sticky; bottom: -22px; padding: 12px 0; background: #1c1c1c; }
    .dxtv-touch .dxtv-toast { bottom: calc(76px + env(safe-area-inset-bottom, 0px)); max-width: calc(100vw - 32px); }
  `;
  function ensureStyle() {
    if (PHONE) document.documentElement.classList.add("dxtv-touch");
    if (document.getElementById("dxtv-style")) return;
    const style = document.createElement("style");
    style.id = "dxtv-style";
    style.textContent = STYLE;
    (document.head || document.documentElement).append(style);
  }

  const esc = (s) => String(s == null ? "" : s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  let openModal = null;

  // Resolves with the button index (or { button, value } when there's an input); -1 when dismissed
  async function dialog({ title, message, buttons, input }) {
    ensureStyle();
    closeModal();
    // One dialog at a time: Stremio's own (a promotion, an addon to install) goes first, since it
    // holds on to the remote's focus while it's open
    while (document.querySelector('[class*="modal-container"]')) await new Promise((r) => setTimeout(r, 400));
    // (another dialog asked for meanwhile gives way to this one: never two on screen)
    closeModal();
    return new Promise((resolve) => {
      const el = document.createElement("div");
      el.className = "dxtv-modal";
      el.innerHTML = `<div class="dxtv-sheet" role="dialog">
        <h2>${esc(title)}</h2>${message ? `<p>${esc(String(message).replace(/<[^>]+>/g, ""))}</p>` : ""}
        ${input != null ? `<input type="text" value="${esc(input)}">` : ""}
        <div class="dxtv-buttons">${buttons.map((b, i) => `<button class="dxtv-btn" data-i="${i}">${esc(b)}</button>`).join("")}</div>
      </div>`;
      const field = el.querySelector("input");
      const finish = (i) => {
        el.remove();
        if (openModal && openModal.el === el) openModal = null;
        resolve(input != null ? { button: i, value: field ? field.value : "" } : i);
      };
      el.addEventListener("click", (e) => {
        const b = e.target.closest("[data-i]");
        if (b) finish(Number(b.dataset.i));
      });
      document.body.append(el);
      openModal = { el, dismiss: () => finish(-1) };
      const first = field || el.querySelector(".dxtv-btn");
      first.focus();
      // a dialog of Stremio's that just closed hands focus back a moment later: take it again
      for (const ms of [150, 450]) setTimeout(() => { if (el.isConnected && !el.contains(document.activeElement)) first.focus(); }, ms);
    });
  }

  function closeModal() {
    if (openModal) openModal.dismiss();
  }

  function toast(text, ms = 4500) {
    ensureStyle();
    document.querySelectorAll(".dxtv-toast").forEach((n) => n.remove());
    const el = document.createElement("div");
    el.className = "dxtv-toast";
    el.textContent = text;
    document.body.append(el);
    setTimeout(() => el.remove(), ms);
  }

  // ---------------------------------------------------------------------------
  // Streaming server: a TV has none of its own, and torrents need one. Nathflix or Stremio running on
  // a computer on the same network has one (it listens on the network), so the app looks for it.
  // ---------------------------------------------------------------------------
  let serversWaiting = null;
  let scanning = null;
  tv.serversFound = (urls) => {
    if (serversWaiting) serversWaiting(Array.isArray(urls) ? urls : []);
    serversWaiting = null;
  };
  // (Find pressed again while a search runs: the same search answers both)
  const scanForServers = () => scanning || (scanning = new Promise((resolve) => {
    serversWaiting = resolve;
    setTimeout(() => { if (serversWaiting === resolve) tv.serversFound([]); }, 25000);
    native.findServers();
  }).finally(() => { scanning = null; }));

  async function useServer(url) {
    const { getState, dispatch } = core();
    if (!getState || !dispatch) return false;
    const ctx = await getState("ctx");
    if (!ctx || !ctx.profile || !ctx.profile.settings) return false;
    const known = ((ctx && ctx.streamingServerUrls) || []).some((s) => (s && s.url ? s.url : s) === url);
    if (!known) dispatch({ action: "Ctx", args: { action: "AddServerUrl", args: url } });
    dispatch({ action: "Ctx", args: { action: "UpdateSettings", args: { ...ctx.profile.settings, streamingServerUrl: url } } });
    return true;
  }

  const serverName = (url) => /\/\/127\.0\.0\.1:/.test(url) ? t("{device} (Stremio's app)", { device: THIS_CAP }) : url.replace(/^http:\/\/|\/$/g, "");

  // Resolves true once a server has been picked
  async function findServer() {
    for (;;) {
      toast(t("Looking for Nathflix or Stremio on your network…"), 6000);
      const urls = await scanForServers();
      document.querySelectorAll(".dxtv-toast").forEach((n) => n.remove());
      if (!urls.length) {
        const again = await dialog({
          title: t("No streaming server found"),
          message: t("Open Nathflix or Stremio on a computer connected to the same network as {device}, leave it running, then try again.\n\nDebrid streams play without a streaming server.", { device: THIS }),
          buttons: [t("Try again"), t("Close")],
        });
        if (again === 0) continue;
        return false;
      }
      const pick = await dialog({
        title: urls.length === 1 ? t("Streaming server found") : t("Streaming servers found"),
        message: t("Torrents will play through the one you pick. Keep it running while you watch."),
        buttons: [...urls.map(serverName), t("Cancel")],
      });
      if (pick < 0 || pick >= urls.length) return false;
      const ok = await useServer(urls[pick]);
      toast(ok ? t("Using the streaming server at {server}", { server: serverName(urls[pick]) }) : t("Couldn't change the streaming server. Try again from Settings."), 5000);
      return ok;
    }
  }

  // Report a problem: a description typed with the remote, then the plugin's report goes to the developer
  async function reportProblem() {
    const dx = window.__nathflix;
    if (!dx || !dx.report) { toast(t("Nathflix hasn't finished loading yet. Try again in a moment.")); return; }
    const r = await dialog({
      title: t("Report a problem"),
      message: t("What went wrong, and what were you doing? Nathflix adds its version, the model of {device}, your settings, addon names and recent errors. No passwords, keys or stream links are sent.", { device: THIS }),
      buttons: [t("Send report"), t("Cancel")],
      input: "",
    });
    if (!r || r.button !== 0) return;
    toast(t("Sending…"), 8000);
    try {
      const code = await dx.report.send(r.value || "");
      document.querySelectorAll(".dxtv-toast").forEach((n) => n.remove());
      await dialog({ title: t("Report sent"), message: t("Thanks! Your report code is {code}.\nMention it when you ask for help so it can be matched to you.", { code }), buttons: [t("OK")] });
    } catch (err) {
      document.querySelectorAll(".dxtv-toast").forEach((n) => n.remove());
      await dialog({ title: t("Couldn't send the report"), message: (err && err.message) || t("Try again in a moment."), buttons: [t("OK")] });
    }
  }

  // Nathflix's settings: an entry at the top of Stremio's Settings, opening a list made for the remote
  // Options for Nathflix's in-page player, which the TV doesn't use (it has its own player)
  const DESKTOP_ONLY = new Set(["playerExtras", "enhance", "seekPreviews", "subtitleStyle", "subtitleFont",
    "convertVideo", "subtitlesStart", "hdrSubtitles", "pictureSound", "streamStats", "preloadNext", "upNext", "skipFiller",
    "rememberPlayback", "playerKeys", "pip", "externalPlayer", "remainingTime", "watchParty", "partyFollow", "partyActivity", "partySounds"]);

  // Hover cards and their trailers: phones and tablets have no hover (a tap opens the title)
  const NOT_ON_TOUCH = new Set(["previews", "trailers", "trailerDelay"]);

  function settingsSheet() {
    const plugin = PLUGIN.replace(/\.plugin\.js$/, "");
    const schema = (schemas[plugin] || []).filter((o) => !DESKTOP_ONLY.has(o.key) && !(PHONE && NOT_ON_TOUCH.has(o.key)));
    ensureStyle();
    closeModal();
    const el = document.createElement("div");
    el.className = "dxtv-modal";
    const values = () => readSettings(plugin);
    const shown = (o, v) => {
      if (o.type === "toggle") return v ? t("On") : t("Off");
      if (o.type === "select") return ((o.options || []).find((x) => x.value === v) || {}).label || v;
      return v ? t("Set") : t("Not set");
    };
    const render = (focusKey) => {
      const current = values();
      el.innerHTML = `<div class="dxtv-sheet" role="dialog"><h2>${esc(t("Nathflix settings"))}</h2>
        ${schema.map((o, i) => {
          const v = current[o.key] === undefined ? o.defaultValue : current[o.key];
          // a heading wherever a new group of settings starts
          const heading = o.group && o.group !== (schema[i - 1] || {}).group ? `<h3 class="dxtv-group">${esc(o.group)}</h3>` : "";
          return `${heading}<button class="dxtv-row" data-key="${esc(o.key)}"><span>${esc(o.label)}${o.description ? `<small>${esc(o.description)}</small>` : ""}</span><b>${esc(shown(o, v))}</b></button>`;
        }).join("")}
        <div class="dxtv-buttons"><button class="dxtv-btn" data-close>${esc(t("Done"))}</button></div></div>`;
      const target = (focusKey && el.querySelector(`[data-key="${CSS.escape(focusKey)}"]`)) || el.querySelector(".dxtv-row, .dxtv-btn");
      if (target) target.focus();
    };
    el.addEventListener("click", async (e) => {
      if (e.target.closest("[data-close]")) { closeModal(); return; }
      const row = e.target.closest("[data-key]");
      if (!row) return;
      const o = schema.find((x) => x.key === row.dataset.key);
      const current = values();
      const v = current[o.key] === undefined ? o.defaultValue : current[o.key];
      if (o.type === "toggle") {
        await api.saveSetting(plugin, o.key, !v);
      } else if (o.type === "select") {
        const options = o.options || [];
        const next = options[(options.findIndex((x) => x.value === v) + 1) % options.length];
        if (next) await api.saveSetting(plugin, o.key, next.value);
      } else {
        el.style.display = "none";
        openModal = null;
        const r = await dialog({ title: o.label, message: o.description || "", buttons: [t("Save"), t("Cancel")], input: v || "" });
        el.style.display = "";
        openModal = { el, dismiss: () => { el.remove(); openModal = null; } };
        if (r && r.button === 0) await api.saveSetting(plugin, o.key, r.value.trim());
      }
      render(o.key);
    });
    document.body.append(el);
    openModal = { el, dismiss: () => { el.remove(); openModal = null; } };
    render();
  }

  function ensureSettingsEntry() {
    if (!/^#\/settings/.test(location.hash)) return;
    // also in Settings' own list (General, Interface… Shortcuts), where people look for it
    const shortcuts = document.querySelector('[class*="settings-container"] [data-section="shortcuts"], [class*="settings-content"] [data-section="shortcuts"]');
    if (shortcuts && !shortcuts.parentElement.querySelector(".dxtv-side")) {
      const side = document.createElement("div");
      side.className = `${shortcuts.className} dxtv-side`;
      side.tabIndex = 0;
      side.title = "Nathflix";
      side.textContent = "Nathflix";
      side.addEventListener("click", settingsSheet);
      side.addEventListener("keydown", (e) => { if (e.key === "Enter") { e.preventDefault(); settingsSheet(); } });
      shortcuts.after(side);
    }
    const content = document.querySelector('[class*="settings-content"] [class*="sections-container"]');
    if (!content) return;
    ensureStyle();
    if (!content.querySelector(".dxtv-settings-entry")) {
      const entry = document.createElement("button");
      entry.className = "dxtv-row dxtv-settings-entry";
      entry.innerHTML = `<span>${esc(t("Nathflix settings"))}<small>${esc(t("Look, Home rows, previews, trailers, reviews, sounds and more"))}</small></span><b>${esc(t("Open"))}</b>`;
      entry.addEventListener("click", settingsSheet);
      const server = document.createElement("button");
      server.className = "dxtv-row dxtv-settings-entry dxtv-server-entry";
      server.innerHTML = `<span>${esc(t("Streaming server for torrents"))}<small>${esc(t("Torrents play through Nathflix or Stremio running on a computer on your network, or on {device} if you switch on Play torrents on {device} (Nathflix settings). Debrid streams don't need either", { device: THIS }))}</small></span><b>${esc(t("Find"))}</b>`;
      server.addEventListener("click", findServer);
      content.prepend(entry, server);
    }
    // Report a problem: only when the plugin has somewhere to send reports (its REPORT_ENDPOINT)
    const dx = window.__nathflix;
    if (!(dx && dx.report && dx.report.enabled === true) || content.querySelector(".dxtv-report-entry")) return;
    const problem = document.createElement("button");
    problem.className = "dxtv-row dxtv-settings-entry dxtv-report-entry";
    problem.innerHTML = `<span>${esc(t("Report a problem"))}<small>${esc(t("Sends Nathflix's developer the details that help fix it (no passwords, keys or stream links)"))}</small></span><b>${esc(t("Report"))}</b>`;
    problem.addEventListener("click", reportProblem);
    content.querySelector(".dxtv-server-entry").after(problem);
  }

  // ---------------------------------------------------------------------------
  // Playback: every stream plays in the app's native player. Stremio's own player screen stays
  // underneath, silent, so its watch progress (Continue Watching, watched marks) keeps working.
  // ---------------------------------------------------------------------------
  let handoff = null; // { hash, video id } of the stream the native player has

  // Stremio's own player screen never shows: from the moment a stream is picked, a black veil covers
  // the page (with a spinner while the stream is looked up), until the page has left the player screen
  const VEIL_CSS = `
    #dxtv-veil { position: fixed; inset: 0; z-index: 2147483500; display: flex; align-items: center; justify-content: center; background: #000; }
    #dxtv-veil i { width: 46px; height: 46px; border: 4px solid rgba(255,255,255,.18); border-top-color: #e50914; border-radius: 50%; animation: dxtv-spin .8s linear infinite; opacity: 0; transition: opacity .2s .3s; }
    #dxtv-veil.is-pending i { opacity: 1; }
    @keyframes dxtv-spin { to { transform: rotate(360deg); } }
  `;
  function veil(on, pending) {
    let el = document.getElementById("dxtv-veil");
    if (!on) {
      if (el) el.remove();
      return;
    }
    if (!document.getElementById("dxtv-veil-style")) {
      const style = document.createElement("style");
      style.id = "dxtv-veil-style";
      style.textContent = VEIL_CSS;
      (document.head || document.documentElement).append(style);
    }
    if (!el) {
      el = document.createElement("div");
      el.id = "dxtv-veil";
      el.innerHTML = "<i></i>";
      (document.body || document.documentElement).append(el);
    }
    el.classList.toggle("is-pending", !!pending);
  }
  // Earlier still than the address change: the tap on a stream itself
  document.addEventListener("click", (e) => {
    const link = e.target instanceof Element && e.target.closest('a[href^="#/player/"]');
    if (!link || e.defaultPrevented) return;
    veil(true, true);
    // the tap may still be turned down (by Stremio or the plugin): never leave the page covered.
    // Their handlers run after this one: whether they cancelled it is known a moment later
    setTimeout(() => { if (e.defaultPrevented && !/^#\/player\//.test(location.hash)) veil(false); }, 0);
    setTimeout(() => { if (!/^#\/player\//.test(location.hash)) veil(false); }, 1500);
  }, true);

  // Stremio's in-page video never plays while the native player has the stream
  const realPlay = HTMLMediaElement.prototype.play;
  HTMLMediaElement.prototype.play = function () {
    if (handoff && /^#\/player\//.test(location.hash)) {
      this.muted = true;
      try { this.pause(); } catch (_) { /* not ready */ }
      return Promise.resolve();
    }
    return realPlay.apply(this, arguments);
  };

  function silenceWebPlayer() {
    document.querySelectorAll('[class*="player-container"] video').forEach((v) => {
      v.muted = true;
      if (!v.paused) v.pause();
    });
  }

  function dispatch(action) {
    const d = core().dispatch;
    if (!d) return;
    try { d(action, "player"); } catch (err) { console.warn("[Nathflix TV] dispatch failed", err); }
  }

  async function playerState() {
    const get = core().getState;
    if (!get) return null;
    try { return await get("player"); } catch (_) { return null; }
  }

  function subtitleLabel(lang) {
    try { return new Intl.DisplayNames([LANG], { type: "language" }).of(lang) || lang; } catch (_) { return lang; }
  }

  // Stremio's own settings (Settings → Player): the languages to start a video in
  async function profileSettings() {
    try {
      const ctx = await core().getState("ctx");
      return (ctx && ctx.profile && ctx.profile.settings) || {};
    } catch (_) {
      return {};
    }
  }

  // Addons can offer dozens of subtitle files: keep up to three per language, with the Stremio
  // subtitle language (then Nathflix's language, then English) first, so the subtitle menu stays short
  async function pickSubtitles(list) {
    const preferred = (await profileSettings()).subtitlesLanguage || "";
    const own = LANG === "fr" ? /^(fre|fra|fr)$/ : /^(eng|en)$/;
    const rank = (lang) => (lang === preferred ? 0 : own.test(lang) ? 1 : /^(eng|en)$/.test(lang) ? 2 : 3);
    const count = {};
    const out = [];
    for (const sub of list.filter((x) => x && x.url).sort((a, b) => rank(a.lang || "") - rank(b.lang || ""))) {
      const lang = sub.lang || "";
      count[lang] = (count[lang] || 0) + 1;
      if (count[lang] > 3) continue;
      // "English", "English 2", "English 3"
      out.push({ url: sub.url, lang, label: subtitleLabel(lang) + (count[lang] > 1 ? ` ${count[lang]}` : "") });
      if (out.length >= 45) break;
    }
    return out;
  }

  // Whether Stremio's streaming server (on this TV, or a computer picked in Settings) is answering
  async function streamingServerUp() {
    try {
      const s = await core().getState("streaming_server");
      return !!(s && s.settings && s.settings.type === "Ready");
    } catch (_) {
      return false;
    }
  }

  async function startHandoff() {
    const hash = location.hash;
    if (!/^#\/player\//.test(hash) || (handoff && handoff.hash === hash)) return;
    handoff = { hash, pending: true };
    veil(true, true);
    let state = null;
    // With no streaming server online, torrents play on the TV itself: nothing to wait for
    const serverUp = await streamingServerUp();
    // The player screen fills in over a moment: wait until the stream is known, and give the
    // title's details (name, episode, saved position) a few seconds more to arrive
    let known = 0;
    for (let i = 0; i < 60 && location.hash === hash; i++) {
      state = await playerState();
      // (right after Next episode, Stremio can still hold the previous episode's player for a moment)
      if (!forThisRoute(state, hash)) { await new Promise((r) => setTimeout(r, 250)); continue; }
      const stream = state && state.selected && state.selected.stream;
      const streamKnown = stream && (stream.url || (state.stream && state.stream.type === "Ready") || stream.infoHash || stream.externalUrl);
      if (streamKnown) {
        known++;
        const metaReady = state.metaItem && state.metaItem.type !== "Loading";
        // A torrent's address comes from the streaming server, which can take a while to answer:
        // wait for it (up to ~10 s) before saying there's no server
        const waitingForServer = serverUp && !stream.url && stream.infoHash && !(state.stream && /^(Ready|Err)$/.test(state.stream.type));
        if ((metaReady && state.libraryItem !== undefined && !waitingForServer) || known >= (waitingForServer ? 40 : 12)) break;
      }
      await new Promise((r) => setTimeout(r, 250));
    }
    if (location.hash !== hash || !state || !state.selected) { handoff = null; veil(false); return; }
    const stream = state.selected.stream || {};
    // Trailers are YouTube videos, which Stremio plays through a streaming server (none on a TV): the
    // TV's YouTube app plays them instead, and Back comes straight back here
    if (stream.ytId) {
      handoff = null;
      history.back();
      native.openExternal(`https://www.youtube.com/watch?v=${encodeURIComponent(stream.ytId)}`);
      return;
    }
    silenceWebPlayer();

    const ready = state.stream && state.stream.type === "Ready" && state.stream.content;
    // (for a torrent, Stremio gives a streaming server address even when none is running)
    let url = (ready && ready.url && (serverUp || !stream.infoHash) ? ready.url : null) || stream.url || null;
    // A torrent and no streaming server. Nathflix for Android can download it itself, but only when that's switched
    // on in Nathflix settings (off by default: debrid, or a computer's streaming server, is the usual way);
    // otherwise the choice is offered for this stream
    let torrent = false;
    if (!url && stream.infoHash) {
      let onTv = readSettings(PLUGIN.replace(/\.plugin\.js$/, "")).tvTorrents === true;
      if (!onTv) {
        const choice = await dialog({
          title: t("This stream needs a streaming server"),
          message: t("It's a torrent, and {device} has no streaming server. Use the one in Nathflix or Stremio on a computer on your network, or let {device} download it itself (it connects to other people sharing the file).\n\nDebrid streams play without either. To always play torrents on {device}: Nathflix settings → Play torrents on {device}.", { device: THIS }),
          buttons: [t("Find one on my network"), t("Play it on {device}", { device: THIS }), t("Cancel")],
        });
        if (choice === 1 && location.hash === hash) onTv = true;
        else {
          handoff = null;
          if (location.hash === hash) history.back();
          if (choice === 0) findServer();
          return;
        }
      }
      // (an addon's "sources" reach the player as "announce")
      const trackers = [...(stream.announce || []), ...(stream.sources || [])].filter((x) => /^tracker:/.test(x)).map((x) => x.replace(/^tracker:/, ""));
      const name = (stream.behaviorHints && stream.behaviorHints.filename) || "";
      url = native.torrentUrl(JSON.stringify({ infoHash: stream.infoHash, fileIdx: Number.isInteger(stream.fileIdx) ? stream.fileIdx : -1, trackers, name })) || null;
      torrent = !!url;
      if (!url) {
        handoff = null;
        history.back();
        toast(t("Couldn't start the torrent. Try another stream."), 5000);
        return;
      }
    }
    if (!url && stream.externalUrl) {
      native.openExternal(stream.externalUrl);
      handoff = null;
      history.back();
      return;
    }
    if (!url) { handoff = null; veil(false); return; }

    const meta = state.metaItem && state.metaItem.type === "Ready" ? state.metaItem.content : (state.metaItem && state.metaItem.content) || {};
    const videoId = state.selected.streamRequest && state.selected.streamRequest.path && state.selected.streamRequest.path.id;
    const video = (meta.videos || []).find((v) => v.id === videoId);
    const lib = state.libraryItem && state.libraryItem.state;
    const resumeMs = lib && lib.video_id === videoId && lib.timeOffset > 0 ? lib.timeOffset : 0;
    const headers = (stream.behaviorHints && stream.behaviorHints.proxyHeaders && stream.behaviorHints.proxyHeaders.request) || {};
    // the stream's own subtitle files first, then the subtitle addons'
    const subtitles = await pickSubtitles([...(stream.subtitles || []), ...(state.subtitles || [])]);
    const episode = video && video.season != null ? `${t("S{s} E{e}", { s: video.season, e: video.episode })}${video.title || video.name ? ` · ${video.title || video.name}` : ""}` : "";

    const profile = await profileSettings();
    // Skip intro / recap / credits: the times Nathflix has for this episode (looked up while the player
    // gets ready, never holding it up for long)
    const metaPath = state.selected.metaRequest && state.selected.metaRequest.path;
    const dx = window.__nathflix;
    const skips = dx && dx.skipSegments && videoId
      ? await Promise.race([dx.skipSegments(meta.type || (metaPath && metaPath.type), meta.id || (metaPath && metaPath.id), videoId).catch(() => []), new Promise((r) => setTimeout(() => r([]), 4000))])
      : [];
    if (location.hash !== hash) { handoff = null; return; }
    handoff = { hash, videoId, started: Date.now() };
    veil(true, false);
    // Phones and tablets: the player's Episodes and Next episode, and how it behaves when you leave
    const mobile = PHONE ? mobilePlayback(state, meta, video, metaPath) : {};
    native.play(JSON.stringify({
      ...mobile,
      hash,
      videoId,
      skips,
      torrent,
      autoSkip: (dx && dx.settings && dx.settings.autoSkip) || "off",
      audioLanguage: profile.audioLanguage || "",
      // Stremio's subtitle settings (Settings → Player), drawn the same way by the native player
      subtitleStyle: {
        size: profile.subtitlesSize,
        offset: profile.subtitlesOffset,
        bold: profile.subtitlesBold,
        text: profile.subtitlesTextColor,
        background: profile.subtitlesBackgroundColor,
        outline: profile.subtitlesOutlineColor,
        opacity: profile.subtitlesOpacity,
      },
      url,
      title: meta.name || state.title || stream.title || "Nathflix",
      subtitle: episode,
      startMs: resumeMs,
      headers,
      subtitles,
      background: meta.background || meta.poster || "",
    }));
  }

  // Whether Stremio's player state is the one for [hash]: a player route ends with the video's id
  // (#/player/<stream>/<addon>/<addon>/<type>/<id>/<video>); shorter routes can't be checked
  function forThisRoute(state, hash) {
    const parts = hash.replace(/^#\/player\//, "").split(/[?]/)[0].split("/");
    if (parts.length < 6) return true;
    let video = parts[parts.length - 1];
    try { video = decodeURIComponent(video); } catch (_) { /* as is */ }
    const req = state && state.selected && state.selected.streamRequest;
    const id = req && req.path && req.path.id;
    return !id || id === video;
  }

  // Episodes and Next episode for the phone and tablet player: where the page goes for each (the
  // player hands the route back as it closes). The next episode's player when Stremio already knows
  // its stream (same release), otherwise its list of streams, like Stremio's own "play next"
  function mobilePlayback(state, meta, video, metaPath) {
    const type = meta.type || (metaPath && metaPath.type) || "series";
    const metaId = meta.id || (metaPath && metaPath.id) || "";
    const streamsOf = (id) => `#/detail/${encodeURIComponent(type)}/${encodeURIComponent(metaId)}/${encodeURIComponent(id)}`;
    const label = (v) => (v.season != null ? t("S{s} E{e}", { s: v.season, e: v.episode }) : "") + (v.title || v.name ? ` · ${v.title || v.name}` : "");
    const out = {};
    if (video && video.season != null && metaId) {
      out.episodes = (meta.videos || [])
        .filter((v) => v && v.season === video.season)
        .sort((a, b) => (a.episode || 0) - (b.episode || 0))
        .slice(0, 80)
        .map((v) => ({ route: streamsOf(v.id), episode: v.episode || 0, title: v.title || v.name || "", overview: String(v.overview || v.description || "").slice(0, 220), thumbnail: /^https?:/.test(v.thumbnail || "") ? v.thumbnail : "", current: v.id === video.id }));
      out.season = video.season;
    }
    const next = state.nextVideo;
    if (next && next.id && metaId) {
      const player = next.deepLinks && next.deepLinks.player;
      out.next = { label: label(next).replace(/^ · /, ""), route: player ? "#" + String(player).replace(/^#/, "") : streamsOf(next.id) };
    }
    const own = readSettings(PLUGIN.replace(/\.plugin\.js$/, ""));
    out.mobile = { pip: own.mobilePip !== false, background: own.backgroundPlay !== false, autoNext: own.autoNext !== false };
    return out;
  }

  // Goes somewhere in place of the current page (the player's), so Back from there doesn't land on a
  // player that has closed
  function replaceRoute(hash) {
    history.replaceState(history.state, "", hash);
    window.dispatchEvent(new PopStateEvent("popstate", { state: history.state }));
  }

  // The native player reports where it is every few seconds, and once more when it closes
  tv.onProgress = (timeMs, durationMs, paused) => {
    if (!handoff || !/^#\/player\//.test(location.hash)) return;
    silenceWebPlayer();
    dispatch({ action: "Player", args: { action: "TimeChanged", args: { time: Math.max(0, Math.round(timeMs)), duration: Math.max(0, Math.round(durationMs)), device: "nathflix-tv" } } });
    dispatch({ action: "Player", args: { action: "PausedChanged", args: { paused: !!paused } } });
  };

  // playRoute: the player went on to another episode (Next episode, or one picked in Episodes)
  function closeHandoff(timeMs, durationMs, ended, error, playRoute) {
    if (handoff && /^#\/player\//.test(location.hash)) {
      if (timeMs > 0) tv.onProgress(timeMs, durationMs, true);
      if (ended) dispatch({ action: "Player", args: { action: "Ended" } });
      handoff = null;
      if (playRoute && /^#\/(player|detail)\//.test(playRoute)) replaceRoute(playRoute);
      else history.back();
    } else {
      handoff = null;
    }
    if (error) toast(t("This stream couldn't play: {error}. Try another one.", { error }), 7000);
  }

  // The native player closed: the app keeps how (see PlaybackBus) until the page takes it
  let resuming = false;
  tv.playerClosed = () => {
    if (resuming) return; // picked up once the restarted page is back on the player screen
    const raw = native.takePendingClose();
    if (!raw) return;
    try {
      const c = JSON.parse(raw);
      // an older film's player (another one has started since): nothing to do on this page
      if (c.hash && handoff && handoff.hash && c.hash !== handoff.hash) return;
      closeHandoff(c.timeMs, c.durationMs, c.ended, c.error, c.playRoute);
    } catch (err) {
      console.warn("[Nathflix TV] couldn't read how the player closed", err);
    }
  };

  // Android restarted the page while a video played (TVs short of memory): go back to that video's
  // player screen, silently, so its watch progress is still recorded, and finish up if it has closed
  async function resumePlayback() {
    let target = null;
    try {
      const active = native.activePlayback();
      const closed = active ? null : native.takePendingClose();
      const info = JSON.parse(active || closed || "null");
      if (info && /^#\/player\//.test(info.hash || "")) target = { info, closed };
    } catch (_) { /* nothing to pick up */ }
    if (!target) return;
    const { info, closed } = target;
    resuming = true;
    handoff = { hash: info.hash, videoId: info.videoId || null, started: Date.now(), resumed: true };
    veil(true, false);
    if (location.hash !== info.hash) location.hash = info.hash;
    // Stremio needs a moment to load the player screen before it takes progress
    for (let i = 0; i < 60 && location.hash === info.hash; i++) {
      const state = await playerState();
      if (state && state.selected) break;
      await new Promise((r) => setTimeout(r, 250));
    }
    silenceWebPlayer();
    resuming = false;
    if (closed) {
      closeHandoff(info.timeMs, info.durationMs, info.ended, info.error, info.playRoute);
    } else {
      tv.playerClosed(); // it may have closed while we waited
    }
  }

  // ---------------------------------------------------------------------------
  // The remote's Back button: closes what's open, otherwise goes back a screen.
  // Returns false on Home, where the app decides (it leaves).
  // ---------------------------------------------------------------------------
  tv.back = () => {
    if (openModal) { closeModal(); return true; }
    const sheet = document.querySelector(".dxe-modal");
    if (sheet) {
      const close = sheet.querySelector("[data-act=close]");
      if (close) close.click(); else sheet.remove();
      return true;
    }
    if (window.__nathflix && typeof window.__nathflix.closePreview === "function" && window.__nathflix.closePreview()) return true;
    // Stremio's own dialogs (addon install, promotions…) close with their × button
    const stremioModal = document.querySelector('[class*="modal-container"]');
    if (stremioModal) {
      const close = stremioModal.querySelector('[class*="close-button"]');
      if (close) { close.click(); return true; }
    }
    const menu = document.querySelector('[class*="menu-container"][class*="open"], [class*="popup-menu-container"]');
    if (menu) {
      document.body.dispatchEvent(new MouseEvent("mousedown", { bubbles: true }));
      return true;
    }
    const route = (location.hash || "#/").replace(/^#\/?/, "").split(/[/?]/)[0];
    if (!route || route === "board") return false;
    history.back();
    return true;
  };

  // ---------------------------------------------------------------------------
  // Load the built-in Nathflix plugin once the page is up, the way Stremio Enhanced does
  // ---------------------------------------------------------------------------
  function loadPlugin() {
    if (document.getElementById(PLUGIN)) return;
    const source = native.pluginSource();
    if (!source) return;
    const base = PLUGIN.replace(/\.plugin\.js$/, "");
    const script = document.createElement("script");
    script.id = PLUGIN;
    script.textContent = `(function () {
      const StremioEnhancedAPI = {
        logger: {
          info: (m) => window.StremioEnhancedAPI.info(${JSON.stringify(base)}, m),
          warn: (m) => window.StremioEnhancedAPI.warn(${JSON.stringify(base)}, m),
          error: (m) => window.StremioEnhancedAPI.error(${JSON.stringify(base)}, m),
        },
        getSetting: (key) => window.StremioEnhancedAPI.getSetting(${JSON.stringify(base)}, key),
        saveSetting: (key, value) => window.StremioEnhancedAPI.saveSetting(${JSON.stringify(base)}, key, value),
        registerSettings: (schema) => window.StremioEnhancedAPI.registerSettings(${JSON.stringify(base)}, schema),
        onSettingsSaved: (cb) => window.StremioEnhancedAPI.onSettingsSaved(${JSON.stringify(base)}, cb),
        showAlert: window.StremioEnhancedAPI.showAlert,
        showPrompt: (t, m, d) => window.StremioEnhancedAPI.showPrompt(${JSON.stringify(base)}, t, m, d),
      };
      try {
        ${source}
      } catch (err) {
        console.error("[Nathflix TV] plugin crashed", err);
      }
    })();`;
    document.body.append(script);
  }

  // Text boxes (Search, an episode list's search): the TV's keyboard opened the moment the
  // selection reached one and took over the arrows, so the remote seemed stuck. A box is just a
  // stop on the way until OK is pressed on it; then the keyboard opens.
  // Only for a remote (or a keyboard's arrows): on a phone or tablet a box is only locked when the
  // arrows just moved to it, never when Stremio puts the cursor in it itself (the sign-in's e-mail
  // box), and a tap types straight away: there's no OK to press
  const typable = (t) => t && t.tagName === "INPUT" && !/^(checkbox|radio|button|submit|reset|color|file|range|hidden)$/.test(t.type);
  let lastTouch = 0;
  let lastArrow = 0;
  window.addEventListener("keydown", (e) => { if (/^Arrow/.test(e.key)) lastArrow = Date.now(); }, true);
  window.addEventListener("pointerdown", (e) => {
    lastTouch = Date.now();
    // a box that's locked (the arrows reached it) opens for typing when it's touched
    const t = e.target;
    if (typable(t) && t.dataset.dxtvLocked) {
      t.readOnly = false;
      delete t.dataset.dxtvLocked;
      t.dataset.dxtvTyping = "1";
    }
  }, true);
  window.addEventListener("focusin", (e) => {
    const t = e.target;
    if (!typable(t) || t.readOnly || t.dataset.dxtvTyping || t.closest(".dxtv-modal")) return;
    if (Date.now() - lastTouch < 1000) return;
    if (PHONE && Date.now() - lastArrow > 1000) return;
    t.readOnly = true;
    t.dataset.dxtvLocked = "1";
  }, true);
  window.addEventListener("focusout", (e) => {
    const t = e.target;
    if (!t || !t.dataset) return;
    if (t.dataset.dxtvLocked) {
      t.readOnly = false;
      delete t.dataset.dxtvLocked;
    }
    delete t.dataset.dxtvTyping;
  }, true);
  window.addEventListener("keydown", (e) => {
    const t = e.target;
    if (e.key !== "Enter" || !typable(t) || !t.dataset.dxtvLocked) return;
    e.preventDefault();
    e.stopImmediatePropagation();
    t.blur();
    t.dataset.dxtvTyping = "1";
    t.focus();
    t.click(); // what makes Android bring up its keyboard
  }, true);

  // An actor's or director's name on a title opens Search with the name filled in. Stremio then
  // puts the cursor in the search box, and the TV's keyboard covered the results: the results get
  // the remote instead (Up still reaches the box to type)
  let searchWatch = 0;
  function resultsFirst() {
    clearInterval(searchWatch);
    const since = Date.now();
    searchWatch = setInterval(() => {
      // (an arrow pressed meanwhile: the remote is going somewhere, the search box included)
      if (!/^#\/search\?search=./.test(location.hash) || Date.now() - since > 4000 || lastArrow > since) {
        clearInterval(searchWatch);
        return;
      }
      const field = document.activeElement;
      const first = document.querySelector('#app [class*="search-content"] [class*="meta-item-container"] a[href], #app [class*="search-content"] [class*="meta-item-container"] [tabindex]');
      if (field && field.tagName === "INPUT") field.blur();
      if (first) {
        first.focus({ preventScroll: true });
        clearInterval(searchWatch);
      }
    }, 100);
  }

  function onRoute() {
    ensureSettingsEntry();
    if (/^#\/player\//.test(location.hash)) {
      startHandoff().catch((err) => {
        console.warn("[Nathflix TV] handoff failed", err);
        if (handoff && handoff.pending) handoff = null;
        veil(false);
      });
    } else {
      veil(false);
      // Off the player screen with the native player still going (picture-in-picture, playing in
      // the background): the same stream picked again starts a new handoff rather than waiting on this one
      if (handoff && handoff.hash !== location.hash) handoff = null;
    }
    if (/^#\/search\?search=./.test(location.hash) && document.activeElement && document.activeElement.tagName !== "INPUT") resultsFirst();
  }

  window.addEventListener("hashchange", onRoute);
  const start = () => {
    ensureStyle();
    loadPlugin();
    resumePlayback();
    onRoute();
    new MutationObserver(ensureSettingsEntry).observe(document.body, { childList: true, subtree: true });
  };
  if (document.readyState === "complete") start();
  else window.addEventListener("load", start);
})();
