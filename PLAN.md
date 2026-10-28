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
- Faut-il ajouter un workflow qui compile l'APK debug à chaque push touchant `tv/` ? Il n'y a aujourd'hui aucune vérification de compilation Android hors release.

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
| 2026-10-28 | T-003 | Créée : nouveau logo, en attente du logo de l'utilisateur |
| 2026-10-28 | T-002 | Créée et implémentée : renommage en Nathflix, 0.1.0, liens et mises à jour vers ce dépôt, Discord désactivé. À valider (compilation Android et desktop, appareil) |
| 2026-10-28 | T-001 | Créée et implémentée : mises à jour TV et `@updateUrl` vers le fork, rapports de problème coupés. À valider sur appareil |
| 2026-10-28 | — | Création de PLAN.md (aucune tâche pour le moment) |
