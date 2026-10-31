# Nathflix — audit de performance, fluidité et réactivité (31 octobre 2026)

Analyse statique du dépôt à la version 0.5.2 (commit `f2ae445`), sans modification du code. Elle reprend les mesures du banc de T-039 quand elles existent, et les tailles réelles des ressources mesurées aujourd'hui (plugin, thème, bundle de web.stremio.com).

Ordre de priorité demandé : Android TV, puis Android téléphone/tablette, puis PC. Mac et Linux non testés par l'utilisateur.

---

## 1. Résumé

**Nathflix est une surcouche d'une application web déjà très lourde, qui tourne dans une WebView sur des appareils faibles.** Le plancher incompressible, avant tout code Nathflix, c'est Stremio Web : 10,3 Mo de JavaScript (3 Mo compressés), un cœur Rust compilé en WebAssembly de 5,7 Mo dans un Web Worker, et 427 Ko de CSS. L'application Stremio officielle pour Android TV ne passe pas par là : son cœur est natif (stremio-core-kotlin) et son interface est embarquée dans l'APK. C'est la raison structurelle de l'écart de fluidité que tu constates sur la même Fire TV. On ne peut pas rattraper cet écart complètement, mais on peut réduire fortement ce que Nathflix ajoute par-dessus, et ce qu'il ajoute est aujourd'hui conséquent.

Ce que Nathflix ajoute, par ordre d'impact estimé :

1. **Un plugin de 1 Mo (sur TV) injecté sous forme de texte à chaque démarrage**, donc analysé et compilé à chaque fois, sans aucun cache de compilation V8. Idem sur le desktop (1,55 Mo).
2. **Une boucle `sync()` de 30 étapes relancée après toute mutation du DOM de Stremio** (90 ms de regroupement). Plusieurs étapes interrogent le cœur Stremio (`getState`), ce qui sérialise l'état complet d'un modèle depuis le Worker WASM et le clone vers le thread principal. `coreMeta.refresh()` fait cela pour cinq modèles toutes les 4 s tant que la page bouge ; `endsAt` le fait pour `meta_details` à chaque mutation sur la page d'un film.
3. **Sur TV, toutes les rangées de l'Accueil sont chargées d'un coup** (`homeRows.preload` → `LoadRange 0..n`), là où Stremio ne charge que celles qui approchent. DOM, images, état du cœur et coût de chaque `getState("board")` sont multipliés d'autant.
4. **Un thème CSS dont 541 sélecteurs sur 1 043 se terminent par un `[class*="…"]`**, et 202 contiennent un sélecteur universel. `sharpen` corrige cela à l'exécution depuis 0.5.2, mais le premier démarrage de chaque build de Stremio paie le coût entier, et 300 Ko de CSS « affûté » sont relus depuis `localStorage` à chaque démarrage.
5. **Des effets GPU encore actifs sur TV** : 19 `backdrop-filter`, 16 `filter: blur()`, 44 `box-shadow`, 78 transitions. Les sticks Fire TV (4 cœurs Cortex‑A53, GPU PowerVR d'entrée de gamme, 1,5 à 2 Go) rendent ces effets en plusieurs passes plein écran.
6. **La navigation à la télécommande mesure la mise en page à chaque touche** (`keyNav.inView` : tous les focusables de la page, un `getBoundingClientRect` chacun).
7. **`store.set` réécrit tout le magasin Nathflix dans `localStorage` à chaque écriture** (`JSON.stringify` de tout : profils, propriétaires de bibliothèque, `played:*`, `stats:*`), y compris pendant la lecture (stats toutes les 30 mises à jour).
8. **Desktop : fenêtre Electron transparente et sans cadre dès que mpv est disponible** (donc par défaut sur Windows). Une fenêtre transparente désactive les optimisations de composition de Chromium (occlusion, redessin partiel) sur toute l'interface, pas seulement pendant un film.

Il y a aussi un cap plus structurel, à décider : charger le plugin **par adresse** (script mis en cache et compilé une fois), le **minifier** pour l'APK et l'app, **découper le thème par appareil à la construction**, et faire passer la boucle `sync()` à une architecture **pilotée par les événements** avec un budget de temps par frame. Ce sont des chantiers de plusieurs jours chacun, mais ce sont ceux qui changeront la sensation sur TV.

---

## 2. Méthode et limites

- Lecture du plugin (`nathflix.plugin.js`, 16 355 lignes), du thème, de `nathflix-tv.js`, des fichiers Kotlin et de l'app Electron.
- Statistiques calculées par script sur le thème (sélecteurs) et sur le plugin (tailles par section).
- Tailles réseau de web.stremio.com mesurées par `curl` (build `f0222603…`).
- Je n'ai pas d'appareil sous la main : pas de profil Chrome DevTools de la Fire TV. Les ordres de grandeur « sur un stick » sont des estimations à partir des mesures de T-039 (processeur bridé ×6) et de l'expérience de ce genre d'appareil. **Chaque piste ci-dessous doit être mesurée avant/après sur l'appareil** : `perfOverlay` existe pour ça, et `chrome://inspect` sur la build Nathflix Dev donne le profil complet.

---

## 3. Ce qui coûte, poste par poste

### 3.1 Le plancher : Stremio Web

| Ressource (build `f0222603…`) | Taille |
|---|---|
| `scripts/main.js` | 10 270 860 octets (2 993 858 compressés) |
| `binaries/stremio_core_web_bg.wasm` | 5 745 603 octets |
| `styles/main.css` | 426 875 octets |

- Le cœur tourne dans un Web Worker. Chaque `core.getState(model)` demande au Worker de sérialiser l'état du modèle et de le renvoyer par `postMessage` (clonage structuré). Pour `board` avec toutes les rangées chargées, c'est plusieurs centaines de Ko à sérialiser puis à cloner, à chaque appel. Stremio lui-même ne le fait que sur l'événement `NewState` du modèle affiché.
- Le cache HTTP de la WebView garde ces fichiers entre deux lancements, et Chromium produit un cache de bytecode pour un script chargé depuis le cache HTTP à partir du deuxième ou troisième chargement. Donc après quelques démarrages, le gros du coût de `main.js` est déjà amorti, **à condition** que la WebView ne purge pas son cache (mémoire basse sur un stick de 1,5 Go : fréquent).

### 3.2 Chargement du plugin

| Partie de `nathflix.plugin.js` | Octets |
|---|---|
| Code JavaScript (hors lignes géantes) | 852 065 |
| Thème intégré (`/*@bundled-theme*/`) | 163 003 |
| Images de marque (`brandImages`, data URI) | 47 958 |
| Vidéo d'intro (`introVideoData`, base64) | 382 518 |
| Shaders Anime4K | 105 653 |
| **Total** | **1 551 201** |
| `EXTRAS_CSS` (dans le code) | 71 246 |
| `frDictionary()` (dans le code) | 66 887 |

Sur TV, `Updates.forTv()` retire l'intro et les shaders : il reste **environ 1,06 Mo** à analyser à chaque démarrage.

Comment il est chargé :

- **Android** (`nathflix-tv.js:888`, `loadPlugin`) : appel JNI synchrone `bridge.pluginSource(TOKEN)` qui lit l'asset (ou le fichier mis à jour), passe deux regex sur 1,5 Mo (`Updates.forTv`, `Updates.kt:54`), renvoie la chaîne au JS ; le JS la colle dans un gabarit et l'affecte en `script.textContent`. Trois copies d'1 Mo et surtout **zéro cache de compilation** : un script inline n'est jamais mis en cache par V8. Sur un A53, l'analyse d'1 Mo de JS non minifié prend de l'ordre de 300 à 800 ms, à chaque lancement et à chaque rechargement de page (changement de langue, crash du renderer).
- **Desktop** (`app/src/preload/ui/mod/modController.ts`, `loadPlugin`) : même principe, `script.innerHTML` avec le fichier entier (1,55 Mo, intro et shaders compris).
- **Stremio Enhanced** : idem (c'est son mécanisme).

### 3.3 CSS : volume et forme des sélecteurs

Statistiques sur `nathflix.theme.css` (5 098 lignes, 163 Ko) :

| Mesure | Valeur |
|---|---|
| Sélecteurs | 1 043 |
| Contenant `[class*=`] | 623 |
| Dont le composé le plus à droite contient `[class*=`] | 541 |
| Contenant un `*` universel | 202 |
| Au moins 4 niveaux de descendance | 493 |
| `:has(` | 14 |
| `backdrop-filter` | 19 |
| `filter: blur(` | 16 |
| `box-shadow` | 44 |
| `transition` | 78 |
| `animation` | 33 |
| `!important` | 397 |

