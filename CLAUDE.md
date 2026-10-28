# CLAUDE.md

Fork de [notdxbi/dxbi-stremio](https://github.com/notdxbi/dxbi-stremio) (dx.bi : interface « à la Netflix » pour Stremio). Ce fork se concentre sur l'app Android (`tv/`), mais le dépôt contient tout le projet.

**Plan et suivi des tâches : [`PLAN.md`](PLAN.md).** Le lire avant tout travail. Le tenir à jour selon ses règles (statut, notes techniques, vérification, journal), dans le même commit que le code.

## Vue d'ensemble

Un seul plugin JavaScript, `dxbi.plugin.js`, alimente trois cibles :

| Cible | Dossier | Ce que c'est |
|---|---|---|
| **Plugin** | `dxbi.plugin.js`, `dxbi.theme.css` | Plugin Stremio Enhanced injecté dans `web.stremio.com`. C'est le cœur du projet (≈13 000 lignes). |
| **dx.bi TV / Android** | `tv/` | App Kotlin : une WebView sur `web.stremio.com`, plus un lecteur natif Media3/ExoPlayer et un moteur torrent libtorrent4j. Tourne sur Android TV, Google TV, Fire TV, téléphones et tablettes. |
| **App desktop** | `app/` | Electron + TypeScript. C'est Stremio Enhanced 1.2.0 renommé, avec le plugin intégré et mpv sur Windows. |

Le reste du dépôt :
- `tools/` : `bundle-theme.mjs` (étape de release obligatoire), tests de fumée (`smoke-test.mjs`, `mpv-test.mjs`), rendu de l'intro (`render_intro.py`, `intro_audio.py`), `report-relay/` (Cloudflare Worker de « Report a problem »).
- `install.sh` / `install.ps1` : installeurs du plugin pour Stremio Enhanced.
- `.github/workflows/` : builds de release (`app.yml`, `tv.yml`), tests (`cross-platform.yml`, `mpv-windows.yml`), annonce Discord.

## Version : une seule source de vérité

- La version est le `@version` en tête de `dxbi.plugin.js`.
- `node tools/bundle-theme.mjs` fait trois choses :
  - copie `dxbi.theme.css` dans le plugin (la chaîne `/*@bundled-theme*/"…"`) ;
  - synchronise `PLUGIN_VERSION` ;
  - synchronise `app/version` et `app/package.json`.
- L'APK lit la version dans le plugin au build (`tv/app/build.gradle.kts`) : `versionCode = major*10000 + minor*100 + patch`.
- Pour une release :
  1. monter `@version` ;
  2. ajouter une entrée en haut de `CHANGELOG.md` ;
  3. lancer `node tools/bundle-theme.mjs` ;
  4. commiter.
- **Ne jamais modifier à la main** la chaîne du thème intégrée au plugin. Modifier `dxbi.theme.css`, puis relancer le script.

## dx.bi TV / Android (`tv/`)

### Stack

- AGP 8.7.3, Kotlin 2.0.21, JDK 17 (toolchain), compile/target SDK 35, minSdk 24.
- Package `com.notdxbi.dxbi.tv`, un seul module `:app`.
- Dépendances : androidx.webkit, Media3 1.5.1 (ExoPlayer, HLS, DASH, UI, OkHttp), OkHttp 4.12, libtorrent4j 2.1.0-39, NanoHTTPD 2.3.1.
- Il n'y a pas de Compose ni de tests unitaires.

### Fichiers (`tv/app/src/main/java/com/notdxbi/dxbi/tv/`)

- **`MainActivity.kt`** : la WebView sur `https://web.stremio.com/`, mise en page à 1280 px CSS (600 px sur un téléphone tenu droit).
  - User-Agent desktop suivi de `dxbi-tv/<version>`. C'est ce marqueur qui active `IN_TV` dans le plugin.
  - Injecte `assets/dxbi-tv.js` au démarrage du document.
  - Gère aussi : Back, liens `stremio://`, récupération après crash du renderer (`onRenderProcessGone`), WebView minimale 100.
- **`assets/dxbi-tv.js`** : tourne dans la page avant les scripts de Stremio.
  - Remplace l'API de Stremio Enhanced (`window.StremioEnhancedAPI` : réglages, alertes, prompts).
  - Charge le plugin.
  - Ajoute les entrées « dx.bi settings », « Streaming server for torrents » et « Report a problem » dans Settings.
  - Gère le bouton Back de la télécommande et les champs texte verrouillés jusqu'à OK.
  - Fait le **handoff de lecture** (`startHandoff`) : quand Stremio ouvre `#/player/…`, le flux est envoyé au lecteur natif.
- **`Bridge.kt`** : interface JS `window.DxbiTV`.
  - Chaque méthode `@JavascriptInterface` prend `token` en premier argument et le vérifie avec `allowed()`. `isPhone()` et `version()` font exception.
  - Le token est aléatoire à chaque lancement ; il remplace `__DXBI_TV_TOKEN__` dans `dxbi-tv.js`. Il empêche les iframes (trailers YouTube) d'appeler le bridge.
- **`PlayerActivity.kt`** : lecteur Media3.
  - En-têtes `proxyHeaders` (le User-Agent de l'addon remplace celui de dx.bi), sous-titres des addons, style des sous-titres Stremio.
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

- **Page → app** : `native.xxx()` dans `dxbi-tv.js`, qui appelle `bridge.xxx(TOKEN, …)`.
- **App → page** : `MainActivity.js("window.__dxbiTv && window.__dxbiTv.xxx(...)")`.
- **Plugin ↔ couche TV** : `window.__dxbiTv` (exposé par `dxbi-tv.js`) et `window.__dxbi` (exposé par le plugin : `skipSegments`, `report`, `closePreview`, `settings`…).

### Invariants à respecter

- **Le plugin évolue plus vite que l'APK.** Le plugin se met à jour seul sur TV, donc un plugin récent peut tourner dans une vieille APK. Côté plugin, toute fonction de la couche TV doit être détectée avant usage, par exemple `window.__dxbiTv && window.__dxbiTv.nativeIntro`. Dans `dxbi-tv.js`, même chose pour les nouvelles méthodes du bridge : `bridge.torrentUrl ? … : ""`.
- **Ajouter une méthode au bridge**, c'est quatre choses :
  - l'annotation `@JavascriptInterface` ;
  - la vérification du token ;
  - le wrapper dans l'objet `native` de `dxbi-tv.js` ;
  - les règles ProGuard (elles gardent déjà les méthodes annotées de `Bridge`).
- **Ne pas reformater `introVideoData()` ni `anime4kShaders()`** en bas du plugin. Ce sont d'énormes lignes uniques (base64 / JSON), et `Updates.forTv()` les retrouve par regex sur leur forme exacte. Pour la même raison, éviter de lire ces lignes en entier.
- **Garder en miroir le Kotlin et le plugin** :
  - `PAGE_READY` (`MainActivity`) ↔ `SPLASH_READY` (plugin) ;
  - `ACCENTS` (`Intro.kt`) ↔ `ACCENTS` (plugin).
- **Réglages côté natif** : ils sont dans les SharedPreferences `plugin-settings`, clé `dxbi` (un objet JSON). `Intro.kt` et `MainActivity.recoverFromCrash` y lisent ou écrivent `splash`, `introSound`, `soundVolume` et `accent`.
- **Release** : uniquement les ABI ARM (`armeabi-v7a`, `arm64-v8a`), avec minify et shrinkResources. Le debug inclut aussi x86/x86_64 pour l'émulateur.
- **Cleartext HTTP autorisé** (`network_security.xml`) : les serveurs de streaming locaux sont en http.

### Build et debug

```bash
cd tv
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease      # signé si tv/keystore.properties ou DXBI_TV_KEYSTORE* existent
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat -s dxbi dxbi-torrent
```

- Il faut JDK 17 ou 21 et l'Android SDK 35. Ils ne sont pas installés dans l'environnement cloud de Claude (pas d'`ANDROID_HOME`) : `./gradlew` n'y marchera pas sans installer le SDK.
- Les builds debug s'inspectent depuis `chrome://inspect`.
- `preBuild` copie `../dxbi.plugin.js` dans les assets générés. Le plugin de la racine est donc toujours celui embarqué dans l'APK.

## Plugin (`dxbi.plugin.js`)

- Une seule IIFE en `"use strict"`, **sans build ni dépendance**. Il est chargé tel quel par Stremio Enhanced, l'app desktop et la TV.
- Environnement détecté par le User-Agent :
  - `IN_APP` (`dxbi/`) pour l'app desktop ;
  - `IN_TV` (`dxbi-tv/`) pour la TV ;
  - sinon, le plugin dans Stremio Enhanced.
- Organisé en sections délimitées par `// ----…` puis un titre. Pour s'y retrouver :

```bash
awk '/^  \/\/ -{20,}/{getline n; print NR+1": "n}' dxbi.plugin.js | grep -v -- -----
```

- **Ajouter un réglage** :
  - le mettre dans `DEFAULTS`, `SCHEMA` et `SETTING_GROUPS` ;
  - s'il ne concerne que la TV, ajouter `tvOnly: true` dans `SCHEMA` ;
  - s'il ne sert que sur desktop (lecteur in-page), ajouter sa clé au `Set` `DESKTOP_ONLY` de `dxbi-tv.js`. Sinon il apparaîtra dans les réglages TV.
- **Sélecteurs** : les classes de Stremio contiennent un hash de build (`meta-row-xtlB1`). Utiliser `[class*="meta-row"]`, jamais le nom complet.
- **Accès au core Stremio** : `core()` renvoie `getState` / `dispatch` et garde une copie, car `window.core` disparaît sur certaines routes.

## App desktop (`app/`)

- Electron 41, TypeScript strict (`tsconfig.json`), Node 22+.
- Organisation du code :
  - `src/main.ts` : processus principal ;
  - `src/core/` : fonctionnalités dx.bi (`BuiltIn`, `MpvShell`, `MiniPlayer`, `AutoUpdate`…) ;
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
node --check dxbi.plugin.js
node --check tv/app/src/main/assets/dxbi-tv.js
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
- **En-tête de licence** des nouveaux fichiers JS : `// dx.bi — Copyright (C) 2026 dx.bi. GNU General Public License, version 3 or later (see LICENSE).`
- **CHANGELOG** : une section `## x.y.z` par version. Chaque entrée commence par une phrase en **gras** orientée utilisateur, puis l'explication. Les entrées propres à la TV sont préfixées par « dx.bi TV: ».
- **Commits de release** : `x.y.z: résumé court (#PR)`, puis une liste de puces techniques (fichiers et fonctions touchés).
- **Licence GPL-3.0-or-later** (MIT jusqu'à 2.15.9). Les parties tierces sont listées dans `NOTICE.md`.

## Points d'attention propres au fork

Le code pointe encore vers l'amont `notdxbi/dxbi-stremio`. Avant de distribuer un build du fork, décider quoi faire de chacun de ces points :

- **Mise à jour du plugin** : `tv/.../Updates.kt` (`PLUGIN_URL`) télécharge le plugin de la dernière release **amont**. Ce plugin remplace celui du fork dès que sa version est plus haute.
- **Mise à jour de l'APK** : `Updates.kt` (`RELEASES_URL`) propose les APK amont. Elles sont signées avec une autre clé, donc Android refusera de les installer par-dessus un build du fork.
- **Plugin** : `@updateUrl` et les liens vers l'amont.
- **App desktop** : `build.publish` dans `app/package.json` (cible de l'auto-update) et `app/src/constants/index.ts`.
- **Installeurs** : `install.sh` et `install.ps1` téléchargent depuis l'amont.
- **Rapports de problème** : `REPORT_ENDPOINT` dans le plugin envoie les rapports au Worker du développeur amont.
- **Nom et logo** : `NOTICE.md` demande que les forks distribués prennent un autre nom et un autre logo. `applicationId` vaut `com.notdxbi.dxbi.tv`.
- **Signature** : release signée par `tv/keystore.properties` (hors git) ou par les secrets `DXBI_TV_KEYSTORE_B64` et `DXBI_TV_KEYSTORE_PASSWORD`. Sans clé, `tv.yml` ne construit rien.
