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
| T-003 | Nouveau logo Nathflix : icônes, bannière TV, intro, captures d'écran | À valider | P2 | tv, plugin, app, tools, docs | T-002 | 2026-10-30 |
| T-004 | Téléphones et tablettes : bases (connexion, mode mobile, mise en page, APK de test) | À valider | P0 | tv, plugin, ci | T-002 | 2026-10-29 |
| T-005 | Téléphones et tablettes : navigation façon Netflix mobile | À valider | P1 | plugin, tv | T-004 | 2026-10-29 |
| T-006 | Téléphones et tablettes : lecteur façon Netflix mobile (gestes, PiP, arrière-plan) | À valider | P1 | tv | T-004 | 2026-10-29 |
| T-007 | Pas de piste audio sur certains épisodes (Dolby, DTS, TrueHD), puis release 0.2.0 | À valider | P0 | tv, ci | T-006 | 2026-10-29 |
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
| T-018 | Revue : plantage du lecteur, gel de la Bibliothèque, faille XSS, écran noir, torrents | À valider | P0 | tv, plugin | — | 2026-10-29 |
| T-019 | Revue : robustesse du plugin (vieilles WebView, billboard hors ligne, stockage plein…) | À valider | P1 | plugin, tv | — | 2026-10-29 |
| T-020 | Revue : performances (mesure des rangées, observateur, langue, boutons) | À valider | P2 | plugin, tv | — | 2026-10-29 |
| T-021 | Organiser les rangées de l'Accueil (ordre, masquer), par spectateur | À valider | P2 | plugin | — | 2026-10-29 |
| T-022 | Code et avatar par profil, verrouillage après inactivité | À valider | P2 | plugin | — | 2026-10-29 |
| T-023 | Téléchargements hors ligne | À faire | P3 | tv | — | 2026-10-29 |
| T-024 | Bouton « Lecture » absent de la page d'une série ; croix de Continuer de regarder retirée | À valider | P1 | plugin | T-016 | 2026-10-29 |
| T-025 | Profils façon Netflix, bouton profil en haut à droite, menu du compte dans les Réglages | À valider | P1 | plugin | T-022 | 2026-10-29 |
| T-026 | Release 0.3.0 ; releases sans plugin ni installeurs | Fait | P1 | release | — | 2026-10-29 |
| T-027 | Profils synchronisés entre appareils via le compte Stremio (Worker + D1) | À valider | P1 | plugin, tools | T-022 | 2026-10-29 |
| T-028 | Release 0.4.0 (synchro des profils, première APK signée) | Terminé | P1 | release, tv, plugin | T-027 | 2026-10-29 |
| T-029 | Profils absents jusqu'au redémarrage après la connexion | À valider | P1 | plugin | T-027 | 2026-10-30 |
| T-030 | Connexion obligatoire : sans compte, seulement la page de connexion | À valider | P1 | plugin | — | 2026-10-30 |
| T-031 | Continuer à regarder propre à chaque profil | À valider | P1 | plugin, tools | T-022, T-027 | 2026-10-30 |
| T-032 | Progression séparée pour chaque profil | À faire | P2 | plugin, tv | T-031 | 2026-10-30 |
| T-033 | Continuer à regarder pas à jour sur le 2e appareil sans forcer l'arrêt de l'app | À valider | P1 | plugin | T-031 | 2026-10-30 |
| T-034 | Choix du profil à l'ouverture, au retour et après un film ; « Ne plus me demander » | À valider | P1 | plugin | T-025 | 2026-10-30 |
| T-035 | Refonte du choix de profil et des pages de profil, comme Netflix | À valider | P1 | plugin | T-025 | 2026-10-30 |
| T-036 | Release 0.5.0 (nouveau logo, connexion obligatoire, profils façon Netflix) | Terminé | P1 | release, tv, plugin, app | T-003 | 2026-10-30 |
| T-037 | Intro en portrait sur Android | À valider | P2 | tv, tools | T-003 | 2026-10-30 |

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

- **Statut :** À valider (icônes et intro sur les appareils)
- **Priorité :** P2
- **Cible :** tv, plugin, app, tools, docs
- **Dépend de :** T-002
- **Créée le :** 2026-10-28
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Je ferai un nouveau logo plus tard aussi.

Puis, le 2026-10-30 :
> J'ai fait deux nouveaux logos pour Nathflix, un logo "Nath" pour tout les icones d'apps et un pour remplacer le texte branding text "Nathflix" qui n'a pas la deformation comme le logo Netflix original. Pour l'icone ajoute un peu de padding sur les bords quand même et un fond pour les icones d'app, fait en sorte que ça soit comme le logo des apps Netflix. Remplace bien tout les logos, icons, et les branding text pour utiliser ces nouveaux. Une fois que tu aura terminé, fait moi une nouvelle release.

> Pour l'intro utilise plutôt le logo Nath que le texte Nathflix entier

**Critères d'acceptation**
- [x] Sources dans `branding/` (`nathflix-wordmark.png`, `nathflix-mark.png`), recadrées sans les pixels presque transparents
- [x] Icônes desktop : `app/images/icon*.png`, `icon.ico` (16 à 256), `icon.icns`, `presence/logo.png`
- [x] Icônes Android :
  - `mipmap-*/ic_launcher.png` ;
  - icône adaptative (`mipmap-anydpi-v26`, `ic_launcher_foreground.png`, fond noir) ;
  - bannière TV `drawable/banner.png` (320×180) et `drawable-xhdpi/banner.png` (640×360)