À cela s'ajoutent `EXTRAS_CSS` (71 Ko, toujours injecté) et le `main.css` de Stremio (427 Ko).

- Un moteur CSS filtre les règles par le composé de droite. Avec `[class*="…"]` à droite, il n'y a pas d'index possible : chaque règle est testée sur chaque élément à chaque recalcul de style. C'est le constat de T-039, et `sharpen` (`nathflix.plugin.js:14978`) réécrit ces sélecteurs en vrais noms de classes à l'exécution. Mais : le premier démarrage sur un nouveau build de Stremio (ou après un stockage plein) refait tout le travail, 300 ms après le chargement, au moment où l'Accueil se construit ; et le résultat (≈ 230 Ko + 70 Ko) est relu depuis `localStorage` en JSON à chaque démarrage, de façon synchrone.
- `#app *` avec `font-family` (ligne 86) et `.dx-tv #app * { scroll-behavior }` : deux règles universelles appliquées à chaque élément. Peu chères individuellement, mais présentes dans chaque recalcul.
- `@import url("https://fonts.googleapis.com/…Inter…Bebas+Neue…")` (ligne 12) : une requête réseau bloquante en chaîne (CSS → CSS → woff2) à chaque démarrage à froid, puis un `swap` qui remet toute la page en page quand la police arrive.
- Effets GPU : le bloc « WINDOWS AND TV » (lignes 4560–4660) en désactive déjà plusieurs sur TV (zoom du billboard, `will-change`, fondus de posters, masque des rangées, aura de la carte). Restent actifs sur TV, entre autres : les `backdrop-filter` des lignes 744, 760, 799, 874, 1139, 1360, 1910, 2051, 2103 (blur 24 px), 2616, 2653, 3621, 4028 ; les flous de `.dx-wide-blur` (`filter: blur(30px)` ligne 1510, remplacé par `brightness` sur TV seulement pour cette classe), 1525, 3510 ; les ombres et transitions des boutons et des tuiles.

