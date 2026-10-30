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
```

Change `MARK` and `ACCENT` at the top of `render_intro.py` to use another mark and colour (the cuts
between its letters are in the "logo masks" part).
Preview a few frames quickly with `--w 960 --h 540 --frames 60,120,180`.

## Profile sync

`profile-sync/` is the Cloudflare Worker (with a D1 database) that keeps each person's Nathflix profiles
in step across their devices, filed under their Stremio account. Its README has the set-up steps;
`node tools/profile-sync/test.mjs` checks it without Cloudflare.
