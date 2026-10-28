# CLAUDE.md

**Nathflix** : interface « à la Netflix » pour Stremio. Le projet est basé sur [dx.bi](https://github.com/notdxbi/dxbi-stremio) 2.21.0 (projet d'origine, `notdxbi/dxbi-stremio`) et vit dans le dépôt `lavagnou/dxbi-stremio-android`. Il se concentre sur l'app Android (`tv/`), mais le dépôt contient tout le projet.

**Plan et suivi des tâches : [`PLAN.md`](PLAN.md).** Le lire avant tout travail. Le tenir à jour selon ses règles (statut, notes techniques, vérification, journal), dans le même commit que le code.

## Vue d'ensemble

Un seul plugin JavaScript, `nathflix.plugin.js`, alimente trois cibles :

| Cible | Dossier | Ce que c'est |
|---|---|---|
| **Plugin** | `nathflix.plugin.js`, `nathflix.theme.css` | Plugin Stremio Enhanced injecté dans `web.stremio.com`. C'est le cœur du projet (≈13 000 lignes). |
| **Nathflix TV / Android** | `tv/` | App Kotlin : une WebView sur `web.stremio.com`, plus un lecteur natif Media3/ExoPlayer et un moteur torrent libtorrent4j. Tourne sur Android TV, Google TV, Fire TV, téléphones et tablettes. |
| **App desktop** | `app/` | Electron + TypeScript. C'est Stremio Enhanced 1.2.0 renommé, avec le plugin intégré et mpv sur Windows. |

Le reste du dépôt :
- `tools/` : `bundle-theme.mjs` (étape de release obligatoire), tests de fumée (`smoke-test.mjs`, `mpv-test.mjs`), rendu de l'intro (`render_intro.py`, `intro_audio.py`), `report-relay/` (Cloudflare Worker de « Report a problem »).
- `install.sh` / `install.ps1` : installeurs du plugin pour Stremio Enhanced.
- `.github/workflows/` : builds de release (`app.yml`, `tv.yml`), tests (`cross-platform.yml`, `mpv-windows.yml`), annonce Discord.

## Version : une seule source de vérité

- La version est le `@version` en tête de `nathflix.plugin.js`.
- `node tools/bundle-theme.mjs` fait trois choses :
  - copie `nathflix.theme.css` dans le plugin (la chaîne `/*@bundled-theme*/"…"`) ;
  - synchronise `PLUGIN_VERSION` ;
  - synchronise `app/version` et `app/package.json`.
- Numérotation propre à Nathflix, partie de **0.1.0** (dx.bi était en 2.21.0). Le `CHANGELOG.md` garde l'historique de dx.bi sous l'entrée 0.1.0.
- L'APK lit la version dans le plugin au build (`tv/app/build.gradle.kts`) : `versionCode = major*10000 + minor*100 + patch` (0.1.0 → 100).
- Pour une release :
  1. monter `@version` ;
  2. ajouter une entrée en haut de `CHANGELOG.md` ;
  3. lancer `node tools/bundle-theme.mjs` ;
  4. commiter ;
  5. publier une release GitHub avec un tag `vX.Y.Z` (les mises à jour le comparent à la version installée) et y joindre à la main `nathflix.plugin.js`, `install.sh` et `install.ps1`. Les workflows `app.yml` et `tv.yml` y ajoutent les apps desktop et `nathflix-tv.apk`.
- **Ne jamais modifier à la main** la chaîne du thème intégrée au plugin. Modifier `nathflix.theme.css`, puis relancer le script.

## Nathflix TV / Android (`tv/`)

### Stack

- AGP 8.7.3, Kotlin 2.0.21, JDK 17 (toolchain), compile/target SDK 35, minSdk 24.
- Package `com.lavagnou.nathflix`, un seul module `:app`.
- Dépendances : androidx.webkit, Media3 1.5.1 (ExoPlayer, HLS, DASH, UI, OkHttp), OkHttp 4.12, libtorrent4j 2.1.0-39, NanoHTTPD 2.3.1.
- Il n'y a pas de Compose ni de tests unitaires.

### Fichiers (`tv/app/src/main/java/com/lavagnou/nathflix/`)

- **`MainActivity.kt`** : la WebView sur `https://web.stremio.com/`, mise en page à 1280 px CSS (600 px sur un téléphone tenu droit).
  - User-Agent desktop suivi de `nathflix-tv/<version>`. C'est ce marqueur qui active `IN_TV` dans le plugin.
  - Injecte `assets/nathflix-tv.js` au démarrage du document.
  - Gère aussi : Back, liens `stremio://`, récupération après crash du renderer (`onRenderProcessGone`), WebView minimale 100.
- **`assets/nathflix-tv.js`** : tourne dans la page avant les scripts de Stremio.
  - Remplace l'API de Stremio Enhanced (`window.StremioEnhancedAPI` : réglages, alertes, prompts).
  - Charge le plugin.
  - Ajoute les entrées « Nathflix settings », « Streaming server for torrents » et « Report a problem » dans Settings.
  - Gère le bouton Back de la télécommande et les champs texte verrouillés jusqu'à OK.
  - Fait le **handoff de lecture** (`startHandoff`) : quand Stremio ouvre `#/player/…`, le flux est envoyé au lecteur natif.
- **`Bridge.kt`** : interface JS `window.NathflixTV`.
  - Chaque méthode `@JavascriptInterface` prend `token` en premier argument et le vérifie avec `allowed()`. `isPhone()` et `version()` font exception.
  - Le token est aléatoire à chaque lancement ; il remplace `__NATHFLIX_TV_TOKEN__` dans `nathflix-tv.js`. Il empêche les iframes (trailers YouTube) d'appeler le bridge.
- **`PlayerActivity.kt`** : lecteur Media3.
  - En-têtes `proxyHeaders` (le User-Agent de l'addon remplace celui de Nathflix), sous-titres des addons, style des sous-titres Stremio.
  - Boutons Skip intro/recap/crédits, ±10 s.
  - Renvoie la position toutes les 5 s.
- **`PlaybackBus.kt`** : état de lecture partagé (`activePlayback`, `pendingClose`). Il survit au redémarrage de la page ou de `MainActivity` pendant une lecture.
- **`TorrentEngine.kt`** : libtorrent en téléchargement séquentiel, servi au lecteur par NanoHTTPD sur `127.0.0.1:<port>/t/<hash>`.
  - Un seul torrent à la fois, supprimé à l'arrêt.
  - Désactivé par défaut (réglage `tvTorrents`).
- **`ServerFinder.kt`** : scanne le /24 local sur le port 11470 pour trouver un serveur de streaming Stremio.
- **`Updates.kt`** :
  - met à jour le plugin seul, entre deux releases APK, depuis la dernière release GitHub (stocké dans `filesDir`) ;
  - propose la nouvelle APK ;
  - `forTv()` retire du plugin la vidéo d'intro et les shaders Anime4K.
- **`Intro.kt`** : film d'intro natif (`res/raw/intro.mp4`, H.264), recoloré selon l'accent choisi.

### Sens des communications

- **Page → app** : `native.xxx()` dans `nathflix-tv.js`, qui appelle `bridge.xxx(TOKEN, …)`.
- **App → page** : `MainActivity.js("window.__nathflixTv && window.__nathflixTv.xxx(...)")`.
- **Plugin ↔ couche TV** : `window.__nathflixTv` (exposé par `nathflix-tv.js`) et `window.__nathflix` (exposé par le plugin : `skipSegments`, `report`, `closePreview`, `settings`…).

### Invariants à respecter

- **Le plugin évolue plus vite que l'APK.** Le plugin se met à jour seul sur TV, donc un plugin récent peut tourner dans une vieille APK. Côté plugin, toute fonction de la couche TV doit être détectée avant usage, par exemple `window.__nathflixTv && window.__nathflixTv.nativeIntro`. Dans `nathflix-tv.js`, même chose pour les nouvelles méthodes du bridge : `bridge.torrentUrl ? … : ""`.
- **Ajouter une méthode au bridge**, c'est quatre choses :
  - l'annotation `@JavascriptInterface` ;
  - la vérification du token ;
  - le wrapper dans l'objet `native` de `nathflix-tv.js` ;
  - les règles ProGuard (elles gardent déjà les méthodes annotées de `Bridge`).
- **Ne pas reformater `introVideoData()` ni `anime4kShaders()`** en bas du plugin. Ce sont d'énormes lignes uniques (base64 / JSON), et `Updates.forTv()` les retrouve par regex sur leur forme exacte. Pour la même raison, éviter de lire ces lignes en entier.
- **Garder en miroir le Kotlin et le plugin** :
  - `PAGE_READY` (`MainActivity`) ↔ `SPLASH_READY` (plugin) ;
  - `ACCENTS` (`Intro.kt`) ↔ `ACCENTS` (plugin).
- **Réglages côté natif** : ils sont dans les SharedPreferences `plugin-settings`, clé `nathflix` (un objet JSON). `Intro.kt` et `MainActivity.recoverFromCrash` y lisent ou écrivent `splash`, `introSound`, `soundVolume` et `accent`.
- **Release** : uniquement les ABI ARM (`armeabi-v7a`, `arm64-v8a`), avec minify et shrinkResources. Le debug inclut aussi x86/x86_64 pour l'émulateur.
- **Cleartext HTTP autorisé** (`network_security.xml`) : les serveurs de streaming locaux sont en http.

### Build et debug

```bash
cd tv
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # signé si tv/keystore.properties ou NATHFLIX_TV_KEYSTORE* existent
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat -s nathflix nathflix-torrent
```

- Il faut JDK 17 ou 21 et l'Android SDK 35. Ils ne sont pas installés dans l'environnement cloud de Claude (pas d'`ANDROID_HOME`) : `./gradlew` n'y marchera pas sans installer le SDK.
- Les builds debug s'inspectent depuis `chrome://inspect`.
- `preBuild` copie `../nathflix.plugin.js` dans les assets générés. Le plugin de la racine est donc toujours celui embarqué dans l'APK.

## Plugin (`nathflix.plugin.js`)

- Une seule IIFE en `"use strict"`, **sans build ni dépendance**. Il est chargé tel quel par Stremio Enhanced, l'app desktop et la TV.
- Environnement détecté par le User-Agent :
  - `IN_APP` (`nathflix/`) pour l'app desktop ;
  - `IN_TV` (`nathflix-tv/`) pour la TV ;
  - sinon, le plugin dans Stremio Enhanced.
- Organisé en sections délimitées par `// ----…` puis un titre. Pour s'y retrouver :

```bash
awk '/^  \/\/ -{20,}/{getline n; print NR+1": "n}' nathflix.plugin.js | grep -v -- -----
```

- **Ajouter un réglage** :
  - le mettre dans `DEFAULTS`, `SCHEMA` et `SETTING_GROUPS` ;
  - s'il ne concerne que la TV, ajouter `tvOnly: true` dans `SCHEMA` ;
  - s'il ne sert que sur desktop (lecteur in-page), ajouter sa clé au `Set` `DESKTOP_ONLY` de `nathflix-tv.js`. Sinon il apparaîtra dans les réglages TV.
- **Sélecteurs** : les classes de Stremio contiennent un hash de build (`meta-row-xtlB1`). Utiliser `[class*="meta-row"]`, jamais le nom complet.
- **Accès au core Stremio** : `core()` renvoie `getState` / `dispatch` et garde une copie, car `window.core` disparaît sur certaines routes.

## App desktop (`app/`)

- Electron 41, TypeScript strict (`tsconfig.json`), Node 22+.
- Organisation du code :
  - `src/main.ts` : processus principal ;
  - `src/core/` : fonctionnalités Nathflix (`BuiltIn`, `MpvShell`, `MiniPlayer`, `AutoUpdate`…) ;
  - `src/preload/` : code injecté dans la page ;
  - `src/components/` : HTML et TS des composants, le HTML est copié par `copyComponents.js`.

```bash
cd app && npm ci
npm run dist     # copie le plugin dans static/, tsc, copie les composants
npm run dev      # lance depuis les sources (--devtools)
npm run build:linux:x64   # ou build:win:x64, build:mac:arm64…
```

- `npm run lint` appelle eslint, mais eslint n'est pas dans les devDependencies.
- Sur Windows, mpv est récupéré par `scripts/fetch-mpv.sh`.

## Vérifications avant de commiter

Il n'y a pas de suite de tests unitaires. Au minimum :

```bash
node --check nathflix.plugin.js
node --check tv/app/src/main/assets/nathflix-tv.js
cd app && npx tsc --noEmit        # si app/ a changé (après npm ci)
cd tv && ./gradlew assembleDebug  # si tv/ a changé et que le SDK est disponible
```

Côté CI :
- `cross-platform.yml` (push sur main et PR) installe le plugin et lance un vrai Stremio Enhanced sur Windows, macOS et Linux.
- `mpv-windows.yml` se lance quand `app/` ou le plugin changent.
- `tv.yml` et `app.yml` ne tournent qu'à la publication d'une release (ou à la main).

## Conventions

- **Code, commentaires, CHANGELOG et messages de commit en anglais**, comme en amont.
- **Commentaires** : phrases simples qui expliquent le *pourquoi* et le comportement visible par l'utilisateur. Le plus souvent sans point final. Suivre la densité de commentaires du code qui entoure.
- **JS** : 2 espaces, guillemets doubles, pas de dépendances, syntaxe moderne (Chrome/WebView ≥ 100).
- **Kotlin** : style officiel (`kotlin.code.style=official`).
- **En-tête de licence** des nouveaux fichiers JS : `// Nathflix — Copyright (C) 2026 lavagnou. Based on dx.bi, Copyright (C) 2026 dx.bi. GNU General Public License, version 3 or later (see LICENSE).`
- **CHANGELOG** : une section `## x.y.z` par version. Chaque entrée commence par une phrase en **gras** orientée utilisateur, puis l'explication. Les entrées propres à la TV sont préfixées par « Nathflix TV: ».
- **Commits de release** : `x.y.z: résumé court (#PR)`, puis une liste de puces techniques (fichiers et fonctions touchés).
- **Licence GPL-3.0-or-later.** Garder les mentions de copyright de dx.bi : la licence l'exige. Les parties tierces et l'attribution à dx.bi sont dans `NOTICE.md`.

## Nom, liens et services

Renommage dx.bi → Nathflix et liens vers ce dépôt : T-001 et T-002 dans `PLAN.md`.

- **Noms** : affiché « Nathflix » (« NATHFLIX » pour le logo texte du thème), identifiants en minuscules `nathflix` (fichiers, clés de stockage, `window.__nathflix`, `window.__nathflixTv`, `NathflixTV`, User-Agent `nathflix/` et `nathflix-tv/`, variables `NATHFLIX_*`).
- **Identifiants d'app** : Android `com.lavagnou.nathflix` (package Kotlin `tv/app/src/main/java/com/lavagnou/nathflix/`), desktop `com.lavagnou.nathflix.desktop`. Ils ne doivent plus changer une fois l'app distribuée.
- **Préfixes CSS internes inchangés** : les classes `dx-`, `dxe-`, `dxtv-` et les variables `--dx-*` viennent de dx.bi. Elles ne sont pas visibles par l'utilisateur et sont gardées telles quelles.
- **Toutes les mises à jour viennent des releases de ce dépôt** :
  - `Updates.kt` (`REPO`) pour le plugin et l'APK sur TV ;
  - electron-updater (`build.publish` dans `app/package.json`) et `URLS` dans `app/src/constants/index.ts` pour le desktop ;
  - `@updateUrl` du plugin et du thème (branche `main`) ;
  - les installeurs `install.sh` / `install.ps1`.
- **Renommer le dépôt plus tard** : remplacer `lavagnou/dxbi-stremio-android` partout (`git grep -l "lavagnou/dxbi-stremio-android"`), ainsi que `"repo": "dxbi-stremio-android"` dans `app/package.json`. Garder les liens vers `notdxbi/dxbi-stremio` : ce sont des attributions à dx.bi.
- **Rapports de problème** : `REPORT_ENDPOINT` est vide, donc « Report a problem » est masqué (`report.enabled`, vérifié aussi par `nathflix-tv.js`). Pour le réactiver, déployer `tools/report-relay` (Worker Cloudflare et webhook Discord) et mettre son adresse `/report` dans `REPORT_ENDPOINT`.
- **Statut Discord (desktop)** : `DISCORD.CLIENT_ID` est vide dans `app/src/constants/index.ts`, donc le statut ne démarre pas et son interrupteur est masqué. Pour l'activer, y mettre l'Application ID d'une application Discord « Nathflix ».
- **Pas encore renommés (images)** : l'intro (`res/raw/intro.mp4`, `introVideoData()` du plugin), les icônes (`app/images/`, `tv/app/src/main/res/mipmap-*`, `drawable/banner.png`) et les captures d'écran montrent encore le logo DX.BI. À refaire avec le nouveau logo ; `tools/render_intro.py` a déjà `TEXT = "NATHFLIX"`.
- **Signature Android** : release signée par `tv/keystore.properties` (hors git) ou par les secrets `NATHFLIX_TV_KEYSTORE_B64` et `NATHFLIX_TV_KEYSTORE_PASSWORD` (alias `nathflix-tv`). Sans clé, `tv.yml` ne construit rien.
