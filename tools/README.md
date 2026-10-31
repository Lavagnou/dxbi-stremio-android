# Tools

## Built-in theme

The plugin carries a copy of `nathflix.theme.css` (so the plugin alone gives the whole look). After
changing the theme, copy it in before releasing:

```bash
node tools/bundle-theme.mjs
```

## Logo

The two drawings are in `branding/`: `nathflix-wordmark.png` (NATHFLIX) and `nathflix-mark.png` (NATH).
Every other logo image is made from them:

```bash
python3 tools/make_brand.py    # app icons (desktop, Android, adaptive icon), TV banner, README banner, branding/web
node tools/embed_brand.mjs     # puts branding/web's images into brandImages() in nathflix.plugin.js
```

App icons are made like Netflix's: the NATH mark on black, with room around it. In the plugin, the
wordmark is a mask painted in the accent colour, and the mark takes the accent's hue like the intro.

## Intro

The intro is rendered from code, so you can change it and re-render. It animates the NATH mark
(`branding/nathflix-mark.png`), cut into its four letters.

| File | What it does |
|---|---|
| `render_intro.py` | Renders the picture (1920×1080, 60 fps) as a PNG sequence: the logo landing letter by letter, the push-in, and the 3D tunnel of light ribbons with motion blur, bloom and a saturation-preserving tone map. |
| `intro_audio.py` | Renders the matching stereo score (letter ticks, "ta-dum", chord, dive whoosh, room reverb), synced to the same timeline. |

Requirements: Python 3 with `numpy` and `Pillow`, and `ffmpeg` with `libsvtav1` and `libopus`.

```bash
cd tools
python3 render_intro.py frames            # ~2–3 min on 16 cores
python3 intro_audio.py score.wav
ffmpeg -framerate 60 -i frames/f%04d.png -i score.wav \
  -c:v libsvtav1 -preset 4 -crf 34 -pix_fmt yuv420p10le -g 300 \
  -c:a libopus -b:a 96k -ac 2 -shortest intro.webm

base64 -w0 intro.webm   # paste into introVideoData() at the bottom of nathflix.plugin.js

# Nathflix TV plays an H.264 copy natively (every TV decodes it in hardware)
ffmpeg -i intro.webm -vf format=yuv420p -c:v libx264 -profile:v high -level 4.2 -preset veryslow \
  -crf 20 -x264-params keyint=300 -c:a aac -b:a 128k -movflags +faststart \
  ../tv/app/src/main/res/raw/intro.mp4

# …and an upright one for phones held upright (same score; the mark fits 56 % of the width)
python3 render_intro.py frames-portrait --w 1080 --h 1920
ffmpeg -framerate 60 -i frames-portrait/f%04d.png -i score.wav -vf format=yuv420p -c:v libx264 \n  -profile:v high -level 4.2 -preset veryslow -crf 20 -x264-params keyint=300 -c:a aac -b:a 128k \n  -movflags +faststart -shortest ../tv/app/src/main/res/raw/intro_portrait.mp4
```

Change `MARK` and `ACCENT` at the top of `render_intro.py` to use another mark and colour (the cuts
between its letters are in the "logo masks" part).
Preview a few frames quickly with `--w 960 --h 540 --frames 60,120,180`.

## Profile sync

`profile-sync/` is the Cloudflare Worker (with a D1 database) that keeps each person's Nathflix profiles
in step across their devices, filed under their Stremio account. Its README has the set-up steps;
`node tools/profile-sync/test.mjs` checks it without Cloudflare.

## Performance bench

`perf-bench.mjs` loads web.stremio.com in Chromium with a slowed-down processor (×6 by default), runs
Nathflix in it the way the apps do (the plugin served from a made-up web.stremio.com address, inside the
same `StremioEnhancedAPI` wrapper as the desktop app, added again after every reload), and prints a JSON
of what it measured: the median of the runs. Run it before and after a change, on the same machine.

Playwright isn't a dependency of the repository; npx brings it. The bench uses the installed Chrome when
Playwright's own Chromium isn't downloaded (`npx --yes playwright@1 install chromium`).

```bash
npx --yes -p playwright@1 node tools/perf-bench.mjs --tv --runs 5 --out tv.json   # or --pc, --phone
```

Each run (after one load that warms the caches, so a warm start like a TV's):

- `pageReadyMs`: until Stremio's first title is on screen; `pluginLoadMs`: the plugin's load and first run;
- 15 s to settle (`--settle`), then 30 s at rest on Home (`--idle`): `sync` (the main loop: calls and ms),
  `getState` (core reads by model), `saveLocal` (writes of Nathflix's data), `dispatch` (actions sent to the
  core), `filterMs` (time in the per-viewer library filter: two profiles or more), long tasks, number of
  elements;
- 20 arrow keys 400 ms apart: `keys` (median, p90 and worst answer, from Event Timing; under 16 ms counts as 0);
- the control walk (Discover, Library, Calendar, Settings, Home, a title's page, Home): `session` holds the
  same counters for the whole visit. Any error from Nathflix makes the bench fail;
- with `--dwell "#/route,#/other"` (a title's page, the calendar…), a rest on each after the walk, as long as the
  rest on Home: `dwell` holds their counters, by route. In Git Bash, set `MSYS_NO_PATHCONV=1` first: it
  otherwise turns `#/detail/…` into a Windows path;
- with two profiles or more (in `nathflix.data`, see `--storage`): a switch to another profile and back,
  3 s each, as at "Who's watching?": `switching` holds its counters.

No Stremio account: guest mode is allowed with `nathflix.allowGuest`, like the CI. `--settings '{"lightMode":"on"}'`
sets Nathflix's settings, `--storage '{"key":"value"}'` (or `--storage @file.json`) puts entries in localStorage
first, at every load. Never set `nathflix.lang` there: a language change reloads the page. Two profiles:
`{"nathflix.data": "{\"viewers\":{\"t\":1,\"v\":{\"list\":[{\"id\":\"main\",\"name\":\"A\"},{\"id\":\"v2\",\"name\":\"B\"}],\"current\":\"v2\"}}}"}`,
with `--settings '{"whoIsWatching":false}'` so that "Who's watching?" doesn't cover Home.

`--loss-test` checks that a change survives the page going away: a profile is added, then chosen, and the
page goes 200 ms later, three ways: closed normally (`unload`), sent to the background first (`hidden`,
what Android does before it may close an app) and a crashed renderer (`abrupt`). `loss` says, for each,
whether the next start still has the chosen profile.

Screenshots: `--shots dir` takes Home, Discover, a title's page and Settings, with pictures hidden and the
billboard held still; `--compare refdir` compares them with an earlier set pixel by pixel and gives the
number of pixels that differ and where. Rating badges that arrive late and text smoothing on a computer
make a few hundred pixels differ between two runs of the same build. `--no-measure` takes only the
screenshots.

On guest Home nothing changes at rest, so the rest counters are usually 0: the `session` counters and the
keys are the ones that move. Times vary a lot from one run to the next (the plugin's load from 0.4 to 1.2 s):
to compare two builds, run both one after the other, the old one with `--plugin` (for example
`git show HEAD:nathflix.plugin.js > old.js`). A run during which the page reloads is taken again (twice at
most; `retries` in the report).

The counters are in the plugin (`perf`, `window.__nathflix.perf()`), and the performance readout setting
shows them on the device as reads, main-loop runs and saves a minute.