- [x] Plugin et thème :
  - wordmark dans la barre du haut et sur « Qui est-ce ? » ;
  - mark NATH sur le splash de secours. Devant « Série » / « Film » (billboard, bandes-annonces) : d'abord le mark, puis de nouveau le texte « NATHFLIX » en Bebas Neue, à la demande de l'utilisateur (« Sur le billboard je préférais la typo fine avec les textes "Nathflix" qu'on avait avant ») ;
  - le tout suit l'accent
- [x] Intro : le mark NATH lettre par lettre, avec ses plis ; `introVideoData()` et `res/raw/intro.mp4` refaits ; aperçu `screenshots/intro.webp`
- [x] `screenshots/banner.jpg` (bandeau du README)
- [ ] Autres captures du README (Accueil, Découvrir, aperçu, Accueil TV, réglages « dx.bi Settings ») : encore l'ancien logo
- [ ] Vérifié sur les appareils (icône du lanceur, bannière TV, intro native)

**Notes techniques**
- `tools/make_brand.py` (Pillow) fabrique toutes les images depuis `branding/` :
  - icônes : NATH à 64 % sur un carré noir arrondi (22 %), 4 % de marge ;
  - adaptative : NATH sur 54 dp de large dans 108 dp, pour tenir dans le cercle de 66 dp qu'aucun lanceur ne coupe ;
  - `branding/web/` : wordmark en masque blanc (PNG, 120 px de haut) et mark en WebP (240 px, net en grand sur le splash).
- `tools/embed_brand.mjs` met ces deux images dans `brandImages()` (adresses `data:`, environ 48 ko).
  - `applyAccent()` pose `--dx-wordmark`, `--dx-mark`, `--dx-mark-filter` (le `hue-rotate` d'`accentFilter()`, comme le film) et `html.dx-brand`.
  - Le CSS ne change le texte en image qu'avec cette classe : le texte reste pour les lecteurs d'écran (taille 0).
  - Sur « Qui est-ce ? », les règles de taille de la porte sont plus spécifiques : `overflow: hidden` et texte transparent.
- Intro (`render_intro.py`) :
  - le mark est découpé en N | A | T | H. Les lettres se chevauchent, d'où des coupes qui suivent le dessin : barre du T en haut à partir de 49,1 %, jambe à partir de 56,5 %, H à partir de 74,4 % ;
  - la couverture garde les plis (rouge du pixel / 0,92) ;
  - 4 lettres espacées de 0,15 s (même durée qu'avant) ; `intro_audio.py` a 4 « tic » au même rythme ;
  - plus besoin de la police Bebas Neue.
- `Updates.forTv()` retrouve toujours `introVideoData()` (même forme : la version TV du plugin passe de 1,54 Mo à 1,05 Mo).
- Le message « Error parsing Opus packet header » au décodage du WebM existait déjà avec l'ancienne intro.

**Vérification**
- Planches des images produites (icônes, cercle adaptatif simulé, bannières) et images-clés de l'intro (lettres colorées par découpe, 7 instants).
- Playwright (web.stremio.com, plugin injecté) : wordmark dans la barre du haut et sur « Qui est-ce ? », « NATH FILM » sur le billboard, splash NATH net.
- `node --check`, tests du Worker, banc d'essai, dictionnaire FR (pas de nouvelle clé manquante).

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

- **Statut :** À valider (le son sur l'appareil ; l'APK signée existe depuis la release 0.4.0, T-028)
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
- [x] `nathflix-tv.apk` signée : secrets ajoutés le 2026-10-29, première APK dans la release v0.4.0 (T-028). L'utilisateur n'a pas voulu en joindre aux releases précédentes.
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

### T-018 — Revue : plantage, gel, faille, écran noir, torrents

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P0
- **Cible :** tv, plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Fait une passe de revue de l'app, pour vérifier de potentiels soucis, bugs, optimisation possible, soucis de performances, corrections à apporter, etc. […] Oui, corrige tout

**Critères d'acceptation**
- [x] Plus de plantage « Session ID must be unique » en passant à l'épisode suivant
- [x] Bibliothèque triée par Vus / Non vus sans résultat : plus de gel
- [x] Adresses d'images des addons échappées (plus d'injection possible via `style`)
- [x] Plus d'écran noir bloqué en relançant un flux laissé en PiP ou en arrière-plan
- [x] Épisode suivant : le bon flux, jamais celui de l'épisode précédent
- [x] Torrents :
  - un torrent remplacé s'arrête ;
  - un 2ᵉ torrent ne reste pas bloqué derrière le 1ᵉʳ ;
  - chaque fichier d'un pack a sa propre adresse.
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `PlayerActivity` :
  - `finishWith()` libère la session et les contrôles tout de suite ; l'id de session est unique (`nathflix-player-<nanoTime>`).
  - `ownPlayback` : un lecteur remplacé ne renvoie plus ni progression ni fermeture à la page (`ownsPlayback()`).
  - `closeQuietly(keepTorrent)` arrête son torrent, sauf si le nouveau film lit le même.
  - `setWakeMode(WAKE_MODE_NETWORK)` en lecture en arrière-plan (permission `WAKE_LOCK`).
  - `configChanges` gagne `locale|layoutDirection|fontScale|density`.
  - Le statut du torrent est lu hors du thread principal.
  - Ouvert depuis la notification sans film, il se ferme sans rien envoyer.
- `TorrentEngine` :
  - clé `hash/fichier` : adresse `/t/<hash>/<idx>`, l'ancienne `/t/<hash>` reste lue ;
  - `close(key)` ne ferme que le torrent de ce lecteur ;
  - pool de threads en cache au lieu d'un thread unique ;
  - session en pause quand rien ne joue ;
  - ancien dossier supprimé en arrière-plan ;
  - plages `bytes=-N` ;
  - affectation de `active` sous verrou.
- `nathflix-tv.js` :
  - `onRoute` oublie le `handoff` hors du lecteur ;
  - `startHandoff().catch` retire le voile ;
  - `forThisRoute()` compare `streamRequest.path.id` au dernier segment de la route ;
  - `playerClosed` ignore la fermeture d'un autre flux.
- Plugin :
  - `libraryWatched` n'écrit son texte que s'il change, et `.dxl-empty` est ignoré par l'observateur ;
  - `cssUrl()` pour toute image placée dans `url()`.
- Mineurs :
  - `Updates` : `isDestroyed`, réessais seulement sur erreur réseau, APK supprimée ensuite ;
  - `Intro` : son coupé à `onStop`, `Surface` libérée ;
  - hôte comparé exactement dans `onPageStarted` ;
  - `PlaybackBus.closed` synchronisé ;
  - listener vidé après un crash de la page.

**Vérification**
- Playwright, ancienne version puis nouvelle (`review-test.cjs`) :
  - Bibliothèque « Vus » vide : page **gelée** avant, réactive (16 ms) après, avec le texte ;
  - même flux relancé après un retour sans fermer le lecteur : 1 lancement avant, 2 après ;
  - épisode suivant avec l'état de Stremio en retard d'1,2 s : `tt1` relancé avant, `tt2` après.
- `cssUrl` : une adresse contenant `"` et `onmouseover=` reste dans `url("…")`.
- Kotlin : CI `android-dev.yml`. L'API `SessionManager.pause/resume/isPaused` est vérifiée dans les sources de libtorrent4j 2.1.0-39.

### T-019 — Revue : robustesse du plugin

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> (même demande que T-018)

**Critères d'acceptation**
- [x] Les fiches se chargent sur WebView 100 à 102 (sans `AbortSignal.timeout`)
- [x] Billboard : nouvel essai une minute après un démarrage hors ligne
- [x] Les réglages par défaut attendent la copie disque de `store`
- [x] Pas de rechargement en boucle de la langue ; caches anciens purgés
- [x] Les petits défauts de la revue sont corrigés
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `timeoutSignal(ms)` remplace les 38 `AbortSignal.timeout` (repli `AbortController` + `setTimeout`).
- `hero` :
  - `gen` : un chargement dépassé par un `reset()` n'écrit plus ;
  - liste vide → `loading = null` dans 60 s ;
  - les fiches complétées passent par `getMeta` (cache).
- `profileDefaults.apply()` attend `store.ready`.
- `langWatch` :
  - ne recharge que si `nathflix.lang` a bien été écrit ;
  - au plus une fois par minute (`sessionStorage` `nathflix.langReload`).
- `pruneOld()`, au démarrage et quand le stockage est plein : `nathflix.age:` et `nathflix.tmdb2imdb.` (30 j), `nathflix.eps.` (7 j).
- `ages` : une recherche en échec n'est pas enregistrée (`skip`, nouvel essai dans 10 min).
- `addonTools.open()` : garde `opening`.
- `reregisterSchema()` : l'effacement et l'enregistrement du schéma passent l'un après l'autre.
- `catalogChoices.urls()` garde le `?…` des adresses d'addons.
- `episodePlay` :
  - revérifie la route avant de cliquer ;
  - `findEpisode` ne réessaie pas plus d'une fois toutes les 2 s.
- `episodes.refresh` libère `loading` en `finally` ; `metaBases` ne garde pas une liste vide quand le core n'est pas prêt.
- `restrict.card` : `.catch`. Les flux en direct sont ignorés par `placeholder`.
- `nathflix-tv.js` :
  - boutons Save / Cancel / OK traduits ;
  - une seule recherche de serveur à la fois, garde sur `ctx` ;
  - jamais deux fenêtres ;
  - voile retiré si le toucher est annulé ;
  - la recherche rend le focus à la première flèche ;
  - garde sur `document.documentElement`.

**Vérification**
- Billboard sans `AbortSignal.timeout` : rien avant (erreurs `AbortSignal.timeout is not a function`), les deux titres Trakt après.
- Tests existants rejoués, résultats identiques à l'ancienne version :
  - billboard : Trakt, liste vide, addon retiré, deux listes ;
  - Reprendre : en cours, fini, rien ;
  - retours T-008 à T-013.
- Dictionnaire FR : 0 clé manquante.

### T-020 — Revue : performances

- **Statut :** À valider (test sur l'appareil, surtout une clé TV)
- **Priorité :** P2
- **Cible :** plugin, tv
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> (même demande que T-018)

**Notes techniques**
- `updateAllArrows` :
  - rien sur téléphone (les flèches y sont cachées) ;
  - ailleurs, il ne mesure que les rangées nouvelles, changées ou mesurées il y a plus de 3 s, et toutes au redimensionnement (`measuredRows`).
- Observateur `mo` : il ignore les mutations dont tous les nœuds ajoutés ou retirés sont des éléments du plugin (`ownNode`, classes `dx…`).
- `profileDefaults.watch` avance `next` avant `pending()`, qui relisait `localStorage.profile` à chaque passage.
- `langWatch` : toutes les 2 s dans Réglages, 15 s ailleurs.
- `episodePlay` et `hideButton` comparent le libellé enregistré (`data-label`) au lieu d'`innerHTML`.
- Kotlin :
  - `Http.client` partagé (`newBuilder()`) ;
  - `Lang.text` garde son contexte par langue ;
  - les regex de `TorrentEngine` sont compilées une fois ;
  - `PieceStream.read()` n'alloue plus rien.

**Vérification**
- Les tests Playwright de T-018 et T-019 donnent les mêmes résultats.
- Non vérifié : le gain réel sur une clé TV.

### T-021 — Organiser les rangées de l'Accueil

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Les features de Stremio supporters ne sont pas disponible sur Nathflix ? […] Je ne suis pas supporter, j'aimerais voir si cependant je peux porter ces features sur mon app quand même. […] Rangées de l'Accueil, Code et avatar par profil

On construit nos propres versions : l'abonnement de Stremio n'est pas débloqué.

**Critères d'acceptation**
- [x] Bouton « Organiser l'Accueil » au bas de l'Accueil (pas pour les profils enfants)
- [x] Fenêtre : glisser-déposer (poignée ⠿ ou souris), flèches, œil pour masquer, « Ordre de Stremio », Enregistrer
- [x] Ordre et rangées masquées par spectateur ; Continuer de regarder, la bannière et « À l'antenne » restent en haut
- [x] La télécommande suit le nouvel ordre ; Bas depuis la dernière rangée mène au bouton
- [ ] Vérifié sur l'appareil (avec la vraie page de Stremio)

**Notes techniques**
- `homeArrange` : préférences dans `store` sous `viewers.key("homeRows")` = `{ order, hidden }`.
  - Clés `<id du manifest>|<type>|<id du catalogue>`, jamais l'adresse de l'addon.
  - La rangée est identifiée sur la page par son lien « Voir tout » ; l'adresse est convertie en id de manifest (`ids`, rafraîchi toutes les 60 s).
- Les éléments de Stremio ne sont jamais déplacés :
  - le conteneur reçoit `dx-rows-arranged` (`display:flex; flex-direction:column`) ;
  - chaque enfant reçoit `style.order` : puces −40, bannière −30, Continuer −20, À l'antenne −10, rangées classées 1…, inconnues 1000+, bouton 100000 ;
  - masquées : `dx-row-hidden`.
- `homeRows.preload(…, force)` charge toutes les rangées quand un ordre est actif (Stremio les charge selon leur place).
- `keyNav.rows()` trie par position à l'écran quand l'ordre est actif.
- `dragList(list, onMove)` est extrait de `addonTools.wireDrag`, et partagé par les deux fenêtres.

**Vérification**
- Playwright (`supporter-test.cjs`, faux core, 2 addons, 4 rangées + un catalogue à recherche exclu) :
  - téléphone :
    - la liste montre les 4 rangées ;
    - flèches, œil et glisser à la souris donnent Beta New, Beta Hot, Alpha Top Movies à l'écran, Alpha Top Series masquée ;
    - les éléments de Stremio restent dans leur ordre ;
    - toutes les rangées sont demandées (`LoadRange`) ;
  - TV : Bas depuis Beta New → Beta Hot → Alpha Top Movies → « Organiser l'Accueil ».
- Non vérifié : sur la vraie page de Stremio (chargement des rangées, mise en page).

### T-022 — Code et avatar par profil, verrouillage après inactivité

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> (voir T-021) Code et avatar par profil

**Critères d'acceptation**
- [x] Un code à 4 chiffres par spectateur (Gérer les spectateurs → « Code »), demandé pour passer à ce profil
- [x] Changer ou retirer un code : l'ancien, ou le code parental ; supprimer un profil à code aussi
- [x] Clavier de code : chiffres masqués, au doigt, à la télécommande et au clavier ; le code parental l'utilise aussi
- [x] Galerie de 24 avatars (emoji sur une couleur), ou les initiales
- [x] Réglage « Verrouiller les profils après inactivité » (Jamais, 5, 15, 30 min, 1 h) ; jamais pendant un film
- [x] Écran verrouillé : Back ne le ferme pas, pas de « Gérer » ; un profil à code le demande à chaque démarrage
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `viewers.setPin` et `viewers.setAvatar` ; champs `pin` (SHA-256 de `<id>:<code>`, via `parentPin.hash`) et `avatar` (`AVATARS`, `avatarHtml()`).
- `viewerPin` : `check`, `authorize` (code du profil ou parental), `change` (nouveau code, confirmation, « Supprimer le code »).
- `pinPad.open({ title, note, length, verify, remove, cancel })` : un code faux secoue les cases et affiche « Code incorrect ».
- `parentPin.set` et `check` passent par `pinPad`. Changer le code parental demande l'actuel.
- `idleLock` :
  - activité : `pointerdown`, `keydown`, `wheel`, `touchstart` ;
  - contrôle toutes les 30 s et au retour de l'app (`visibilitychange`) ;
  - route `player` ou mini-lecteur : compte comme activité ;
  - rien à verrouiller avec un seul profil sans code.
- `whoIsWatching` :
  - `locked` ;
  - focus sur le profil courant ;
  - badge cadenas ;
  - boutons « Avatar » et « Code » ;
  - `back()`.
- `closePreview` (Back de la couche TV) ferme d'abord le clavier, puis recule dans « Qui regarde ? » (sauf s'il est verrouillé).

**Vérification**
- Playwright (`supporter-test.cjs pin`, 2 spectateurs, verrou à 5 min) :
  - « Qui regarde ? » au démarrage, focus sur le profil courant ;
  - code 1234 posé et confirmé pour Léo (hash enregistré) ;
  - renard choisi pour Nath ; cadenas sur Léo ;
  - code faux → « Code incorrect », le clavier reste ; le bon → profil Léo ;
  - 6 min plus tard → écran verrouillé sans « Gérer », Back ne le ferme pas ;
  - pendant un film (`#/player/…`), une heure plus tard : rien.
- Tests précédents rejoués, résultats identiques (retours, billboard, Reprendre, revue).

### T-023 — Téléchargements hors ligne

- **Statut :** À faire (future mise à jour, à la demande de l'utilisateur)
- **Priorité :** P3
- **Cible :** tv
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29

**Demande**
> Pour le téléchargement hors-ligne ça sera dans une future mis à jour, ça m'intéresse mais je vais pas le faire pour le moment

**Notes techniques** (pistes)
- L'équivalent du « gestionnaire de téléchargements » des Supporters de Stremio : télécharger un film ou un épisode pour le lire sans réseau dans le lecteur natif.
- Liens directs et debrid d'abord (Media3 `DownloadManager` / `DownloadService`). Les torrents viendraient ensuite.

### T-024 — Bouton « Lecture » de la page d'une série absent ; croix de Continuer de regarder

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-016
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Enlève la croix pour effacer les séries/films dans "Continuer de regarder", vu que le menu contextuel permet déjà de le faire […] Sur les détails d'une série, j'ai toujours pas le bouton "Lecture" qui lance l'épisode en cours ou le prochain épisode non-lu en prenant la première source (mais en laissant le temps aux sources de charger, ça prend environ 5 secondes je crois.

**Critères d'acceptation**
- [x] Le bouton apparaît sur la page d'une série avec les vraies données de Stremio
- [x] Plus de ✕ sur les cartes de Continuer de regarder ; « Effacer » reste dans le menu ⋮
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Cause :** dans `meta_details`, la fiche est `metaItem.content.content` (`metaItem` est la réponse de l'addon, `content` un `Loadable`), et non `metaItem.content` (vérifié dans `useMetaDetails.js` de stremio-web). `findEpisode` ne trouvait jamais les épisodes.
- Le faux core des tests avait la même erreur : `series-real-test.cjs` reprend la vraie forme.
- **L'attente des sources** ne change pas : jusqu'à ce que tous les addons aient répondu (12 s au plus), plus 700 ms.
- **La croix :** `[class*="dismiss-icon-layer"]` est masquée (`EXTRAS_CSS`). Le bouton « Retirer » de la carte de survol (ordinateur) la clique toujours.

**Vérification**
- Playwright avec la vraie forme de `meta_details` :
  - l'ancienne version n'affiche aucun bouton (comme sur l'appareil) ;
  - la nouvelle affiche « Reprendre S1 É2 », « Lancer S1 É3 » ou « Lancer S1 É1 », attend l'addon lent et lance la première source.
- Croix masquée : vérifié (`profiles-test.cjs`).

### T-025 — Profils façon Netflix, bouton profil en haut à droite, menu du compte dans les Réglages

- **Statut :** À valider (test sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-022
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Le menu en haut à droite du compte stremio est souvent par dessus d'autres éléments, comme le bouton de recherche, qui d'ailleurs est un peu petit et mal aligné. Je voudrais l'enlever carrément, pour le remplacer par un bouton de choix du profil (comme le fait "Qui regarde ?") qui doit fonctionner et être visuellement comme dans l'app Netflix […] pareil pour toute les fenêtres lié au profil, il faut toujours reprendre le design de Netflix. Pour le bouton menu stremio enlevé du coup, il faut le remettre dispo dans l'onglet réglage, là on a déjà le compte affiché.

**Critères d'acceptation**
- [x] Le menu du compte de Stremio n'est plus dans la barre du haut ; à sa place, l'image du profil courant, qui ouvre « Qui regarde ? »
- [x] Bouton de recherche à la taille du bouton profil, centré et aligné
- [x] « Qui regarde ? » façon Netflix :
  - fond noir, logo, grandes vignettes carrées, noms en gris ;
  - « Modifier » en haut à droite sur téléphone, « Gérer les profils » sous les profils sur TV et ordinateur.
- [x] Pages Netflix :
  - « Modifier le profil » / « Ajouter un profil » : icône, nom, Profil Enfants, Verrouillage du profil, Titres masqués, Supprimer le profil ;
  - « Choisir une icône » ;
  - « Supprimer le profil ? » ;
  - écran « Le verrouillage de profil est activé. ».
- [x] Réglages : sous le compte, Profils, Vos statistiques, Addons, Compte Stremio (et Signaler un problème quand il est actif)
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `whoIsWatching` réécrit avec des vues `gate | manage | edit | avatars | hidden | delete` et un brouillon (`draft`) pour la page d'un profil. Le nom tapé est gardé en passant aux icônes (`keepName`).
- Modifier un profil verrouillé demande son code ou le code parental (`viewerPin.authorize`).
- `pinPad` : option `eager` (un code de 4 chiffres passe sans OK, un code parental plus long reste possible). Fond noir opaque.
- `navAvatar` est ajouté à la fin de `buttons-container` de la barre du haut. Le menu de Stremio est masqué par CSS (`menu-button-container` dans la barre).
- `settingsMenu` : un bloc `.dxe-set-menu` après la section du compte (`user-info-content`). « Lire une URL / un lien magnet » n'a pas été repris : il passe par une fonction interne de Stremio.
- Thème (bloc téléphones) : la recherche fermée n'affiche que sa loupe (2,75 rem, icône 1,5 rem).

**Vérification**
- Playwright (`profiles-test.cjs`, téléphone et TV, faux Stremio avec la mise en page de base de sa barre) :
  - le menu de Stremio est masqué, la croix aussi ;
  - avatar 🦊 en haut à droite, aligné avec la loupe (même milieu, 28 px) ;
  - « Qui regarde ? » avec le focus sur le profil courant ;
  - Modifier → Gérer les profils → page de Léo :
    - nom « Léonard », Enfants 12+ ;
    - icône 🚀 ;
    - verrouillage 2468 posé et confirmé ;
    - enregistré ;
  - rouvrir Léonard demande son code ; « Supprimer le profil ? » puis Annuler ;
  - choisir Léonard affiche « Le verrouillage de profil est activé. », le code 2468 passe, et l'avatar du haut devient 🚀 ;
  - Réglages : le bloc suit le compte (Profils, Vos statistiques, Addons, Compte Stremio).
- Captures relues, téléphone et TV.
- Non vérifié : la vraie barre de Stremio sur l'appareil.

### T-026 — Release 0.3.0 et fichiers des releases

- **Statut :** Fait
- **Priorité :** P1
- **Cible :** release, tv, plugin
- **Dépend de :** —
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`, puis `main`

**Demande**
> J'aimerais que tu me fasse une nouvelle release (sans l'apk signé du coup, pas grave). Mais par contre, pour les release je voudrais garder seulement les fichiers exe windows portable et setup, les deux images linux, les deux images mac, et l'apk. Le reste je voudrais l'enlever

Réponses ensuite :
- garder `latest*.yml` et `.blockmap` (mise à jour automatique de l'app desktop) ;
- fusionner dans `main` puis faire la release ;
- ne pas toucher à v0.2.0 ;
- version 0.3.0.

**Critères d'acceptation**
- [x] La release ne joint plus `nathflix.plugin.js`, `install.sh` ni `install.ps1`
- [x] La mise à jour du plugin sur Android lit `raw.githubusercontent.com/<dépôt>/<tag>/nathflix.plugin.js`
- [x] Les installeurs, le README et la CI pointent vers les fichiers de `main`
- [x] `@version 0.3.0`, section `## 0.3.0` du CHANGELOG
- [x] PR fusionnée dans `main` (lavagnou/dxbi-stremio-android#2)
- [x] Release v0.3.0 publiée : exe (installation, portable), dmg ×2, AppImage ×2, `latest*.yml`, `.blockmap` ; l'APK manque (pas de clé)

**Notes techniques**
- `release.yml` : `gh release create` sans fichiers. Les notes renvoient vers le README pour le plugin.
- `Updates.checkPlugin` : la dernière release (`RELEASES_URL`), puis le plugin au tag de cette release. Jamais le travail en cours.
- `install.sh` / `install.ps1` : `BASE_URL` par défaut = `https://raw.githubusercontent.com/lavagnou/dxbi-stremio-android/main`.
- `cross-platform.yml` : les one-liners du README sont testés depuis `main` (ils l'étaient depuis la dernière release).

**Vérification**
- `node --check`, `bash -n install.sh`, section `## 0.3.0` lue comme `release.yml` (30 lignes).
- `main/install.sh` répond (200).
- **CI de la PR** : `mpv-windows.yml` échouait depuis T-009 (`744fe52`) sur « embedded subtitles ».
  - Cause : l'app de test démarre maintenant Stremio en français, donc le bouton est « Sous-titres ». `tools/mpv-test.mjs` le cherchait par son nom anglais (`data-dx-label="Subtitles"`).
  - Correctif : il le cherche par son icône (`data-dx-icon="subtitles"`), et la piste sous « Track 1 » ou « Piste 1 ».
  - Résultat : Windows, Linux, macOS et mpv verts.
- **Release** : PR fusionnée (`1d4a04b`), puis `release.yml` lancé à la main avec 0.3.0 (run 40947636619, vert).
- **Fichiers de la release** (lus par l'API GitHub) : les 2 exe, le `.blockmap`, les 2 dmg, les 2 AppImage et les 3 `latest*.yml`. Pas de plugin ni d'installeurs.
- **Nouvelles adresses** : `main/install.sh`, `main/nathflix.plugin.js` et `v0.3.0/nathflix.plugin.js` (`@version 0.3.0`) répondent tous en 200.

### T-027 — Synchroniser les profils entre appareils (compte Stremio, Worker + D1)

- **Statut :** À valider (déploiement du Worker, puis test sur les appareils)
- **Priorité :** P1
- **Cible :** plugin, tools
- **Dépend de :** T-022, T-025
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `claude/quirky-cannon-g7e9oy`

**Demande**
> Je veux que les profils Nathflix se synchronisent entre tous mes appareils (et ceux des gens à qui je partage l'app), via le compte Stremio de chacun. […] un Cloudflare Worker gratuit avec une base D1 (pas KV) […] La clé ne doit jamais être stockée par le Worker.

Choix de l'utilisateur :
- **Stats** synchronisées et **fusionnées** : le plus haut compteur l'emporte.
- **Interrupteur** `profileSync`, activé d'office.

**Critères d'acceptation**
- [x] Worker `tools/profile-sync/` :
  - D1 (`entries` : `account`, `k`, `t`, `v`, `s`) ;
  - identification par `POST https://api.strem.io/api/getUser` `{"type":"GetUser","authKey"}` → `result._id` (vérifié dans stremio-core) ;
  - la clé n'est jamais stockée.
- [x] Clés acceptées :
  - `viewers`, `libraryOwners`, `kidsAllow`, `parentPin` ;
  - `homeRows:*`, `hidden:*`, `cwDismissed:*`, `stats:*` ; `played:*` depuis T-031.

  Pas les caches.
- [x] Limites :
  - 1 Mo par compte, 100 Ko par valeur, 200 entrées par requête ;
  - 60 requêtes par minute et par adresse, 30 par minute et par compte.
- [x] CORS : `https://web.stremio.com` seulement.
- [x] Plugin :
  - `SYNC_ENDPOINT` (vide = rien ne change) ;
  - envoi groupé (20 s, 60 s au plus) ;
  - réception au démarrage et au retour au premier plan ;
  - hors ligne : tout continue en local, puis l'envoi suit.
- [x] Indicateur dans Réglages, sous Profils.
- [x] Guide Cloudflare (`tools/profile-sync/README.md`).
- [x] Worker déployé et `SYNC_ENDPOINT` rempli : `https://nathflix-profiles.profile-sync.workers.dev/sync`
- [ ] Vérifié sur les appareils (APK de la release 0.4.0) :
  - deux appareils connectés au même compte Stremio ;
  - un profil modifié sur l'un apparaît sur l'autre après retour au premier plan ;
  - Réglages → Profils : « Profils synchronisés avec votre compte Stremio ».

**Notes techniques**
- **Worker** :
  - une seule route, `POST /sync` `{ since, entries }` → `{ now, entries }` ;
  - la plus récente gagne (`ON CONFLICT … WHERE excluded.t > entries.t`) ;
  - `s` est l'heure du serveur, avec une minute de recouvrement sur `since` ;
  - un cache en mémoire relie l'empreinte SHA-256 de la clé à l'identifiant du compte (10 min).
- **Plugin** (`cloudSync`, juste après `store`) :
  - `store.set` appelle `cloudSync.changed()` ; `store.put()` écrit une entrée reçue avec son propre `t`, sans la renvoyer.
  - **Fusion** :
    - cas général : la plus récente gagne ;
    - `viewers` : le profil courant reste celui de l'appareil ;
    - `stats:*` : `mergeStats` garde le maximum par jour, heure, titre et genre. Si la fusion apporte quelque chose de local, elle repart avec un `t` neuf.
  - **Stats > 100 Ko** : envoyées sans les affiches (`p`) ; si c'est encore trop, elles restent locales.
  - **Compte** :
    - premier appareil : réception, puis envoi de tout le local ;
    - autre compte connecté sur l'appareil (`nathflix.syncAccount`) : les entrées synchronisées locales passent à `t: 1`, et celles du compte arrivent.
    - `nathflix.syncSince:<compte>` dans `localStorage`.
  - **Erreurs** :
    - 401 : arrêt jusqu'à un changement de clé ;
    - 413 : pause d'une heure ;
    - 429 : 5 min ;
    - réseau : 1, 5, puis 15 min.

    Ce qui n'a pas pu partir reste à envoyer. À la mise en arrière-plan, l'envoi part avec `keepalive`.
  - Réglage `profileSync` : n'apparaît que si `SYNC_ENDPOINT` est rempli (filtre de `registerSchema`).
- **Déploiement** (compte Cloudflare de l'utilisateur) :
  - Worker `nathflix-profiles`, base D1 `nathflix-profiles` (`database_id` dans `wrangler.toml`), schéma appliqué avec `--remote` ;
  - sous-domaine `workers.dev` du compte : `profile-sync`, pris tout seul par wrangler au premier `deploy`. L'API refuse de le changer ensuite ; l'utilisateur l'a gardé ;
  - Cloudflare Access bloquait d'abord tout (page 403 « Default-Deny policy », même sur `OPTIONS`). L'utilisateur a ajouté une règle Bypass pour tout le monde, sur l'adresse du Worker seulement ;
  - les deux pièges sont notés dans `tools/profile-sync/README.md`.

**Vérification**
- `node tools/profile-sync/test.mjs` (vrai `worker.js`, `node:sqlite` à la place de D1, Stremio simulé) : 16 vérifications passées.
  - CORS, 401 ;
  - envoi et réception, la plus récente gagne, `since` ;
  - clé refusée hors liste, 413 sur 100 Ko et sur 1 Mo, date future refusée ;
  - Stremio pas redemandé ;
  - clé jamais stockée, rangement par compte.
- Playwright (`profile-sync-test.cjs`, deux appareils, vrai Worker) :
  - **A → B** : profils, avatars, code, titres masqués, rangées. Les caches ne partent pas. B garde son profil courant.
  - **B → A** : l'avatar de Léo ; A garde son profil courant.
  - **Stats fusionnées** des deux côtés : aujourd'hui 600, la veille 100, Drama et Comedy.
  - **Hors ligne** : le changement reste local, puis part au retour du serveur, et B le reçoit.
  - **Indicateur** : « Profils synchronisés avec votre compte Stremio · à l'instant ».
  - **Interrupteur coupé** : 0 requête.
  - **Changement de compte** : profils par défaut, et les 4 lignes de `u1` intactes.
- Tests précédents rejoués : profils, rangées, revue, Reprendre, billboard. Dictionnaire FR : 0 clé manquante.
- **Worker en ligne** (2026-10-29) :
  - `OPTIONS /sync` avec `Origin: https://web.stremio.com` → 204, `Access-Control-Allow-Origin: https://web.stremio.com` ;
  - `POST` sans `Authorization` → 401 ; avec une fausse clé → 401 (Stremio interrogé) ;
  - `node tools/profile-sync/test.mjs` repassé, `node --check` du plugin.
- Pas vérifié : un `POST` avec une vraie clé (la commande `!` ne permet pas de la saisir sans l'écrire quelque part), ni l'indicateur des Réglages dans un navigateur (Playwright pas installé, et il faudrait un compte connecté). Les deux se vérifient sur les appareils.

### T-028 — Release 0.4.0 (synchro des profils, première APK signée)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin
- **Dépend de :** T-027
- **Créée le :** 2026-10-29
- **Mise à jour :** 2026-10-29
- **Branche :** `main`

**Demande**
> Release : demande-moi le numéro. Je propose 0.4.0 (nouvelle fonction : la synchro des profils). […] android, qui doit maintenant construire nathflix-tv.apk.

Réponses ensuite :
- version 0.4.0 ;
- pas d'APK ajoutée après coup à v0.3.0.

**Critères d'acceptation**
- [x] `@version 0.4.0`, section `## 0.4.0` du CHANGELOG, `node tools/bundle-theme.mjs` (PLUGIN_VERSION, `app/version`, `app/package.json`)
- [x] Release v0.4.0 publiée : 2 exe, `.blockmap`, 2 dmg, 2 AppImage, 3 `latest*.yml`, `nathflix-tv.apk`, et rien d'autre
- [x] APK signée par la clé de release (`CN=Nathflix, O=lavagnou`), pas par `nathflix-dev`

**Notes techniques**
- Première release avec les secrets de signature : `tv.yml` construit enfin `nathflix-tv.apk`.
- L'APK n'a pas de signature v1 (minSdk 24 : v2 seulement). `keytool -printcert -jarfile` répond donc « Not a signed jar file » : vérifier avec `apksigner verify --print-certs`.
- Empreinte SHA-256 du certificat de release : `22:AD:17:73:69:FE:F8:DC:9A:50:ED:01:C1:9C:3C:F1:71:4C:79:BC:6A:88:B3:C7:2E:D8:A4:60:77:FE:96:2B`. Toute APK de release doit avoir celle-ci, sinon Android refuse la mise à jour.

**Vérification**
- CI de `f681811` (SYNC_ENDPOINT) verte avant la release : Cross-platform, mpv Windows, Android.
- Tag `v0.4.0` poussé sur `b52b264` ; run « Release » 40967283302 vert : publish, android / apk, desktop ×5.
- `gh release view v0.4.0` : 11 fichiers, exactement ceux attendus, sans plugin ni installeurs.
- `apksigner verify -v --print-certs nathflix-tv.apk` : « Verifies », v2, `CN=Nathflix, O=lavagnou`, RSA 4096, SHA-256 identique à la clé.
- `aapt2 dump badging` : `com.lavagnou.nathflix`, versionCode 400, versionName 0.4.0, ABI arm64-v8a et armeabi-v7a. Le plugin embarqué est en 0.4.0, avec l'adresse du Worker.

**Livraison**
- Commits : `f681811` (Worker branché), `b52b264` (0.4.0)
- Version / CHANGELOG : 0.4.0
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.4.0

### T-029 — Profils absents jusqu'au redémarrage après la connexion

- **Statut :** À valider (sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-027
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Ca fonctionne, mais sur Android, j'ai du redémarrer l'app pour que mes profils apparaissent.

**Critères d'acceptation**
- [x] Se connecter à Stremio fait arriver les profils du compte sans redémarrer
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Cause** : `cloudSync` ne synchronisait qu'au démarrage (`start()`, 3 s après) et au retour au premier plan. Sur une installation neuve, on n'est pas connecté au démarrage (`run()` s'arrête en `state = "signin"`). Ensuite, la connexion à Stremio ne recharge pas la page et ne déclenche pas `visibilitychange`.
- **Correctif** : `cloudSync.watch()`, dans `sync()`, au plus toutes les 5 s (`auth()` lit tout le profil enregistré). Il garde la clé de session vue (`seenKey`) et lance `run()` dès qu'elle change, en oubliant le délai d'un échec précédent. `signInGate` l'appelle tout de suite à la connexion (`watch(true)`).

**Vérification**
- Banc d'essai (scratchpad, `viewer-cw-test.mjs`, qui exécute le vrai `cloudSync` avec des doublures) : pas de requête sans session, une requête dès la connexion, profils du compte reçus, pas de deuxième requête pour la même session.
- Playwright (web.stremio.com, plugin injecté) : juste après une fausse connexion, une requête part vers le Worker (401, puisque la clé était fausse).

### T-030 — Connexion obligatoire : sans compte, seulement la page de connexion

- **Statut :** À valider (sur l'appareil, avec une vraie connexion)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> tant qu'on est pas connecté a notre compte stremio il faudrait que l'app affiche seulement la page de connexion a son compte stremio, car sans compte tout façon l'app est inutilisable.

Réponse ensuite : partout (Android, desktop, Stremio Enhanced).

**Critères d'acceptation**
- [x] Sans compte, toute route renvoie vers `#/intro`
- [x] « Guest login » masqué ; Inscription, Facebook, Apple et Connexion restent
- [x] « Qui regarde ? » et le verrou d'inactivité ne passent jamais devant la connexion
- [x] Après la connexion : synchro, puis « Qui regarde ? » s'il y a plusieurs profils
- [ ] Vérifié avec une vraie connexion sur l'appareil

**Notes techniques**
- Stremio n'y redirige pas lui-même : sans compte, il ouvre l'Accueil en invité.
- `signInGate.check()` (dans `sync()`) :
  - lecture rapide du profil enregistré, au plus toutes les 5 s une fois connecté ;
  - sinon, confirmation par le core (`getState("ctx")`, `profile.auth` nul) avant `location.replace("#/intro")`. Le profil enregistré n'arrive qu'un moment après la connexion : sans cette confirmation, on renverrait l'utilisateur à la connexion juste après qu'il s'est connecté.
- Bouton invité : `[class*="intro-container"] [class*="guest-login-button"]` dans `EXTRAS_CSS` (toujours chargé, même sans le thème).
- **CI** : `cross-platform.yml` et `mpv-windows.yml` ont échoué sur `728507a`. Les tests lancent Stremio sans compte : la page restait sur `#/intro` (pas de billboard, mpv jamais lancé).
  - Correctif : `localStorage` `nathflix.allowGuest = "1"`, posé par `smoke-test.mjs` et `mpv-test.mjs`, laisse le mode invité.
  - `smoke-test.mjs` vérifie en plus, sans le drapeau, la redirection vers la connexion et le bouton invité masqué.

**Vérification**
- Playwright, web.stremio.com sans compte, plugin injecté :
  - l'Accueil passe à `#/intro` en moins d'une demi-seconde ;
  - `#/discover` et `#/settings` y reviennent ;
  - bouton invité invisible, les quatre autres visibles (en français) ;
  - pas de « Qui regarde ? » ; aucune erreur du plugin.
- Avec une session dans le profil enregistré : plus de renvoi (Découvrir, Réglages). Pas vérifié : la confirmation par le core avec une vraie session. Ma fausse session était refusée par le core (« missing field email »).

### T-031 — Continuer à regarder propre à chaque profil

- **Statut :** À valider (sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin, tools
- **Dépend de :** T-022, T-027
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Si je regarde une série/film sur un deuxième profil, il apparait des mes "Continuer a regarder" sur mon profil à moi(le principal). Il faut pas que ça soit le cas

Réponses ensuite :
- c'est arrivé sur un seul appareil ;
- on ne sait pas si le titre était nouveau ;
- règle choisie : « Ce que j'ai lancé » ;
- l'utilisateur aimerait aussi une progression séparée (T-032).

**Critères d'acceptation**
- [x] Un profil ne voit dans Continuer à regarder que ce qu'il a lancé lui-même
- [x] Un titre lancé par deux profils est chez les deux
- [x] Rien ne disparaît le jour de la mise à jour : un titre que personne n'a relancé depuis garde la règle d'avant
- [x] `played:<spectateur>` synchronisé ; le Worker accepte la clé (redéployé avant le plugin)
- [ ] Vérifié sur l'appareil

**Notes techniques**
- **Cause probable** : `viewerLibrary.claim()` partage un titre avec le premier profil dès que le compte l'a déjà dans sa bibliothèque, y compris en « temp » (toute lecture passée, même dans l'app Stremio officielle). Sur un compte avec de l'historique, presque tout ce que lance le profil 2 restait donc aussi chez le principal. Le chemin d'un titre neuf (réclamé pour le profil 2 seul) n'a pas montré de faille.
- **Règle** (`viewerLibrary.inProgress()`) : la bibliothèque et Ma liste gardent `libraryOwners`. Continuer à regarder (`continue_watching`, `continue_watching_preview`) suit `played:<spectateur>` (id → date, `markPlayed()` sur `Load` `Player`, 400 titres au plus). Si quelqu'un a lancé le titre : seulement ceux qui l'ont lancé ; sinon : règle de la bibliothèque.
- Enregistré même avec un seul spectateur : un profil ajouté plus tard ne prend pas les titres du premier.
- Fusion à la synchro : la date la plus récente par titre, comparée par contenu (pas par ordre des clés).
- Suppression d'un profil : `played:<id>` effacé.
- Worker : `played` ajouté à `KEY`, test ajouté dans `test.mjs`, redéployé (version `cc0325fc`).

**Vérification**
- `node tools/profile-sync/test.mjs` : le nouveau test échouait avant la modification du Worker, tout passe après.
- Banc d'essai `viewer-cw-test.mjs` (vrai `viewerLibrary`, core simulé) :
  - avant toute lecture, le principal voit tout ;
  - le profil 2 commence vide, puis voit ce qu'il a lancé (titre connu du compte et titre neuf) ;
  - le principal ne voit plus ce que le profil 2 a lancé ; après l'avoir lancé lui aussi, il est chez les deux ;
  - avec un seul profil, les lancements sont enregistrés ;
  - fusion : les titres des deux appareils sont gardés, rien n'est renvoyé si le contenu est identique.
- Worker en ligne après redéploiement : 204, 401, 401.

### T-032 — Progression séparée pour chaque profil

- **Statut :** À faire
- **Priorité :** P2
- **Cible :** plugin, tv
- **Dépend de :** T-031
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** —

**Demande**
> j'aurais aimé avoir une progression séparé aussi sur les deux comptes, à voir si c'est possible, sinon tant pis.

**Besoin**
Stremio ne garde qu'une progression par titre pour tout le compte (`libraryItem.state` : `timeOffset`, `video_id`, épisodes vus). Chaque profil devrait reprendre là où lui s'est arrêté.

**Pistes**
- Retenir la position par profil (`progress:<spectateur>` : id → `{ video, t, d, at }`), synchronisée, à ajouter au Worker.
- Écrire :
  - Android : les positions renvoyées toutes les 5 s par le lecteur natif (`nathflix-tv.js`) ;
  - desktop : l'état du lecteur.
- Relire :
  - remplacer `libraryItem.state` dans `getState` (`player`, `meta_details`, `continue_watching*`) par celui du profil ;
  - sur Android, la position de départ passée au lecteur natif. Attention, l'APK est plus vieille que le plugin : détecter avant usage.
- À trancher : les épisodes « vus » (champ `watched` du compte), le bouton « Reprendre S1 É3 », le floutage des épisodes non vus.

### T-033 — Continuer à regarder pas à jour sur le 2e appareil sans forcer l'arrêt de l'app

- **Statut :** À valider (sur deux appareils Android)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-031
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Si je passe sur un deuxième appareil, je ne vois pas dans "Continuer à regarder" ce que je viens de regarder sur l'appareil précédent, pour que ça apparaisse je dois forcer la fermeture de l'app et la ré-ouvrir, même si je la ferme normalement(dans les apps android par exemple) et que je la ré-ouvre ça ne suffit pas.

**Critères d'acceptation**
- [x] Au retour au premier plan, Stremio recharge la bibliothèque du compte
- [x] La synchro des profils (`played:*`) repart aussi au retour (toutes les 15 s au plus, au lieu d'une minute)
- [ ] Vérifié sur deux appareils

**Notes techniques**
- **Cause** (lue dans `main.js` de Stremio Web) : la bibliothèque n'est rechargée (`PullAddonsFromAPI`, `PullUserFromAPI`, `SyncLibraryWithAPI`, `PullNotifications`) qu'au chargement de la page et sur `window` `focus`. Une WebView Android n'envoie pas `focus` au retour au premier plan. Android garde aussi souvent la page en mémoire quand on « ferme » l'app : seul l'arrêt forcé rechargeait la page.
- **Correctif** : `comeBack.onVisible` (`visibilitychange`). Sur Android (`IN_ANDROID`), il envoie un `focus` à la fenêtre, au plus toutes les 15 s, pour réutiliser le code de Stremio, puis `whoIsWatching.onVisible()`.

**Vérification**
- Pas de reproduction possible ici (aucun appareil branché, pas de compte de test). Le comportement de Stremio a été vérifié dans son code en ligne. Le passage au premier plan (`visibilitychange`) est testé dans Playwright via T-034.

### T-034 — Choix du profil à l'ouverture, au retour et après un film ; « Ne plus me demander »

- **Statut :** À valider (sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-025
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Par défault quand on ouvre l'app, ça doit afficher le choix de profil, et on doit rajouter un bouton "Ne plus me demander"

Réponses ensuite :
- au démarrage, quand on revient sur l'app, et quand on arrête la lecture et qu'on revient sur l'accueil ;
- aussi avec un seul profil ;
- « Ne plus me demander » est une case à cocher, pas un bouton.

**Critères d'acceptation**
- [x] Au démarrage, au retour dans l'app (après plus de 5 s, jamais en sortant du lecteur), sur l'Accueil après un film
- [x] Aussi avec un seul profil
- [x] Case « Ne plus me demander » : coupe le réglage tout de suite ; rouverte depuis le menu, elle est cochée et la décocher le remet
- [x] Menu du profil : « Changer de profil », toujours présent
- [x] Réglage renommé « Demander qui regarde », avec une nouvelle description
- [ ] Vérifié sur l'appareil

**Notes techniques**
- `whoIsWatching.maybeShow({ start })` : plus de minimum de deux profils. Il ne s'ouvre ni par-dessus le lecteur ou la connexion, ni s'il est déjà ouvert. Un profil avec code reste demandé à chaque démarrage, même réglage coupé.
- `onVisible` retient l'heure et la route au passage en arrière-plan : sur Android, le lecteur natif cache la page pendant la lecture, et la route est alors `player`.
- `onRoute(route, previous)` est appelé par `updateRoute`. Après une sortie de `player`, la prochaine arrivée sur `board` ouvre le choix.
- La case (`[data-noask]`) : événement `change`, plus Entrée (OK de la télécommande), qui ne coche pas une case toute seule.

**Vérification**
- Playwright (web.stremio.com, plugin injecté, fausse session, `document.hidden` simulé) :
  - absent après 1,5 s d'absence, présent après 5,6 s (un seul profil) ;
  - absent sur `#/player/…` et sur la fiche du film, présent en arrivant sur l'Accueil ;
  - case cochée : réglage coupé, rien au retour suivant ;
  - ouvert depuis le menu : case cochée, la décocher remet le réglage.

### T-035 — Refonte du choix de profil et des pages de profil, comme Netflix

- **Statut :** À valider (sur l'appareil)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-025
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> La page de choix de profil de Netflix est mieux faite que celle de Nathflix, fait une petite refonte graphique pour que ça corresponde au design de Netflix parfaitement

Puis, avec des captures de Netflix :
- « Il faut aussi ajuster la page de modification de profil » ;
- le mode Gérer de Netflix : « je veux exactement la même chose » ;
- « sur Netflix il est circulaire (avec le centre sur le haut de la fenêtre) ».

**Critères d'acceptation**
- [x] Fond en dégradé rouge circulaire centré en haut ; logo NATHFLIX en haut à gauche ; « Qui est-ce ? »
- [x] Tuiles arrondies de 160 px, noms en gras gris clair, blancs à la sélection, anneau blanc intérieur
- [x] « Gérer les profils » : pilule avec crayon ; « Ajouter un profil » : tuile translucide avec un +
- [x] Mode Gérer : même titre, avatars assombris sous un crayon blanc, panneau clair arrondi sur le profil sélectionné, pas de tuile Ajouter, « Terminé » en pilule blanche avec coche
- [x] Page Modifier : grand titre, avatar arrondi, champ et réglages sur un panneau sombre arrondi, pilules Enregistrer, Annuler et Supprimer. Sur téléphone : barre du haut (retour, Enregistrer)
- [x] Icônes et suppression au même style
- [ ] Vérifié sur l'appareil (TV, téléphone)

**Notes techniques**
- CSS dans `EXTRAS_CSS`, bloc « Who's watching and its pages ».
- Pièges :
  - le bouton d'une tuile coupe ce qui dépasse : un anneau extérieur (`box-shadow` hors de l'avatar) était rogné, d'où l'anneau en `inset` ;
  - Stremio fixe la taille des `span` (15 px) : `font: inherit !important` dans les pilules et la case ;
  - les règles des tuiles de la porte sont plus spécifiques que celles du téléphone : largeurs redonnées sous `html.dx-mobile` ;
  - à l'inverse, `html.dx-mobile .dxe-who h1` (graisse 500) gagnait sur le titre : sur téléphone, « Qui est-ce ? » restait en graisse normale (capture de l'utilisateur). Titre en Inter 800, lettres resserrées (-0,025 em), graisse forcée sous `html.dx-mobile`.
- Une nouvelle tuile de profil se crée depuis la porte : Retour et Enregistrer y reviennent (et non au mode Gérer).

**Vérification**
- Captures Playwright à 1920×920 et 412×870 (`dx-mobile`) : porte, mode Gérer avec le premier profil sélectionné, page Modifier. Comparées aux captures de Netflix données par l'utilisateur.
- Pas vu sur une vraie TV (focus de la télécommande) : les styles `:focus` sous `.dx-tv` reprennent ceux du survol.

### T-036 — Release 0.5.0 (nouveau logo, connexion obligatoire, profils façon Netflix)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-003, T-029 à T-035
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Une fois que tu aura terminé, fait moi une nouvelle release.

Numéro : 0.5.0 (nouvelles fonctions depuis la 0.4.0).

**Critères d'acceptation**
- [x] `@version 0.5.0`, section `## 0.5.0` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] Release v0.5.0 : 2 exe, `.blockmap`, 2 dmg, 2 AppImage, 3 `latest*.yml`, `nathflix-tv.apk`, et rien d'autre
- [x] APK signée par la clé de release (`CN=Nathflix, O=lavagnou`)

**Vérification**
- CI verte sur `56139e4` et `4fc44ec` (logo) avant la release : Cross-platform, Android, mpv Windows.
- Tag `v0.5.0` sur `69a4215` ; run « Release » 40982733331 vert : publish, android / apk, desktop ×5.
- `gh release view v0.5.0` : 11 fichiers, exactement ceux attendus.
- APK :
  - `apksigner` : « Verifies », `CN=Nathflix, O=lavagnou`, même SHA-256 que la clé ;
  - `aapt2` : versionCode 500, versionName 0.5.0, icône = l'XML adaptatif ;
  - nouvelle intro, bannières et calques de l'icône retrouvés par leur contenu (R8 renomme les ressources : `res/JX.mp4`…) ;
  - plugin embarqué avec `brandImages()`.

**Livraison**
- Commits : `4fc44ec` (logo), `69a4215` (0.5.0)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.5.0

### T-037 — Intro en portrait sur Android

- **Statut :** À valider (sur un téléphone, debout et couché)
- **Priorité :** P2
- **Cible :** tv, tools
- **Dépend de :** T-003
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> L'intro est très cool avec le Nath animé, mais c'est une vidéo en 16:9 et du coup en portrait sur Android ça s'affiche pas bien avec les bandes noire en haut et en bas, il faudrait que ça s'adapte pour le mod portrait

**Critères d'acceptation**
- [x] Film portrait 1080×1920 (`res/raw/intro_portrait.mp4`), même animation et même musique
- [x] `Intro.kt` : le film portrait si le téléphone est tenu droit au lancement, sinon le 16:9
- [x] Plus de bandes noires : le film remplit l'écran (recadré au centre)
- [ ] Vérifié sur un téléphone

**Notes techniques**
- `render_intro.py` : `LOGO_PX = min(H × 0,36, W × 0,56 / format du mark)`. En 16:9, rien ne change (389 px de haut). En portrait, le mark tient dans 56 % de la largeur, ce qui laisse de la marge au recadrage d'un écran 19,5:9.
- `Intro.kt` :
  - `upright` vient de `configuration.orientation` au lancement ;
  - la vue fait la taille du film « en cover », centrée dans la boîte, qui la coupe ;
  - `MainActivity` gère elle-même la rotation (`configChanges`) : le film en cours continue et se recadre.
- Encodage portrait direct depuis les images (H.264, niveau 4.2), commandes dans `tools/README.md`.

**Vérification**
- Aperçu de 6 images en 360×640 : mark centré dans la largeur, plongée qui remplit la hauteur.
- Compilation Kotlin : CI `android-dev.yml`.

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
| 2026-10-29 | Profils synchronisés par un Cloudflare Worker + D1 (`tools/profile-sync`) à l'utilisateur, rangés sous l'identifiant du compte Stremio (`getUser`). La clé de session n'est jamais stockée. Les codes ne partent que hachés. Les caches et les réglages de l'appareil ne sont pas synchronisés. | Pas de compte en plus ; D1 plutôt que KV (limite d'écritures) ; offre gratuite | T-027 |
| 2026-10-29 | Les releases ne portent que les apps : exe (installation, portable), dmg, AppImage, APK, plus `latest*.yml` et `.blockmap` (mise à jour automatique du desktop). Le plugin et les installeurs sont servis depuis le dépôt : `main` pour Stremio Enhanced, le tag de la release pour l'app Android. | Choix de l'utilisateur : des pages de release plus simples | T-026 |
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
| 2026-10-30 | T-037 | Créée et implémentée : film d'intro portrait, choisi selon l'orientation, plein écran sans bandes noires |
| 2026-10-30 | T-035 | « Qui est-ce ? » en graisse normale sur téléphone (ancienne règle mobile) : titre en Inter 800 partout |
| 2026-10-30 | T-003 | Billboard et bandes-annonces : retour au texte « NATHFLIX » (typo fine) à la place du mark NATH, à la demande de l'utilisateur |
| 2026-10-30 | T-036 | Fait : release v0.5.0 publiée (apps desktop, APK signée avec le nouveau logo et la nouvelle intro) |
| 2026-10-30 | T-036 | Créée : release 0.5.0 ; version montée, en cours (CI, puis tag) |
| 2026-10-30 | T-003 | Nouveau logo de l'utilisateur partout : icônes desktop et Android (adaptative), bannière TV, wordmark et mark NATH dans le plugin, intro refaite avec NATH. Captures du README encore à refaire |
| 2026-10-30 | T-033 à T-035 | Créées et implémentées : bibliothèque Stremio rechargée au retour sur Android, choix du profil à l'ouverture, au retour et après un film (case « Ne plus me demander »), refonte façon Netflix (porte, mode Gérer, pages de profil). Testées (Playwright, captures) ; à valider sur l'appareil |
| 2026-10-30 | T-030 | CI rouge sur `728507a` (tests sans compte renvoyés vers la connexion) ; corrigée par `nathflix.allowGuest` dans les tests, plus une vérification de la redirection (`106d29c`, CI verte) |
| 2026-10-30 | T-029 à T-032 | Créées : retours sur la 0.4.0. T-029 (synchro à la connexion), T-030 (connexion obligatoire) et T-031 (Continuer à regarder par profil, Worker redéployé avec `played:*`) implémentées et testées (banc d'essai, Playwright) ; T-032 (progression séparée) à faire |
| 2026-10-29 | T-028 | Fait : release v0.4.0 publiée avec la première APK signée (certificat vérifié) ; T-007 débloquée (reste le son à vérifier sur l'appareil) |
| 2026-10-29 | T-028 | Créée : release 0.4.0 ; version montée, en cours (CI, puis tag) |
| 2026-10-29 | T-027 | Worker déployé (`nathflix-profiles.profile-sync.workers.dev`), `SYNC_ENDPOINT` rempli ; CORS et 401 vérifiés en ligne. À valider sur deux appareils |
| 2026-10-29 | T-007 | Clé de signature Android de release créée (alias `nathflix-tv`, RSA 4096, PKCS12), gardée par l'utilisateur hors du dépôt (sauvegarde à faire de son côté). Secrets GitHub `NATHFLIX_TV_KEYSTORE_B64` et `NATHFLIX_TV_KEYSTORE_PASSWORD` ajoutés : `tv.yml` peut construire l'APK signée |
| 2026-10-29 | T-027 | Créée et implémentée : Worker `tools/profile-sync` (D1), `cloudSync` dans le plugin, guide Cloudflare ; testée (Worker seul, deux appareils Playwright). En attente du déploiement |
| 2026-10-29 | T-026 | Fait : PR #2 fusionnée, release v0.3.0 publiée (apps desktop, `latest*.yml`, `.blockmap` ; sans APK faute de clé) |
| 2026-10-29 | T-026 | Créée : release 0.3.0, releases réduites aux apps ; en cours (PR vers main, puis release) |
| 2026-10-29 | T-024, T-025 | Bouton Lecture d'une série corrigé (forme de `meta_details`), croix de Continuer retirée, profils façon Netflix, bouton profil, menu du compte dans les Réglages. Testés (Playwright). APK de test publiée |
| 2026-10-29 | T-021 à T-023 | Équivalents Nathflix des fonctions Stremio Supporters : rangées de l'Accueil et codes, avatars, verrou par profil implémentés et testés (Playwright) ; téléchargements notés pour plus tard. APK de test publiée |
| 2026-10-29 | T-018 à T-020 | Revue complète (rapport), puis corrections à la demande de l'utilisateur ; testées (Playwright avant/après, CI). APK de test publiée |
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
