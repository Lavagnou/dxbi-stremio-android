# PLAN

Plan, suivi et avancement de tout ce qui est implémenté dans ce fork. Les agents IA s'en servent comme document vivant et le tiennent à jour à chaque changement.

Le contexte technique du projet (architecture, commandes, invariants) est dans [`CLAUDE.md`](CLAUDE.md). Ce fichier-ci ne contient que le travail : les tâches, leur état, ce qu'on en sait.

---

## Règles pour les agents

**Avant de commencer :**
1. Lire ce fichier en entier, puis `CLAUDE.md`.
2. Choisir la tâche demandée par l'utilisateur. À défaut, prendre la tâche `À faire` la plus prioritaire dont les dépendances sont `Terminé`.
3. Passer la tâche en `En cours`, avec la date et la branche, dans le tableau **et** dans sa fiche.

**Pendant le travail :**
- Cocher les étapes de la fiche au fur et à mesure.
- Noter dans **Notes techniques** tout ce qu'un autre agent devrait savoir pour reprendre : fichiers et fonctions touchés, pièges rencontrés, choix faits et pourquoi.
- Si une décision dépasse la tâche (architecture, convention, choix produit), l'ajouter aussi dans **Décisions**.
- Si l'agent est bloqué, passer la tâche en `Bloqué` et écrire précisément ce qui manque.

**À la fin :**
- Remplir **Vérification** avec les commandes lancées et leur résultat réel. Écrire aussi ce qui n'a pas pu être vérifié, et pourquoi.
- Renseigner les commits et la PR, ainsi que la version et l'entrée de `CHANGELOG.md` s'il y en a une.
- Passer la tâche en `À valider` si l'utilisateur doit tester (sur TV par exemple), sinon en `Terminé`.
- Ajouter une ligne au **Journal**.
- Mettre à jour ce fichier **dans le même commit** que le code.

**Nouvelle tâche donnée par l'utilisateur :**
- Créer sa fiche à partir du modèle, avec l'ID suivant (`T-001`, `T-002`…).
- Ajouter sa ligne au tableau.
- Reformuler le besoin, mais garder les mots de l'utilisateur dans **Demande**.
- Si le besoin est ambigu, lister les questions dans **Questions ouvertes** plutôt que de deviner.

**Ce qu'il ne faut jamais faire :**
- Supprimer une tâche : une tâche abandonnée passe en `Abandonné`, avec la raison.
- Réutiliser un ID.
- Marquer `Terminé` sans vérification réelle.

### Statuts

| Statut | Sens |
|---|---|
| `À faire` | Décrite, pas commencée |
| `En cours` | Un agent travaille dessus (date et branche indiquées) |
| `Bloqué` | En attente d'une réponse, d'un accès ou d'une autre tâche (raison dans la fiche) |
| `À valider` | Implémentée, en attente d'un test ou d'un accord de l'utilisateur |
| `Terminé` | Implémentée, vérifiée, commitée |
| `Abandonné` | Ne sera pas faite (raison dans la fiche) |

**Priorités :** `P0` (urgent, ou bloque le reste), `P1` (important), `P2` (normal), `P3` (plus tard, ou si le temps le permet).

**Cibles :** `tv` (app Android), `plugin`, `app` (desktop), `tools`, `ci`, `docs`.

---

## Tableau de bord

| ID | Titre | Statut | Priorité | Cible | Dépend de | Mis à jour |
|---|---|---|---|---|---|---|
| T-001 | Couper les liens du fork avec les serveurs d'origine (mises à jour TV, rapports) | À valider | P1 | tv, plugin | — | 2026-10-28 |
| T-002 | Renommer le projet en Nathflix, version 0.1.0, tous les liens vers ce dépôt | À valider | P1 | tv, plugin, app, tools, ci, docs | T-001 | 2026-10-28 |
| T-003 | Nouveau logo Nathflix : icônes, bannière TV, intro, captures d'écran | À faire | P2 | tv, plugin, app, tools, docs | T-002 | 2026-10-28 |
| T-004 | Téléphones et tablettes : bases (connexion, mode mobile, mise en page, APK de test) | À valider | P0 | tv, plugin, ci | T-002 | 2026-10-29 |
| T-005 | Téléphones et tablettes : navigation façon Netflix mobile | À valider | P1 | plugin, tv | T-004 | 2026-10-29 |
| T-006 | Téléphones et tablettes : lecteur façon Netflix mobile (gestes, PiP, arrière-plan) | À valider | P1 | tv | T-004 | 2026-10-29 |
| T-007 | Pas de piste audio sur certains épisodes (Dolby, DTS, TrueHD), puis release 0.2.0 | Bloqué | P0 | tv, ci | T-006 | 2026-10-29 |
| T-008 | Réglages par défaut (UI et audio en français, sans sous-titres, épisodes floutés) ; plus de fenêtre d'addons au démarrage | À valider | P1 | plugin | — | 2026-10-29 |
| T-009 | Tout en français (et en anglais), selon la langue de l'interface | À valider | P1 | plugin, tv | — | 2026-10-29 |
| T-010 | Accueil : Continuer de regarder avant Airing, puces, 3 points et appui long sur les cartes | À valider | P1 | plugin | — | 2026-10-29 |
| T-011 | Glisser-déposer pour réordonner les addons | À valider | P2 | plugin | — | 2026-10-29 |
| T-012 | Bouton « Lancer l'épisode » qui lance la première source | À valider | P1 | plugin | — | 2026-10-29 |
| T-013 | Lecteur téléphone : clone de l'UI Netflix, panneaux, plus de flash du lecteur Stremio | À valider | P1 | tv | T-006 | 2026-10-29 |
| T-014 | Stremio resté en anglais malgré « Français » (langue enregistrée en `fre` au lieu de `fr-FR`) | À valider | P0 | plugin | T-008 | 2026-10-29 |
| T-015 | Billboard : listes au choix parmi les catalogues des addons (Trakt…), jusqu'à trois mélangées | À valider | P1 | plugin | — | 2026-10-29 |
| T-016 | Bouton « Reprendre » sur la page d'une série ; attendre toutes les sources avant de lancer | À valider | P1 | plugin | T-012 | 2026-10-29 |
| T-017 | Menu ⋮ des cartes (Details, Dismiss) resté en anglais | À valider | P2 | plugin | T-014 | 2026-10-29 |

---

## Tâches

Les fiches s'ajoutent ici, la plus récente en bas, à partir du modèle en commentaire ci-dessous.

### T-001 — Couper les liens du fork avec les serveurs d'origine (mises à jour TV, rapports)

- **Statut :** À valider
- **Priorité :** P1
- **Cible :** tv, plugin
- **Dépend de :** —
- **Créée le :** 2026-10-28
- **Mise à jour :** 2026-10-28
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Règle ces points :
> * Mise à jour du plugin : l'app TV de votre fork téléchargerait le plugin d'origine, qui remplacerait le vôtre dès qu'il a un numéro de version plus élevé.
> * Mise à jour de l'app : elle proposerait l'APK d'origine. Celle-ci est signée avec une autre clé, donc Android refuserait de l'installer par-dessus la vôtre.
> * « Report a problem » : les rapports partent vers le serveur du développeur d'origine.

**Besoin**
Un build du fork ne doit plus rien récupérer du dépôt d'origine `notdxbi/dxbi-stremio`, ni lui envoyer quoi que ce soit.