### 3.4 Le JavaScript du plugin à l'exécution

**La boucle principale** (`nathflix.plugin.js:7544–7600`)

- `mo` observe `#app` en `childList + subtree`. Toute mutation de Stremio (chargement d'une rangée, d'une image, d'un menu, une classe qui change sur un conteneur) appelle `scheduleSync()` → `sync()` 90 ms plus tard.
- `sync()` sur Android enchaîne environ 25 étapes, dont beaucoup commencent par un `querySelectorAll` sur toute la page : `syncCards` (tous les `meta-item-container`, puis trois `querySelector` par carte dans `cardInfo`, puis `restrict.card`, puis `restrict.rows()` qui refait un `querySelectorAll` par rangée), `updateAllArrows` (toutes les rangées, mesure de celles qui ont changé), `homeArrange.apply`, `filterRows`, `hero.ensure`, `episodePlay`, `reviews`, `hideButton`, `endsAt`, `calendarFill`, `restrict.guard`, `communitySync` (sept sous-étapes), etc. Chaque étape est raisonnable seule ; c'est leur somme, relancée à chaque mutation, qui pèse. T-039 a mesuré « 60 ms en 30 s » pour `sync()` sur un Accueil invité : un Accueil réel avec 20 rangées chargées, des badges, des profils et une bibliothèque, c'est bien plus, et c'est le pire moment (pendant que Stremio dessine).

**Les appels au cœur Stremio (`getState`)**

- `coreMeta.refresh()` (section 1535) : appelé par `syncCards` à chaque sync ; limité à une fois toutes les 4 s, mais alors **cinq `getState` d'affilée** (`board`, `discover`, `search`, `library`, `continue_watching_preview`) puis une marche récursive (`collect`, profondeur 7) sur chaque état. Sur TV avec toutes les rangées chargées, `board` seul représente des centaines de Ko à sérialiser dans le Worker, cloner, puis parcourir. Cinq fois toutes les 4 s tant que la page bouge.
- `viewerLibrary.patch` (ligne ≈ 10330) enveloppe `getState` pour **tous** les appels, ceux de Stremio compris : avec plus d'un profil, chaque état de `board`/`discover`/`search` reçoit `unmark` (marche récursive profondeur 8) et chaque élément de `library`/`continue_watching` passe par `mine()` → `viewers.list()` → `owners()`. Ces fonctions sont peu coûteuses une à une, mais elles sont appelées pour chaque titre de chaque état, à chaque `NewState` de Stremio.
- `endsAt.ensure()` (12617) : sur la page d'un **film**, `getState("meta_details")` à **chaque sync**, c'est-à-dire à chaque mutation. `meta_details` contient les flux de tous les addons. `calendarFill.ensure()` fait pareil sur le calendrier.
- `homeRows.preload` (974) : sur TV, `getState("board")` puis `LoadRange` de **toutes** les rangées 1,5 s après l'arrivée sur l'Accueil. Stremio, lui, ne charge que les rangées proches de l'écran. Conséquences : DOM (T-039 : environ 3 300 éléments avec tout chargé, contre quelques centaines), images de posters, requêtes réseau vers tous les addons de catalogue, état `board` énorme pour chaque `getState` suivant, et `coreMeta.collect` qui parcourt tout. `content-visibility: auto` évite la mise en page des rangées lointaines, mais pas leur construction ni leur présence dans l'état.
- `langWatch`, `profileDefaults.watch`, `catalogChoices.watch`, `cloudSync.watch` sont bien limités dans le temps (2 à 15 s). RAS.

**Navigation à la télécommande** (`keyNav`, section 2779)

- À chaque flèche : `reach()` (`querySelectorAll('#app [tabindex="-1"]')` et réécriture de `tabIndex`), puis `inView()` : `querySelectorAll` de tous les focusables, un `getBoundingClientRect` par rangée puis par focusable visible. Après `scrollBy`, `look(box)` recommence. Un `getBoundingClientRect` après une modification force la mise en page ; T-039 a ramené le pire cas de 43 à 11 ms sur PC bridé, soit sans doute 30 à 80 ms sur le stick, à chaque appui. Avec la répétition limitée à une touche toutes les 140 ms, le budget est presque entièrement consommé par la mesure.
- `preview.onFocus` à chaque focus et à chaque scroll (`keyNav.onScroll`) : hors mode léger, la carte de survol se prépare (`getInfo`, décodage de deux images).

**Décorations de cartes**

- `syncCards` marque chaque carte et, sur TV, `decorateLater` décore 12 cartes par passage en `requestIdleCallback`. Bon design. Mais `decorate` déclenche `getInfo` → `coreMeta.refresh()` (voir plus haut) et `getMeta` (Cinemeta, 4 requêtes en parallèle, cache `localStorage` par titre : une entrée JSON par film, lue et analysée à la demande).
- `restrict.rows()` (profils enfants) : `querySelectorAll` par rangée à chaque sync.

**Magasin Nathflix** (`store`, 10009)

- `store.set` → `saveLocal()` → `JSON.stringify(this.data)` **de tout le magasin** à chaque écriture, puis `flush()` 2,5 s plus tard (un `saveSetting` JNI par clé modifiée, `SharedPreferences.apply()` qui réécrit le fichier XML complet). `stats.flush()` écrit toutes les 30 mises à jour de lecture (`stats.tick` sur `timeupdate`), `markPlayed` à chaque lancement, `cloudSync.changed` derrière. Le magasin grossit avec `played:<profil>` (jusqu'à 400 titres par profil), `libraryOwners`, `stats:*`, `homeRows:*`.

**Index des épisodes** (12361)

- Au démarrage puis toutes les 10 min : jusqu'à 80 séries de la bibliothèque, 3 requêtes en parallèle vers Cinemeta/TMDB/Kitsu, chaque réponse `meta` complète (toutes les vidéos d'une série : souvent 100 à 500 Ko) analysée et réduite. Cache `localStorage` 6 h par série. Sur TV avec une grande bibliothèque, c'est plusieurs Mo de JSON à analyser pendant que l'Accueil se dessine. T-039 l'avait noté comme piste non faite (« index des épisodes allégé sur TV »).

**Billboard** (`hero`, 1124)

- Largement optimisé en 0.5.2 (une image au démarrage, arrêt hors écran, pas de zoom sur TV). Reste : jusqu'à 12 candidats complétés par Cinemeta (`candidates`), `restrict.decide` pour chacun, et des fonds `background/large` 1920×1080 de 550 à 680 Ko (décodage ≈ 80 à 150 ms sur un stick, un par diapositive).

### 3.5 Côté Android (Kotlin)

- **WebView** (`MainActivity.kt`) : réglages de base seulement. Pas de `setRendererPriorityPolicy`, pas de préchauffage (la première création d'une WebView charge le provider Chromium : 300 à 800 ms sur un stick, pendant l'intro), pas de `WebViewAssetLoader`. `useWideViewPort + loadWithOverviewMode` sur TV, viewport 1280 px CSS : sur une sortie 1080p le facteur d'échelle est 1,5 et tout est rendu à 1920×1080.
- **Démarrage** : l'intro (`Intro.kt`, `MediaPlayer` + `TextureView`, H.264 1080p) décode une vidéo pendant que la WebView charge 10 Mo de JS et 5,7 Mo de WASM, et `Updates.checkPlugin` + `checkApp` partent aussitôt (deux requêtes GitHub et, si une version plus récente existe, le téléchargement et la lecture du plugin). Tout cela se dispute 4 cœurs au moment le plus chargé.
- **Lecteur** (`PlayerActivity.kt`) : ExoPlayer avec `LoadControl` par défaut, `SurfaceView` par défaut, FFmpeg en secours. Correct. Rien de bloquant à signaler ; les ralentissements de l'utilisateur sont dans l'interface, pas dans la lecture.
- **Torrents** (`TorrentEngine.kt`) : attente de pièces par `Thread.sleep(60)`, un thread par ouverture, NanoHTTPD. Correct pour un usage à un seul lecteur.
- `Thumbs.kt` sous-échantillonne les images (`inSampleSize`) avec un `LruCache` de 8 Mo. Correct.
- `Bridge.saveSetting` : `apply()` asynchrone. Correct. `pluginSource` : voir 3.2.

### 3.6 Côté desktop (Electron 41)

- `app/src/main.ts` : `transparent: true` et `frame: false` dès que `MpvShell.isEnabled()` (mpv livré et fonctionnel : le cas normal sur Windows). Une fenêtre transparente dans Electron/Chromium perd l'occlusion et le redessin partiel : chaque frame recompose toute la fenêtre, sur tout le parcours (Accueil, Découvrir…), pas seulement pendant un film. C'est connu pour faire chuter la fluidité du défilement et des animations CSS, surtout sous Windows avec DWM. `backgroundThrottling: false` et `disable-renderer-backgrounding` gardent en plus l'app à plein régime quand elle est au second plan.
- Flags GPU (`gpuController.ts`) : `enable-gpu-rasterization`, `enable-zero-copy`, `ignore-gpu-blocklist`, ANGLE d3d11 : raisonnables.
- `preload/ui/titleBar.ts:115` : un `MutationObserver` `childList + subtree` sur `#app` qui fait un `querySelector` à chaque mutation (`followPlayer`), et `preload/index.ts:170` un second sur `document.body` quand la transparence est active (`addTitleBar`). Deux observateurs globaux de plus à côté de `mo`.
- **Bandes-annonces dans la carte de survol** (`trailer.start`, 12768) : une iframe YouTube complète (lecteur de plusieurs Mo de JS, autoplay) par survol de plus de 600 ms. C'est de loin l'élément le plus lourd d'un survol sur PC : un parcours à la souris sur l'Accueil crée et détruit des lecteurs YouTube à la chaîne.
- Le plugin complet (1,55 Mo, intro et shaders compris) est injecté inline : voir 3.2.

### 3.7 Téléphones et tablettes

Le Pixel 7 (8 cœurs) et la Honor Pad ne passent pas en mode léger (`MODEST_DEVICE` = 4 cœurs ou moins, ou 2 Go ou moins). Ils subissent donc tout ce qui précède côté plugin (sync, `coreMeta`, store, thème), sans la précharge de toutes les rangées (TV seulement) mais avec les effets GPU complets (les `backdrop-filter` des feuilles `.dxm-*`, les flous). Le plugin n'a pas de cache de compilation non plus. Les pistes 4.1 à 4.6 et 4.9 s'appliquent telles quelles.

---

## 4. Pistes d'amélioration

Chaque piste : problème, proposition, gain attendu, effort, risques, comment vérifier. « Gain » est une estimation à confirmer sur appareil.

### Palier A : gains rapides (quelques heures à deux jours chacun)

**4.1 `coreMeta` piloté par les événements du cœur, et un seul modèle à la fois**
- Problème : 5 `getState` toutes les 4 s + marche récursive (3.4).
- Proposition : s'abonner aux événements du transport du cœur (`services.core.transport.on("NewState", models => …)` dans Stremio Web ; le plugin a déjà `core()`), ne relire que le modèle qui a changé et seulement s'il correspond à la route courante (`board` sur l'Accueil, `discover` sur Découvrir, etc.). Remplacer la marche générique `collect` par des chemins connus (`board.catalogs[].content.content[]`, `discover.catalog.content.content[]`, `search.catalogs[]…`, `library.catalog[]`, `continue_watching_preview.items[]`). Sur TV et en mode léger, se contenter de ce que le DOM donne (`cardInfo`) et de Cinemeta.
- Gain : suppression d'une rafale de sérialisation de plusieurs centaines de Ko toutes les 4 s. Sur un stick, probablement plusieurs dizaines de ms de tâche longue à chaque fois, dans le Worker et sur le thread principal.
- Risque : l'API d'événements du transport n'est pas documentée ; prévoir le repli actuel (lecture périodique) si `on` n'existe pas.
- Vérifier : compter les `getState` par minute (un wrapper de mesure dans `core()`), et les tâches longues de `perfOverlay` au repos sur l'Accueil.

**4.2 `endsAt` et `calendarFill` sans `getState` à chaque sync**
- Lire `meta_details` une fois par fiche (clé = id du titre) et mémoriser le résultat ; relire seulement si `libraryItem.state` peut avoir changé (retour du lecteur). Même traitement pour `calendar` (par mois affiché).
- Gain : sur la page d'un film, plus aucune sérialisation de `meta_details` par mutation (c'est la page où les addons de flux font le plus de mutations).

**4.3 `viewerLibrary.filter` : moins de travail par état**
- Mémoriser `viewers.list()` et `owners()` au début de chaque `filter` (une lecture, pas une par titre). Remplacer `unmark` récursif par les chemins connus (comme 4.1). Court-circuiter quand l'état est identique au précédent (comparer la référence ou un compteur de version si le transport en fournit un).
- Gain : réduit le surcoût de chaque `NewState` de Stremio avec plusieurs profils. Modeste mais sur le chemin critique de tout affichage.

**4.4 `store.saveLocal` différé et `stats` moins bavard**
- Regrouper `saveLocal()` (par exemple 1 s après la dernière écriture, et dans `onLeave`), ne pas resérialiser si rien n'a changé. Garder `flush()` disque à 2,5 s. Pour `stats`, écrire toutes les 60 s ou à la pause/fin, pas toutes les 30 mises à jour.
- Gain : fin des `JSON.stringify` répétés du magasin pendant la lecture et les changements de profil. Petit mais gratuit.

**4.5 `sync()` ciblé par ce qui a changé**
- Dans `mo`, déduire du `target` des enregistrements une zone (`rows` si dans `board-content`/`meta-items-container`, `detail` si dans `metadetails-container`, `nav`, `other`) et passer ce drapeau à `sync()`. Les étapes lourdes (`syncCards`, `restrict.rows`, `updateAllArrows`, `homeArrange.apply`, `filterRows`) ne tournent que si la zone `rows` a bougé ou si la route a changé ; les étapes de fiche seulement pour `detail`. Garder un sync complet sur `hashchange` et toutes les 5 s en filet de sécurité.
- Gain : la majorité des mutations (menus, images, barre du haut, lecteur) ne déclenchent plus que 3 ou 4 étapes au lieu de 25.
- Risque : oublier une dépendance ; le sync complet périodique limite les dégâts.
- Vérifier : instrumenter `sync()` (durée et nombre d'étapes par appel) et comparer avant/après sur le banc.

**4.6 Thème : liste noire d'effets sur TV et en mode léger**
- Sous `.dx-tv` et `html.dx-light` (classe à poser quand `light()` est vrai, si elle n'existe pas) : `backdrop-filter: none` partout (remplacer par un fond semi-opaque), `filter: blur()` → `opacity` ou `brightness`, `box-shadow` réduits à une ombre sans flou ou supprimés sur les tuiles, `text-shadow` supprimés, transitions limitées à `opacity` et `transform` (jamais `width`, `height`, `top`, `left`, `box-shadow`, `filter`), `transition-duration` plafonnée à 150 ms.
- Gain : sur un GPU PowerVR d'entrée de gamme, chaque `backdrop-filter` visible coûte une passe plein écran par frame. C'est probablement la piste la plus rentable pour la « sensation » de fluidité au défilement sur TV, à faible risque visuel (le mode léger existe déjà et est accepté).
- Vérifier : captures avant/après (comme T-039), et le compteur de tâches longues pendant 10 flèches Bas sur l'Accueil.

**4.7 Polices : plus d'`@import` bloquant**
- Charger Inter et Bebas Neue par un `<link rel="preload" as="style">` + `<link rel="stylesheet">` posés par le plugin au démarrage (pas en `@import` dans la feuille), avec `font-display: optional` sur TV (si la police n'est pas en cache au premier rendu, on reste sur la police système, sans remise en page). Option plus radicale sur TV : Bebas Neue seulement (titres), `system-ui` pour le texte.
- Gain : une chaîne réseau bloquante en moins au démarrage à froid, plus de remise en page au `swap`.

**4.8 Démarrage Android : ordre et concurrence**
- Précréer la WebView dans `Application.onCreate` (une `WebView(applicationContext)` jetable ou `WebView.setDataDirectorySuffix` puis une instance gardée) pour payer l'initialisation de Chromium avant l'activité ; ou au moins appeler `WebViewCompat`/`WebSettings` le plus tôt possible.
- `web.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)` (API 26+) pour que le renderer garde la priorité quand l'intro ou le lecteur sont au premier plan.
- Retarder `Updates.checkPlugin` / `checkApp` jusqu'à `PAGE_READY` + quelques secondes (et après l'intro).
- Sur TV, intro en 720p (ou bitrate réduit) : le décodeur matériel décode sans effort, mais le `TextureView` 1080p compose une couche plein écran de plus pendant tout le chargement.
- Gain : quelques centaines de ms au démarrage, et moins de concurrence CPU pendant le chargement de Stremio.

**4.9 Index des épisodes allégé**
- Sur TV et en mode léger : limiter à 30 séries (les plus récentes), 1 requête à la fois, démarrer 20 s après l'Accueil, traiter les réponses par `requestIdleCallback`, et ne garder du `meta` que ce qui sert (`videos` réduit tout de suite dans un `TransformStream`/`response.text()` + `JSON.parse` différé plutôt que `r.json()` pendant que l'Accueil se dessine). Désactiver l'index quand ni `newEpisodes` ni `airingRow` ne sont actifs (déjà le cas) et proposer ces deux réglages coupés par défaut en mode léger.
- Gain : plusieurs Mo de JSON en moins à analyser au démarrage pour une grande bibliothèque.

**4.10 Rendu à résolution réduite sur TV (expérimental, réglage)**
- Dans `MainActivity`, sur TV et si le réglage l'active : donner à la WebView une taille physique de 1280×720 (ou largeur écran / 1,5) et l'agrandir par `scaleX = scaleY = 1,5` avec `pivot` à 0. Le framework transforme les événements, et la couche matérielle est agrandie par le GPU à coût nul. Rastérisation, composition, flous et images sont alors calculés sur 2,25× moins de pixels.
- Gain : c'est le levier le plus puissant pour tout ce qui est « lié au GPU » (défilement, flous, fondus) sur un stick 1080p. Netflix et consorts rendent leur interface TV en 720p sur ces appareils. Contrepartie : texte un peu plus doux. D'où un réglage (« Rendu léger sur TV »), proposé par défaut quand `MODEST_DEVICE`.
- Risque : à tester avec `displayCutout`/insets (TV : aucun) et le passage au lecteur (activité séparée : aucun impact). Le viewport CSS reste 1280 px, donc rien ne change côté page.

### Palier B : chantiers moyens (deux à dix jours chacun)

**4.11 Charger le plugin par adresse, pour le cache de compilation**
- Android : servir le plugin via `WebViewAssetLoader` (`https://appassets.androidplatform.net/assets/nathflix.plugin.js`, ou un chemin `files/` pour la copie mise à jour) ou par `shouldInterceptRequest` sur une adresse sous `https://web.stremio.com/__nathflix/plugin.js` avec `Cache-Control: max-age` et un `ETag` égal à la version. `nathflix-tv.js` insère alors `<script src=…>` au lieu de `textContent`. Garder l'enveloppe `StremioEnhancedAPI` dans un petit script inline avant. `forTv` peut rester (fait côté Kotlin au moment de servir) ou disparaître (voir 4.12).
- Desktop : `<script src="file://…/plugins/nathflix.plugin.js">` (ou un protocole `nathflix://` enregistré avec `protocol.handle`), même enveloppe.
- Gain : V8 met le bytecode en cache à partir du deuxième chargement d'un même script par adresse : l'analyse d'1 Mo passe de centaines de ms à quelques dizaines. Et `pluginSource` JNI disparaît du chemin critique.
- Risques : `addDocumentStartJavaScript` ne sait pas charger une adresse, l'insertion doit rester dans `nathflix-tv.js` ; `WebViewAssetLoader` interdit certains en-têtes ; vérifier le comportement après `onRenderProcessGone`. Compatibilité ascendante : une vieille APK continue de recevoir le plugin texte par `pluginSource` (invariant CLAUDE.md), donc garder les deux chemins.

**4.12 Minifier le plugin embarqué (APK et app desktop), pas celui du dépôt**
- Étape de build (`tv/app/build.gradle.kts` `preBuild` et `app/scripts`) : `terser` sur `nathflix.plugin.js` avec `compress` prudent (pas de `unsafe`), `mangle` activé, `keep_classnames` inutile ; sortir `nathflix.plugin.min.js` dans les assets. Retirer à la construction l'intro et les shaders pour l'APK (plutôt que par regex au démarrage). Le fichier source lisible reste celui de `main` pour Stremio Enhanced et `Updates.checkPlugin`.
- Attention : `Updates.forTv()` et `Updates.checkPlugin` doivent continuer à fonctionner sur le plugin non minifié téléchargé (c'est déjà le cas), et `version()` doit lire `@version` dans l'en-tête (le conserver par `comments: /@version/`).
- Gain : 850 Ko de code → ≈ 350 à 400 Ko. L'analyse JS est proportionnelle à la taille ; avec 4.11 le gain est cumulatif.
- Risque : un bug du minifieur ; `node --check` + le banc Playwright + la CI `android-dev.yml` le couvrent. Les tests mpv/cross-platform tournent sur le source non minifié : faire tourner `smoke-test.mjs` aussi sur la version minifiée.

**4.13 Thème découpé par appareil à la construction**
- `tools/bundle-theme.mjs` produit déjà la chaîne intégrée ; lui faire produire trois variantes (`tv`, `mobile`, `desktop`) en appliquant à la construction ce que `sharpen.prune` fait à l'exécution (retrait des sélecteurs d'autres appareils et, pour TV, des `:hover`), et intégrer les trois dans le plugin (ou seulement la bonne dans chaque APK/app). `sharpen.rules` (remplacement des `[class*=]` par les classes réelles) reste à l'exécution car il dépend du build de Stremio, mais sur une feuille déjà réduite de moitié.
- Gain : moins de règles à injecter, à affûter et à mettre en cache ; premier démarrage sur un nouveau build de Stremio bien plus court.
- À creuser ensuite : remplacer le cache `localStorage` du CSS affûté par `CacheStorage` (asynchrone, pas de JSON) ou par un fichier côté Kotlin (`saveSetting`-like), pour ne plus bloquer le démarrage sur la lecture de 300 Ko de JSON.

**4.14 Accueil TV : charger les rangées à l'approche, pas toutes**
- Retirer la précharge inconditionnelle sur TV. Laisser Stremio charger par plage et, dans `keyNav`, quand Bas vise une rangée pas encore chargée (pas de `meta-items-container` ou vide), la faire défiler en vue et attendre sa mutation (`mo` la verra) avant d'y poser le focus. Précharger seulement les 2 rangées suivantes (`LoadRange start..start+2`) à chaque arrivée sur une rangée.
- Garder la précharge totale uniquement quand un ordre personnalisé est actif (`homeArrange`), comme aujourd'hui pour les autres appareils, ou mieux : charger dans l'ordre personnalisé par plage.
- Gain : DOM divisé par 3 à 5 à l'arrivée sur l'Accueil, moins de requêtes aux addons au démarrage, état `board` plus petit pour tous les `getState`, `coreMeta.collect` et `syncCards` plus courts.
- Risque : une latence visible à la descente vers une rangée non chargée (afficher le squelette de Stremio pendant ce temps).

**4.15 `keyNav` : mesurer moins, mémoriser plus**
- Tenir un `IntersectionObserver` sur les rangées de l'Accueil (celles en vue) plutôt que de mesurer chaque rangée à chaque touche ; pour Haut/Bas depuis une carte, ne considérer que la rangée précédente/suivante dans `rows()` (déjà trié) ; pour Gauche/Droite rester dans la rangée (index dans `cards(track)`, pas de rectangles). Mémoriser les rectangles par frame (`requestAnimationFrame` invalide le cache) pour que `look()` après `scrollBy` ne remesure pas deux fois. `reach()` une fois par route et après les mutations de `rows`, pas à chaque touche.
- Gain : la latence par touche devient quasi constante et petite ; `perfOverlay` (« Réponse aux touches ») est la mesure directe.

**4.16 Desktop : fenêtre opaque sauf pendant un film**
- Electron ne sait pas changer la transparence d'une fenêtre existante. Options par ordre de simplicité :
  1. fenêtre principale opaque (`transparent: false`, `backgroundColor: "#0b0b0d"`, cadre natif ou `titleBarStyle: "hidden"`), et une **seconde fenêtre** transparente sans cadre, créée à l'ouverture du lecteur mpv et détruite à la sortie, qui héberge seulement la couche de contrôles du lecteur ; la fenêtre principale reste derrière, masquée ;
  2. mpv rendu **dans** la fenêtre principale (`--wid` sur le `HWND`/`NSView` de la `BrowserWindow`, mpv derrière un `<canvas>` ou une zone transparente du DOM) : plus propre mais plus de travail d'intégration ;
  3. à défaut, un réglage « fenêtre classique (sans mpv) » bien visible.
- Gain : toute la navigation desktop récupère l'occlusion et le redessin partiel de Chromium. Sur Windows, c'est souvent la différence entre « pas hyper fluide » et fluide.
- Risque : l'intégration mpv est le cœur de l'app desktop ; prévoir des tests manuels avec `mpv-test.mjs`.

**4.17 Desktop : bandes-annonces et observateurs**
- Une seule iframe YouTube réutilisée (changer `src`, ou mieux l'API `loadVideoById` de l'IFrame API déjà activée par `enablejsapi=1`), délai par défaut porté à 1,5 s, pas de bande-annonce quand la carte n'est pas restée visible 1 s. `titleBar.followPlayer` sur `hashchange` au lieu d'un observateur global ; `addTitleBar` idem.
- Gain : fin des créations/destructions de lecteurs YouTube à la chaîne lors d'un balayage à la souris.

### Palier C : chantiers longs (plusieurs semaines, à décider)

**4.18 Architecture événementielle à budget de temps**
- Remplacer « une mutation → tout `sync()` » par des domaines indépendants (Accueil, fiche, lecteur, barre du haut, profils), chacun avec son observateur limité à son conteneur, et un ordonnanceur commun : file de tâches priorisées (visible d'abord), exécutées par `requestIdleCallback` avec un budget (8 ms par frame sur TV), les tâches longues découpées (par rangée, par carte). C'est la généralisation de `decorateLater`. À faire progressivement : commencer par extraire `syncCards`/`restrict.rows`/`updateAllArrows` dans un domaine « rangées » (4.5 en est la première marche).
- Gain : plus aucune tâche Nathflix de plus de 16 ms sur le thread principal, quelle que soit la taille de la page. C'est ce qui donne la sensation « jamais bloqué » même quand Stremio travaille.

**4.19 Stremio Web embarqué dans l'APK (optionnel)**
- Servir le build exact de Stremio Web (`main.js`, `worker.js`, WASM, CSS, index) depuis les assets via `WebViewAssetLoader`, avec le plugin et le thème référencés directement dans l'`index.html`. Plus de dépendance au réseau au démarrage, cache de compilation garanti pour les 10 Mo de JS (il n'est plus à la merci d'une purge du cache HTTP), contrôle de la version de Stremio (les sélecteurs du thème ne cassent plus à l'improviste). Il faut un script de mise à jour du build (tâche `tools/`), et se souvenir que l'app officielle fait exactement cela.
- Gain : démarrage à froid nettement plus court et plus régulier ; résilience hors ligne. Ne change rien à la fluidité une fois chargé, sauf par l'effet mémoire (moins de pression → moins de GC).
- Risque : licence GPL de Stremio Web (compatible avec GPL‑3 de Nathflix), taille de l'APK (+ 15 Mo), maintenance.

**4.20 Ce que je ne recommande pas**
- Porter Nathflix sur stremio-core-kotlin avec une interface native : c'est réécrire l'app ; tout le plugin repose sur le DOM de Stremio Web.
- Un cache d'images intermédiaire (proxy de redimensionnement) : dépendance externe, et le coût de décodage des fonds est déjà borné depuis 0.5.2.

---

## 5. Mesurer, et ne pas régresser

- **Banc reproductible dans le dépôt** : le banc Playwright de T-039 n'est pas commité. Le recréer dans `tools/perf-bench.mjs` (Chromium bridé ×6, User-Agent `nathflix-tv/`, plugin servi depuis une fausse adresse de web.stremio.com comme décrit dans T-039, Accueil gonflé) et faire sortir un JSON : durée totale de `sync()` sur 30 s, nombre de `getState` par modèle, tâches longues, nombre d'éléments, temps de réponse par touche (Event Timing), temps jusqu'à `PAGE_READY`. Le lancer à la main avant/après chaque piste, et l'ajouter en job facultatif de CI (non bloquant, résultat en artefact).
- **Sur l'appareil** : `perfOverlay` (déjà là) pour les touches et les tâches longues ; `chrome://inspect` sur la build Nathflix Dev pour un profil Performance de 30 s « démarrage → Accueil → 10 flèches Bas → une fiche → retour ». C'est la seule mesure qui compte vraiment pour la Fire TV. `adb shell dumpsys meminfo com.lavagnou.nathflix` pour la mémoire du renderer (une WebView qui dépasse ~600 Mo sur un stick de 1,5 Go se fait tuer : `onRenderProcessGone`).
- **Compteurs à ajouter dans le plugin** (derrière `perfOverlay`) : `getState` par minute et par modèle, étapes de `sync()` et durée, `saveLocal` par minute, taille du DOM. Trois lignes de plus dans la mesure à l'écran.
- **Critères de succès proposés (Fire TV 4K)** : réponse médiane aux touches < 50 ms et pire cas < 150 ms ; aucune tâche longue de Nathflix > 50 ms au repos sur l'Accueil ; Accueil utilisable moins de 6 s après l'intro à chaud ; aucun redémarrage du renderer en 30 min de navigation.

---

## 6. Plan proposé

**Phase 1 — « Performances 2 » (release 0.6.0)** : 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8, 4.9, et 4.10 en réglage expérimental. Tout cela est contenu dans le plugin et `MainActivity`, sans changement visuel hors mode léger, chaque point mesurable isolément.

**Phase 2 — chargement et navigation (0.7.0)** : 4.11 et 4.12 ensemble (nouvelle APK et nouvelle app desktop), 4.14, 4.15, 4.17, et 4.16 dans sa variante 1 ou 3.

**Phase 3 — structure (0.8.0 et suivantes)** : 4.13, 4.18 par domaines successifs, puis décision sur 4.19 et 4.16 variante 2.

---

