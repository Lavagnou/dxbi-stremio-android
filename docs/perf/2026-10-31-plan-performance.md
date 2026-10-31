# Plan technique — performance, fluidité et réactivité de Nathflix

> **Pour l'agent :** ce plan s'exécute tâche par tâche, dans l'ordre. Chaque tâche devient une fiche `T-0xx` de `PLAN.md` (ID à la suite du dernier ; les IDs proposés ici supposent que T-040 est le dernier). Les cases `- [ ]` servent au suivi. Le spec est l'audit : [`2026-10-31-audit-performance.md`](2026-10-31-audit-performance.md). Le lire avant de commencer ; les numéros de section (§3.x, §4.x) renvoient à lui.

**Objectif :** rendre Nathflix nettement plus fluide, dans cet ordre de priorité : Android TV (Fire TV 4K : 4 cœurs, 1,5 à 2 Go), puis Android téléphone et tablette, puis PC Windows. Sans changement visuel hors TV et mode léger.

**Architecture :** on ne touche pas au plancher (Stremio Web, son cœur WASM dans un Worker). On réduit ce que Nathflix ajoute, dans cet ordre : les appels au cœur et la boucle `sync()` (phase 1), le chargement du plugin et la navigation à la télécommande (phase 2), puis la structure du thème et de l'ordonnancement (phase 3). Tout est mesuré par un banc reproductible avant et après.

**Pile :** plugin JS sans build ni dépendance (Chrome/WebView ≥ 100), thème CSS, Kotlin (`tv/`), Electron + TypeScript (`app/`), Playwright pour le banc (lancé par `npx`, jamais ajouté en dépendance du plugin).

**Spec :** `docs/perf/2026-10-31-audit-performance.md`.

## Contraintes globales

- **Aucun changement visible** hors `html.dx-tv` et mode léger (`light()`). Captures avant/après identiques (PC, téléphone, TV), comme dans T-039.
- **Compatibilité descendante :** un plugin récent tourne dans une vieille APK ; une nouvelle APK accepte un plugin texte téléchargé par `Updates.checkPlugin`. Toute nouvelle fonction du bridge est détectée avant usage (`bridge.x ? … : …` dans `nathflix-tv.js`, `window.__nathflixTv && window.__nathflixTv.x` dans le plugin).
- **Plugin :** une seule IIFE `"use strict"`, 2 espaces, guillemets doubles, pas de dépendance, pas d'`AbortSignal.timeout()` (utiliser `timeoutSignal(ms)`). Textes visibles par `t("…")` + `frDictionary()`. Ne jamais reformater `introVideoData()` ni `anime4kShaders()`.
- **Thème :** modifier `nathflix.theme.css` puis `node tools/bundle-theme.mjs` (jamais la chaîne intégrée au plugin). Sélecteurs Stremio en `[class*="…"]`.
- **Avant chaque commit :** `node --check nathflix.plugin.js`, `node --check tv/app/src/main/assets/nathflix-tv.js`, `cd app && npx tsc --noEmit` si `app/` change ; Kotlin vérifié par la CI `android-dev.yml` (pousser, lire le run).
- **Chaque tâche :** mesure de référence sur le banc avant de toucher au code, mesure après, les deux dans **Vérification** de la fiche. Sur l'appareil quand c'est possible (`perfOverlay`, `chrome://inspect` sur la build Nathflix Dev).
- **Une piste sans gain mesurable n'est pas gardée :** le noter dans la fiche (`Abandonné`, raison) et passer à la suivante.
- Code, commentaires, CHANGELOG et commits en anglais. Un commit par tâche (ou sous-tâche), `PLAN.md` dans le même commit.

## Points de vigilance (à tester explicitement)

