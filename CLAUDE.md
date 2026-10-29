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
  5. lancer `release.yml` : soit pousser le tag (`git tag vX.Y.Z && git push origin vX.Y.Z`), soit le lancer à la main avec la version (Actions → Release → Run workflow, ou l'API `run_workflow` avec `inputs.version`). Lancé à la main, il pose lui-même le tag sur le commit où il tourne. En session cloud, pousser un tag est refusé (403) : passer par le lancement manuel (outil GitHub `actions_run_trigger`). Le workflow fait le reste :
     - il vérifie que le tag correspond à `@version` et à une section `## X.Y.Z` du CHANGELOG ;
     - il publie la release avec ces notes et une liste de téléchargements ;
     - il y joint `nathflix.plugin.js`, `install.sh` et `install.ps1` ;
     - il appelle `tv.yml` (APK signée) et `app.yml` (apps desktop).

     Une release créée par le jeton du workflow ne déclenche pas l'événement `release` : d'où les appels `workflow_call`.
- **Ne jamais modifier à la main** la chaîne du thème intégrée au plugin. Modifier `nathflix.theme.css`, puis relancer le script.

## Nathflix TV / Android (`tv/`)

### Stack

- AGP 8.7.3, Kotlin 2.0.21, JDK 17 (toolchain), compile/target SDK 35, minSdk 24.
- Package `com.lavagnou.nathflix`, un seul module `:app`.
- Dépendances : androidx.webkit, Media3 1.5.1 (ExoPlayer, HLS, DASH, UI, OkHttp, Session), OkHttp 4.12, libtorrent4j 2.1.0-39, NanoHTTPD 2.3.1.
- **Décodeur audio FFmpeg** : `org.jellyfin.media3:media3-ffmpeg-decoder:1.5.0+1`, la build par Jellyfin de l'extension FFmpeg de Media3.
  - Il décode en logiciel AC3, E-AC3, DTS, TrueHD, FLAC, Opus… quand le matériel ne sait pas, ce qui est le cas de la plupart des téléphones pour Dolby et DTS. Sans lui, ces épisodes se jouaient sans son.
  - Le matériel passe toujours en premier (`EXTENSION_RENDERER_MODE_ON`).
  - Sa version doit suivre celle de Media3 (même mineure).
- Il n'y a pas de Compose ni de tests unitaires.

### Fichiers (`tv/app/src/main/java/com/lavagnou/nathflix/`)

- **`Device.kt`** : TV, téléphone ou tablette.
  - `isMobile()` : ni mode télévision ni leanback.
  - `kind()` renvoie `tv`, `phone` ou `tablet` (tablette si `smallestScreenWidthDp >= 600`).
  - `noun()` donne le nom de l'appareil dans les messages.
  - `thisOne()` et `theOne()` le donnent traduit (« ce téléphone », « la tablette »…).
- **`Lang.kt`** : langue des écrans de l'app (lecteur, messages, mises à jour), celle de l'interface de Stremio.
  - La page la transmet (`Bridge.setLanguage`), gardée dans les SharedPreferences `nathflix-lang`. Avant cela, c'est la langue de l'appareil qui décide.
  - `Lang.text(ctx, R.string.x, …)` donne un texte ; `Lang.context(ctx, theme)` un contexte pour gonfler des vues.
  - **Tout texte visible de l'app** va dans `res/values/strings.xml`, avec sa traduction dans `res/values-fr/strings.xml`.
- **`MainActivity.kt`** : la WebView sur `https://web.stremio.com/`.
  - User-Agent desktop suivi de `nathflix-tv/<version>` sur TV ou `nathflix-mobile/<version>` sur téléphone et tablette. Ces marqueurs activent `IN_TV` ou `IN_MOBILE` dans le plugin.
  - **Téléphone et tablette** :
    - barres système visibles ;
    - la racine reçoit en padding les insets `systemBars`, `displayCutout` et `ime` (page entre les barres, au-dessus du clavier) ;
    - `adjustResize` ;
    - pas de `FLAG_KEEP_SCREEN_ON`.
  - **TV** : plein écran, écran toujours allumé.
  - Injecte `assets/nathflix-tv.js` au démarrage du document.
  - Gère aussi : Back, liens `stremio://`, récupération après crash du renderer (`onRenderProcessGone`), WebView minimale 100.
- **`assets/nathflix-tv.js`** : tourne dans la page avant les scripts de Stremio.
  - Remplace l'API de Stremio Enhanced (`window.StremioEnhancedAPI` : réglages, alertes, prompts).
  - Charge le plugin.
  - Ajoute les entrées « Nathflix settings », « Streaming server for torrents » et « Report a problem » dans Settings.
  - Le viewport fait 1280 px CSS sur TV, la largeur de l'écran (`device-width`) sur téléphone et tablette.
  - `KIND` et `NOUN` adaptent les libellés à l'appareil (« this phone »…).
  - **Langue** : `LANG` est lu comme le fait le plugin (`nathflix.lang`, sinon le profil Stremio). `t()` cherche dans son petit dictionnaire `FR`. La langue est transmise à l'app (`native.setLanguage`).
  - **Voile du relais de lecture** (`veil()`) : un `#dxtv-veil` noir couvre la page dès le toucher d'un flux (`a[href^="#/player/"]`) et pendant `startHandoff`, pour que le lecteur de Stremio ne se voie jamais. `onRoute` le retire hors de `#/player/`.
  - La classe `dxtv-touch` adapte les fenêtres au doigt.
  - Gère le bouton Back de la télécommande.
  - Champs texte verrouillés jusqu'à OK :
    - sur TV, sauf juste après un toucher ;
    - sur téléphone et tablette, seulement si une flèche vient d'y mener. Le focus automatique de Stremio ne verrouille jamais (sinon le clavier ne s'ouvre pas à la connexion).
    - Toucher un champ verrouillé le libère.
  - Fait le **handoff de lecture** (`startHandoff`) : quand Stremio ouvre `#/player/…`, le flux est envoyé au lecteur natif.
