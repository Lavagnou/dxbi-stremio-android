#!/usr/bin/env python3
"""Make every Nathflix logo image from the two drawings in branding/.

  branding/nathflix-wordmark.png   the NATHFLIX wordmark (red, transparent background)
  branding/nathflix-mark.png       the NATH mark, for app icons and the intro

usage: python3 tools/make_brand.py

Writes the desktop icons (app/images), the Android icons and TV banner (tv/app/src/main/res), the
README banner (screenshots/banner.jpg) and the small images the plugin embeds (branding/web, put into
nathflix.plugin.js with tools/embed_brand.mjs). App icons are made like Netflix's: the mark on black,
with room around it.
"""
import os

from PIL import Image, ImageDraw, ImageFilter

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BRAND = os.path.join(ROOT, "branding")
WORDMARK = Image.open(os.path.join(BRAND, "nathflix-wordmark.png")).convert("RGBA")
MARK = Image.open(os.path.join(BRAND, "nathflix-mark.png")).convert("RGBA")
BLACK = (0, 0, 0, 255)


def path(*parts):
    p = os.path.join(ROOT, *parts)
    os.makedirs(os.path.dirname(p), exist_ok=True)
    return p


def fit(img, w, h):
    """img scaled to fit in w × h, keeping its shape"""
    s = min(w / img.width, h / img.height)
    return img.resize((max(1, round(img.width * s)), max(1, round(img.height * s))), Image.LANCZOS)


def centred(canvas, img, dy=0):
    canvas.alpha_composite(img, ((canvas.width - img.width) // 2, (canvas.height - img.height) // 2 + dy))
    return canvas


def app_icon(size, margin=0.04, mark=0.64):
    """The mark on a black rounded square (drawn 4× larger, then scaled down for smooth corners)"""
    big = size * 4
    m = round(big * margin)
    shape = Image.new("L", (big, big), 0)
    ImageDraw.Draw(shape).rounded_rectangle((m, m, big - m - 1, big - m - 1), radius=round((big - 2 * m) * 0.22), fill=255)
    shape = shape.resize((size, size), Image.LANCZOS)
    icon = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    icon.paste(Image.new("RGBA", (size, size), BLACK), (0, 0), shape)
    side = round(size * (1 - 2 * margin) * mark)
    return centred(icon, fit(MARK, side, side))


def banner(w, h, logo_w, glow=False):
    """The wordmark on black (glow: Who's watching's red light from the top middle)"""
    img = Image.new("RGBA", (w, h), BLACK)
    if glow:
        light = Image.new("L", (w, h), 0)
        r = round(h * 1.3)
        ImageDraw.Draw(light).ellipse((w // 2 - r, -r, w // 2 + r, r), fill=110)
        light = light.filter(ImageFilter.GaussianBlur(h * 0.35))
        img.paste(Image.new("RGBA", (w, h), (110, 21, 29, 255)), (0, 0), light)
    return centred(img, fit(WORDMARK, logo_w, h * 0.8))


# Desktop app (electron-builder: icon.png; the window: icon.ico on Windows, icon.png elsewhere)
icon = app_icon(1024)
icon.save(path("app", "images", "icon.png"), optimize=True)
app_icon(512).save(path("app", "images", "icon-512.png"), optimize=True)
app_icon(256).save(path("app", "images", "icon-256.png"), optimize=True)
icon.save(path("app", "images", "icon.ico"), sizes=[(s, s) for s in (16, 24, 32, 48, 64, 128, 256)])
icon.save(path("app", "images", "icon.icns"))
app_icon(512).save(path("app", "images", "presence", "logo.png"), optimize=True)

# Android: the launcher icon (adaptive from Android 8: the mark inside the circle every launcher
# keeps, on a black layer; before that, the rounded square), and Android TV's banner
RES = ("tv", "app", "src", "main", "res")
for folder, px in (("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)):
    app_icon(px).save(path(*RES, f"mipmap-{folder}", "ic_launcher.png"), optimize=True)
    fg_size = round(px * 108 / 48)
    fg = Image.new("RGBA", (fg_size, fg_size), (0, 0, 0, 0))
    # (54 of 108 dp wide: the whole mark stays inside the 66 dp circle no launcher crops)
    side = round(fg_size * 54 / 108)
    centred(fg, fit(MARK, side, side)).save(path(*RES, f"mipmap-{folder}", "ic_launcher_foreground.png"), optimize=True)
with open(path(*RES, "mipmap-anydpi-v26", "ic_launcher.xml"), "w", newline="\n") as f:
    f.write('<?xml version="1.0" encoding="utf-8"?>\n'
            '<!-- Made by tools/make_brand.py: the NATH mark on black, like Netflix\'s icon -->\n'
            '<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">\n'
            '    <background android:drawable="@color/ic_launcher_background" />\n'
            '    <foreground android:drawable="@mipmap/ic_launcher_foreground" />\n'
            '</adaptive-icon>\n')
banner(320, 180, 250).convert("RGB").save(path(*RES, "drawable", "banner.png"), optimize=True)
banner(640, 360, 500).convert("RGB").save(path(*RES, "drawable-xhdpi", "banner.png"), optimize=True)

# The README's banner
banner(1600, 416, 900, glow=True).convert("RGB").save(path("screenshots", "banner.jpg"), quality=92)

# For the plugin: the wordmark as a mask (Nathflix colours it with the accent) and the mark in colour
mask = fit(WORDMARK, 2000, 120)
white = Image.new("RGBA", mask.size, (255, 255, 255, 0))
white.putalpha(mask.getchannel("A"))
white.save(path("branding", "web", "wordmark-mask.png"), optimize=True)
# (240 px high: the splash shows it large, on a TV too)
fit(MARK, 2000, 240).save(path("branding", "web", "mark.webp"), quality=88, method=6)

print("brand images written")