1. **`sync()` ciblé rate une zone** (T-046) : un élément de Nathflix n'apparaît plus (bouton « Lancer l'épisode », badges, flèches) après une mutation classée dans la mauvaise zone. Filet : sync complet sur `hashchange` et toutes les 5 s ; test de parcours (Accueil → fiche → flux → Réglages → Bibliothèque → Calendrier) sur le banc.
2. **`store.saveLocal` différé perd une écriture** (T-045) : l'app est tuée dans la seconde qui suit un changement de profil. Filet : écriture immédiate sur `visibilitychange` (hidden), `pagehide` et `onLeave()`.
3. **`coreMeta` sans événements** (T-042) : la page n'expose pas l'API d'événements attendue, ou un modèle change sans événement. Filet : repli périodique (comportement actuel) si l'API est absente, et une lecture forcée sur `hashchange`.
4. **`viewerLibrary.filter` par chemins connus laisse passer un titre** (T-044) : un modèle dont la forme n'est pas celle attendue n'est plus filtré, et un profil voit la liste d'un autre. Filet : le chemin connu ne remplace la marche récursive que si la forme attendue est trouvée ; sinon la marche récursive reste.
5. **Plugin par adresse** (T-052) : vieille APK + plugin récent (chemin texte gardé), nouvelle APK + plugin texte téléchargé (même adresse, version dans l'URL), et renderer redémarré après crash (`recoverFromCrash`) qui recharge l'adresse.
6. **Rendu 720p** (T-051) : focus décalé, `getBoundingClientRect` cohérent, passage au lecteur natif et retour, clavier à l'écran, « Qui regarde ? ».

---

## Phase 1 — plugin et `MainActivity`, sans changement visuel (release 0.6.0)

### T-041 — Banc de mesure `tools/perf-bench.mjs` et compteurs dans `perfReadout`

**Fichiers :**
- Créer : `tools/perf-bench.mjs`
- Modifier : `nathflix.plugin.js` (nouvelle petite section `perf` juste avant « Performance readout » ≈ 7453 ; `perfReadout.paint()` ≈ 7516 ; `coreMeta.refresh()` ≈ 1543 ; `sync()` ≈ 7556 ; `store.saveLocal()` ≈ 10066 ; `window.__nathflix = api` ≈ 15466)
- Modifier : `tools/README.md` (section « Banc de performance »)

**Interfaces :**
- Produit (plugin) : `perf.count(name: string): void`, `perf.time(name: string, ms: number): void`, `perf.snapshot(): { since: number, counts: Record<string, number>, ms: Record<string, number> }`, exposé en `window.__nathflix.perf()`. Les noms utilisés : `getState:<model>`, `sync`, `sync:<étape>` (optionnel, seulement si `settings.perfOverlay`), `saveLocal`, `dispatch`.
- Produit (banc) : un JSON sur la sortie standard (ou `--out <fichier>`) de la forme :

```json
{ "mode": "tv", "version": "0.5.2", "commit": "f2ae445", "runs": 5,
  "pageReadyMs": 0, "pluginLoadMs": 0,
  "sync": { "calls": 0, "ms": 0 }, "getState": { "board": 0 }, "saveLocal": 0,
  "longTasks": { "count": 0, "totalMs": 0, "maxMs": 0 },
  "elements": 0, "keys": { "median": 0, "p90": 0, "worst": 0 } }
```

Toutes les valeurs sont la médiane des `runs`.

- [ ] **Étape 1 : `perf` dans le plugin.** Objet `perf` avec `counts` et `ms` (objets simples), `count`, `time`, `snapshot` (copie), `reset()`. Coût nul quand rien ne lit : des incréments d'entiers, pas de chaînes formatées. Appels : `perf.count("getState:" + model)` dans le wrapper de `viewerLibrary.patch` (`live.getState`, ≈ 10330 : c'est par là que passent tous les `getState` du plugin et de Stremio) **et** dans `core()` quand le cœur n'est pas encore patché ; `perf.count("sync")` + `perf.time("sync", dur)` autour de la boucle `for (const step of steps)` de `sync()` ; `perf.count("saveLocal")` dans `store.saveLocal()`. Dans `window.__nathflix`, ajouter `perf: () => perf.snapshot()`.
- [ ] **Étape 2 : trois lignes dans `perfReadout.paint()`**, derrière `settings.perfOverlay` (il l'est déjà) : `getState/min` par modèle (les 5 plus fréquents), `sync()` : appels/min et ms/min, `saveLocal/min`. Calculées sur la fenêtre depuis `perf.since` ; textes via `t("…")` + `frDictionary()`.
- [ ] **Étape 3 : le banc.** `node tools/perf-bench.mjs [--tv|--pc] [--runs 5] [--plugin path] [--out file] [--throttle 6]`. Playwright chargé par `await import("playwright")` après `npx --yes playwright@1 install chromium` documenté dans `tools/README.md` (pas de `package.json` à la racine, pas de dépendance ajoutée au dépôt). Pour chaque run :
  1. contexte Chromium, User-Agent desktop + ` nathflix-tv/<version>` en mode `--tv` (version lue dans `@version`), viewport 1280×720 en TV, 1600×900 en PC ;
  2. `localStorage` `nathflix.allowGuest = "1"` posé par `addInitScript` (la CI n'a pas de compte) ; **ne jamais poser `nathflix.lang`** (piège de T-039 : `langWatch` recharge la page) ;
  3. bridage CPU via CDP `Emulation.setCPUThrottlingRate({ rate })` ;
  4. le plugin servi par `page.route("https://web.stremio.com/__nathflix/plugin.js", …)` : corps = enveloppe `StremioEnhancedAPI` minimale du banc (`getSetting`/`saveSetting` en mémoire, `registerSettings`, `onSettingsSaved`, `showAlert`, `showPrompt`, `logger`) + source du plugin, exactement comme `modController.loadPlugin` (`app/src/preload/ui/mod/modController.ts:25-45`) ; injecté par `addInitScript` qui, sur `load`, ajoute `<script src="…/__nathflix/plugin.js">` (survit aux rechargements) ;
  5. `addInitScript` pose aussi `window.__bench = { longTasks: [], events: [], t0: performance.now() }` alimenté par deux `PerformanceObserver` (`longtask`, et `event` avec `durationThreshold: 16`) ;
  6. attendre `SPLASH_READY` (`document.querySelector('[class*="meta-item-container"]')`) → `pageReadyMs` ; `pluginLoadMs` = temps entre l'ajout du `<script>` et `window.__nathflix` défini (mesuré dans l'init script) ;
  7. repos 30 s sur l'Accueil, puis `perf.snapshot()` → `sync`, `getState`, `saveLocal`, `longTasks` depuis `__bench`, `elements = document.getElementsByTagName("*").length` ;
  8. 20 touches (`ArrowDown` ×2, puis `ArrowRight` ×10, `ArrowLeft` ×4, `ArrowUp` ×2, `ArrowDown` ×2) espacées de 400 ms → `keys` (durées Event Timing des `keydown`, médiane, p90, pire) ;
  9. parcours de contrôle (sert aux tâches suivantes) : `#/discover`, `#/library`, `#/calendar`, `#/settings`, fiche du premier titre de l'Accueil, puis retour `#/` ; le banc compte les erreurs console du plugin (`[Nathflix]`) et échoue s'il y en a.
- [ ] **Étape 4 : vérifier le banc.** `node tools/perf-bench.mjs --tv --runs 3` puis `--pc --runs 3` : JSON produit, aucune erreur, chiffres stables à ±20 % entre runs. Sinon, augmenter les runs ou le repos avant de continuer.
- [ ] **Étape 5 : mesure de référence** sur `main` (avant toute autre tâche), TV et PC, 5 runs. La coller dans la fiche T-041 et la garder comme référence pour toutes les suivantes.
- [ ] **Étape 6 : `node --check nathflix.plugin.js`, traductions (`fr-missing.mjs` si présent, sinon relire `frDictionary()`), commit :** `perf: measurement bench (tools/perf-bench.mjs) and core/sync counters in the performance readout`.

### T-042 — `coreMeta` piloté par les événements du cœur

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `core()` ≈ 138, `coreMeta` ≈ 1538 (`refresh`, `collect`), `syncCards()` ≈ 2009 (`coreMeta.refresh()`), `getInfo()` ≈ 1609, `viewerLibrary.refresh()` ≈ 10424 (`window.onCoreEvent`)

**Interfaces :**
- Produit : `coreEvents.on(model: string, fn: () => void): void` ; `coreEvents.available: boolean` ; `coreEvents.touch(models: string[])` (interne, appelé par la source d'événements). Utilisé ensuite par T-043 et T-044.
- Produit : `coreMeta.refresh(force?: boolean): Promise<void>` garde sa signature ; `coreMeta.collectKnown(model: string, state: object): boolean` (vrai si la forme attendue a été trouvée).

- [ ] **Étape 1 : trouver la source d'événements dans la page** (console de `web.stremio.com`, build courant) : essayer `window.services.core.transport.on("NewState", fn)` (stremio-core-web : `transport` est un émetteur, `NewState` porte la liste des modèles) ; sinon `window.core.transport` ; sinon envelopper `window.onCoreEvent` (le plugin l'appelle déjà avec `{ name: "NewState", args: [models] }`, ≈ 10425 : c'est donc le point d'entrée des événements du Worker vers l'UI). Noter dans la fiche laquelle existe et sa forme exacte.
- [ ] **Étape 2 : `coreEvents`** (nouvelle petite section juste avant « Metadata from the user's own addons » ≈ 1535) : `on(model, fn)` (listeners par modèle, `Set`), `touch(models)` (appelle les listeners, jamais plus d'une fois par frame et par modèle : regroupement par `requestAnimationFrame`), `install()` appelé depuis `core()` au premier cœur trouvé : branche la source de l'étape 1 ; `available = true` si une source a été branchée. Quand on enveloppe `window.onCoreEvent`, l'original est toujours appelé en premier et l'enveloppe ne lève jamais.
- [ ] **Étape 3 : formes connues des modèles** (vérifier dans la console, noter dans la fiche) : `board.catalogs[].content.content[]` (chaque `content` est un `Loadable` : `{ type: "Ready", content: [...] }`), `discover.catalog.content.content[]`, `search.catalogs[].content.content[]`, `library.catalog[]`, `continue_watching_preview.items[].metaItem` (ou `libraryItem`). `coreMeta.collectKnown(model, state)` lit ces chemins et appelle `remember(id, item)` pour chaque élément ayant `id`, `type`, `name` ; renvoie `false` si le chemin n'existe pas, auquel cas `collect(state, 0)` (récursif) reste le repli.
- [ ] **Étape 4 : `coreMeta.refresh` événementiel.** `coreEvents.on(model, …)` pour chacun des `MODELS` : marque `coreMeta.dirty.add(model)`. `refresh(force)` : si `coreEvents.available`, ne lit (`getState`) que les modèles de `dirty` **et** qui correspondent à la route courante (`board` sur `board`, `discover` sur `discover`, `search` sur `search`, `library` sur `library`, `continue_watching_preview` sur `board`), puis vide `dirty`. Si `!coreEvents.available` : comportement actuel (5 modèles, 4 s). Sur `hashchange` : `dirty` reçoit le modèle de la nouvelle route. Sur TV et en mode léger (`IN_TV || light()`), `MODELS` se limite au modèle de la route courante.
- [ ] **Étape 5 : mesure.** Banc TV + PC : `getState` par modèle sur 30 s de repos (référence : ≈ 5 toutes les 4 s = 37 en 30 s) et pendant le parcours. Attendu : ≤ 1 par modèle par changement d'état réel. Vérifier visuellement que les badges IMDb, les cartes de survol (PC) et le billboard ont toujours leurs données (captures avant/après).
- [ ] **Étape 6 : `node --check`, commit :** `perf: coreMeta reads a model only when the core says it changed (coreEvents), known paths instead of a recursive walk`.

### T-043 — `endsAt` et `calendarFill` mémorisés

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `endsAt` ≈ 12616, `calendarFill` ≈ 12548, `updateRoute` (section « Route + header state » ≈ 947)

**Interfaces :**
- Consomme : `coreEvents.on("meta_details" | "calendar", fn)` (T-042).
- Produit : `endsAt.invalidate(): void`, `calendarFill.invalidate(): void`.

- [ ] **Étape 1 : `endsAt`.** Mémo `this.memo = { id: null, text: "" }`. `ensure()` ne fait `getState("meta_details")` que si `t.id !== this.memo.id` ou si `this.dirty` (posé par `coreEvents.on("meta_details")` et par `invalidate()`). Sinon elle pose `memo.text` dans la puce (en comparant avant d'écrire : règle de l'observateur `mo`). `invalidate()` est appelée dans `updateRoute` quand la route précédente était `player` (la progression a changé).
- [ ] **Étape 2 : `calendarFill`.** Même schéma : `this.key = location.hash` (le mois est dans la route) + `dirty` posé par `coreEvents.on("calendar")` et `invalidate()` (retour du lecteur). `getState("calendar")` seulement si la clé change ou `dirty`. `episodes.refresh(true)` garde sa propre limite de 10 min.
- [ ] **Étape 3 : mesure.** Banc : fiche d'un film ouverte 30 s au repos → `getState:meta_details` passe de « un par sync » à 1 ; calendrier idem pour `getState:calendar`. Captures : « Se termine à » et les épisodes du calendrier identiques.
- [ ] **Étape 4 : commit :** `perf: "Ends at" and the calendar read the core once per title or month`.

### T-044 — `viewerLibrary.filter` allégé

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `viewerLibrary` ≈ 10209 : `owners()`, `mine()`, `patch()` ≈ 10325, `filter()` ≈ 10365, `unmark()` ≈ 10405

**Interfaces :**
- Produit : `viewerLibrary.filter(model: string, state: object): void` (signature inchangée) ; `viewerLibrary.unmarkKnown(model, state, all): boolean`.

- [ ] **Étape 1 : une lecture par appel.** Au début de `filter()` : `const me = viewers.current().id; const owners = this.owners(); const base = this.base();` et une fermeture `mine = (id) => !on || (owners[id] || [base]).includes(me)` passée partout (plus de `this.mine(id)` par titre, qui relisait `store` et `viewers.list()` à chaque fois).
- [ ] **Étape 2 : chemins connus.** `unmarkKnown(model, state, all)` parcourt les mêmes chemins que `coreMeta.collectKnown` (T-042) pour `board`, `discover`, `search`, `local_search`, et `metaItem.content.content` + `.videos[]` pour `meta_details` ; renvoie `false` si la forme n'est pas trouvée → `unmark(state, 0, all)` récursif reste le repli (point de vigilance 4).
- [ ] **Étape 3 : court-circuit.** Quand `this.on()` est faux (un seul profil, aucun code), `filter` sort immédiatement (déjà le cas via `patch`, vérifier). Quand il est vrai et que ni `viewers`, ni `libraryOwners`, ni `played:*` n'ont changé depuis le dernier appel sur le même modèle (`store` : garder un `stamp` incrémenté dans `store.set`/`put`), et que l'état vient d'un `getState` déclenché par le plugin lui-même (pas par Stremio), ne pas refaire `unmark`. Si ce court-circuit s'avère fragile, ne garder que les étapes 1 et 2 et le noter.
- [ ] **Étape 4 : mesure.** Banc PC avec deux profils (poser `viewers` dans `nathflix.data` via `localStorage` dans l'init script : prendre un export réel) : durée totale de `filter` (ajouter `perf.time("filter", …)` autour) sur le parcours, avant/après. Vérifier à la main : Ma liste, Continuer de regarder, calendrier et notifications d'un profil n'affichent pas ceux de l'autre.
- [ ] **Étape 5 : commit :** `perf: per-viewer library filter reads the viewers once per call and walks known model paths`.

### T-045 — `store.saveLocal` différé, `stats` toutes les 60 s

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `store` ≈ 10016 (`set`, `put`, `saveLocal`, `flush`), `stats.add()` ≈ 11996 (`unsaved >= 30`), `onLeave()` ≈ 15250, enregistrement des événements de page dans `start()`

**Interfaces :**
- Produit : `store.saveLocal(): void` (planifie), `store.saveLocalNow(): void` (écrit tout de suite).

- [ ] **Étape 1 :** `saveLocal()` → `clearTimeout(this.localTimer); this.localTimer = setTimeout(() => this.saveLocalNow(), 1000)`. `saveLocalNow()` = l'ancien corps (`localStorage.setItem(DATA_LOCAL, JSON.stringify(this.data))`) + `clearTimeout`. `flush()` appelle `saveLocalNow()` d'abord (le disque et le local restent cohérents).
- [ ] **Étape 2 : filets.** `saveLocalNow()` sur `visibilitychange` (quand `document.hidden`), `pagehide`, et dans `onLeave()` (qui appelle déjà `store.flush()`). Vérifier que `onLeave` est bien branché sur `beforeunload`/`pagehide` dans `start()` ; sinon l'ajouter.
- [ ] **Étape 3 : `stats`.** `if (this.unsaved >= 60) this.flush()` ; `flush()` aussi sur `pause` et `ended` de la vidéo (le `onEvent` reçoit déjà `pause`), et dans `onLeave`.
- [ ] **Étape 4 : mesure.** Banc : `saveLocal` sur 30 s de repos et pendant le parcours (référence : une écriture par `store.set`, dont `markPlayed`, `cloudSync`, drapeaux). Test de perte : changer de profil puis fermer l'onglet dans la seconde (Playwright `page.close()` après 200 ms) → le profil courant est bien celui relu au lancement suivant.
- [ ] **Étape 5 : commit :** `perf: Nathflix's saved data is written to localStorage at most once a second, watch stats once a minute`.

### T-046 — `sync()` ciblé par zone

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `scheduleSync()` ≈ 7547, `sync()` ≈ 7556, observateur `mo` ≈ 7573, `start()` (abonnement `hashchange`), tous les appels `scheduleSync()` existants (restent des syncs complets)

**Interfaces :**
- Produit : `scheduleSync(zones?: Set<string>): void` — sans argument : sync complet (comportement actuel) ; `sync(zones?: Set<string>)`. Zones : `"rows"`, `"detail"`, `"calendar"`, `"nav"`, `"player"`, `"other"`.
- Produit : `zoneOf(target: Node): string` — d'après `target.closest(...)` : `rows` pour `[class*="board-content"], [class*="meta-row"], [class*="continue-watching-row"], [class*="discover-content"], [class*="library-content"], [class*="search-content"]` ; `detail` pour `[class*="metadetails-container"]` ; `calendar` pour `[class*="calendar"]` ; `nav` pour `nav[class*="horizontal-nav-bar"]` ; `player` pour `[class*="player-container"]` ; sinon `other`.

- [ ] **Étape 1 : classer les étapes de `sync()`.** Toujours exécutées (bon marché) : `updateRoute`, `brandTitle`, `addonTools.ensure`, `communitySync`, `perfReadout.ensure`, `signInGate.check`, `langWatch.check`, `profileDefaults.watch`, `cloudSync.watch`, `catalogChoices.watch`, `restrict.guard`, et sur desktop les `ensure()` du lecteur (ils testent la route d'abord). Par zone : `rows` et `other` → `hero.ensure`, `mobileChips.ensure`, `filterRows`, `homeArrange.apply`, `updateAllArrows`, `syncCards`, `librarySort.ensure`, `libraryWatched.ensure`, `applyWatchedTitles` ; `detail` et `other` → `episodePlay.ensure`, `reviews.ensure`, `hideButton.ensure`, `endsAt.ensure`, `streamTools.ensure` ; `calendar` et `other` → `calendarFill.ensure`. Une étape dont on n'est pas sûr va dans « toujours » : le coût d'une étape en trop est faible, celui d'une étape manquante est un bug.
- [ ] **Étape 2 : `mo`** accumule `zoneOf(r.target)` dans un `Set` (module-level `pendingZones`) et appelle `scheduleSync(pendingZones)` ; `scheduleSync()` fusionne les zones en attente ; un appel sans argument force le complet. `sync(zones)` saute les étapes dont aucune zone ne correspond ; `zones` vide ou contenant `other` = complet.
- [ ] **Étape 3 : filets.** Sync complet sur `hashchange` (déjà via `updateRoute` ? vérifier, sinon l'ajouter dans `start()`) et par `setInterval(() => scheduleSync(), 5000)` hors lecteur (`currentRoute() !== "player"`).
- [ ] **Étape 4 : mesure.** Banc TV et PC : `sync` ms/30 s au repos et sur le parcours ; nombre d'appels. Attendu sur TV : la moitié ou moins au repos. Parcours de contrôle sans erreur ; captures identiques (dont « Lancer l'épisode », flèches des rangées, badges, chips mobiles).
- [ ] **Étape 5 : commit :** `perf: sync() runs only the steps for the part of the page that changed; a full pass every 5 s and on route changes`.

### T-047 — Thème TV et mode léger : liste noire d'effets, classe `html.dx-light`

**Fichiers :**
- Modifier : `nathflix.plugin.js` — là où `light()` est évalué (`applySettings`/`loadSettings` ≈ 538–600, et `onSettingsSaved`) ; `sharpen.prune` `DEVICE_CLASSES` ≈ 15000+ (ne **pas** y ajouter `dx-light` : le mode peut changer sans rechargement)
- Modifier : `nathflix.theme.css` — bloc « WINDOWS AND TV » (≈ 4476–4660) : nouveau sous-bloc « TV AND LIGHT MODE: no glass, no blur, no soft shadows » ; puis `node tools/bundle-theme.mjs`

**Interfaces :**
- Produit : classe `html.dx-light` posée quand `light()` est vrai (et retirée sinon), mise à jour à chaque sauvegarde des réglages.

- [ ] **Étape 1 : la classe.** `document.documentElement.classList.toggle("dx-light", light())` dans la fonction qui applique les réglages (même endroit que `dx-brand`/`applyAccent`), appelée au chargement et dans `onSettingsSaved`.
- [ ] **Étape 2 : le sous-bloc du thème**, sélecteurs préfixés `.dx-tv …, .dx-light …` :
  - `backdrop-filter: none !important` + fond semi-opaque équivalent (`background: rgba(0,0,0,.78)` ou la couleur du composant à 90 %) pour chacune des 13 déclarations actives (lignes ≈ 744, 760, 799, 874, 1139, 1360, 1910, 2051, 2103, 2616, 2653, 3621, 4028 ; celles déjà à `none` : 3036, 3380, 3395, 4487, 5024) ;
  - `filter: none !important` et une image statique assombrie (dégradé) à la place des flous d'images (≈ 1510, 1525, 3510 et les autres `filter: blur(` : `grep -n "filter: *blur" nathflix.theme.css`) ;
  - `box-shadow: none !important` et `text-shadow: none !important` sur les tuiles (`[class*="meta-item-container"]`, `[class*="poster-container"]`), les boutons et la barre du haut ; garder l'anneau de focus `inset` des tuiles TV (il est en `box-shadow`, le réécrire en `outline` ou le laisser : vérifier qu'il reste) ;
  - `transition-property` limité à `opacity, transform` et `transition-duration: 150ms` au plus sur `#app *` sous ces classes ; `animation: none` sur ce qui n'est pas la roue de chargement.
- [ ] **Étape 3 : `node tools/bundle-theme.mjs`**, puis captures PC/téléphone (sans `dx-light`) identiques à la référence ; captures TV et mode léger : les différences se limitent au flou, aux ombres et aux transitions.
- [ ] **Étape 4 : mesure.** Banc TV : tâches longues et `keys` sur l'Accueil ; sur la Fire TV, `perfOverlay` avant/après (médiane des touches). Noter aussi `chrome://inspect` → Performance → part de « Rendering/Painting » si disponible.
- [ ] **Étape 5 : commit :** `perf: TV and light mode drop blurred glass, blurs, soft shadows and long transitions (html.dx-light)`.

### T-048 — Polices sans `@import`

**Fichiers :**
- Modifier : `tools/bundle-theme.mjs` (retirer la ligne `@import url("https://fonts.googleapis.com/…")` du CSS avant de l'intégrer : le fichier `nathflix.theme.css` garde son `@import` pour qui l'installe seul dans Stremio Enhanced)
- Modifier : `nathflix.plugin.js` — nouvelle fonction `ensureFonts()` appelée dans `start()` avant l'injection du thème (section « Theme: built-in copy » ≈ 14953)

**Interfaces :**
- Produit : `ensureFonts(): void` — ajoute dans `<head>`, une seule fois : `<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>`, puis `<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@300..800&family=Bebas+Neue&display=swap">` (`display=optional` quand `IN_TV`, pour ne jamais bloquer ni faire sauter le texte ; `swap` ailleurs, identique à aujourd'hui).

- [ ] **Étape 1 :** `bundle-theme.mjs` : `css.replace(/^@import url\("https:\/\/fonts\.googleapis\.com[^\n]*\n/m, "")` avant `JSON.stringify` ; vérifier que la chaîne intégrée ne contient plus `@import`.
- [ ] **Étape 2 :** `ensureFonts()` + appel ; sur TV, aussi `<link rel="preload" as="style">` de la même adresse.
- [ ] **Étape 3 : mesure.** Banc PC : `pageReadyMs` et moment du premier rendu du texte (Playwright `page.evaluate(() => document.fonts.ready)` chronométré) ; `pluginLoadMs`. Captures identiques (polices chargées).
- [ ] **Étape 4 : commit :** `perf: fonts linked by the plugin instead of a blocking @import in the theme`.

### T-049 — Démarrage Android

**Fichiers :**
- Modifier : `tv/app/src/main/java/com/lavagnou/nathflix/MainActivity.kt` — `onCreate` (≈ 50–160), nouvelle fonction `whenPageReady(block: () -> Unit)` à côté de `pageReady()` ≈ 268
- Optionnel : créer `NathflixApp.kt` (`Application`) + `android:name=".NathflixApp"` dans `AndroidManifest.xml`
- Optionnel : `tv/app/src/main/res/raw/intro.mp4` ré-encodé en 1280×720 (commande dans `tools/README.md`, `render_intro.py`)

- [ ] **Étape 1 : mesure de référence sur l'appareil** (build Nathflix Dev, Fire TV) : `adb logcat -s nathflix` avec un log `page ready in N ms` ajouté dans `MainActivity` (de `onCreate` au premier `pageReady == true`) ; 5 lancements à chaud. Noter aussi `adb shell dumpsys meminfo com.lavagnou.nathflix.dev` au repos sur l'Accueil.
- [ ] **Étape 2 : priorité du renderer.** Après `web = WebView(this)` : `if (Build.VERSION.SDK_INT >= 26) web.setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)`.
- [ ] **Étape 3 : mises à jour après la page.** Remplacer les appels `Updates.checkPlugin(this)` / `Updates.checkApp(this)` de `onCreate` par `whenPageReady { web.postDelayed({ Updates.checkPlugin(this); Updates.checkApp(this) }, 5000) }`. `whenPageReady` : sonde `pageReady` toutes les 1 s, 60 fois au plus, puis exécute le bloc une seule fois (et aussi s'il n'y a pas de page prête au bout des 60 s : les mises à jour doivent toujours finir par être vérifiées).
- [ ] **Étape 4 : préchauffage (seulement si mesuré).** `NathflixApp.onCreate` : sur TV uniquement (`!Device.isMobile`), `WebView(applicationContext)` créé puis mis de côté (démarre le processus du renderer et charge la bibliothèque WebView avant `MainActivity`). Garder si `page ready` gagne ≥ 200 ms à chaud sur 5 lancements ; sinon retirer et le noter.
- [ ] **Étape 5 : intro 720p (seulement si mesuré).** Encoder `intro.mp4` en 1280×720 ; garder si `page ready` gagne ≥ 150 ms ou si logcat montrait un `MediaCodec` en concurrence avec la WebView. Sinon retirer.
- [ ] **Étape 6 : CI `android-dev.yml` verte**, mesure après sur l'appareil, commit :** `perf: Android start-up: renderer priority, update checks after the page, (WebView warm-up / 720p intro if kept)`.

### T-050 — Index des épisodes allégé sur TV et en mode léger

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `episodes` ≈ 12366 : `librarySeries()` (`.slice(0, 80)`), `refresh()` (3 `worker()`), `get()` ; premier appel de `episodes.refresh()` (chercher `episodes.refresh(` : `airing`, badges, `calendarFill`)

- [ ] **Étape 1 : constantes.** `const lightIndex = () => IN_TV || light();` ; `librarySeries()` : `.slice(0, lightIndex() ? 30 : 80)` ; `refresh()` : `lightIndex() ? [worker()] : [worker(), worker(), worker()]`.
- [ ] **Étape 2 : départ différé.** Sur `lightIndex()`, le premier `refresh()` (hors `force`) n'est accepté que 20 s après le premier passage sur `board` (`episodes.notBefore = Date.now() + 20000` posé dans `updateRoute` au premier `board`) ; `refresh(true)` (calendrier) n'attend pas.
- [ ] **Étape 3 : traitement en idle.** Dans `get()`, l'analyse de la réponse (`.map(...)` des `videos`, ≈ 12450) passe dans un `await idle()` (`requestIdleCallback` avec `timeout: 1000`, sinon `setTimeout 0`) quand `lightIndex()`.
- [ ] **Étape 4 : mesure.** Banc TV avec une bibliothèque de 80 séries (export réel dans `localStorage` `library` par l'init script) : tâches longues pendant les 60 premières secondes, `pageReadyMs`. Vérifier : badges « Nouvel épisode » et rangée « Cette semaine » toujours là (avec 30 séries sur TV).
- [ ] **Étape 5 : commit :** `perf: lighter episode index on TV and in light mode (30 shows, one request at a time, 20 s after Home, parsed when idle)`.

### T-051 — Réglage expérimental « Rendu léger sur TV » (WebView 720p agrandie)

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `DEFAULTS`, `SCHEMA`, `SETTING_GROUPS` (groupe « Performance » ≈ 514) : réglage `tvLowRes` (`auto` | `on` | `off`, défaut `auto`, `tvOnly: true`, `androidOnly: true`), libellés `t("Lighter rendering on TV")`, description « … takes effect at the next start » + `frDictionary()`
- Modifier : `tv/app/src/main/java/com/lavagnou/nathflix/MainActivity.kt` — `onCreate` après `root.addView(web, …)` ; lecture du réglage comme `Intro.kt` lit `accent` (SharedPreferences `plugin-settings`, clé `nathflix`, JSON)
- Modifier : `tv/app/src/main/assets/nathflix-tv.js` — rien (le viewport reste 1280 px CSS : la WebView fait 1280 px physiques, donc DPR 1)

- [ ] **Étape 1 : le réglage** (plugin) et sa lecture côté Kotlin : `fun lowRes(): Boolean` = `on`, ou `auto` et appareil modeste (`Runtime.getRuntime().availableProcessors() <= 4 || ActivityManager.isLowRamDevice || memoryClass <= 2 Go`), **TV seulement** (`!mobile`).
- [ ] **Étape 2 : la WebView réduite.** Si `lowRes()` et que l'écran fait plus de 1280 px de large : `val scale = displayWidth / 1280f` ; la WebView reçoit `LayoutParams(ceil(displayWidth / scale), ceil(displayHeight / scale))`, `web.pivotX = 0f; web.pivotY = 0f; web.scaleX = scale; web.scaleY = scale`. (Sur une Fire TV 1080p : 1280×720 physiques, agrandie ×1,5.)
- [ ] **Étape 3 : vérifier sur l'appareil** les six points de vigilance n° 6 ; `window.innerWidth === 1280`, `devicePixelRatio === 1` ; netteté acceptable (le texte est un peu plus doux : c'est le compromis du réglage, dit dans sa description).
- [ ] **Étape 4 : mesure** sur la Fire TV : `perfOverlay` (médiane et pire des touches, tâches longues) au repos et en parcourant 10 rangées, avant/après. Garder le réglage en `auto` seulement si la médiane baisse d'au moins 20 % ; sinon défaut `off` (le réglage reste disponible).
- [ ] **Étape 5 : CI verte, commit :** `perf: experimental "Lighter rendering on TV" (720p WebView scaled up)`.

### T-0xx — Release 0.6.0

Après T-042 à T-051 : `@version 0.6.0`, section `## 0.6.0` du CHANGELOG (entrées orientées utilisateur, « Nathflix TV: » pour la TV), `node tools/bundle-theme.mjs`, commit, release (`release.yml`). Fiche de release comme T-040. Mesures finales du banc et de la Fire TV dans la fiche, comparées à la référence de T-041.

---

## Phase 2 — chargement et navigation (release 0.7.0 : nouvelle APK et nouvelle app)

### T-052 — Plugin chargé par adresse sur Android (cache de compilation)

**Fichiers :**
- Modifier : `tv/app/src/main/java/com/lavagnou/nathflix/MainActivity.kt` — `webViewClient.shouldInterceptRequest`
- Modifier : `tv/app/src/main/java/com/lavagnou/nathflix/Bridge.kt` — nouvelle méthode `pluginUrl(token)` (4 points : annotation, token, wrapper `native`, ProGuard déjà couvert)
- Modifier : `tv/app/src/main/java/com/lavagnou/nathflix/Updates.kt` — `pluginSource` inchangé ; nouvelle `pluginVersion(context)` (lit `@version`)
- Modifier : `tv/app/src/main/assets/nathflix-tv.js` — `loadPlugin()` ≈ 888

**Interfaces :**
- Produit (bridge) : `pluginUrl(token: String): String` → `"https://web.stremio.com/__nathflix/plugin.js?v=<version>"` (vide si le token est faux).
- Produit (réponse interceptée) : corps = `window.__nathflixPlugin = function (StremioEnhancedAPI) {\n<source Updates.forTv>\n};` ; en-têtes `Content-Type: application/javascript; charset=utf-8`, `Cache-Control: public, max-age=31536000, immutable`, `ETag: "<version>"`, `Last-Modified` fixe (date de build).
- Produit (`nathflix-tv.js`) : `loadPlugin()` : si `bridge.pluginUrl` existe → `<script src>` puis, sur `load`, `window.__nathflixPlugin(api)` avec le même objet `StremioEnhancedAPI` qu'aujourd'hui (construit en JS, plus dans une chaîne) ; sinon chemin texte actuel.

- [ ] **Étape 1 : mesure de référence du coût d'analyse** sur la Fire TV : dans `loadPlugin`, `performance.now()` avant/après `document.body.append(script)` → `console.log("[Nathflix TV] plugin evaluated in N ms")` (lu dans `chrome://inspect`) ; aussi dans Performance : la tâche « Compile Script » / « Evaluate Script ». 5 lancements à chaud.
- [ ] **Étape 2 : implémentation** comme décrit dans les interfaces. `shouldInterceptRequest` ne répond que pour `request.url.host == "web.stremio.com" && path == "/__nathflix/plugin.js"` ; corps construit une fois par lancement (mis en cache en mémoire). `recoverFromCrash` : rien à faire (la page recharge et redemande l'adresse).
- [ ] **Étape 3 : mesure après.** Même log, 5 lancements à chaud (le cache de code V8 ne se crée qu'à partir du deuxième ou troisième chargement). Attendu : « evaluated in » divisé par 2 ou plus. **Si aucun gain** (les réponses de `shouldInterceptRequest` ne passent pas par le cache HTTP, donc pas de cache de code) : variante B — servir le plugin par un petit serveur local (NanoHTTPD, déjà dans l'APK ; `127.0.0.1:<port>/nathflix.plugin.js?v=…`, `mixedContentMode` est déjà `ALWAYS_ALLOW`), mêmes en-têtes ; `pluginUrl` renvoie cette adresse. Mesurer de nouveau ; garder la variante qui gagne, abandonner la tâche si aucune ne gagne (le noter).
- [ ] **Étape 4 : compatibilité.** Vieille APK (0.5.2) + plugin récent : inchangé (le plugin ne dépend pas de ce chemin). Nouvelle APK + plugin texte téléchargé par `Updates.checkPlugin` : `pluginUrl` porte la version du plugin servi (celui de `filesDir` s'il est plus récent) → nouvelle adresse, pas de cache périmé. Tester les deux.
- [ ] **Étape 5 : CI verte, commit :** `perf: Nathflix TV loads the plugin from an address (browser cache and V8 code cache) instead of inline text`.

### T-053 — Plugin minifié pour l'APK et l'app

**Fichiers :**
- Créer : `tools/minify-plugin.mjs` — `node tools/minify-plugin.mjs --out <fichier> [--tv]` : lit `nathflix.plugin.js`, en `--tv` applique les deux mêmes regex que `Updates.forTv()` (intro et shaders vidés), minifie avec `terser` (`npx --yes terser@5`, ou `await import("terser")` après `npx` ; pas de dépendance ajoutée au dépôt), `format: { comments: /@version|@name|@description|@updateUrl|Copyright/ }`, `compress: { passes: 2 }`, `mangle: true` ; **re-préfixe l'en-tête complet** (bloc de commentaires jusqu'à la première ligne vide) pour `Updates.checkPlugin` et Stremio Enhanced.
- Modifier : `tv/app/build.gradle.kts` — tâche `copyPlugin` ≈ 87 : si `node` est disponible, lance le script en `--tv` vers `generated/nathflixAssets/nathflix.plugin.js`, sinon copie telle quelle (log) ; `Updates.forTv` reste (sans effet sur un plugin déjà vidé, nécessaire pour un plugin texte téléchargé).
- Modifier : `app/copyComponents.js` — copie minifiée (sans `--tv`) vers `static/nathflix.plugin.js`.
- Modifier : `.github/workflows/cross-platform.yml` — un job (ou une étape) qui installe la version minifiée à la place du fichier de `main` et relance `smoke-test.mjs`.

- [ ] **Étape 1 : risques à vérifier avant** : `grep -n "\.name ===\|\.toString()\|Function(" nathflix.plugin.js` (rien ne doit dépendre d'un nom de fonction ou du texte source) ; `sharpen.stamp` hache la chaîne du thème (inchangée par la minification) ; `t()` et `frDictionary()` sont des littéraux (inchangés).
- [ ] **Étape 2 :** script, Gradle, `copyComponents.js`, workflow. Taille attendue : ≈ 1 Mo → ≈ 550 Ko (TV), 1,55 Mo → ≈ 1,1 Mo (desktop).
- [ ] **Étape 3 : vérifications.** `node --check` sur la sortie ; `node tools/smoke-test.mjs` avec le plugin minifié ; banc TV avec `--plugin <minifié>` : `pluginLoadMs` avant/après ; sur la Fire TV : « evaluated in » avant/après.
- [ ] **Étape 4 : CI verte (cross-platform, android-dev), commit :** `perf: minified plugin built into the APK and the desktop app (source on main stays readable)`.

### T-054 — Accueil TV chargé à l'approche

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `homeRows.preload()` ≈ 974, `keyNav.toRow()` ≈ 3400, `keyNav.fromCard()` ≈ 3184 (branche `dy`), `homeArrange.apply()` (appel `homeRows.preload(…, true)`)

**Interfaces :**
- Produit : `homeRows.loadRows(start: number, end: number): Promise<void>` (dispatch `CatalogsWithExtra`/`LoadRange` pour `[start, end)` seulement) ; `homeRows.loaded(row: Element): boolean` (la rangée a des cartes) ; `keyNav.toRowWhenLoaded(row, x, e)`.

- [ ] **Étape 1 :** `preload()` ne charge plus tout sur TV : seulement avec `force` (ordre `homeArrange` actif). `loadRows(i, i + 3)` appelée par `keyNav` quand la rangée visée (Bas/Haut) n'est pas chargée, et pour les deux suivantes.
- [ ] **Étape 2 :** `toRowWhenLoaded` : si `loaded(row)` → `toRow` ; sinon `loadRows`, puis `MutationObserver` (childList, sur la rangée) ou sondage 50 ms, 2 s au plus, puis `toRow` ; pendant l'attente, les touches répétées sont ignorées (`e.preventDefault()`).
- [ ] **Étape 3 : mesure.** Banc TV : `elements` et `getState:board` au repos après l'arrivée sur l'Accueil ; sur la Fire TV : `dumpsys meminfo` et `perfOverlay` ; descente de 10 rangées à la télécommande sans blocage ni saut de focus.
- [ ] **Étape 4 : commit :** `perf: Home's rows on TV load as the remote approaches them, not all at once`.

### T-055 — `keyNav` sans remesure

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `keyNav` ≈ 2793 : `key()` (appel `reach()`), `inView()` ≈ 2967, `reach()` ≈ 3009, `rows()` ≈ 3056, `rest()`, `view()`, `nearest()`, `enter()`/`leave()`

**Interfaces :**
- Produit : `keyNav.rect(n: Element): DOMRect` (cache par frame : `WeakMap` vidé à chaque `requestAnimationFrame`) ; `keyNav.rowIO: IntersectionObserver` sur les rangées de l'Accueil (`visibleRows: Set<Element>`) ; `keyNav.rowsCache: { route: string, rows: Element[] } | null` invalidé par un `MutationObserver` `childList` (sans `subtree`) sur le conteneur des rangées et sur `hashchange` ; `keyNav.reached: Set<string>` des routes où `reach()` a déjà tourné (relancé sur mutation des rangées seulement).

- [ ] **Étape 1 :** `rect()` et remplacement de tous les `getBoundingClientRect()` de `keyNav` par `this.rect(n)`.
- [ ] **Étape 2 :** `rows()` mis en cache + invalidation ; `reach()` par route + invalidation.
- [ ] **Étape 3 :** `inView()` : `away` = rangées hors de `visibleRows` (plus de mesure par rangée) ; Haut/Bas dans `fromCard` = rangée adjacente dans `rows()` (déjà le cas) ; Gauche/Droite = index dans `cards(track)` (déjà le cas) : vérifier qu'aucun `getBoundingClientRect` n'y reste hors `rect()`.
- [ ] **Étape 4 : mesure.** Banc TV : `keys` (médiane, p90, pire) sur les 20 touches ; Fire TV : `perfOverlay`. Parcours de contrôle à la télécommande (13 flèches de T-039) identique.
- [ ] **Étape 5 : commit :** `perf: remote navigation measures the page once per frame, watches rows with IntersectionObserver, and reaches elements once per route`.

### T-056 — Desktop : iframe de bande-annonce unique, observateurs du preload

**Fichiers :**
- Modifier : `nathflix.plugin.js` — `trailer.start()` ≈ 12776, `trailer.stop()`
- Modifier : `app/src/preload/ui/titleBar.ts` ≈ 115 (`followPlayer`), `app/src/preload/index.ts` ≈ 170 (`addTitleBar`)

- [ ] **Étape 1 : une seule iframe.** `trailer.frame` créée une fois, réutilisée (`frame.src = …` ; `stop()` met `src = "about:blank"` et la détache). Le délai (`trailerDelay`, minimum 600 ms) ne change pas : aucun changement de comportement.
- [ ] **Étape 2 : `followPlayer`** : observer `childList` **sans** `subtree` sur le conteneur de route de Stremio (trouver en DevTools le parent direct de `[class*="player-container"]`, noter son sélecteur `[class*="…"]` dans la fiche) + `hashchange` ; `addTitleBar` : idem sur `document.body` sans `subtree`, + `hashchange`. Si l'élément attendu est plus profond, garder `subtree` mais limiter à l'ancêtre le plus proche.
- [ ] **Étape 3 : mesure.** Banc PC : tâches longues et `sync` au repos (les observateurs du preload ne sont pas dans le banc : mesurer dans l'app, `npm run dev`, DevTools → Performance, 30 s de repos sur l'Accueil, avant/après). `npx tsc --noEmit`.
- [ ] **Étape 4 : commit :** `perf: one reused trailer iframe; desktop title bar observers no longer watch the whole page`.

### T-057 — Desktop : fenêtre transparente seulement pendant mpv (étude, puis décision)

**Fichiers :**
- Lire : `app/src/main.ts` ≈ 36–100 (`transparencyEnabled`, `createWindow`), `app/src/core/MpvShell.ts`
- À proposer avant d'implémenter.

- [ ] **Étape 1 : mesure** sur Windows : Gestionnaire des tâches (GPU %, et « Moteur 3D » du processus) et DevTools Performance au repos sur l'Accueil, avec `transparent: true` (défaut mpv) puis `false` (mpv désactivé par son réglage, `MpvShell.setupSetting`). Noter l'écart.
- [ ] **Étape 2 : si l'écart est net (≥ 2× au repos)**, proposer dans la fiche une des deux voies, avec son coût : (a) fenêtre principale opaque et seconde `BrowserWindow` transparente créée par `MpvShell` seulement pendant la lecture (la page y est déplacée, ou mpv y est attaché par `--wid`) ; (b) réglage « Fenêtre classique » (mpv coupé, fenêtre opaque avec cadre) mis en avant dans les Réglages. Attendre l'accord de l'utilisateur avant la voie (a).

### T-0xx — Release 0.7.0

Comme pour 0.6.0, après T-052 à T-056 (T-057 selon décision). Nouvelle APK obligatoire (bridge) et nouvelle app.

---

## Phase 3 — structure (à proposer avant de commencer)

### T-058 — Thème découpé par appareil à la construction

**Fichiers :** `tools/bundle-theme.mjs` (trois variantes `tv` / `mobile` / `desktop` : `prune` fait à la construction avec la liste `DEVICE_CLASSES`, et les `:hover` retirés de la variante TV), `nathflix.plugin.js` (trois chaînes `/*@bundled-theme:tv*/…` etc., ou une seule chaîne + un index des plages ; `sharpen.prune` conservé pour les règles d'`EXTRAS_CSS` ; `sharpen.rules` conservé à l'exécution), `tools/minify-plugin.mjs` (l'APK n'embarque que la variante TV + mobile), cache du CSS affûté hors `localStorage` (`CacheStorage`/`caches.open("nathflix")` ou IndexedDB, lu en asynchrone après l'injection de la version brute).

- [ ] Mesure avant : `pluginLoadMs`, temps de `sharpen.run()` (ajouter `perf.time("sharpen", …)`), taille de `nathflix.sharp.*` et temps de `JSON.parse` au démarrage (TV).
- [ ] Proposer le découpage dans la fiche, implémenter après accord, mesurer après, captures identiques sur les trois appareils.

### T-059 — Ordonnanceur à budget (domaine « rangées » d'abord)

**Fichiers :** `nathflix.plugin.js` — nouvelle section `scheduler` (file de tâches `{ pri, fn, key }` dédupliquées par `key`, exécutée dans `requestIdleCallback` avec un budget de 8 ms par passage sur TV / 16 ms ailleurs, `timeout: 500`) ; `syncCards` découpé par rangée (une tâche par rangée, les rangées visibles de `keyNav.visibleRows` (T-055) d'abord) ; `decorateLater` fusionné dans l'ordonnanceur ; observateur `mo` restreint au conteneur des rangées pour la zone `rows` (T-046) ; puis, dans des tâches suivantes, les domaines « fiche », « lecteur », « barre », « profils ».

- [ ] Mesure avant/après : tâches longues au repos et pendant le défilement de 10 rangées (banc TV, Fire TV).
- [ ] Proposer dans la fiche, implémenter après accord, une tâche par domaine.

### T-060 — Étude : Stremio Web embarqué dans l'APK

- [ ] Étude seulement, sans implémentation : faisabilité juridique (licence de Stremio Web), technique (`WebViewAssetLoader` sur `https://appassets.androidplatform.net/` ou interception de `web.stremio.com`, origine et `localStorage` existants, mises à jour du build Stremio, bundle de 10 Mo + WASM de 5,7 Mo dans l'APK), gain attendu (plus de téléchargement de 3 Mo gzip au démarrage à froid ; le temps d'analyse reste). Recommandation écrite dans la fiche ; décision de l'utilisateur.

---

## Critères de succès (Fire TV 4K, build release, mesurés avec `perfOverlay` et le banc)

| Mesure | Cible |
|---|---|
| Réponse médiane aux touches | < 50 ms |
| Pire réponse sur 20 touches | < 150 ms |
| Tâches longues de Nathflix au repos sur l'Accueil (30 s) | aucune > 50 ms |
| Accueil utilisable après l'intro, à chaud | < 6 s |
| Redémarrages du renderer en 30 min de navigation | 0 |
| `getState` au repos (30 s) | ≤ 1 par modèle par changement réel |
| `sync()` au repos (30 s), TV | ≤ la moitié de la référence T-041 |