- **`nathflix-tv.js` → lecteur (téléphone et tablette)** :
  - `mobilePlayback()` ajoute au JSON de `native.play` :
    - `episodes` : la saison, chaque épisode avec sa `route` et sa `thumbnail` ; `season` ;
    - `next` : `label` et `route` (le lecteur du prochain épisode si Stremio connaît son flux, `nextVideo.deepLinks.player`, sinon sa liste de flux) ;
    - `mobile` : `{ pip, background, autoNext }`, tirés des réglages.
  - `closeHandoff(…, playRoute)` remplace la page du lecteur par cette route (`replaceRoute`), sinon fait `history.back()`.
- **`Bridge.kt`** : interface JS `window.NathflixTV`.
  - Chaque méthode `@JavascriptInterface` prend `token` en premier argument et le vérifie avec `allowed()`. `isPhone()` et `version()` font exception.
  - Le token est aléatoire à chaque lancement ; il remplace `__NATHFLIX_TV_TOKEN__` dans `nathflix-tv.js`. Il empêche les iframes (trailers YouTube) d'appeler le bridge.
- **`PlayerActivity.kt`** : lecteur Media3.
  - En-têtes `proxyHeaders` (le User-Agent de l'addon remplace celui de Nathflix), sous-titres des addons, style des sous-titres Stremio.
  - Boutons Skip intro/recap/crédits, ±10 s.
  - Renvoie la position toutes les 5 s.
  - **TV** : contrôles Media3 à la télécommande ; Home met en pause (`onPause`).
  - **Téléphone et tablette** (`setUpMobile`) :
    - `useController = false`, avec à la place `MobilePlayerControls` ;
    - picture-in-picture (`pipParams`, `onUserLeaveHint`, entrée automatique dès Android 12) ; fermer la petite fenêtre ferme le lecteur ;
    - `MediaSession`, confiée à `PlaybackService` pour la lecture en arrière-plan ;
    - pause dans `onStop` seulement si l'arrière-plan est coupé ;
    - « Next episode » prend la place du bouton Skip pendant le générique ou les 30 dernières secondes ;
    - épisode suivant automatique à la fin.
  - **Routes rendues à la page** : `finishWith(playRoute = …)` renvoie à la page la route où aller (épisode suivant ou épisode choisi), via `PlaybackBus.closed`.
  - **Un seul lecteur à la fois** : un nouveau film ferme celui qui reste en picture-in-picture (`current`, `closeQuietly`).
- **`MobilePlayerControls.kt`** (+ `res/layout/player_mobile_controls.xml`, icônes `res/drawable/ic_*.xml`, `mc_*.xml`) : contrôles, clone de ceux de Netflix, gonflés dans la langue de Nathflix (`Lang.context`).
  - Haut : retour, titre sur une ligne, PiP.
  - Centre : −10 s, lecture, +10 s.
  - Bas : barre de progression (`DefaultTimeBar`) avec le temps restant, puis une rangée centrée de petits boutons (icône de 20 dp et libellé de 12 sp) : Vitesse (1x) / Verrouiller / Épisodes / Audio et sous-titres / Épisode suivant.
  - Gestes : tap, double tap ±10 s, glisser à gauche pour la luminosité et à droite pour le volume (pas près des bords haut et bas, réservés aux gestes d'Android).
  - Panneaux faits en code dans `mc_panel_body` :
    - Vitesse : cinq points sur une ligne ;
    - Audio et sous-titres : deux colonnes ; le choix (`pickedAudio`, `pickedText`) n'est appliqué qu'à « Appliquer » (`TrackSelectionOverride`) ;
    - Épisodes : cartes avec image (`Thumbs.kt` : OkHttp, cache mémoire) et résumé.
- **`PlaybackService.kt`** : `MediaSessionService` (media3-session) qui garde le film en arrière-plan, avec la notification. La session appartient à `PlayerActivity`, qui la passe via `PlaybackService.session`.
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
- **Plugin ↔ couche TV** : `window.__nathflixTv` (exposé par `nathflix-tv.js`) et `window.__nathflix` (exposé par le plugin : `skipSegments`, `report`, `closePreview`, `settings`, `lang`, `t`…).

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

- Il faut JDK 17 ou 21 et l'Android SDK 35. Ils ne sont pas installés dans l'environnement cloud de Claude (pas d'`ANDROID_HOME`, `dl.google.com` bloqué).
- **Build de test « Nathflix Dev »** : le debug, avec `applicationIdSuffix ".dev"`, son nom dans `src/debug/res`, et une clé fixe commitée (`tv/app/dev.keystore`, non secrète).
  - Elle s'installe à côté de la vraie app et par-dessus la build de test précédente.
  - Elle ne vérifie aucune mise à jour.
  - Le workflow `.github/workflows/android-dev.yml` la construit à chaque push touchant `tv/**` ou le plugin, et la publie dans la pré-release `dev` : `releases/download/dev/nathflix-dev.apk`.
  - **C'est la vérification de compilation Kotlin en session cloud** : pousser, puis lire le run avec les outils GitHub (`actions_list`, `get_job_logs`).
- **Tailles du lecteur** : `res/values/dimens.xml` pour téléphone et tablette, `res/values-television/dimens.xml` pour la TV (ne pas y toucher sans raison TV).
- Ne pas ajouter de dossier `values-sw600dp` pour les tailles du lecteur : `smallestWidth` passe avant le mode TV dans l'ordre des qualificatifs, et certaines TV en tvdpi font plus de 600dp, donc la TV en hériterait.
- Les builds debug s'inspectent depuis `chrome://inspect`.
- `preBuild` copie `../nathflix.plugin.js` dans les assets générés. Le plugin de la racine est donc toujours celui embarqué dans l'APK.

## Plugin (`nathflix.plugin.js`)

- Une seule IIFE en `"use strict"`, **sans build ni dépendance**. Il est chargé tel quel par Stremio Enhanced, l'app desktop et la TV.
- Environnement détecté par le User-Agent :
  - `IN_APP` (`nathflix/`) pour l'app desktop ;
  - `IN_TV` (`nathflix-tv/`) pour la TV : télécommande et optimisations pour les petites clés TV (`keyNav`, `SCROLL` instantané, `dx-tv`…) ;
  - `IN_MOBILE` (`nathflix-mobile/`) pour téléphone et tablette : tactile, classe `html.dx-mobile`, pas de cartes de survol ;
  - `IN_ANDROID` (`IN_TV || IN_MOBILE`) pour tout ce qui tient au lecteur natif et à la WebView : fonctions du lecteur in-page mises de côté, bouton Cast, intro native ;
  - sinon, le plugin dans Stremio Enhanced.
- `DEVICE` (« TV », « phone », « tablet », lu dans `__nathflixTv.deviceKind`) sert aux libellés ; `THIS_DEVICE` / `THE_DEVICE` le donnent traduit.
- **Langue (français ou anglais)** : elle suit la langue de l'interface de Stremio (`interfaceLanguage`).
  - `LANG` (« fr » ou « en ») et `LOCALE` sont fixés au chargement : la langue vue en dernier dans le core (`localStorage` `nathflix.lang`), sinon le profil Stremio.
  - `langWatch` (dans `sync()`) recharge la page quand la langue change, jamais pendant un film.
  - **Tout texte visible passe par `t("English text", { vars })`**, avec sa traduction dans `frDictionary()` (en fin de fichier, juste avant `anime4kShaders()`). La clé est le texte anglais exact, et `{nom}` est rempli par `t()`. Un texte absent du dictionnaire reste en anglais.
  - Dates : `LOCALE`. Les libellés écrits en CSS par le thème ont leur variante `html.dx-lang-fr` (bloc « FRENCH » du thème).
- **Réglages Stremio par défaut** (`profileDefaults`) : interface en français, audio en français, pas de sous-titres, épisodes non vus floutés (`hideSpoilers`). Une seule fois par compte (drapeau `store` `profileDefaults:<id>`).
  - **Langue de l'interface : `"fr-FR"`, jamais `"fre"`.** Le menu de Stremio enregistre `codes[0]` de `interfaceLanguages.json` et `i18n.changeLanguage()` ne connaît que ce code. Avec `"fre"`, le menu affiche Français mais Stremio reste en anglais. Les langues audio et sous-titres, elles, sont en code à trois lettres (`"fre"`).
  - Les comptes passés en `"fre"` par une version précédente sont corrigés une fois (drapeau `profileLangFixed:<id>`).
- **Billboard** (`hero`) : jusqu'à trois listes mélangées (`billboardSource`, `billboardSource2`, `billboardSource3`), au choix parmi les listes Cinemeta et **tous les catalogues des addons du compte** (`catalogChoices`, `readCatalogs`, `catalogOptions`).
  - Valeur `cat:<id du manifest>|<nom du catalogue>` : jamais l'adresse de l'addon (elle contient souvent une clé, et les réglages partent dans les rapports).
  - Les choix sont gardés dans `localStorage` `nathflix.catalogs` (le schéma des réglages est enregistré avant que le core soit prêt), puis rafraîchis (`catalogChoices.watch` sur Addons et Settings), avec `registerSchema()` pour réenregistrer les réglages.
  - Les titres sans image ni description sont complétés par Cinemeta. Si rien n'est utilisable, le billboard prend les titres populaires.
- Organisé en sections délimitées par `// ----…` puis un titre. Pour s'y retrouver :

```bash
awk '/^  \/\/ -{20,}/{getline n; print NR+1": "n}' nathflix.plugin.js | grep -v -- -----
```

- **Ajouter un réglage** :
  - le mettre dans `DEFAULTS`, `SCHEMA` et `SETTING_GROUPS`, libellés et description en `t("…")`, avec leur traduction dans `frDictionary()` ;
  - s'il ne concerne que la TV, ajouter `tvOnly: true` dans `SCHEMA` ; s'il concerne l'app Android (TV, téléphone et tablette), ajouter `androidOnly: true` ;
  - s'il ne concerne que téléphone et tablette, ajouter `mobileOnly: true` (exemples : `mobilePip`, `backgroundPlay`, `autoNext`) ;
  - s'il n'a pas de sens au doigt, ajouter sa clé au `Set` `NOT_ON_TOUCH` de `nathflix-tv.js` ;
  - s'il ne sert que sur desktop (lecteur in-page), ajouter sa clé au `Set` `DESKTOP_ONLY` de `nathflix-tv.js`. Sinon il apparaîtra dans les réglages TV.
- **Thème** : le bloc « PHONES AND TABLETS » en fin de `nathflix.theme.css` est gardé par `html.dx-mobile`. Le bloc « WINDOWS AND TV » ne vaut que pour `dx-tv`. Le bloc mobile contient :
  - la barre d'onglets en bas (« Library » affiché « My List ») ;
  - les colonnes de posters ;
  - les commandes visibles sans survol ;
  - le billboard haut en portrait ;
  - la page d'un titre en une colonne (image, détails, puis épisodes ou flux) ;
  - les grilles Discover et Bibliothèque à 3 colonnes ;
  - Réglages empilés ;
  - les toasts au-dessus de la barre d'onglets.
- **Plugin, téléphone et tablette** :
  - `titleSheet` : un tap sur un poster (Accueil, Recherche, Découvrir, Bibliothèque ; sauf Reprendre) ouvre une feuille en bas de l'écran, avec Play ou Resume, My List, Hide et « Episodes & info ». Back la ferme (`closePreview`).
  - `mobileChips` : puces Shows, Movies et My List en haut de l'accueil, qui mènent aux catalogues « top » de Cinemeta.
  - Leur CSS (`.dxm-*`) est dans `EXTRAS_CSS`, toujours chargé.
- **Partout** :
  - `cardPress` : un appui long (500 ms) ou un clic droit sur une carte ouvre son menu ⋮ de Stremio (`menu-label-container`, replacé par le thème dans le coin de la miniature).
  - `episodePlay` : bouton « Lancer l'épisode » (« Lecture » pour un film) avant la bande-annonce, sur la page des flux. Il clique la première source affichée (sans `data-dx-hide`, plus petit `style.order`).
  - `addonTools` (Addons → Order & backup) : glisser-déposer en Pointer Events (poignée ⠿ au doigt, toute la ligne à la souris).
  - Accueil : « Airing this week » (`airing`) vient après Continuer de regarder.
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
cd tv && ./gradlew assembleDebug  # si tv/ a changé et que le SDK est disponible (sinon : CI android-dev.yml)
```

Côté CI :
- `cross-platform.yml` (push sur main et PR) installe le plugin et lance un vrai Stremio Enhanced sur Windows, macOS et Linux.
- `mpv-windows.yml` se lance quand `app/` ou le plugin changent.
- `android-dev.yml` compile l'app Android (debug) à chaque push touchant `tv/**` ou le plugin, et publie la pré-release `dev`. Ensuite, il construit aussi la release non signée, pour vérifier R8 (minify).
- `release.yml` : au push d'un tag `v*`, publie la release et lance `tv.yml` et `app.yml`.
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
- **Signature Android** : release signée par `tv/keystore.properties` (hors git) ou par les secrets `NATHFLIX_TV_KEYSTORE_B64` et `NATHFLIX_TV_KEYSTORE_PASSWORD` (alias `nathflix-tv`).
  - Sans clé, `tv.yml` ne construit rien, et la release n'a pas d'APK.
  - Après avoir ajouté les secrets, lancer « TV app » à la main avec `tag` (par exemple `v0.2.0`) pour joindre l'APK à une release existante.
  - Ne jamais signer une release avec la clé de test (`dev.keystore`) : elle est publique.
