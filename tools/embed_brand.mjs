// Nathflix — Copyright (C) 2026 lavagnou. Based on dx.bi, Copyright (C) 2026 dx.bi. GNU General Public License, version 3 or later (see LICENSE).
// Puts the logo images made by tools/make_brand.py (branding/web) into brandImages() in
// nathflix.plugin.js, as data: addresses. Run it after make_brand.py: node tools/embed_brand.mjs
import { readFileSync, writeFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const root = join(dirname(fileURLToPath(import.meta.url)), "..");
const plugin = join(root, "nathflix.plugin.js");
const data = (name) => `data:image/${name.split(".").pop()};base64,` + readFileSync(join(root, "branding", "web", name)).toString("base64");
const images = { wordmark: data("wordmark-mask.png"), mark: data("mark.webp") };

const src = readFileSync(plugin, "utf8");
const shape = /(\n  function brandImages\(\) \{\n    return )\{[^\n]*\};/;
if (!shape.test(src)) throw new Error("brandImages() not found in nathflix.plugin.js");
writeFileSync(plugin, src.replace(shape, (_, head) => `${head}${JSON.stringify(images)};`));
console.log(`brandImages(): wordmark ${images.wordmark.length} chars, mark ${images.mark.length} chars`);