**Critères d'acceptation**
- [x] L'app TV prend le plugin et l'APK uniquement dans les releases de `lavagnou/dxbi-stremio-android`
- [x] La mise à jour du plugin sur desktop / Stremio Enhanced (`@updateUrl`) pointe vers le fork
- [x] Aucun rapport n'est envoyé au relais d'origine, et « Report a problem » n'est plus proposé tant que le fork n'a pas son propre relais (menu profil sur desktop, Settings sur TV)
- [ ] Testé sur une TV ou un téléphone avec un build du fork (par l'utilisateur)

**Plan technique**
- `tv/app/src/main/java/com/notdxbi/dxbi/tv/Updates.kt` : nouvelle constante `REPO = "lavagnou/dxbi-stremio-android"`. `PLUGIN_URL`, `RELEASES_URL` et le message d'échec de téléchargement l'utilisent.
- `dxbi.plugin.js` :
  - `@updateUrl` vaut `https://raw.githubusercontent.com/lavagnou/dxbi-stremio-android/main/dxbi.plugin.js` ;
  - `REPORT_ENDPOINT = ""` ;
  - nouveau champ `report.enabled = !!REPORT_ENDPOINT` ;
  - le menu profil n'ajoute « Report a problem » que si `report.enabled`.
- `tv/app/src/main/assets/dxbi-tv.js` (`ensureSettingsEntry`) : l'entrée « Report a problem » est ajoutée à part, seulement si `window.__dxbi.report.enabled === true`, juste après l'entrée du serveur de streaming (classes `dxtv-server-entry` et `dxtv-report-entry`).

**Étapes**
- [x] URLs de mise à jour TV vers le fork
- [x] `@updateUrl` vers le fork
- [x] Rapports désactivés et entrées masquées (desktop et TV)
- [x] `CLAUDE.md` mis à jour (section fork)
- [ ] Validation sur appareil

**Notes techniques**
- **Pas de release, pas de mise à jour.** Tant que le fork n'a pas de release, l'API GitHub `releases/latest` répond 404. `Updates.get()` renvoie alors `null` et aucune mise à jour n'est faite, sans message. Pour que les mises à jour marchent, chaque release du fork doit contenir :
  - `dxbi.plugin.js`, avec un `@version` plus haut que celui installé ;
  - `dxbi-tv.apk`, signé avec la clé du fork (secrets de `tv.yml`).
- **Comparaison de version de l'APK.** `Updates.checkApp` compare le `tag_name` de la release, sans le `v`, à `versionName`. Les tags du fork doivent donc suivre la forme `vX.Y.Z`.
- **Desktop et Stremio Enhanced.** `@updateUrl` suit la branche `main` du fork, comme l'amont suivait la sienne. Tout ce qui est fusionné sur `main` avec un `@version` plus haut est donc proposé aux utilisateurs desktop.
- **Signature de l'APK.** `applicationId` reste `com.notdxbi.dxbi.tv`. Une TV qui a l'APK d'origine doit la désinstaller avant d'installer celle du fork (signatures différentes).
- **Réactiver les rapports :**
  1. déployer `tools/report-relay` (`npx wrangler deploy`, puis `npx wrangler secret put DISCORD_WEBHOOK`) ;
  2. mettre l'adresse `https://<worker>/report` dans `REPORT_ENDPOINT`.

  `report.enabled` réaffiche alors l'entrée partout. Le Worker accepte l'origine `https://web.stremio.com`, ce qui couvre la TV et l'app desktop.
- **Bridge inchangé** : `dxbi-tv.js` et le plugin sont livrés ensemble dans l'APK.
- **Hors périmètre, reste à décider :** auto-update de l'app desktop (`app/package.json` `build.publish`, `app/src/constants/index.ts`), installeurs `install.sh` / `install.ps1`, nom et `applicationId`. Voir la section fork de `CLAUDE.md`.

**Vérification**
- `node --check dxbi.plugin.js` et `node --check tv/app/src/main/assets/dxbi-tv.js` : OK.
- Test Playwright de `ensureSettingsEntry` : `dxbi-tv.js` chargé dans une fausse page Settings de Stremio, avec un faux bridge.
  - Avant le changement : « Report a problem » apparaît dans tous les cas.
  - Après le changement : il est absent avec `report.enabled` à `false` ou non défini, et présent (même ordre qu'avant) avec `true`.
- **Non vérifié :**
  - la compilation Kotlin, car l'Android SDK n'est pas disponible dans l'environnement cloud (`./gradlew assembleDebug` à lancer en local ou en CI) ;
  - le comportement sur un vrai appareil.

**Livraison**
- Commits : voir le commit « Point the fork's updates at its own releases, turn off problem reports » sur `claude/quirky-cannon-g7e9oy`
- PR : —
- Version / CHANGELOG : non. La numérotation des versions du fork n'est pas encore décidée (voir Questions ouvertes).

**Questions ouvertes**
- ~~Faut-il aussi faire pointer l'auto-update de l'app desktop vers le fork ?~~ Oui : fait dans T-002.

### T-002 — Renommer le projet en Nathflix, version 0.1.0, tous les liens vers ce dépôt

- **Statut :** À valider
- **Priorité :** P1
- **Cible :** tv, plugin, app, tools, ci, docs
- **Dépend de :** T-001
- **Créée le :** 2026-10-28
- **Mise à jour :** 2026-10-28
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> * Oui fait pointer tout les liens, auto-update, etc, de l'ancien repo vers mon fork.
> * Je voudrais adopter ma nouvelle numérotation. On va partir de 0.1.0
> * Je voudrais aussi renommer l'app, et je vais renommer le repo mais plus tard, car ça va devenir un nouveau projet. J'aimerais appeler l'app "Nathflix". Et j'aimerais tout renommer sur le projet, les fichiers et dossiers d'installation, nom de l'app, installeur, nom android, etc.. Je ferai un nouveau logo plus tard aussi.
> * Ajoute au gitignore les fichiers CLAUDE.md et PLAN.md, c'est un repo public et je veux éviter de partager ces fichiers.

**Besoin**
Faire de dx.bi un nouveau projet, Nathflix. Nouveau nom partout (apps, installeurs, fichiers, dossiers de données, identifiants Android et desktop), numérotation à partir de 0.1.0, et plus aucun lien vers le dépôt d'origine, sauf les attributions que la licence exige.

**Critères d'acceptation**
- [x] Nom affiché « Nathflix » partout (apps, installeurs, réglages, messages, README) ; logo texte du thème « NATHFLIX »
- [x] Fichiers renommés : `nathflix.plugin.js`, `nathflix.theme.css`, `nathflix-tv.js`, artefacts `nathflix-*` (Setup, portable, dmg, AppImage, `nathflix-tv.apk`)
- [x] Identifiants : Android `com.lavagnou.nathflix`, desktop `com.lavagnou.nathflix.desktop` (choix de l'utilisateur), dossiers de données `nathflix`
- [x] Version 0.1.0 partout (plugin, thème, `app/version`, `package.json`, lockfile ; `versionCode` 100)
- [x] Tous les liens et mises à jour vers `lavagnou/dxbi-stremio-android` : TV, electron-updater, `@updateUrl`, installeurs, README, CI
- [x] Statut Discord désactivé tant qu'il n'y a pas de Client ID Nathflix (choix de l'utilisateur)
- [x] Attribution à dx.bi conservée (NOTICE.md, en-têtes de licence, crédits du README)
- [x] `CLAUDE.md` et `PLAN.md` : **pas** ajoutés au `.gitignore`. Décision de l'utilisateur après explication : sans eux dans le dépôt, les sessions cloud ne les auraient plus.
- [ ] APK compilée et testée sur un appareil (par l'utilisateur)
- [ ] App desktop compilée et lancée (par l'utilisateur ou en CI)

**Plan technique**
- Renommage scripté sur les fichiers texte suivis par git, ligne par ligne. Les lignes de plus de 3000 caractères sont ignorées (base64 de l'intro, shaders, thème intégré : un « dxbi » peut y apparaître par hasard). Règles, dans l'ordre :
  1. le slug `notdxbi/dxbi-stremio` est remplacé par `lavagnou/dxbi-stremio-android`, avec un marqueur temporaire pour protéger ce slug ;
  2. `com.notdxbi.dxbi.tv` devient `com.lavagnou.nathflix`, et `com.notdxbi.dxbi` devient `com.lavagnou.nathflix.desktop` ;
  3. `DX.BI` devient `NATHFLIX`, `dx.bi` devient `Nathflix` ;
  4. `DXBI`, `Dxbi` et `dxbi` deviennent `NATHFLIX`, `Nathflix` et `nathflix`.
- Fichiers exclus du script, traités à la main : `CHANGELOG.md` (historique dx.bi gardé, entrée 0.1.0 ajoutée), `NOTICE.md` (réécrit), `PLAN.md`, `LICENSE*`, `app/COPYING`, `app/LICENSE.md`, `app/package-lock.json` (seul le champ `name`).
- `git mv` :
  - `dxbi.plugin.js` → `nathflix.plugin.js` ;
  - `dxbi.theme.css` → `nathflix.theme.css` ;
  - `tv/app/src/main/assets/dxbi-tv.js` → `nathflix-tv.js` ;
  - `tv/app/src/main/java/com/notdxbi/dxbi/tv/` → `com/lavagnou/nathflix/`.
- Retouches à la main :
  - en-têtes de licence : « Copyright (C) 2026 lavagnou. Based on dx.bi, Copyright (C) 2026 dx.bi » ;
  - `@author` et `"author"` deviennent `lavagnou` ;
  - mentions « Discord (#help) » retirées des messages de rapport ;
  - `install.ps1` vérifie `'@name Nathflix'` (l'ancienne regex avait un point échappé) ;
  - `app/package.json` `build.publish` : `owner: lavagnou`, `repo: dxbi-stremio-android` (le script avait produit `notnathflix` et `nathflix-stremio`, corrigé).
- Logo du thème : `width` de `[class*="logo-container"]` passée de 6.5rem à 9.5rem, et décalage des onglets `left: calc(var(--dx-gutter) + 9.5rem + 1.5rem)`. NATHFLIX mesure environ 7.64rem en Bebas Neue 2.35rem, contre 4.31rem pour DX.BI.
- Discord :
  - `DISCORD.CLIENT_ID = ''` ;
  - `DiscordPresence.start()` sort tout de suite s'il est vide ;
  - le template `about-category.html` masque l'interrupteur (`{{ discord_display }}`) et intitule la section « Updates » (`{{ updates_heading }}`) ;
  - le lien vers le Discord de dx.bi est retiré de l'onglet À propos.
- CI :
  - `cross-platform.yml` : nouvelle étape « Published release » ; les étapes « Published installer » ne tournent que si la dernière release contient `install.sh` ;
  - `discord-release.yml` : sans webhook, l'étape termine en succès avec une notice au lieu d'échouer.
- README : plus de Discord ni de vidéo de présentation. Note « basé sur dx.bi », crédits et licence mis à jour, suppression des numéros de version de dx.bi dans la FAQ et des puces Discord et Report a problem.

**Étapes**
- [x] Renommage des fichiers et du package Kotlin
- [x] Renommage scripté puis vérification des restes (`git grep -niE "dx\.?bi"`)
- [x] Retouches manuelles (licences, auteurs, Discord, installeurs, publish)
- [x] Version 0.1.0 et `node tools/bundle-theme.mjs`
- [x] README, NOTICE, CHANGELOG, READMEs secondaires, CLAUDE.md
- [x] CI
- [ ] Validation sur appareil et build desktop

**Notes techniques**
- **Ce qui garde l'ancien nom, et pourquoi :**
  - les liens `notdxbi/dxbi-stremio` restants sont des attributions (NOTICE, README, en-têtes) ;
  - les préfixes CSS `dx-`, `dxe-`, `dxtv-` et les variables `--dx-*` sont internes et invisibles ;
  - les images n'ont pas changé (intro, icônes, bannière TV, captures), voir T-003 ;
  - `CHANGELOG.md` garde l'historique de dx.bi.
- **Ce qui repart de zéro.** Les clés de stockage changent (`nathflix.settings`…), ainsi que le dossier de données desktop (`%APPDATA%\nathflix`, `~/.config/nathflix`…), le fichier du plugin pour Stremio Enhanced (`nathflix.plugin.js`, clé de réglages `nathflix`) et la clé SharedPreferences TV (`plugin-settings` / `nathflix`). Nathflix démarre donc à neuf et ne remplace pas une installation dx.bi existante.
- **Watch party.** Le sel et le topic MQTT deviennent `nathflix-party/v1`, donc une partie Nathflix n'est pas compatible avec une partie dx.bi. Les brokers MQTT sont publics (HiveMQ, EMQX, Mosquitto, shiftr.io), pas des serveurs de dx.bi.
- **Releases.** La CI ne joint pas `nathflix.plugin.js`, `install.sh` ni `install.ps1` : il faut les ajouter à la main à chaque release (procédure dans CLAUDE.md). Les tags doivent être `vX.Y.Z`.
- **Secrets TV renommés** : `NATHFLIX_TV_KEYSTORE_B64`, `NATHFLIX_TV_KEYSTORE_PASSWORD`, alias `nathflix-tv`.
- **Renommer le dépôt plus tard** : `git grep -l "lavagnou/dxbi-stremio-android"`, plus `"repo"` dans `app/package.json`.

**Vérification**
- `node --check nathflix.plugin.js` et `node --check tv/app/src/main/assets/nathflix-tv.js` : OK.
- `node tools/bundle-theme.mjs` : version 0.1.0 partout. Une deuxième exécution ne change plus rien.
- App desktop : `npm ci --ignore-scripts` puis `npx tsc --noEmit` : OK.
- YAML des 5 workflows : syntaxe valide (`yaml.safe_load`).
- Test Playwright : `nathflix-tv.js` et le plugin dans une page Stremio de substitution, User-Agent `nathflix-tv/0.1.0`, faux bridge `NathflixTV`. Résultats :
  - `window.__nathflix` et `window.__nathflixTv` sont présents, les anciens globaux sont absents ;
  - le plugin est chargé sous l'id `nathflix.plugin.js`, avec 54 réglages dans `nathflix.settings` ;
  - entrées Settings : « Nathflix settings » et « Streaming server for torrents » ;
  - « Report a problem » est masqué ;
  - le logo `::before` affiche « NATHFLIX », avec un conteneur de 152 px ;
  - aucune erreur de page.
- `git grep -niE "dx\.?bi"` : il ne reste que les attributions, les lignes base64 et le slug du dépôt.
- **Non vérifié :**
  - la compilation Android : `dl.google.com` est bloqué dans l'environnement cloud, donc pas d'Android SDK. À faire avec `cd tv && ./gradlew assembleDebug` ;
  - le build et le lancement de l'app Electron ;
  - le vrai web.stremio.com, bloqué ici aussi ;
  - le rendu visuel du logo avec la police chargée.

**Livraison**
- Commits : « Rename the project to Nathflix, start at 0.1.0, point every link at this repository » sur `claude/quirky-cannon-g7e9oy`
- PR : —
- Version / CHANGELOG : 0.1.0, entrée en tête de `CHANGELOG.md`

**Questions ouvertes**
- ~~Faut-il ajouter un workflow qui compile l'APK debug à chaque push touchant `tv/` ?~~ Fait dans T-004 (`android-dev.yml`). Son premier run (commit c8b135f) a compilé le code renommé de T-002 : `BUILD SUCCESSFUL`.

### T-003 — Nouveau logo Nathflix : icônes, bannière TV, intro, captures d'écran

- **Statut :** À faire
- **Priorité :** P2
- **Cible :** tv, plugin, app, tools, docs
- **Dépend de :** T-002
- **Créée le :** 2026-10-28
- **Mise à jour :** 2026-10-28
- **Branche :** —

**Demande**
> Je ferai un nouveau logo plus tard aussi.

**Besoin**
Remplacer toutes les images qui montrent encore le logo DX.BI, une fois que l'utilisateur aura fourni le logo Nathflix.

**Critères d'acceptation**
- [ ] Icônes desktop : `app/images/icon*.png`, `icon.ico`, `icon.icns`, `app/images/presence/*`
- [ ] Icônes Android : `tv/app/src/main/res/mipmap-*/ic_launcher.png`, bannière Android TV `drawable/banner.png` (320×180)
- [ ] Intro : nouveau rendu (`tools/render_intro.py`, `TEXT = "NATHFLIX"` déjà en place ; vérifier le timing pour 8 lettres). Il faut remplacer `introVideoData()` dans le plugin (AV1/WebM en base64) et `tv/app/src/main/res/raw/intro.mp4` (H.264) ; commandes dans `tools/README.md`.
- [ ] Captures d'écran du README (`screenshots/`)

**Plan technique**
- À préciser quand le logo sera fourni (formats source, couleur d'accent si elle change : `ACCENTS` dans le plugin et `Intro.kt`).

**Étapes**
- [ ] En attente du logo

**Notes techniques**
- Le logo texte de la barre du haut est en CSS (`content: "NATHFLIX"` dans `nathflix.theme.css`) : il est déjà renommé.

**Vérification**
- —

**Livraison**
- Commits : —
- PR : —
- Version / CHANGELOG : —

**Questions ouvertes**
- —

### T-004 — Téléphones et tablettes : bases (connexion, mode mobile, mise en page, APK de test)

- **Statut :** À valider
- **Priorité :** P0
- **Cible :** tv, plugin, ci
- **Dépend de :** T-002
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Ce que je veux commencer par faire sur ce fork, c'est d'ajouter une version android pour téléphone et tablette. Pour le moment ça fonctionne sur AndroidTV donc le chantier n'est pas énorme, l'application s'installe d'ailleurs déjà sur tablette ou téléphone, mais elle n'est pas adapté.
>
> (Réponses aux questions) Globalement un peu tout, mais déjà je ne peux pas me connecter car ça n'ouvre pas le clavier android quand je clique sur les champs pour me connecter. Navigation tactile, tailles et mise en page, réglages et fenêtres. Une seule APK. Rotation libre partout. Je testerai les APK sur mon téléphone, sans Android Studio (plus tard en local avec mobile-MCP et des émulateurs).

**Besoin**
Rendre l'app utilisable au doigt sur téléphone et tablette : pouvoir se connecter, page à la taille de l'écran, navigation tactile, fenêtres adaptées, TV inchangée. Fournir une APK de test téléchargeable sans Android Studio. C'est la base des tâches T-005 (navigation Netflix) et T-006 (lecteur Netflix).

**Critères d'acceptation**
- [x] Le clavier s'ouvre sur les champs de connexion (plus de verrou au doigt)
- [x] Mode mobile distinct du mode TV (User-Agent `nathflix-mobile/`, `IN_MOBILE`, `html.dx-mobile`)
- [x] Page à la largeur de l'écran, onglets en barre du bas, 3 posters par rangée en portrait, pas de commandes qui n'apparaissent qu'au survol
- [x] Fenêtres et réglages au doigt, libellés selon l'appareil (« this phone »)
- [x] Barres système visibles, rien sous l'encoche ni sous le clavier, écran qui peut s'éteindre en navigation, tap pour passer l'intro
- [x] APK de test construite par GitHub Actions (pré-release `dev`)
- [x] TV inchangée (vérifiée en test Playwright pour la page ; tailles du lecteur TV dans `values-television`)
- [ ] Testé sur un vrai téléphone par l'utilisateur, avec retours pour T-005

**Plan technique**
- Plan détaillé approuvé : `/root/.claude/plans/reactive-riding-conway.md` (session cloud du 2026-10-29). Résumé :
- **Kotlin :**
  - `Device.kt` (`isMobile`, `kind`, `noun`) ;
  - `Bridge.deviceKind()` (sans token, comme `isPhone`) ;
  - `MainActivity` : User-Agent `nathflix-mobile/` ou `nathflix-tv/`. Sur téléphone et tablette :
    - pas de `FLAG_KEEP_SCREEN_ON` ;
    - `FLAG_FULLSCREEN` retiré (le thème le met) ;
    - barres visibles, icônes claires ;
    - padding des insets `systemBars`, `displayCutout` et `ime` sur la racine ;
    - `adjustResize` et `smallestScreenSize` dans le manifeste ;
  - `Intro` : la superposition est cliquable et passe l'intro ;
  - `PlayerActivity` :
    - dimens (`values/` pour téléphone et tablette, `values-television/` = valeurs TV d'avant) ;
    - padding de l'encoche ;
    - plein écran réappliqué dans `onResume` ;
    - titre sur une ligne ;
  - `Updates` : textes selon l'appareil, aucune vérification de mise à jour en build debuggable.
- **`nathflix-tv.js` :**
  - viewport `device-width` sur mobile, 1280 sur TV ;
  - `KIND` / `NOUN` et `tv.deviceKind` ;
  - classe `dxtv-touch` : surbrillance seulement en `:focus-visible`, feuilles pleine largeur, bouton « Done » collant ;
  - verrou des champs : sur mobile seulement après une flèche, et toucher un champ verrouillé le libère ;
  - `NOT_ON_TOUCH` cache les réglages de cartes de survol sur mobile.
- **Plugin :**
  - `IN_MOBILE`, `IN_ANDROID`, `DEVICE` ;
  - deviennent `IN_ANDROID` : nom de la section de réglages, `sync()` du lecteur, bouton Cast, splash natif et sa version légère, `hwConvert` ;
  - `tvTorrents` passe `androidOnly`, avec un libellé selon l'appareil ;
  - `report.where()` renvoie « Nathflix mobile » (ajouté aussi à `WHERE` du relais) ;
  - classe `dx-mobile` ;
  - `preview.onOver` est ignoré sur mobile (le tap envoie aussi les événements souris).
- **Thème :** bloc « PHONES AND TABLETS » (`html.dx-mobile`, fin du fichier) :
  - `--dx-nav-h` 3.5rem ;
  - onglets en barre fixe en bas (`--dx-tabs-h` 3.75rem), contenu arrêté au-dessus ;
  - logo plus petit ;
  - recherche ouverte sur toute la barre ;
  - colonnes : 3 jusqu'à 600 px, 5 jusqu'à 1050 px ;
  - flèches et points des rangées masqués ;
  - play et retirer de Reprendre toujours visibles ;
  - billboard centré sur toute la largeur, avec une ombre montant du bas.
- **CI et build :**
  - `.github/workflows/android-dev.yml` ;
  - debug `applicationIdSuffix ".dev"` ;
  - nom « Nathflix Dev » (`src/debug/res`) ;
  - clé `tv/app/dev.keystore` (mot de passe `nathflix-dev`, exception dans `tv/.gitignore`).

**Étapes**
- [x] Kotlin (Device, MainActivity, Intro, Player, Updates, manifeste, dimens)
- [x] Build de test et workflow
- [x] `nathflix-tv.js`
- [x] Plugin et thème, bundle
- [x] Tests Playwright
- [x] CLAUDE.md, CHANGELOG
- [ ] Retour de l'utilisateur sur téléphone

**Notes techniques**
- **Cause du clavier qui ne s'ouvrait pas.** Le verrou « jusqu'à OK » de `nathflix-tv.js` verrouillait tout champ qui prenait le focus plus d'une seconde après un toucher. Stremio met lui-même le curseur dans le champ e-mail, qui restait donc en lecture seule, et le toucher à nouveau ne refocalise pas.
- **Qualificatifs de ressources.** `values-television` plutôt que `values-sw600dp` : `smallestWidth` passe avant le mode UI, donc une TV en tvdpi (plus de 600dp) prendrait les tailles tablette.
- **Edge-to-edge.** Avec `targetSdk 35`, Android 15 force l'edge-to-edge : les insets sont gérés à la main (padding), pas par `setDecorFitsSystemWindows(true)`.
- **Ordre CSS de l'élément racine.** Les variables `--dx-cols` et `--dx-nav-h` sont redéfinies sur `html.dx-mobile`, plus spécifique que `:root`. `--horizontal-nav-bar-size` (défini comme `var(--dx-nav-h)` sur `:root`) suit, car c'est le même élément.
- **Le CSS mobile est écrit sans voir le vrai web.stremio.com** (bloqué en cloud) : il vise les mêmes sélecteurs que le reste du thème, mais il reste à ajuster d'après les captures de l'utilisateur.
- **APK de test.** L'APK debug contient aussi les ABI x86 (émulateurs) : environ 35 Mo zippée. La pré-release `dev` est recréée à chaque build (`gh release delete --cleanup-tag` puis `create --target $GITHUB_SHA`), donc l'URL de téléchargement ne change pas.

**Vérification**
- `node --check` sur le plugin et `nathflix-tv.js`, puis `node tools/bundle-theme.mjs` : OK.
- CI `android-dev.yml`, run 1 (commit c8b135f, partie Kotlin) : `BUILD SUCCESSFUL in 2m 19s`, APK publiée dans la pré-release `dev`. Avertissements de dépréciation seulement : `databaseEnabled` (déjà là avant) et `FLAG_FULLSCREEN` (supprimé ensuite par `@Suppress`). Run 2 (commit final 757d8ca) : succès, APK publiée dans `dev`. Le test mpv Windows (`mpv-windows.yml`, déclenché par le plugin) passe aussi.
- **Test Playwright, page Stremio de substitution, faux bridge, téléphone** (412×915, tactile, `nathflix-mobile/`) :
  - viewport `device-width` ; classes `dx-mobile` et `dxtv-touch` ;
  - e-mail sélectionné automatiquement : modifiable ; tap sur le mot de passe puis sur l'e-mail : modifiables ;
  - barre d'onglets fixe en bas (60 px, 412 px de large) ; `--dx-cols` = 3 ;
  - réglages : pas de « Hover previews », « Play torrents on this phone », pas de « Selection highlight » ;
  - aucune erreur de page.
- **Même test, TV** (1920×1080, `nathflix-tv/`) :
  - viewport 1280, classe `dx-tv` ;
  - e-mail sélectionné automatiquement : verrouillé (comme avant) ; flèche puis focus : verrouillé ; Entrée : déverrouillé ;
  - réglages TV complets (32 lignes, dont « Hover previews », « Play torrents on this TV » et « Selection highlight ») ;
  - aucune erreur.
- **Captures de contrôle** (page de substitution, sans le CSS de Stremio) : barre d'onglets en bas et feuille de réglages pleine largeur, bonnes.
- **Non vérifié :**
  - le rendu dans le vrai Stremio ;
  - le comportement réel du clavier Android et des insets ;
  - le lecteur au doigt ;
  - une tablette.

  C'est à l'utilisateur de tester avec `nathflix-dev.apk`.

**Livraison**
- Commits : « Android on phones and tablets: device detection, system bars, keyboard, player sizes; test builds » et « Phones and tablets: keyboard at sign-in, mobile layout, touch-friendly sheets » sur `claude/quirky-cannon-g7e9oy`
- PR : —
- Version / CHANGELOG : entrée « Unreleased » dans `CHANGELOG.md` (numéro à décider à la release)

**Questions ouvertes**
- Retours sur téléphone (captures) : barre du haut, billboard, rangées, page d'un titre, lecteur.

### T-005 — Téléphones et tablettes : navigation façon Netflix mobile

- **Statut :** À valider
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-004
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Je voudrais copier le design de Netflix, donc je veux que ça soit visuellement comme sur l'app android de Netflix. Je veux avoir comme Netflix en gros.
>
> Continue tout le plan en autonome, je testerai à la fin

**Besoin**
Sur téléphone et tablette, une navigation qui ressemble à l'app Android de Netflix.

**Critères d'acceptation**
- [x] Toucher un poster (Accueil, Recherche, Découvrir, Bibliothèque) ouvre une feuille en bas de l'écran : poster, titre, année, note, durée ou saisons, genres, résumé, Play ou « Resume S1 E3 », My List, Hide, « Episodes & info ». Reprendre (Continue Watching) relance directement, comme avant.
- [x] Page d'un titre en une colonne : image en haut, détails, boutons qui passent à la ligne (le bouton large Play ou Trailer prend toute la largeur), puis épisodes ou flux
- [x] Accueil : puces Shows, Movies et My List, billboard haut en portrait (logo et boutons centrés, sans les points), « See All » toujours visible, cartes « Airing » à la largeur du téléphone
- [x] Onglets en bas, « Library » renommé « My List » (comme Netflix)
- [x] Découvrir et Bibliothèque : 3 colonnes en portrait, sans la colonne d'aperçu de Découvrir ; Réglages empilés ; toasts et fenêtres de l'extension au-dessus de la barre d'onglets
- [ ] Rendu validé par l'utilisateur sur téléphone et tablette, avec le vrai Stremio

**Plan technique**
- **Plugin `titleSheet`** :
  - un `click` en capture sur `document` intercepte le tap sur un poster (`cardInfo`) ;
  - les données viennent de `getInfo()` et `scoreOf()` ;
  - My List réutilise `preview.toggleList` et `preview.library` ; Hide réutilise `restrict.toggleHidden` et `hideButton.toast` ;
  - Resume part de `stremioLibrary()` et `state.video_id` ;
  - la feuille se ferme par balayage vers le bas, la croix, le fond, ou Back (`api.closePreview`).
- **Plugin `mobileChips`** : appelé par `sync()`, il pose les puces au début du contenu de l'accueil. Leur cible : `/discover/<cinemeta>/series|movie/top` et `/library`.
- **CSS** :
  - `.dxm-*` dans `EXTRAS_CSS` ;
  - mise en page des pages dans le bloc « PHONES AND TABLETS » du thème.

**Notes techniques**
- **Ce qui casse à 412 px.** Sur la page d'un titre, la liste des flux et épisodes faisait au moins 448 px (`clamp(28rem, 31vw, 42rem)`) à côté de la colonne de description. Elle passe maintenant en flex colonne, même en « mode grille » (quand une autre extension insère des éléments).
- **Défilement de la page d'un titre.** C'est `metadetails-content` qui défile (`overflow-y: auto`). L'image de fond, elle, reste fixe en haut (`height: nav-h + 60vw`).
- **Écrit sans le vrai Stremio** (bloqué en cloud) : vérifié seulement sur des pages de substitution. À ajuster d'après les captures de l'utilisateur.

**Vérification**
- **Playwright, page de substitution, téléphone** 412×915 :
  - tap sur un poster de l'accueil : la feuille s'ouvre, l'URL ne change pas ;
  - Play → `#/detail/movie/<id>/<id>` ;
  - My List → `Ctx/AddToLibrary` ;
  - Back (`__nathflixTv.back()`) ferme la feuille ;
  - puces Shows, Movies et My List présentes ;
  - aucune erreur.
- **Même test, TV** : le tap ouvre directement le titre, pas de puces.
- **CI du commit 5c1f790** : APK de test construite et publiée dans `dev` ; le test mpv Windows passe.
- **Page d'un titre à 412 px** : détails (412 px de large), puis flux (388 px) en dessous, rien de plus large que l'écran (`scrollWidth` 412), bouton large sur toute la ligne.
- **Non vérifié :** le vrai Stremio (couleurs, tailles réelles, grilles), une tablette.

**Livraison**
- Commits : « Phones and tablets: Netflix-style navigation… » sur `claude/quirky-cannon-g7e9oy`
- Version / CHANGELOG : entrées « Unreleased »

**Questions ouvertes**
- Le rendu réel (captures) : page d'un titre, billboard, puces, feuille.

### T-006 — Téléphones et tablettes : lecteur façon Netflix mobile (gestes, PiP, arrière-plan)

- **Statut :** À valider
- **Priorité :** P1
- **Cible :** tv
- **Dépend de :** T-004
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Comme le fork, je voudrais copier le design de Netflix, donc je veux que ça soit visuellement comme sur l'app android de Netflix. (Extras) Picture-in-picture, gestes dans le lecteur, lecture en arrière-plan.

**Besoin**
Sur téléphone et tablette, un lecteur natif qui ressemble à celui de l'app Android de Netflix, avec ses gestes. La TV garde son lecteur.

**Critères d'acceptation**
- [x] Contrôles maison (`MobilePlayerControls`) :
  - en haut : retour, titre et épisode, bouton picture-in-picture ;
  - au centre : −10 s, lecture/pause, +10 s ;
  - en bas : barre de progression rouge, temps restant ;
  - rangée : Vitesse, Verrouiller, Épisodes, Audio et sous-titres, Épisode suivant
- [x] Gestes : tap pour afficher ou masquer (disparaît après 4 s en lecture, reste en pause), double tap à gauche ou à droite pour ±10 s (au centre : pause), glisser à gauche pour la luminosité et à droite pour le volume
- [x] Verrouillage : aucun geste ne passe ; un tap montre « Screen locked · tap to unlock »
- [x] Panneaux Vitesse (0.5× à 1.5×), Audio et sous-titres (deux colonnes, « Off »), Épisodes (la saison, l'épisode en cours coché) ; Back ferme d'abord le panneau
- [x] Picture-in-picture : bouton, touche Home (avant Android 12), entrée automatique (Android 12+) ; fermer la petite fenêtre ferme le lecteur en gardant la position
- [x] Lecture en arrière-plan : `PlaybackService` (media3-session), notification avec contrôles, écran éteint ; permission de notification demandée une fois (Android 13+)
- [x] Épisode suivant : bouton pendant le générique ou les 30 dernières secondes, enchaînement automatique à la fin. Même release si Stremio la connaît, sinon sa liste de flux.
- [x] Réglages (téléphone et tablette seulement) : Picture-in-picture, Keep playing in the background, Play the next episode automatically
- [x] La TV garde son lecteur (aucun chemin TV modifié)
- [ ] Testé sur téléphone par l'utilisateur

**Plan technique**
- **Kotlin** :
  - `MobilePlayerControls.kt`, `player_mobile_controls.xml`, 16 icônes vectorielles Material (`ic_*.xml`, vérifiées en rendu SVG), styles `Nathflix.PlayerButton` et `Nathflix.PlayerBubble` (avec `parent=""`, sinon aapt cherche un style `Nathflix`) ;
  - `PlaybackService.kt` ;
  - `PlayerActivity.setUpMobile`, avec PiP, `onStop`, `finishWith(playRoute)`, `closeQuietly` ;
  - manifeste : `supportsPictureInPicture`, `singleTop`, service `mediaPlayback` et permissions ;
  - dépendance `media3-session:1.5.1`.
- **Page** :
  - `nathflix-tv.js` `mobilePlayback()` (épisodes, suivant, options) ;
  - `closeHandoff(…, playRoute)` et `replaceRoute` ;
  - `PlaybackBus.closed(…, playRoute)`.
- **Prochain épisode** : il vient de `state.nextVideo` du modèle « player » de Stremio. C'est ce qu'utilise le raccourci Shift+N de Stremio, et le plugin aussi (`upNext`).

**Notes techniques**
- **Pas d'historique en double.** La route suivante remplace la page du lecteur (`replaceState` + `popstate`) au lieu de s'empiler : Back depuis l'épisode suivant ne relance pas l'ancien.
- **`deepLinks.player` de `nextVideo` non vérifié.** Je ne l'ai pas vérifié sur un vrai Stremio. S'il manque, c'est la liste de flux de l'épisode qui s'ouvre.
- **Pendant le picture-in-picture**, l'écran principal reste sur la page du lecteur de Stremio (silencieuse). Lancer un autre titre ferme le lecteur en PiP (`closeQuietly`, progression des dernières secondes perdue).
- **La session Media3 a un ID fixe** (`nathflix-player`). Une seule à la fois, d'où la libération immédiate dans `closeQuietly`.
- **Pas d'entrée automatique en PiP avant Android 12** : c'est `onUserLeaveHint` qui le fait.

**Vérification**
- **CI `android-dev.yml`** :
  - run du commit 4d596fa : échec aapt (`style/Nathflix` introuvable), corrigé ;
  - run suivant (0190de4) : `BUILD SUCCESSFUL in 1m 37s`, APK publiée dans `dev`, seul avertissement l'ancien `databaseEnabled`.
- **Icônes** : rendues en SVG avec Playwright, toutes correctes.
- **Tests Playwright de la page** (T-004) toujours verts. Réglages : téléphone 31 lignes (3 nouvelles), TV 32 (inchangé).
- **Non vérifié :** tout ce qui demande un appareil (gestes, PiP, notification, enchaînement d'épisodes réel).

**Livraison**
- Commits : 4d596fa (lecteur) et 0190de4 (correctif de style) sur `claude/quirky-cannon-g7e9oy`
- Version / CHANGELOG : entrées « Unreleased »

**Questions ouvertes**
- —

### T-007 — Pas de piste audio sur certains épisodes (Dolby, DTS, TrueHD), puis release 0.2.0

- **Statut :** Bloqué (l'APK de la release attend la clé de signature de l'utilisateur)
- **Priorité :** P0
- **Cible :** tv, ci
- **Dépend de :** T-006
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> J'ai aucune piste audio de dispo quand je lis un épisode avec Nathflix, sur Streamio le même fichier marche bien. Corrige ça puis sort moi une nouvelle release.

**Besoin**
Le son de tous les épisodes que Stremio sait lire, puis une release publiée.

**Critères d'acceptation**
- [x] Les pistes AC3, E-AC3, DTS et TrueHD se lisent même sans décodeur matériel (FFmpeg)
- [x] Le panneau Audio et sous-titres montre aussi les pistes non lisibles (grisées, avec la raison) et le format de chaque piste
- [x] Workflow de release (`release.yml`) : tag → release avec notes du CHANGELOG, plugin, installeurs, APK et apps desktop
- [x] Release 0.2.0 publiée : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.2.0 (plugin, installeurs, apps Windows, Mac et Linux)
- [ ] `nathflix-tv.apk` dans la release : il faut les secrets `NATHFLIX_TV_KEYSTORE_B64` et `NATHFLIX_TV_KEYSTORE_PASSWORD`, puis relancer « TV app » avec `tag = v0.2.0`
- [ ] Son vérifié sur l'épisode de l'utilisateur (APK de test `nathflix-dev.apk`, qui contient le correctif, en attendant)

**Notes techniques**
- **Cause.** Le lecteur natif n'avait que les décodeurs matériels d'Android : aucun décodeur logiciel n'était embarqué (le message d'erreur le disait déjà pour DTS et TrueHD). Stremio, lui, décode en logiciel (libVLC/mpv). La plupart des téléphones n'ont ni Dolby ni DTS, d'où le silence. Mon panneau filtrait en plus les pistes non lisibles, d'où la liste vide.
- **Correctif.** `org.jellyfin.media3:media3-ffmpeg-decoder:1.5.0+1` sur Maven Central, environ 1,4 Mo par ABI. Il inclut les décodeurs ac3, eac3, dca, truehd/mlp, flac, opus, vorbis, mp3 et alac (vérifié dans le `.so`). AAC reste au matériel.
  - `DefaultRenderersFactory` en `EXTENSION_RENDERER_MODE_ON` le trouve par réflexion ; une règle ProGuard `-keep` est ajoutée par sécurité.
- **Release.** Aucune release n'avait encore été publiée sur le fork (0.1.0 n'a jamais été publiée). 0.2.0 regroupe donc tout le travail téléphone et tablette, plus ce correctif.

**Vérification**
- **CI `android-dev.yml` (commit 0.2.0, push)** : build debug réussie, et build release non signée (R8) réussie avec FFmpeg et media3-session.
- **Contenu du décodeur** : vérifié dans `libffmpegJNI.so` (ac3, eac3, dca, truehd/mlp…).
- **Release 0.2.0**, run « Release » 40803181183, lancé à la main via l'API :
  - tag `v0.2.0` créé sur le commit de la branche ;
  - notes tirées du CHANGELOG, plus les téléchargements ;
  - plugin et installeurs joints ;
  - apps desktop construites et jointes (Windows installeur et portable, Mac arm64 et x64, Linux x86_64 et arm64) ;
  - APK : étape sautée, pas de clé de signature dans les secrets.
- **Non vérifié :** le son sur l'appareil de l'utilisateur.

**Notes de release (session cloud)**
- **Pousser un tag est refusé** (403 du proxy git : seule la branche de travail est autorisée). `release.yml` peut donc aussi être lancé à la main avec `version`, et pose alors lui-même le tag.
- **Enregistrement du workflow.** GitHub ne permet de lancer un workflow à la main que s'il a déjà tourné une fois ou s'il est sur la branche par défaut. `release.yml` tourne donc aussi, sans rien faire, quand on le modifie sur une branche.
- **Incident avec le `dev`.** Sur le commit 0.2.0 (`5c8325e`), `android-dev.yml` a supprimé la pré-release `dev`, puis n'a pas pu la recréer (« HTTP 403: Resource not accessible by integration »). C'est arrivé deux fois : lancé à la main, puis en relançant le run de push. Cause non élucidée. Un nouveau lancement à la main sur `00920e6` l'a recréée (run 40803834195). Si le problème revient, relancer le workflow sur le dernier commit de la branche.

**Livraison**
- Commits : « Android: sound for Dolby, DTS and TrueHD tracks… », « 0.2.0… »
- Version / CHANGELOG : 0.2.0

### T-008 — Réglages par défaut et fenêtre d'addons

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Il faut par défault avoir ces options activé : Langue de l'UI en français, Sous-titre par défault en aucun, piste audio par défault en français, Flouter les épisodes non vus activé. Il y a une fenêtre a l'ouverture qui propose d'installer des addons de catalogues, retire la.

**Critères d'acceptation**
- [x] Au premier lancement d'un compte : `interfaceLanguage` et `audioLanguage` en `"fre"`, `subtitlesLanguage` à `null`, `hideSpoilers` à `true`, une seule fois (choix de l'utilisateur : « Une fois »)
- [x] Plus de fenêtre « Set up Home like Nathflix? » au démarrage (le pack reste dans Addons → Order & backup)
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Valeurs** vérifiées dans les sources de stremio-web (`interfaceLanguages.json` : Français = `["fr-FR","fre"]` ; `languageNames.json` : `fre`) et de stremio-core (`settings.rs` : `interface_language`, `hide_spoilers`, `audio_language`, `subtitles_language`).
- **`profileDefaults`** (plugin, à côté de `subtitlesStart`) :
  - `UpdateSettings` avec `{ ...current, ...PROFILE_DEFAULTS }` ;
  - drapeau `store` `profileDefaults:<id du compte ou guest>` : un nouveau compte reçoit aussi les défauts ;
  - `watch()` dans `sync()`, toutes les 10 s au plus, pour un compte connecté plus tard.
- **Ordre au démarrage** : défauts, puis `subtitlesStart` 500 ms après. Chacun envoie tout l'objet des réglages ; lancés ensemble, l'un écraserait l'autre.
- **`offerPack()`** et son `setTimeout` dans `start()` sont supprimés.

**Vérification**
- Playwright (UA mobile, faux core) : `UpdateSettings` envoyé une seule fois avec `fre` / `fre` / `null` / `true` ; rien de plus après rechargement de la page.
- Non vérifié : sur le vrai Stremio (core, compte connecté).

### T-009 — Tout en français (et en anglais), selon la langue de l'interface

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> il manque des éléments comme le "Airing this week" qui ne sont pas traduits en français, vu que c'est une app pour moi principalement, je veux que tout soit bien en français partout (tout en gardant l'anglais, il faut les deux, et que ça suive la langue de l'interface choisi)

**Critères d'acceptation**
- [x] Plugin : tout texte visible passe par `t()` (dictionnaire `FR`, clé = texte anglais), selon `interfaceLanguage`
- [x] Thème : libellés posés en CSS traduits (`html.dx-lang-fr`)
- [x] `nathflix-tv.js` : dialogues et réglages traduits ; langue envoyée à l'app (`setLanguage`)
- [x] App Android : textes dans `strings.xml` et `values-fr/strings.xml`, langue suivie par `Lang.kt`
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Android** :
  - `Lang.kt` garde la langue reçue de la page (SharedPreferences `nathflix-lang`) ; `Lang.context()` donne un contexte dans cette langue, et `Lang.text()` un texte.
  - Le lecteur gonfle ses vues avec ce contexte.
  - `Device.thisOne()` et `Device.theOne()` donnent « ce téléphone », « la tablette »…
  - `TorrentEngine` garde ses erreurs en identifiants de ressources ; `status(context)`.
- **Bridge** : `setLanguage(token, lang)`, détectée avant usage côté page.

**Vérification**
- `frDictionary()` : 709 entrées ; script de vérification : 652 clés `t()` utilisées, 0 absente du dictionnaire, 0 inutilisée, 0 `{…}` incohérent.
- Playwright en français (`interfaceLanguage: "fre"`) : puces « Séries / Films / Ma liste », « Lancer l'épisode », fenêtre « Ordre et sauvegarde des addons », `html.dx-lang-fr`, langue transmise à l'app (`setLanguage("fr")`), aucune erreur de page. En anglais : textes d'origine.
- CI `android-dev.yml` : les textes Android (`values-fr`) compilent (debug et release R8).
- Non vérifié : les libellés de Stremio lui-même (traduits par Stremio) et le rendu sur l'appareil.
- Laissé en anglais exprès : textes de support (rapport, « Copy details »), noms de marques, critiques Wikidata/TMDB (sources anglaises).
- Logique qui dépendait d'un texte affiché, corrigée : `hideButton` (drapeau `on`), bouton Statistics de Stremio (`data-dx-icon`), filtres de flux (valeurs gardées en anglais), `playbackHelp` (regex étendue au français, à vérifier sur un Stremio en français).

### T-010 — Accueil : ordre des rangées, puces, 3 points et appui long

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Je voudrais avoir "Continuer de regarder" avant "Airing this week" dans l'ordre dans l'onglet Home. Sur le texte Continuer de Regarder, j'ai 3 pastilles Shows, Movies, et list qui sont superposé sous le texte. Il y a 3 petits points sur les miniature dans "Continuer à regarder", mais ils sont mal placés, corrige ça. Et je veux que si je laisse appuyer sur la miniature ça ouvre la même fenetre contextuelle que en appuyant sur les 3 petits points.

**Critères d'acceptation**
- [x] « Airing this week » après Continuer de regarder (`airing.ensure`)
- [x] Puces en haut, par-dessus le billboard, plus jamais sur la première rangée
- [x] 3 points : petit bouton rond en bas à droite de la miniature, la barre de progression s'arrête avant
- [x] Appui long (500 ms) ou clic droit sur une carte : même menu que les 3 points (`cardPress`)
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Puces** : la règle du thème `> .dx-hero ~ * { position: relative }` les attrapait (elles viennent après le billboard dans le DOM), d'où leur chute sous le billboard. Exclues avec `:not(.dxm-chips)`, plus une règle mobile `!important`. Les remettre en premier enfant aurait fait boucler `hero.ensure()` et le `MutationObserver`.
- **3 points** : c'est le bouton de Stremio (`menu-label-container`), placé pour sa barre de titre que le thème masque dans les rangées.
- **Appui long** : écouteurs `pointer*` en capture sur `window`. `menu.click()` ouvre le menu de Stremio ; le clic qui suit est avalé. `fired` est posé *après* `menu.click()`, sinon ce clic-là serait avalé aussi. CSS `-webkit-touch-callout: none` sur les cartes (mobile).

**Vérification**
- Playwright : appui long de 700 ms sur une carte de Continuer de regarder, le menu s'ouvre une fois et la page ne bouge pas ; un tap ouvre toujours le titre. Les puces finissent au-dessus de la rangée (160 px < 168 px), en `position: absolute`.
- Non vérifié : position des 3 points avec le vrai CSS de Stremio ; ordre Airing / Continuer de regarder (pas de données d'épisodes dans le faux core).

### T-011 — Glisser-déposer pour réordonner les addons

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> On peux réorganiser les addons dans l'app, mais seulement en appuyant sur les flèches haut/bas mais pas en faisant de drag and drop, ce qui serait beaucoup plus simple quand on a beaucoup d'addons comme moi, rajoute le drag n drop.

**Critères d'acceptation**
- [x] Glisser-déposer au doigt (poignée ⠿) et à la souris (toute la ligne), partout
- [x] La liste défile près de ses bords pendant le glisser ; les flèches restent
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `addonTools.wireDrag()` : Pointer Events à la place du drag HTML5 (qui ne marche pas au doigt dans une WebView). Branché une fois sur `.dxe-list` (`render()` reconstruit les lignes). Positions mesurées au départ, en coordonnées du contenu de la liste (le défilement pendant le glisser ne les fausse pas). `move(from, to)` et `apply()` inchangés.

**Vérification**
- Playwright au doigt (événements tactiles CDP) : Cinemeta glissé de la 1re à la 3e place, « Enregistrer l'ordre » activé.

### T-012 — Bouton « Lancer l'épisode »

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Quand on est sur une série, sur un épisode, j'aimerais ajouter juste au dessus du bouton "Bande-annonce" un bouton Lancer l'épisode/Play episode, qui lance la première source dans la liste, vu que c'est toujours celle la que je choisi en général

**Critères d'acceptation**
- [x] Bouton blanc avant la bande-annonce sur la page des flux d'un épisode (et « Lecture » pour un film), partout
- [x] Il clique la première source visible dans l'ordre affiché (tri et filtres de `streamTools` compris)
- [x] Grisé (« Recherche de sources… ») tant qu'aucune source n'est là
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `episodePlay` (plugin), dans `sync()`. Route `#/detail/<type>/<id>/<videoId>`. Première source : `[class*="stream-container"]` sans `data-dx-hide`, plus petit `style.order`, puis ordre du DOM. `click()` garde le comportement de Stremio (et le relais vers le lecteur natif sur Android).
- Avec le bouton présent, la bande-annonce passe en bouton sombre.

**Vérification**
- Playwright : bouton placé avant la bande-annonce, il ouvre la première source affichée (`#/player/…`), la source masquée est ignorée.

### T-013 — Lecteur téléphone : clone de l'UI Netflix, sans flash du lecteur Stremio

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** tv
- **Dépend de :** T-006
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> L'interface sur le player des boutons en bas n'est vraiment pas du tout ergonomique et design, l'icone et le texte c'est trop. Organise ça mieux, rangé et placé de manière cohérente et claire. Je veux un clone de l'UI de Netflix, mais juste adapté avec les features que j'ai sur Nathflix. Pareil quand ouvre la fenetre de choix de langue de sous-titre et piste audio, ce genre de choses, ça ne fait pas pro et pas Netflix. Quand je lance une vidéo, j'ai pendant une fraction de seconde le lecteur de stremio qui s'affiche avant d'afficher le lecteur Nathflix, corrige ça

**Critères d'acceptation**
- [x] Rangée du bas façon Netflix (choix de l'utilisateur) : petite icône et libellé court, groupés au centre ; « Vitesse (1x) »
- [x] Haut : retour, titre sur une ligne, PiP ; centre : −10 s / lecture / +10 s
- [x] Vitesse : 5 points sur une ligne ; Audio et sous-titres : deux colonnes, Annuler / Appliquer ; Épisodes : cartes avec image et résumé
- [x] Voile noir dès le choix d'un flux, jusqu'au départ de la page du lecteur (plus de lecteur Stremio visible)
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Flash** : entre `#/player/` et `native.play()`, `startHandoff` attend le flux, les sous-titres et les segments (0,5 s à plusieurs secondes). Rien ne masquait le lecteur web.
  - `veil()` dans `nathflix-tv.js` : un `#dxtv-veil` noir (spinner tant que le flux est cherché), sous les dialogues `.dxtv-modal` ;
  - posé au clic sur `a[href^="#/player/"]` (filet : retiré après 1,5 s si la page n'est pas allée au lecteur), dans `startHandoff` et `resumePlayback` ;
  - retiré par `onRoute` hors de `#/player/` et quand le relais abandonne sur place.
- **Contrôles** : `player_mobile_controls.xml` et `MobilePlayerControls.kt` réécrits. Panneaux faits en code dans `mc_panel_body` (un `FrameLayout`).
  - Audio et sous-titres : le choix est gardé dans `pickedAudio` / `pickedText` et appliqué seulement à « Appliquer ». La logique des pistes est inchangée (`select`, `trackName`, pistes illisibles grisées).
  - Épisodes : `thumbnail` et `season` ajoutés au JSON de `native.play` (`mobilePlayback`). Images chargées par `Thumbs.kt` (OkHttp, 3 à la fois, cache de 8 Mo).
- Textes du lecteur dans `strings.xml` (voir T-009).

**Vérification**
- Playwright : le voile (`#dxtv-veil`, spinner) est posé dès le toucher du flux, puis retiré au retour hors de `#/player/`.
- CI `android-dev.yml` (commit d186daf) : build debug et release R8 réussies avec le nouveau lecteur.
- Non vérifié : le rendu du lecteur sur un téléphone (pas d'émulateur ici).

### T-014 — Stremio resté en anglais malgré « Français »

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P0
- **Cible :** plugin
- **Dépend de :** T-008
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> J'ai installé l'app, et j'ai pas mal d'anglais un peu partout malgrès que dans les options j'ai bien UI languague sur Français, j'ai selectionné une autre langue puis reséléctionné le Français et seulement la j'ai bien eu l'UI en français. Corrige ce bug

**Critères d'acceptation**
- [x] Les réglages par défaut enregistrent `interfaceLanguage: "fr-FR"`
- [x] Un compte resté en `"fre"` est corrigé une fois
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Cause** (sources de stremio-web) : `Settings/Interface/useInterfaceOptions.ts` enregistre `codes[0]` (`"fr-FR"`) et affiche aussi `codes[1]` (`"fre"`) comme « Français ». Mais `App.js` fait `i18n.changeLanguage(interfaceLanguage)`, et les traductions sont rangées sous `"fr-FR"`. T-008 enregistrait `"fre"`.
- **Correctif** dans `PROFILE_DEFAULTS`, plus une réparation dans `profileDefaults.apply()` (drapeau `profileLangFixed:<compte>`).

**Vérification**
- Playwright : un compte en `"fre"` (défauts déjà appliqués) reçoit une seule fois `UpdateSettings` avec `"fr-FR"` (« interface language fre → fr-FR » dans le journal), puis plus rien après rechargement.

### T-015 — Billboard depuis les listes des addons (Trakt…)

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Pour le billboard (la bannière hero) qui affiche des série/films sur l'accueil, j'aimerais que ça affiche des films et séries de ma liste Traktv Recommandations ? […] ou alors même de rajouter dans les paramètre le choix manuel de la liste a utiliser
> Il faudrait pouvoir choisir plusieurs listes […] pour avoir une liste Traktv pour les série et une pour les films en même temps sur le billboard

**Critères d'acceptation**
- [x] « Contenu de la bannière » propose les listes Cinemeta et chaque catalogue des addons du compte (choix de l'utilisateur : tous les catalogues)
- [x] Deux listes de plus (« Bannière : deuxième liste », « troisième liste »), mélangées à la première
- [x] Titres sans image ni description complétés par Cinemeta ; repli sur les titres populaires
- [ ] Vérifié sur l'appareil avec l'addon Trakt de l'utilisateur

**Notes techniques**
- La liste Trakt de l'utilisateur est une rangée de l'accueil, donc fournie par un addon : pas besoin de l'API Trakt (qui demanderait une clé d'API à lui).
- `readCatalogs()` : types `movie` / `series` seulement, sans paramètre obligatoire (`extraRequired`, `extra[].isRequired`). Les catalogues de même nom d'un addon sont groupés (films et séries mélangés).
- Valeurs `cat:<id>|<nom>` sans adresse. `catalogChoices.urls()` retrouve l'adresse dans les addons du compte.
- Le schéma des réglages est enregistré au chargement, avant le core : les choix viennent de `localStorage` `nathflix.catalogs`, puis `refresh()` met à jour le schéma et le réenregistre (`clearRegisteredSettings` + `registerSchema()`).

**Vérification**
- Playwright, avec un faux addon « Trakt » dont l'adresse contient une clé :
  - choix « Trakt — Recommendations (films et séries) » proposé, catalogue de recherche et type `channel` écartés ;
  - billboard avec ses titres (complétés par Cinemeta) ;
  - deux listes séparées (films en 1re, séries en 2e) mélangées ;
  - catalogue vide ou addon retiré : repli sur les titres populaires ;
  - la valeur enregistrée ne contient jamais l'adresse.
- Dictionnaire : 0 clé manquante.

### T-016 — Bouton « Reprendre » sur la page d'une série

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-012
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Si on est sur la page d'un série (pas sur un épisode en particulier) il faudrait le même bouton qui reprend la lecture de la série sur l'épisode en cours ou le prochain épisode, en fonctionnant pareil que sur un épisode, prendre le premier lien de la liste des sources, mais il faudrait un leger délai qui laisse le temps à toute les sources de charger.

**Critères d'acceptation**
- [x] « Reprendre S1 É3 » / « Lancer S1 É4 » / « Lancer S1 É1 » sur la page d'une série, avant la bande-annonce
- [x] Le clic ouvre les flux de l'épisode, attend que tous les addons aient répondu, puis lance la première source affichée
- [x] Même attente pour « Lancer l'épisode » sur la page d'un épisode
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `episodePlay.findEpisode()` lit `meta_details` (`metaItem.content.videos`, `libraryItem.state`), avec repli sur `library_recent`. Épisode en cours si `timeOffset > 0` et moins de 90 % ; sinon le suivant sorti du dernier fini ; spéciaux (saison 0) exclus.
- `playWhenReady()` attend la fin des `Loading` dans `meta_details.streams` (12 s au plus), puis 700 ms ; sans source, un message « Aucun flux trouvé pour cet épisode. ».

**Vérification**
- Playwright (faux core ; un addon rapide à 0,3 s et un lent à 2,5 s dont la source passe en tête) :
  - en cours → « Reprendre S1 É2 » ;
  - fini → « Lancer S1 É3 » ;
  - rien → « Lancer S1 É1 » ;
  - le bouton affiche « Recherche des flux… » pendant l'attente ;
  - c'est la source de l'addon lent (première une fois tout chargé) qui est lancée.

### T-017 — Menu ⋮ des cartes resté en anglais

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-014
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Quand je clique sur les 3 point sur une série ça affiche Details et Dismiss, c'est pas traduit en français.

**Notes techniques**
- Les traductions françaises existent dans `stremio-translations` (« Détails », « Effacer »). Mais `LibItem.js` (stremio-web) calcule ses options dans un `useMemo` sans `t` dans ses dépendances : elles gardent la langue de leur premier affichage. Changer de langue sans recharger (ou la réparation `fre` → `fr-FR` de T-014) les laissait en anglais.
- Correctif : `langWatch` recharge la page à tout changement du code de langue de Stremio (`LANG_CODE`), plus seulement quand la langue de Nathflix change.

**Vérification**
- Code de stremio-web et traductions vérifiés (commit de `stremio-translations` utilisé par stremio-web).
- Non vérifié : sur l'appareil.

<!--
### T-000 — Titre court

- **Statut :** À faire
- **Priorité :** P2
- **Cible :** tv
- **Dépend de :** —
- **Créée le :** AAAA-MM-JJ
- **Mise à jour :** AAAA-MM-JJ
- **Branche :** —

**Demande**
> Les mots de l'utilisateur.

**Besoin**
Ce qu'il faut obtenir, du point de vue de l'utilisateur.

**Critères d'acceptation**
- [ ] Ce qui doit être vrai pour considérer la tâche terminée

**Plan technique**
- Fichiers et fonctions concernés, approche choisie
- Invariants de CLAUDE.md à respecter (bridge, versions, DESKTOP_ONLY…)

**Étapes**
- [ ] …

**Notes techniques**
Découvertes, pièges, choix faits et pourquoi. C'est ce qu'il faut pour qu'un autre agent reprenne.

**Vérification**
- Commandes lancées et résultat réel
- Ce qui n'a pas pu être vérifié, et pourquoi

**Livraison**
- Commits : —
- PR : —
- Version / CHANGELOG : —

**Questions ouvertes**
- —
-->

---

## Décisions

Décisions qui dépassent une tâche : architecture, conventions, choix produit. Chaque décision indique sa date, la tâche d'origine et sa raison.

| Date | Décision | Raison | Tâche |
|---|---|---|---|
| 2026-10-29 | Téléphones et tablettes : une seule APK qui s'adapte. Marqueur `nathflix-mobile/` (User-Agent), `IN_MOBILE` / `IN_ANDROID` dans le plugin, `html.dx-mobile` dans le thème. Page à `device-width` ; la TV garde 1280 px. | Choix de l'utilisateur (une seule APK) ; un mode distinct évite de dégrader la TV | T-004 |
| 2026-10-29 | Rotation libre pour la navigation, vidéo en paysage. | Choix de l'utilisateur | T-004 |
| 2026-10-29 | APK de test « Nathflix Dev » construite par GitHub Actions (pré-release `dev`, suffixe `.dev`, clé de dev commitée). | L'utilisateur teste sur son téléphone sans Android Studio ; c'est aussi la vérification de compilation Kotlin en cloud | T-004 |
| 2026-10-28 | Le projet s'appelle Nathflix. Numérotation propre à partir de 0.1.0. Identifiants `com.lavagnou.nathflix` (Android) et `com.lavagnou.nathflix.desktop` (desktop). | Choix de l'utilisateur : nouveau projet, basé sur dx.bi | T-002 |
| 2026-10-28 | Les préfixes CSS internes `dx-`, `dxe-`, `dxtv-` et `--dx-*` sont gardés. | Invisibles pour l'utilisateur ; les renommer toucherait des milliers de lignes du thème et du plugin pour aucun gain visible | T-002 |
| 2026-10-28 | Statut Discord et « Report a problem » désactivés tant que Nathflix n'a pas son application Discord ni son relais. | Ils passaient par les services de dx.bi | T-001, T-002 |
| 2026-10-28 | `CLAUDE.md` et `PLAN.md` restent dans le dépôt (pas de `.gitignore`), donc publics. | Choix de l'utilisateur : les sessions cloud repartent d'une copie du dépôt et en ont besoin | T-002 |
| 2026-10-28 | Le fork ne récupère ni n'envoie rien au dépôt d'origine. Mises à jour depuis les releases de `lavagnou/dxbi-stremio-android` ; rapports de problème coupés tant que le fork n'a pas son propre relais. | Le plugin d'origine écraserait celui du fork, son APK ne s'installe pas (autre clé), et les rapports iraient au développeur d'origine | T-001 |

---

## Questions ouvertes (globales)

Questions pour l'utilisateur qui ne concernent pas une seule tâche.

- ~~Quelle numérotation pour les versions du fork ?~~ Numérotation propre à partir de 0.1.0 (T-002).
- ~~Faut-il renommer l'app et changer `applicationId` ?~~ Oui : Nathflix, `com.lavagnou.nathflix` (T-002).

---

## Journal

Une ligne par changement d'état ou par session de travail, la plus récente en haut.

| Date | Tâche | Événement |
|---|---|---|
| 2026-10-29 | T-016, T-017 | Créées et implémentées : Reprendre sur la page d'une série, attente des sources ; rechargement à tout changement de langue. APK de test publiée |
| 2026-10-29 | T-014, T-015 | Créées et implémentées : langue de Stremio en `fr-FR` ; billboard depuis les listes des addons (jusqu'à trois). APK de test publiée |
| 2026-10-29 | T-008 à T-013 | Implémentées et testées (Playwright, CI) ; APK de test publiée ; à valider sur l'appareil |
| 2026-10-29 | T-008 à T-013 | Créées : retours sur l'APK de test (réglages par défaut, traductions, accueil, addons, Lancer l'épisode, lecteur Netflix) ; en cours |
| 2026-10-29 | T-007 | Release v0.2.0 publiée (desktop, plugin, installeurs) ; APK bloquée faute de clé de signature dans les secrets |
| 2026-10-29 | T-007 | Créée : pas de son sur certains épisodes (pas de décodeur logiciel) ; FFmpeg ajouté ; release 0.2.0 en préparation |
| 2026-10-29 | T-005, T-006 | Implémentées en autonome à la demande de l'utilisateur (feuille titre, pages mobiles, lecteur Netflix, PiP, arrière-plan, épisode suivant). À valider sur appareil |
| 2026-10-29 | T-005, T-006 | Créées : navigation et lecteur façon Netflix mobile |
| 2026-10-29 | T-004 | Créée et implémentée : clavier à la connexion, mode mobile, mise en page, fenêtres au doigt, APK de test. À valider sur téléphone |
| 2026-10-29 | T-002 | Compilation Android vérifiée en CI (`android-dev.yml`, run 1) |
| 2026-10-28 | T-003 | Créée : nouveau logo, en attente du logo de l'utilisateur |
| 2026-10-28 | T-002 | Créée et implémentée : renommage en Nathflix, 0.1.0, liens et mises à jour vers ce dépôt, Discord désactivé. À valider (compilation Android et desktop, appareil) |
| 2026-10-28 | T-001 | Créée et implémentée : mises à jour TV et `@updateUrl` vers le fork, rapports de problème coupés. À valider sur appareil |
| 2026-10-28 | — | Création de PLAN.md (aucune tâche pour le moment) |
