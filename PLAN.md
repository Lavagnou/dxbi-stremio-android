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
| T-038 | Release 0.5.1 (intro portrait, titre en gras, texte NATHFLIX du billboard) | Terminé | P1 | release, tv, plugin | T-037 | 2026-10-30 |
| T-039 | Nathflix rame sur Fire TV : audit de performance, CSS allégé, billboard, télécommande, mode léger, mesure à l'écran | À valider | P0 | plugin | — | 2026-10-30 |
| T-040 | Release 0.5.2 (performances) | Terminé | P0 | release, tv, plugin, app | T-039 | 2026-10-30 |
| T-041 | Banc de mesure `tools/perf-bench.mjs` et compteurs dans la mesure à l'écran | Terminé | P1 | plugin, tools | T-039 | 2026-10-31 |
| T-042 | `coreMeta` piloté par les événements du cœur (`coreEvents`) | Terminé | P1 | plugin | T-041 | 2026-10-31 |
| T-043 | « Se termine à » et calendrier mémorisés | Abandonné | P2 | plugin, tools | T-042 | 2026-10-31 |
| T-044 | Filtre de la bibliothèque par profil allégé | Abandonné | P2 | plugin, tools | T-042 | 2026-10-31 |
| T-045 | Données de Nathflix écrites au plus une fois par seconde, statistiques toutes les 60 s | Abandonné | P2 | plugin, tools | T-041 | 2026-10-31 |
| T-046 | Boucle principale : flèches des rangées mesurées seulement au survol ; banc connecté à un vrai compte | Terminé | P0 | plugin, tools | T-041 | 2026-10-31 |
| T-047 | TV et mode léger : plus de grandes ombres floues ; effets TV aussi en mode léger (`html.dx-light`) | Terminé | P1 | plugin, theme | T-039 | 2026-10-31 |
| T-048 | Polices liées par le plugin, sans `@import` dans le thème intégré | Abandonné | P2 | plugin, tools | T-041 | 2026-10-31 |
| T-049 | Démarrage Android : temps de démarrage dans la mesure à l'écran, puis mesures sur la Fire TV | À valider | P1 | plugin, tv | T-041 | 2026-11-01 |
| T-050 | Index des épisodes allégé sur TV et en mode léger | Abandonné | P2 | plugin, tools | T-041 | 2026-10-31 |
| T-051 | Réglage « Rendu allégé sur TV » (page dessinée en 720p puis agrandie) | À valider | P1 | plugin, tv | T-041 | 2026-11-01 |
| T-054 | Accueil TV : rangées chargées à l'approche de la télécommande, par paquets (avancée avant la 0.6.0) | Terminé | P0 | plugin, tools | T-046 | 2026-10-31 |
| T-052 | Plugin chargé par adresse sur Android (cache de compilation) | Abandonné | P1 | tv | T-041 | 2026-11-01 |
| T-053 | Plugin minifié pour l'APK et l'app | Abandonné | P2 | tv, app, tools | T-052 | 2026-11-01 |
| T-055 | `keyNav` sans remesure | Abandonné | P1 | plugin, tools | T-041 | 2026-11-01 |
| T-056 | Desktop : iframe de bande-annonce unique, observateurs du preload | Abandonné | P2 | plugin, app | T-041 | 2026-11-01 |
| T-057 | Desktop : fenêtre transparente seulement pendant mpv (étude) | Abandonné | P2 | app | T-041 | 2026-11-01 |
| T-058 | Thème découpé par appareil à la construction (étude) | Abandonné | P2 | plugin, tools | T-041 | 2026-11-02 |
| T-059 | Ordonnanceur à budget (étude) | Abandonné | P2 | plugin | T-041 | 2026-11-02 |
| T-060 | Étude : Stremio Web embarqué dans l'APK | Terminé | P3 | tv | T-052 | 2026-11-02 |
| T-079 | Mesure à l'écran : la dernière minute, et la part de Nathflix par touche | À valider | P1 | plugin | T-041 | 2026-11-01 |
| T-061 | Release 0.6.0 (performances, réglages synchronisés, bouton Lecture des films) | Terminé | P1 | release, tv, plugin, app | T-042 à T-066 | 2026-10-31 |
| T-062 | Release 0.8.0 (performances, OK maintenu, Continuer de regarder, billboard, synchro) | Terminé | P1 | release, tv, plugin, app | T-049 à T-085 | 2026-11-02 |
| T-063 | Bouton « Lecture » de la page des flux plus bas que ses voisins (PC, TV) | Terminé | P2 | plugin | T-012 | 2026-10-31 |
| T-064 | L'Accueil visible une seconde avant « Qui est-ce ? » à l'ouverture | À valider | P1 | plugin | T-034 | 2026-10-31 |
| T-065 | Bouton « Lecture » absent des pages de films ouvertes sans leurs flux (« Plus d'infos » du billboard…) | À valider | P1 | plugin | T-012 | 2026-10-31 |
| T-066 | Réglages de Nathflix synchronisés avec le compte Stremio | À valider | P1 | plugin, tools | T-027 | 2026-10-31 |
| T-067 | « Organiser l'Accueil » : textes des rangées superposés | Terminé | P2 | plugin | — | 2026-11-01 |
| T-068 | TV : sélection à la télécommande (contour coupé, point de départ, déplacements) et défilement fluide | À valider | P1 | plugin, tv | T-046, T-054 | 2026-11-01 |
| T-069 | TV : carte d'aperçu et bande-annonce sur le titre sélectionné ; défauts trouvés avec le compte de l'utilisateur | À valider | P1 | plugin, tv | T-068 | 2026-11-01 |
| T-070 | « Qui est-ce ? » au démarrage seulement ; fenêtres promotionnelles de Stremio supprimées ; contour de la carte d'aperçu TV | À valider | P1 | plugin, tv | T-069 | 2026-11-01 |
| T-071 | Retour depuis un épisode : la page de la série, pas l'Accueil | À valider | P2 | plugin | — | 2026-11-01 |
| T-072 | Titres, résumés, genres et bandes-annonces en français (TMDB) | À valider | P1 | plugin | — | 2026-11-01 |
| T-073 | Profils : un titre lancé sur un 2e profil apparaissait dans le 1er sur un autre appareil ; clés TMDB et MDBList synchronisées | À valider | P1 | plugin | T-027, T-066 | 2026-11-01 |
| T-074 | Release 0.6.1 (télécommande TV, carte d'aperçu, titres en français, synchro des profils) | Terminé | P1 | release, tv, plugin, app | T-067 à T-073 | 2026-11-01 |
| T-075 | Résumé resté en anglais sur la fiche d'un titre sans note IMDb | À valider | P2 | plugin | T-072 | 2026-11-01 |
| T-076 | Lecteur TV comme celui de Netflix (prototype validé) | À valider | P1 | TV, plugin | — | 2026-11-01 |
| T-077 | Dégradé de « Qui est-ce ? » dans la couleur d'accent | À valider | P2 | plugin | — | 2026-11-01 |
| T-078 | Release 0.7.0 (lecteur TV comme Netflix, dégradé du choix de profil) | Terminé | P1 | release, tv, plugin, app | T-075 à T-077 | 2026-11-01 |
| T-080 | TV : OK maintenu ouvre le menu d'un titre (plus de clics en boucle) ; 3 points masqués ; Retour sur la page d'un titre | À valider | P1 | plugin | T-010 | 2026-11-01 |
| T-081 | Page d'un titre : genres et acteurs plus sélectionnables en mode léger ; bouton Partager retiré | À valider | P2 | plugin | T-068 | 2026-11-01 |
| T-082 | Défilement : moins de travail à chaque image (`onScroll`) | À valider | P1 | plugin | T-041 | 2026-11-02 |
| T-083 | Continuer de regarder : une série ouvre sa page, pas son épisode | À valider | P2 | plugin | T-012 | 2026-11-02 |
| T-084 | Billboard écrasé (seul le bas visible) quand l'Accueil est réorganisé | À valider | P1 | plugin | — | 2026-11-02 |
| T-085 | Ordre de l'Accueil pas répercuté sur les autres appareils (synchro plus rapide, réception même quand un envoi est refusé) | À valider | P1 | plugin | T-027 | 2026-11-02 |
| T-086 | Téléphone : bouton Bande-annonce dans la feuille d'un titre, appui long qui l'ouvre | À valider | P2 | plugin | — | 2026-11-02 |
| T-087 | Bande-annonce en plein écran : l'interface du lecteur YouTube ne se voit plus | À valider | P2 | plugin | — | 2026-11-02 |
| T-088 | Release 0.8.1 (bande-annonce et appui long sur téléphone, sans interface YouTube) | Terminé | P1 | release, plugin | T-086, T-087 | 2026-11-02 |
| T-089 | Étude : mémoire et démarrage sur TV | Terminé | P1 | tv, plugin | — | 2026-11-02 |
| T-090 | Accueil organisé : rangées chargées au fil de la télécommande, jamais les masquées | À valider | P1 | plugin | T-089 | 2026-11-02 |
| T-091 | TV : affiches à la taille de l'écran, moins d'images d'aperçu décodées d'avance | À valider | P2 | plugin | T-089 | 2026-11-02 |
| T-092 | TV : aperçus sans bande-annonce par défaut, rendu allégé en Auto par défaut | À valider | P1 | plugin, tv | T-089, T-051 | 2026-11-02 |
| T-093 | Chaque fiche de série quittée restait en mémoire (miniatures d'épisodes en attente) | À valider | P1 | plugin | T-089 | 2026-11-02 |
| T-094 | Rendu allégé sans effet sur la Fire TV : appliqué dès que le réglage change | À valider | P1 | tv, plugin | T-051, T-092 | 2026-11-02 |
| T-095 | Release 0.8.2 (mémoire et démarrage sur TV) | Terminé | P1 | release, tv, plugin | T-090 à T-094 | 2026-11-02 |
| T-096 | TV : le billboard tourne aussi en mode léger et avec la télécommande sur ses boutons | À valider | P2 | plugin | — | 2026-11-02 |

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

### T-038 — Release 0.5.1 (intro portrait, titre en gras, texte NATHFLIX du billboard)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin
- **Dépend de :** T-037
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> Tu peux relancer, quand ça sera bon fait une 0.5.1

**Critères d'acceptation**
- [x] `@version 0.5.1`, section `## 0.5.1` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] Release v0.5.1 : 2 exe, `.blockmap`, 2 dmg, 2 AppImage, 3 `latest*.yml`, `nathflix-tv.apk`, et rien d'autre
- [x] APK signée par la clé de release, avec `intro_portrait`

**Notes techniques**
- Le rendu du film portrait a été arrêté une fois par Claude Code (mémoire presque pleine). Relancé à la demande de l'utilisateur : seules les 49 images manquantes ont été rendues (`--frames`), après contrôle des 203 déjà faites.
- **CI avant la release** : deux échecs sur des commits précédents, sans lien avec le code.
  - Cross-platform sur `d18b570` (PLAN.md seulement) : sur Windows, l'installeur n'a pas joint le serveur HTTP local du test (« connexion refusée »). Les commits suivants passent.
  - mpv Windows sur `2239340` : « the video didn't follow the window (3 points changed) ». Le job relancé sur le même commit passe (essai 2) : la mesure par captures d'écran est instable.

**Vérification**
- Tag `v0.5.1` sur `90608cc` ; run « Release » 41037456339.
  - Essai 1 : desktop / mac x64 en échec au checkout (« Could not resolve host: github.com », réseau du runner).
  - Job relancé seul (essai 2) : vert, et le dmg a été ajouté à la release.
- `gh release view v0.5.1` : les 11 fichiers attendus.
- APK :
  - `apksigner` : « Verifies », `CN=Nathflix, O=lavagnou`, même SHA-256 ;
  - versionCode 501 ;
  - `intro.mp4` et `intro_portrait.mp4` retrouvés par leur contenu ;
  - plugin embarqué en 0.5.1, sans le mark sur le billboard.

**Livraison**
- Commits : `26755ff`, `2239340`, `48ce1d8`, `90608cc` (0.5.1)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.5.1

### T-039 — Nathflix rame sur Fire TV : audit de performance et corrections

- **Statut :** À valider (sur la Fire TV 4K, puis téléphone et tablette)
- **Priorité :** P0
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> J'ai testé l'app sur une FireTV 4K, et niveau performance c'est inutilisable, ça lag, ça freeze tout le temps. Sur ma Honor Pad et mon Pixel 7, c'est pas hyper fluide, il y a quelques ralentissement mais c'est pas à ce point, et sur PC ça va aussi, c'est pareil, pas hyper fluide mais utilisable. Fait une analyse profonde des soucis de performances, cherche des pistes d'amélioration et d'optimisation, et fait moi un rapport de l'audit.

Puis, après le rapport : l'app Stremio ne rame pas du tout sur la même Fire TV ; choix « Tout, release comprise » (corrections 1 à 4, mesure à l'écran, mode léger, release).

**Critères d'acceptation**
- [x] Audit mesuré (banc Playwright, processeur bridé ×6, User-Agent TV) et rapport à l'utilisateur
- [x] Règles CSS des autres appareils retirées à l'injection (`sharpen.prune`)
- [x] `sharpen` sur tous les appareils (avant : TV seulement)
- [x] Billboard : arrêt hors de vue, une seule image au démarrage, plus de recalculs forcés, points sans transition de largeur sur TV
- [x] `keyNav.inView()` ne mesure plus les rangées hors écran
- [x] Mode léger (`lightMode`, Auto sur les appareils Android modestes) et mesure à l'écran (`perfOverlay`)
- [x] Aucune différence visuelle (captures avant/après, PC, téléphone, TV) ; navigation à la télécommande identique
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- **Audit** (Chrome bridé ×6, Accueil invité puis Accueil gonflé à environ 3 300 éléments) :
  - le JavaScript du plugin pèse peu (`sync()` : 60 ms en 30 s) ;
  - le coût vient du CSS : les sélecteurs `[class*=…]` (538 sur 932 dans le thème) sont testés sur chaque élément à chaque recalcul. Changer une classe sur `#app` coûte environ 0 ms sans le CSS de Nathflix, des dizaines de ms avec ; bien plus sans `sharpen` (téléphones et PC, qui ne l'avaient pas) ;
  - le billboard tournait même hors de vue (seule condition : la route `board`), forçait deux recalculs (`void offsetWidth`), animait la largeur des points (mise en page à chaque image), et téléchargeait 8 fonds 1920×1080 (550 à 680 Ko chacun) au démarrage. `background/large` et `background/medium` de metahub sont la même image ;
  - `keyNav.inView()` mesurait tous les focusables, donc mettait en page les rangées hors écran (`content-visibility`) : 43 ms contre 11 ms pour aller sur un titre d'une rangée non dessinée.
- **Piège du banc** : poser `localStorage` `nathflix.lang` (ou laisser `profileDefaults` passer le profil invité en français) fait recharger la page par `langWatch`, et un plugin injecté à la main disparaît. Le banc sert le plugin depuis une fausse adresse de web.stremio.com (`ctx.route`) et le recharge à chaque `load`, comme l'app Android.
- **`sharpen`** :
  - `fill()` et `soon()` ne sont plus réservés à `IN_TV` ; `soon()` abandonne après 200 essais (une feuille de Stremio introuvable relançait toutes les 300 ms pour toujours) ;
  - `prune()` supprime, avant la réécriture, les sélecteurs qui nomment une classe d'appareil absente (`DEVICE_CLASSES` : `dx-tv`, `dx-mobile`, `dx-app`, `dx-win` ; ce qui est dans `:not()` ne compte pas) et, sur TV, les `:hover` ;
  - `VERSION` entre dans `stamp()` : les copies gardées par une version précédente sont refaites ;
  - copies dans `localStorage` (`nathflix.sharp.*`) : environ 230 Ko pour le thème et 70 Ko pour `dx-extras-style`. Stockage plein : rien n'est gardé, c'est refait au démarrage suivant.
- **Billboard** (`hero`) :
  - `load()` ne vérifie que la première image ; les autres sont vérifiées juste avant leur tour (`artLoads`, `nextShowable`), et une image qui ne charge pas perd son point ;
  - le fond n'est posé qu'au tour de la diapositive (`data-bg`, puis `style.backgroundImage` dans `show()`) ;
  - `IntersectionObserver` (`onScreen`, 20 % visible) : hors de vue, `tick()` s'arrête (`stalled`) ; de retour, `resumeIfStalled()` reprend ;
  - plus de `void offsetWidth` : le point qui devient actif repart seul ; le même point recliqué repart par `getAnimations()` ; le zoom lent (`kb`) n'est plus posé sur TV ni en mode léger ;
  - thème : `.dx-tv .dx-hero-dot` sans transition de largeur.
- **`keyNav.inView()`** : les rangées de l'Accueil hors de l'écran (un rectangle par rangée) sont écartées avec tout ce qu'elles contiennent.
- **Mode léger** : `lightMode` (`auto` | `on` | `off`), `MODEST_DEVICE` = `IN_ANDROID` et (4 cœurs ou moins, ou `deviceMemory` ≤ 2). Pas sur ordinateur en Auto : les machines de la CI ont 4 cœurs. `light()` ; `ratingsOn()` et `previewsOn()` remplacent `settings.ratings` et `settings.previews` ; `hero.interval()` donne 0.
- **Mesure à l'écran** (`perfReadout`, réglage `perfOverlay`) : temps de réponse des 20 dernières touches ou touchers (Event Timing, à partir de 16 ms), tâches longues des 10 dernières secondes, nombre d'éléments, état du mode léger. Élément `.dx-perf` ignoré par l'observateur `mo`, réécrit seulement quand le texte change.
- Groupe de réglages « Performances » (après « Apparence »). Pour tester sans le thème : réglage existant « Apparence Nathflix intégrée ».
- Pistes non faites (voir le rapport) : charger le plugin par adresse plutôt qu'en texte (cache de compilation, nouvelle APK), index des épisodes allégé sur TV, réécriture des sélecteurs lents les plus fréquents.

**Vérification**
- `node --check` (plugin, `nathflix-tv.js`), `node tools/profile-sync/test.mjs` : OK.
- Traductions : aucun texte manquant (`fr-missing.mjs`, hors faux positifs des commentaires).
- Captures avant/après (Accueil, Découvrir, fiche, Réglages ; PC, téléphone, TV ; images masquées) : identiques, sauf l'heure de « Se termine à ».
- Télécommande (mode TV, 13 flèches entre billboard, rangées et barre du haut) : même parcours avant et après.
- Banc avant/après (bridé ×6, médiane de 5 ; valeurs absolues bruitées, comparer les paires) :

  | Mesure | TV avant → après | PC avant → après |
  |---|---|---|
  | Classe sur `#app`, Accueil invité | 117 → 71 ms | 343 → 93 ms |
  | Même chose, Accueil gonflé (3 000+ éléments) | 89 → 79 ms | 1 573 → 397 ms |
  | Accueil gonflé, toutes les rangées dessinées | 439 → 340 ms | 1 119 → 644 ms |
  | Repos 12 s (tâches ; mises en page) | 1 074 → 626 ms ; 21 → 1 | 1 236 → 1 117 ms ; 17 → 10 |
  | Fonds du billboard au démarrage | 10 → 4 | 10 → 5 |

- Non vérifié : la Fire TV elle-même (pas sur le même réseau que le PC), et le mode léger en Auto (le PC du banc a plus de 4 cœurs).

**Livraison**
- Commits : `f2ae445`
- Version / CHANGELOG : 0.5.2

### T-040 — Release 0.5.2 (performances)

- **Statut :** Terminé
- **Priorité :** P0
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-039
- **Créée le :** 2026-10-30
- **Mise à jour :** 2026-10-30
- **Branche :** `main`

**Demande**
> (choix « Tout, release comprise » après le rapport d'audit)

**Critères d'acceptation**
- [x] `@version 0.5.2`, section `## 0.5.2` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.5.2`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, plugin embarqué en 0.5.2

**Notes techniques**
- La Fire TV prend aussi le plugin 0.5.2 seule (`Updates.checkPlugin`) dès la release publiée, sans réinstaller l'APK.

**Vérification**
- CI sur `f2ae445` : Cross-platform, mpv Windows et build Android de test verts.
- Tag `v0.5.2` sur `f2ae445` ; run « Release » 41140080781 : tous les jobs verts du premier coup.
- `gh release view v0.5.2` : les 11 fichiers attendus.
- APK :
  - `apksigner` : `CN=Nathflix, O=lavagnou`, même SHA-256 que les releases précédentes ;
  - versionCode 502, versionName 0.5.2 ;
  - plugin embarqué en 0.5.2, avec `perfReadout`.

**Livraison**
- Commits : `f2ae445` (0.5.2)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.5.2

### T-041 — Banc de mesure `tools/perf-bench.mjs` et compteurs dans la mesure à l'écran

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** plugin, tools
- **Dépend de :** T-039
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> Lire `docs/perf/2026-10-31-plan-performance.md` et l'audit, puis exécuter le plan tâche par tâche : T-041 à T-051 puis release 0.6.0, T-052 à T-057 puis release 0.7.0, T-058 à T-060 seulement après les avoir proposées une par une. T-041 d'abord (banc et compteurs), puis une mesure de référence sur main notée dans la fiche. Aucune autre tâche avant.

**Besoin**
Un banc reproductible et des compteurs dans le plugin, pour mesurer chaque tâche du chantier performance avant et après (spec : `docs/perf/2026-10-31-audit-performance.md`, plan : `docs/perf/2026-10-31-plan-performance.md`, commités avec cette tâche).

**Critères d'acceptation**
- [x] Compteurs `perf` dans le plugin (`getState:<modèle>`, `sync`, `saveLocal`, `dispatch`), lisibles par `window.__nathflix.perf()`
- [x] Trois lignes de plus dans la mesure à l'écran (lectures du cœur, boucle principale, enregistrements, par minute), traduites
- [x] `tools/perf-bench.mjs` : JSON des médianes, parcours de contrôle, échec sur erreur de Nathflix, captures et comparaison au pixel
- [x] Mesure de référence TV et PC, 5 runs, ci-dessous
- [x] Rien de visible ne change (captures `main` / T-041, PC, téléphone, TV)

**Notes techniques**
- **Plugin** : section « Performance counters » juste avant `keptCore` (`perf` : `count`, `time`, `snapshot`, `reset`). Des incréments d'entiers, toujours actifs.
  - `getState:<modèle>` est compté dans l'enveloppe de `viewerLibrary.patch` (lectures de Stremio **et** de Nathflix : on ne peut pas les séparer) et dans `core()` quand seul `__permanentCore` répond ;
  - `dispatch` dans l'enveloppe de `viewerLibrary.patch` ; `sync` (nombre et ms) autour des étapes de `sync()` ; `saveLocal` dans `store.saveLocal()`.
  - `perfReadout.counters(now)` : par minute depuis `perf.since`, les 5 modèles les plus lus.
- **Banc** (`tools/README.md`, section « Performance bench ») :
  - Playwright par `npx --yes -p playwright@1 node tools/perf-bench.mjs …`, trouvé par `createRequire` dans le `node_modules` de npx (dans le `PATH`). Sans le Chromium de Playwright, il prend le Chrome installé (`channel: "chrome"`).
  - Le plugin est servi à `https://web.stremio.com/__nathflix/plugin.js` dans l'enveloppe `StremioEnhancedAPI`, réinjecté à chaque `load`. Un premier chargement de chauffe par contexte (cache chaud, comme une TV), et les fenêtres d'annonce de Stremio sont fermées.
  - TV : UA desktop + ` nathflix-tv/<version>`, 1280×720, DPR 1,5. PC : 1600×900. Téléphone : ` nathflix-mobile/<version>`, 412×915, DPR 2, tactile.
  - Captures : images, vidéos et fonds masqués, billboard figé (`billboardInterval: "0"`), « Se termine à » et la mesure masqués.
- **Ce que montre la référence** :
  - Sur l'Accueil invité, **Nathflix ne fait rien au repos** : 0 `sync`, 0 `getState`, 0 tâche longue en 30 s. Les gains se verront dans les compteurs du parcours (`session`), les touches, et sur la Fire TV avec un vrai compte (Continuer à regarder, profils).
  - `ctx` (219 lectures pendant le parcours) est lu par Stremio lui-même à chaque événement du cœur.
  - **Bruit** : le critère « ±20 % entre runs » du plan n'est pas tenu pour les temps (chargement du plugin 0,4 à 1,2 s, touches médianes 28 à 64 ms en TV ; un run PC à 9,6 s de démarrage). Les compteurs, eux, sont stables d'un run à l'autre. D'où la méthode retenue pour les tâches suivantes : l'ancienne et la nouvelle build passées l'une après l'autre (`--plugin`), comparées par paire, plutôt que contre ces chiffres seuls.
  - Bruit des captures entre deux prises d'une même build : quelques centaines de pixels (badges IMDb, RT et Metacritic qui arrivent plus ou moins vite, lissage du texte sur PC).

**Vérification**
- `node --check` (plugin, banc) : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus (`{n} episodes`, `…`).
- Banc sur ce commit (bridé ×6, médiane de 5 runs, Accueil invité ; repos de 30 s après 15 s de stabilisation) :

  | Mesure | TV | PC |
  |---|---|---|
  | Page prête (premier titre affiché) | 3 528 ms | 4 227 ms |
  | Chargement du plugin | 859 ms | 1 059 ms |
  | Repos 30 s : `sync`, `getState`, `saveLocal`, tâches longues | 0, 0, 0, 0 | 0, 0, 0, 0 |
  | Éléments sur l'Accueil | 908 | 863 |
  | 20 touches : médiane / p90 / pire | 56 / 88 / 104 ms | 60 / 136 / 264 ms |
  | Parcours : `sync` | 23 fois, 566 ms | 23 fois, 622 ms |
  | Parcours : `getState` | ctx 219, streaming_server 35, local_search 30, board 28, continue_watching_preview 14, discover 14, library 14, search 10, meta_details 9, live_tv_continue_watching 4, calendar 3, data_export 2, live_tv_guide 2 | idem, board 25 |
  | Parcours : tâches longues | 27, 5 483 ms (pire 801) | 36, 6 315 ms (pire 810) |
  | Erreurs de Nathflix | 0 | 0 |

  `dispatch`, ajouté juste après la référence : 135 pendant le parcours (TV, 1 run), 0 au repos.

- Captures `main` → T-041 (Accueil, Découvrir, fiche, Réglages) : identiques, hors bruit connu. PC : Accueil 738 px sur la ligne des badges IMDb. TV : fiche 2 795 px sur les badges RT et Metacritic. Téléphone : Accueil 967 px sur la ligne des badges IMDb. Les autres écrans : 0 px.
- Non mesuré : la Fire TV (pas d'adb ; photos de la mesure à l'écran demandées à l'utilisateur avant la release 0.6.0).

**Livraison**
- Commits : `cd56e91`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-063 — Bouton « Lecture » de la page des flux plus bas que ses voisins

- **Statut :** Terminé
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-012
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> Le bouton de Lecture est décalé ici sur ton screenshot, corrige ça au passage

(capture de la fiche d'un film sur PC : « Recherche des flux… » plus bas que « Bande-annonce » et les autres boutons)

**Critères d'acceptation**
- [x] « Lecture » / « Lancer l'épisode » à la même hauteur que ses voisins sur PC et TV
- [x] Téléphone inchangé (le bouton y est seul sur sa ligne)

**Notes techniques**
- `.action-buttons-container` aligne ses boutons au centre (`align-items: center`). Ceux de Stremio et de Nathflix (`.dx-reviews-btn`, `.dx-hide-btn`) ont `margin-bottom: 1rem`, pas `.dx-play-ep` (`EXTRAS_CSS`) : il descendait de la moitié de cette marge (7 px).
- `.dx-play-ep` prend la même marge ; `html.dx-mobile .dx-play-ep` la remet à 0 (bouton seul sur sa ligne, déjà bien placé).

**Vérification**
- `node --check` : OK.
- Mesure dans la page (PC) : haut du bouton à 772 px, comme ses voisins (avant : 779 px).
- Captures avant/après (`perf-bench.mjs --shots --compare`, Accueil, Découvrir, fiche, Réglages) :
  - PC et TV : seule la fiche change, et seulement le bouton, maintenant aligné ; le reste n'est que le bruit des badges ;
  - téléphone : le bouton ne bouge pas ; les seules différences sont celles des badges IMDb, RT et Metacritic.

**Livraison**
- Commits : `e503fd1`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-042 — `coreMeta` piloté par les événements du cœur

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-042)

**Critères d'acceptation**
- [x] `coreEvents` : la source d'événements du cœur trouvée et branchée, avec repli si elle manque
- [x] `coreMeta.refresh` ne lit un modèle qu'après un changement, et seulement celui de la route
- [x] Chemins connus (`collectKnown`) avec la marche récursive en repli
- [x] Moins de `getState` sur le banc ; badges et billboard identiques (captures)

**Notes techniques**
- **Source des événements** (web.stremio.com, build du 2026-10-31) :
  - ni `window.services.core` (`services` n'a que `chromecast`) ni `window.core.transport` (`core` : `init`, `getState`, `dispatch`, `encodeStream`, `decodeStream`, `analytics`) ;
  - `window.onCoreEvent(e)` existe : `e.name` vaut `"NewState"` (`e.args` = liste des modèles changés, par exemple `["discover"]`, `["local_search"]`) ou `"CoreEvent"`. Stremio relit `window.onCoreEvent` à chaque événement : l'envelopper suffit, et l'enveloppe est restée en place après plusieurs changements de route.
- **`coreEvents`** (section avant `keptCore`, pour que `core()` puisse l'appeler sans ordre d'initialisation) :
  - `install()`, appelé par `core()` à chaque fois : enveloppe `window.onCoreEvent` si ce n'est pas déjà la nôtre (`__dxEvents`). Elle est donc refaite si Stremio la remplace. L'original passe d'abord (`try … finally`), et l'enveloppe ne lève jamais ;
  - `on(model, fn)` ; `touch(models)` regroupe par `requestAnimationFrame` (au plus un appel par modèle et par image) ;
  - `available` passe à vrai à la première enveloppe posée. Sans `window.onCoreEvent` : comportement d'avant (5 modèles toutes les 4 s ; sur TV et en mode léger, seulement les modèles de la route).
- **`coreMeta`** :
  - `dirty` (tous les modèles au départ), rempli par `coreEvents.on` et par `touchRoute()` (appelé par `onHash`, donc aussi au retour au premier plan par `visibilitychange`) ;
  - `routeModels()` : `board` → `board` et `continue_watching_preview` ; `discover`, `search` et `library` → le leur ; ailleurs, rien (la fiche garde ce qui a été lu avant) ;
  - un modèle qui se charge catalogue après catalogue est lu au plus toutes les 1,5 s, et son dernier changement est toujours lu (`later`).
- **Formes vérifiées dans la console** : `board.catalogs[].content` = `{ type: "Ready", content: [...] }` ; `discover.catalog.content` (idem, `catalog` nul hors de Découvrir). `collectKnown` les lit pour `board`, `search` et `discover`. `library` et `continue_watching_preview` gardent la marche récursive : vides en invité, leur forme n'a pas pu être vérifiée.

**Vérification**
- `node --check` : OK. Aucun texte nouveau.
- Banc avant/après (bridé ×6, médiane de 5 runs, passés l'un après l'autre ; compteurs sur tout le parcours) :

  | Mesure | TV avant → après | PC avant → après |
  |---|---|---|
  | `getState` des 5 modèles de `coreMeta` | 80 → 41 | 77 → 38 |
  | dont `search` | 10 → 0 | 10 → 0 |
  | dont `discover` / `library` / `continue_watching_preview` | 14 / 14 / 14 → 6 / 5 / 8 | idem |
  | dont `board` | 28 → 22 | 25 → 19 |
  | `ctx` (lu par Stremio) | 220 → 219 | 219 → 220 |
  | Boucle principale (`sync`) | 19 fois, 214 ms → 18 fois, 211 ms | 20 fois, 440 ms → 20 fois, 282 ms |
  | Tâches longues | 14, 1 733 ms → 9, 963 ms | 22, 4 580 ms → 20, 2 679 ms |
  | Touches : médiane / pire | 24 / 72 → 16 / 128 ms | 32 / 88 → 32 / 112 ms |
  | Erreurs de Nathflix | 0 → 0 | 0 → 0 |

  Ce qui reste sur `board`, `discover` et `library` est lu par Stremio lui-même. Les temps (tâches longues, touches) restent bruités d'un run à l'autre. La pire touche sur 20 en TV monte, mais la médiane baisse.
- Captures avant/après (Accueil, Découvrir, fiche, Réglages) : PC identique (hors les 738 px connus de la ligne des badges IMDb). TV : 11 et 409 px (lissage du texte, badges RT et Metacritic). Téléphone : 3 850 px sur l'Accueil, uniquement les lignes des badges IMDb, tous présents aux mêmes places (vérifié à l'œil), et 521 px sur la fiche.
- Non vérifié : les cartes de survol sur PC (non couvertes par les captures ; elles lisent `coreMeta.get`, toujours rempli à l'arrivée sur l'Accueil), un vrai compte (bibliothèque, Continuer à regarder), la Fire TV.

**Livraison**
- Commits : `f2315ff`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-043 — « Se termine à » et calendrier mémorisés

- **Statut :** Abandonné (pas de gain mesurable)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-042
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-043)

**Critères d'acceptation**
- [ ] ~~`endsAt` et `calendarFill` ne lisent le cœur qu'une fois par film ou par mois~~ : essayé, pas gardé
- [x] Banc : repos sur d'autres routes (`--dwell`), run repris si la page se recharge

**Notes techniques**
- **Essai** (non gardé) :
  - `endsAt` gardait les minutes restantes du film (`memo`), relues seulement pour un autre film, après un événement `meta_details` (`coreEvents.on`), ou au retour du lecteur (`invalidate()` dans `updateRoute`) ;
  - `calendarFill` gardait ce que le cœur dit du mois (`memo`, clé `location.hash`), avec les mêmes conditions sur `calendar`.
- **Pourquoi aucun gain** :
  - `endsAt.ensure()` et `calendarFill.ensure()` ne tournent que dans `sync()`, et `sync()` ne tourne que si la page change. Au repos sur la fiche ou le calendrier : 0 passage, avant comme après. Le plan supposait une lecture « par sync » au repos : ce n'est pas le cas ;
  - quand la fiche change (flux qui arrivent, bibliothèque), le cœur envoie aussi `NewState` `meta_details` : le mémo est invalidé et relu de toute façon. Il n'épargne que les passages causés par d'autres changements, rares sur cette page.
- **Banc** :
  - `--dwell "#/route,#/autre"` : après le parcours, un repos sur chaque route (5 s d'arrivée, puis la durée de `--idle`), compteurs dans `dwell` par route ;
  - un run pendant lequel la page se recharge (une fois, `Execution context was destroyed` dans `closeModals`) est repris, deux fois au plus (`retries` dans le rapport) ;
  - **piège** : Git Bash change `#/detail/…` en chemin Windows (`#C:/Program Files/Git/detail/…`). Lancer avec `MSYS_NO_PATHCONV=1` (noté dans `tools/README.md`).

**Vérification**
- Banc TV (bridé ×6, médiane de 5 runs, avant/après l'essai) :

  | Mesure | Avant | Après (essai) |
  |---|---|---|
  | `getState` `meta_details` sur le parcours | 9 | 8 |
  | `getState` `calendar` sur le parcours | 3 | 3 |
  | Repos 20 s sur la fiche d'un film : `sync`, `meta_details` | 0, 0 | 0, 0 (1 run chacun) |
  | Repos 20 s sur le calendrier | 0 passage | 0 passage |

  Une lecture de moins sur un parcours qui en compte environ 400 : pas de gain mesurable. Le code du plugin est retiré ; le banc garde `--dwell` et la reprise des runs.
- Captures avec l'essai : identiques à avant (hors bruit des badges), sans objet une fois le code retiré.

**Livraison**
- Commits : `c00a828`
- Version / CHANGELOG : —

### T-044 — Filtre de la bibliothèque par profil allégé

- **Statut :** Abandonné (pas de gain mesurable)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-042
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-044)

**Critères d'acceptation**
- [ ] ~~`viewerLibrary.filter` : spectateurs lus une fois par appel, chemins connus~~ : essayé, pas gardé
- [x] Mesure du filtre : `perf.time("filter")` dans le plugin, `filterMs` dans le banc

**Notes techniques**
- **Essai** (non gardé) :
  - étape 1 : `me`, `owners` et `base` lus une fois au début de `filter()`, et une fermeture `mine` passée à `inProgress(mine)` et à `unmark` ;
  - étape 2 : `unmarkKnown(model, s, all, mine)` sur les chemins de `coreMeta.collectKnown` (`board`, `search`, `discover`) et `metaItem.content.content` + `videos[]` pour `meta_details`, la marche récursive en repli ;
  - étape 3 (court-circuit quand rien n'a changé) écartée d'emblée : chaque `getState` renvoie un état neuf du Worker, qu'il faut filtrer à chaque fois.
- **Pourquoi aucun gain** : le filtre ne coûte presque rien. `store.get` est une simple lecture d'objet, et l'Accueil compte quelques dizaines de titres par lecture. Sur tout le parcours, avec deux profils : environ 27 ms en TV bridée ×6 (≈ 4 ms réels), 8 ms sur PC.
- **Gardé** : le chronomètre autour de `filter()` dans l'enveloppe de `viewerLibrary.patch` (`perf.time("filter", …)`, seulement avec deux profils ou plus) et `filterMs` dans le banc (repos, parcours, `dwell`). Il mesure le filtre sur l'appareil, par `window.__nathflix.perf()`.
- **Banc avec deux profils** : `--storage` pose `nathflix.data` avec `viewers` (`main` et `v2`, `current: "v2"`), `--settings '{"whoIsWatching":false}'` pour que « Qui regarde ? » ne couvre pas l'Accueil.

**Vérification**
- Banc avant/après (deux profils, bridé ×6, médiane de 5 runs, sur tout le parcours) :

  | Mesure | TV avant → après | PC avant → après |
  |---|---|---|
  | Temps dans `filter` | 27,2 → 19,6 ms | 8,4 → 10,4 ms |
  | Boucle principale | 22 fois, 413 ms → 20 fois, 361 ms | 16 fois, 140 ms → 15 fois, 142 ms |
  | Tâches longues | 19, 3 089 ms → 18, 2 606 ms | 8, 665 ms → 6, 464 ms |
  | Erreurs de Nathflix | 0 → 0 | 0 → 0 |

  Quelques millisecondes par minute, dans le bruit (le PC monte même un peu) : pas de gain mesurable, le code de l'essai est retiré.
- Captures avec l'essai (deux profils) : identiques, hors bruit des badges. Sans objet une fois le code retiré.

**Livraison**
- Commits : `3c1b543`
- Version / CHANGELOG : —

### T-045 — Données de Nathflix écrites au plus une fois par seconde, statistiques toutes les 60 s

- **Statut :** Abandonné (pas de gain mesurable sur le banc ; à reprendre si la Fire TV montre beaucoup d'enregistrements)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-045)

**Critères d'acceptation**
- [ ] ~~`store.saveLocal` différé d'une seconde, statistiques toutes les 60 s~~ : essayé, pas gardé
- [x] Banc : changement de profil mesuré (`switching`), test de perte (`--loss-test`), `--storage @fichier`

**Notes techniques**
- **Essai** (non gardé) :
  - `store.saveLocal()` planifiait l'écriture à 1 s (`localTimer`) ; `saveLocalNow()` écrivait (et comptait `saveLocal`) ; `flush()` écrivait d'abord la copie locale ;
  - filets : `onLeave` aussi sur `pagehide`, et `onHidden` (`visibilitychange` quand la page est cachée) ;
  - `stats` : `flush()` toutes les 60 s de lecture au lieu de 30, et sur `pause` et `ended` de la vidéo.
- **Pourquoi pas gardé** :
  - sur le banc (invité, ou deux profils avec 140 Ko de données réalistes), Nathflix écrit très peu : 2 fois au chargement et au parcours, 1 fois par changement de profil. Les changements de profil sont espacés : le délai d'une seconde n'en regroupe aucun ;
  - aucune tâche longue autour d'une écriture, même avec 140 Ko (bridé ×6) ;
  - il crée un cas de perte : un plantage du renderer dans la seconde qui suit un changement le perd (`--loss-test`, cas `abrupt`).
- **Le cas où il aiderait** : la synchronisation des profils avec un compte (`cloudSync`, une `store.put` par clé reçue, donc une écriture complète par clé : une douzaine d'un coup au démarrage ou au retour). Le banc ne peut pas le reproduire sans compte. **À reprendre** si la ligne « Enregistrements par minute » de la mesure à l'écran, sur la Fire TV avec un compte, est élevée.
- **Banc** :
  - avec deux profils ou plus, aller-retour vers un autre profil (`viewers.select`, 3 s chacun), compteurs dans `switching` ;
  - `--loss-test` : un profil ajouté puis choisi, et la page qui disparaît 200 ms après, de trois façons : fermée normalement (`unload`), passée en arrière-plan d'abord (`hidden` : `visibilitychange` simulé), renderer planté (`abrupt` : `Page.crash` par CDP, sans attendre de réponse, qui ne vient jamais). Fermer une page envoie encore `pagehide` : seul le plantage est vraiment abrupt ;
  - `--storage @fichier.json` : une vraie exportation est trop longue pour la ligne de commande (`Argument list too long`).

**Vérification**
- Banc TV (deux profils, `nathflix.data` de 140 Ko : 600 titres attribués, 400 lancés par profil, statistiques ; bridé ×6, médiane de 5 runs) :

  | Mesure | Avant | Après (essai) |
  |---|---|---|
  | Écritures (`saveLocal`) sur le chargement et le parcours | 2 | 1 |
  | Écritures pour un aller-retour de profil | 2 | 2 |
  | Tâches longues pendant l'aller-retour | 0 | 0 |
  | Erreurs de Nathflix | 0 | 0 |

- Test de perte (TV) :

  | Façon | Avant | Après (essai) |
  |---|---|---|
  | Fermée normalement | gardé | gardé |
  | Passée en arrière-plan | gardé | gardé |
  | Renderer planté | gardé | **perdu** |

- Une écriture de moins par visite, contre un nouveau cas de perte : pas gardé, le code du plugin est retiré.
- Machine chargée pendant ces mesures (Android Studio, WSL : environ 1,2 Go de RAM libre) : les temps ne sont pas comparés, seulement les comptes.

**Livraison**
- Commits : `f3eb847`
- Version / CHANGELOG : —

### T-064 — L'Accueil visible une seconde avant « Qui est-ce ? » à l'ouverture

- **Statut :** À valider (sur la Fire TV)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-034
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> Juste en attendant, il y a un bug que j'ai en général quand j'ouvre l'app, juste après l'intro je tombe sur la page d'accueil qui s'affiche pendant une seconde avant que le choix du profil s'affiche. Corrige ce bug pour que ça affiche directement le choix du profil et pas que l'accueil soit visible pendant une seconde avant que le choix du profil apparaisse.

**Critères d'acceptation**
- [x] Après l'intro, « Qui est-ce ? » directement, sans voir l'Accueil
- [x] Rien ne change quand le choix n'est pas proposé (« Ne plus me demander », pas connecté)
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- **Cause** :
  - l'intro native (`Intro.finish` → `MainActivity.pageReady`) s'efface dès que l'Accueil est là (`PAGE_READY`). Elle ne sait rien du choix du profil ;
  - `whoIsWatching.maybeShow` n'ouvre le choix qu'après `store.ready`, la lecture de `ctx` (profils Stremio) et l'attente de `.dx-splash` (sondée toutes les 250 ms) ;
  - puis `.dxe-who` apparaît en fondu (opacité, 0,3 s), avec l'Accueil visible dessous.
- **Correction** (plugin seul, donc aussi avec l'APK actuelle) :
  - `whoIsWatching.hold(start)` pose un cache noir `.dxe-who-hold` (`EXTRAS_CSS`, `z-index` juste sous `.dxe-who`) au début de `maybeShow`, avant toute attente, et seulement si le choix va s'afficher (mêmes conditions : connecté, route, réglage `whoIsWatching` ou profil avec code au démarrage) ;
  - `release()` le retire 350 ms après l'ouverture (fin du fondu), ou tout de suite si le choix ne s'affiche pas (profils Stremio, lecteur…). `maybeShow` le retire dans un `finally`, et un délai de 12 s le retire de toute façon ;
  - le noir prolonge la fin de l'intro. Il vaut aussi pour les autres ouvertures de `maybeShow` (retour dans l'app après 5 s, Accueil après un film).
- **Déjà là, non modifié** : au tout premier démarrage d'un appareil (aucune copie `nathflix.settings`), `maybeShow` peut partir avant `loadSettings()` et proposer le choix même si « Ne plus me demander » est coché sur le compte. Les démarrages suivants lisent la copie.

**Vérification**
- `node --check` : OK. Aucun texte nouveau.
- Test dans Chrome (copie de test du plugin qui se croit connectée, 3 runs, chaque image échantillonnée : Accueil affiché, sans écran de chargement, sans cache noir, avant que `.dxe-who` soit entièrement opaque) :

  | | Accueil visible avant « Qui est-ce ? », avant → après |
  |---|---|
  | TV (UA `nathflix-tv`, 1280×720) | 475, 475, 483 ms → 0, 0, 0 |
  | PC (1600×900) | 351, 358, 375 ms → 0, 0, 0 |

  Sur ce PC rapide ; plus long sur une Fire TV.
- Cas de bord : choix désactivé (copie des réglages en cache) : ni choix ni cache noir ; pas connecté : jamais de cache noir ; choix ouvert : plus aucun cache noir 1,5 s après.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : `cf8d198`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-046 — Boucle principale : flèches des rangées mesurées seulement au survol ; banc connecté à un vrai compte

- **Statut :** Terminé
- **Priorité :** P0
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-046 : `sync()` ciblé par zone)
>
> Tu peux me laisser me connecter sur le banc ? Comme ça tu peux tester sur un compte connecté

**Critères d'acceptation**
- [x] Banc connecté au compte de l'utilisateur, sans garder la session
- [x] Coût de chaque étape de `sync()` mesuré sur ce compte
- [x] L'étape la plus chère réduite, sans changement visible (survol des flèches identique, captures)
- [ ] ~~`sync()` découpé par zone~~ : remplacé, voir les notes

**Notes techniques**
- **Banc connecté** (`--login`) : fenêtre visible, l'utilisateur se connecte lui-même. Contexte Chromium privé (en mémoire, rien sur le disque). Le banc vérifie seulement qu'un `profile.auth.key` existe dans `localStorage`, sans jamais le lire ni le renvoyer. « Qui regarde ? » coupé (`whoIsWatching: false`), rien qui modifie le compte (`switching` sauté, `--loss-test` refusé). Les autres ajouts :
  - `--vs autre.plugin.js` : deux builds dans la même session, en alternance (a, b, puis b, a…), `a` et `b` dans le rapport ;
  - `syncSteps` : temps par étape de `sync()` quand le build les chronomètre (`perf.time("sync:<étape>")`, copie de diagnostic non commitée) ;
  - résultats enregistrés après chaque run (`partial`), pour ne pas tout perdre si le banc est arrêté.
- **Ce que montre le vrai compte** (TV bridée ×6, médiane de 5 runs) :
  - l'Accueil compte environ 7 000 éléments (908 en invité) ;
  - **au repos, rien du tout** (30 s sur l'Accueil, la fiche d'un film, le calendrier : 0 passage de `sync`, 0 lecture, 0 tâche longue). La lenteur vient des moments où la page change ;
  - sur le parcours : `sync` 30 fois, 2,5 s ; tâches longues 42, 14,6 s (pire 1,06 s) ; `board` lu 90 fois (surtout par Stremio) ; filtre par profil 0,25 s ;
  - T-042 sur ce compte (avant → après) : lectures de `discover`, `library` et `continue_watching_preview` 15, 14, 15 → 6, 4, 8 ; les temps restent dans le bruit.
- **Coût des étapes de `sync()`** sur ce compte (parcours, 3 runs) : `updateAllArrows` 1 600 ms (60 %), `syncCards` 600 ms (22 %), `communitySync` 151 ms, `episodePlay` 103 ms, le reste (23 étapes) ≈ 330 ms.
- **Pourquoi pas le découpage par zone du plan** : deux étapes font 82 % du coût. Les traiter directement rapporte presque tout, sans le risque du découpage (point de vigilance 1 : une zone mal classée fait disparaître un élément). Le découpage n'aurait plus à gagner que ≈ 1 s sur le parcours.
- **`updateAllArrows`** : les flèches et les traits de page ne se voient qu'au survol de la souris (thème : opacité 0, `:hover`). Le plugin mesurait pourtant toutes les rangées (`scrollWidth`, `offsetTop`…), une fois toutes les 3 s à chaque passage de `sync`. Chaque mesure force la mise en page de toute la page. Maintenant :
  - `hoverRow` : la rangée sous la souris, posée par `arrowsHover` (dans `onOver`, au `mouseover`) qui la mesure dans le même événement, avant l'affichage des flèches ;
  - `updateAllArrows` crée toujours les flèches de toutes les rangées (`enhanceRow`), mais ne mesure plus que `hoverRow` (titres changés, toutes les 3 s, redimensionnement) ;
  - le défilement d'une rangée la mesure comme avant (`arrowsSoon`).
- **Restent à voir** : `syncCards` (550 ms, réparti sur des centaines de cartes) et surtout les tâches longues hors de Nathflix-JS (≈ 12 s sur le parcours : rendu de Stremio et recalculs de style) → T-047, T-048, T-054.

**Vérification**
- `node --check` (plugin, banc) : OK. Aucun texte nouveau.
- Banc connecté au compte de l'utilisateur, TV bridée ×6, 5 runs de chaque en alternance, chronomètre par étape dans les deux :

  | Sur tout le parcours | Avant | Après |
  |---|---|---|
  | `updateAllArrows` | 1 370 ms | 82 ms |
  | Boucle principale (`sync`) | 30 fois, 2,48 s | 31 fois, 1,14 s |
  | Tâches longues | 43, 14,8 s (pire 1,07 s) | 39, 13,3 s (pire 1,06 s) |
  | Tâches longues, run par run | 15,2 / 15,8 / 14,2 / 14,7 / 14,8 s | 15,1 / 13,3 / 12,2 / 14,3 / 13,2 s |
  | Touches : médiane / pire | 40 / 56 ms | 40 / 56 ms |
  | Erreurs de Nathflix | 0 | 0 |

- Survol sur PC (`arrows-check.mjs`, scratchpad) : état des flèches et des traits (cachées, opacité, `top`, `height`, nombre de traits, trait actif) identique avant et après sur trois rangées, et après un clic sur « suivant ».
- Captures avant/après (invité ; Accueil, Découvrir, fiche, Réglages) : TV identique ; PC et téléphone : seulement le bruit connu des badges (le téléphone ne passe même pas par ce code).
- Non vérifié : la Fire TV (photo de la mesure à l'écran à demander avant la release 0.6.0).

**Livraison**
- Commits : `70242bf`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-054 — Accueil TV : rangées chargées à l'approche de la télécommande, par paquets

- **Statut :** Terminé
- **Priorité :** P0
- **Cible :** plugin, tools
- **Dépend de :** T-046
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-054, phase 2)

Avancée avant T-047 à T-051 au choix de l'utilisateur (« Avancer T-054 »), d'après la trace sur son compte : l'interface de Stremio pèse 9,9 s sur 13,7 s de tâches longues, pour un Accueil de 7 000 éléments. Plugin seul : elle entre dans la release 0.6.0.

**Critères d'acceptation**
- [x] Sur TV, l'Accueil ne demande plus toutes ses rangées à l'ouverture
- [x] Les rangées sont prêtes avant que la télécommande y arrive : descente de 10 rangées sans blocage ni saut
- [x] Ordre personnalisé (« Organiser l'Accueil ») et autres appareils inchangés
- [x] Moins de tâches longues, pas de touche plus lente (banc connecté)

**Notes techniques**
- **Avant** : `homeRows.preload()` demandait, 1,5 s après l'ouverture de l'Accueil sur TV, les catalogues de **toutes** les rangées (`CatalogsWithExtra` / `LoadRange` de 0 à `count`). Il avait été ajouté parce que Stremio va chercher chaque rangée en arrivant dessus, et la reconstruction de l'Accueil en plein défilement figeait la clé jusqu'à une seconde. Sur un vrai compte (24 rangées et plus), l'Accueil atteignait 7 000 éléments, que Stremio redessinait à chaque changement.
- **Maintenant** (`homeRows`) :
  - à l'ouverture, `FIRST` (6) rangées seulement ;
  - `ahead(index)`, appelé par `keyNav.fromCard` à chaque Bas sur une rangée : si moins de `LOW` (3) rangées sont prêtes sous la sélection, les `BATCH` (6) suivantes d'un coup ;
  - `load(board, end)` demande de la première rangée pas encore demandée jusqu'à `end`, en un seul `LoadRange` ;
  - avec `force` (ordre personnalisé, `dx-rows-arranged`, tous appareils) : toutes, comme avant ; `ahead` ne fait rien dans ce cas.
- **Premier essai écarté** : 5 rangées au départ, puis toujours 4 d'avance. Chaque Bas demandait alors une rangée de plus, donc une reconstruction de l'Accueil par appui : 64 petites tâches longues au lieu de 39, pire touche 56 → 104 ms. Les paquets de 6 règlent ça.
- **Banc** :
  - `--trace` : trace du navigateur pendant les touches et le parcours (premier run de chaque build) ; `traced.work` = temps du fil principal par type de travail (style, mise en page, couches, peinture…), `traced.scripts` = temps de script par fichier (l'événement le plus externe, au nom du premier script qu'il lance) ;
  - `--descend N` : N fois Bas sur l'Accueil, 700 ms d'écart ; rangée atteinte à chaque appui (`rows`, `stalls`), temps des touches, taille de l'Accueil à la fin.

**Vérification**
- `node --check` (plugin, banc) : OK. Aucun texte nouveau.
- Banc connecté au compte de l'utilisateur, TV bridée ×6, 5 runs de chaque en alternance :

  | | Avant | Après |
  |---|---|---|
  | Éléments sur l'Accueil | 6 902 | 4 610 |
  | Tâches longues sur le parcours | 42, 14,9 s | 67, 12,6 s |
  | Tâches longues, run par run | 14,9 / 14,8 / 15,7 / 14,7 / 15,7 s | 12,6 / 12,0 / 12,7 / 12,1 / 12,8 s |
  | Pire tâche longue | 1 126 ms | 585 ms |
  | 20 touches : médiane / p90 / pire | 40 / 56 / 96 ms | 32 / 48 / 48 ms |
  | Descente de 10 rangées : rangées atteintes | une par appui, 0 blocage | une par appui, 0 blocage |
  | Descente : touches médiane / pire | 64 / 112 ms | 56 / 120 ms |
  | Accueil après la descente | 6 923 éléments | 5 227 éléments |
  | Trace : scripts de Stremio / de Nathflix | 10,8 / 2,4 s | 10,7 / 2,2 s |
  | Erreurs de Nathflix | 0 | 0 |

  Le pire de la descente varie de 72 à 120 ms d'une mesure à l'autre, avant comme après : bruit.
- Captures avant/après (invité ; Accueil, Découvrir, fiche, Réglages) : identiques, hors bruit (lissage d'un titre de rangée sur PC, badges RT et Metacritic). En invité l'Accueil n'a que 6 rangées : toutes demandées dès l'ouverture, comme avant.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : `86f571d`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-047 — TV et mode léger : plus de grandes ombres floues ; effets TV aussi en mode léger

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** plugin, theme
- **Dépend de :** T-039
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-047)

**Critères d'acceptation**
- [x] Classe `html.dx-light` quand le mode léger est actif (posée au chargement et à chaque sauvegarde des réglages)
- [x] Les effets coûteux déjà retirés sur TV le sont aussi en mode léger
- [x] TV et mode léger : plus de grandes ombres floues
- [x] Rien ne change sur PC ni sur téléphone hors mode léger (captures)

**Notes techniques**
- **Déjà fait sur TV avant cette tâche** (T-039 et avant) : verre dépoli (`backdrop-filter`), flous d'images (aura et ombre de la carte de survol, fonds flous de Continuer à regarder), zoom du billboard, bords estompés des rangées, relief des cartes, fondus.
- **Plugin** : `applyLight()` met `html.dx-light` d'après `light()`, seulement si la valeur change (une classe sur `<html>` restyle toute la page). Appelée avec `applyAccent()` aux trois endroits de `loadSettings()` (copie en cache, réglages lus, `onSettingsSaved`). `dx-light` n'est pas dans `DEVICE_CLASSES` de `sharpen.prune` : le mode peut changer sans rechargement.
- **Thème** (bloc « WINDOWS AND TV », puis `node tools/bundle-theme.mjs`) :
  - les règles d'effets de `.dx-tv` valent aussi pour `.dx-light` : `backdrop-filter`, fonds pleins de la liste des flux et des menus, zoom et `will-change` du billboard, masques des rangées, `.animation-fade-in`, fondu des badges, flou de `.dx-wide-blur`, aura et ombre de la carte de survol ;
  - nouveau : sur `html.dx-tv` et `html.dx-light`, `--dx-shadow-1` et `--dx-shadow-2` deviennent un trait fin. `--dx-shadow-2` (50 px de flou) entourait l'**affiche sélectionnée**, redessinée à chaque flèche, ainsi que la liste des flux, les menus, les fenêtres, les toasts et l'aperçu de Découvrir ;
  - plus d'ombre floue sur la barre du haut défilée, les boutons du billboard au focus et la croix de Continuer à regarder ; le titre et le texte du billboard gardent une ombre courte (3 px) pour rester lisibles ;
  - les anneaux et traits intérieurs (`inset`, 1 à 3 px), dont l'anneau de sélection TV, sont gardés.
- **Pas fait, du plan** : le plafond universel des transitions (`#app *` à 150 ms). Une règle sur tous les éléments ajoute du coût de style à chacun, et elle couperait le fondu du billboard.

**Vérification**
- `node --check`, `node tools/bundle-theme.mjs` : OK. Aucun texte nouveau.
- Banc connecté au compte de l'utilisateur, TV bridée ×6, 5 runs de chaque en alternance, trace sur le premier :

  | | Avant | Après |
  |---|---|---|
  | Trace : couches + peinture + pré-peinture | 1 005 + 628 + 516 = 2 149 ms | 702 + 545 + 422 = 1 669 ms |
  | Trace : calcul des styles / mise en page | 1 586 / 1 264 ms | 1 388 / 1 125 ms |
  | Tâches longues sur le parcours | 12,5 s | 11,8 s |
  | 20 touches : médiane / p90 / pire | 32 / 56 / 168 ms | 32 / 48 / 144 ms |
  | Descente de 10 rangées : médiane / pire, blocages | 44 / 120 ms, 0 | 44 / 56 ms, 0 |
  | Erreurs de Nathflix | 0 | 0 |

- Téléphone en mode léger (`lightMode: "on"`, invité, 5 runs de chaque) : couches + peinture + pré-peinture 393 + 240 + 209 = 842 → 254 + 186 + 162 = 602 ms ; styles 630 → 507 ms ; tâches longues trop bruitées sur un si petit Accueil (médiane 1,24 → 1,10 s).
- Captures avant/après (invité, réglages par défaut) : PC et téléphone identiques, hors bruit connu (le mode léger n'y est pas actif) ; TV : seules les ombres autour de la liste des flux et de l'aperçu de Découvrir deviennent un trait fin.
- Banc : le style qui masque les images des captures est reposé à chaque chargement (`addInitScript`) ; posé une fois, il disparaissait si la page se rechargeait, et une capture « avant » sur PC montrait les images.
- Non vérifié : la Fire TV, un téléphone modeste réel.

**Livraison**
- Commits : `716f0bb`
- Version / CHANGELOG : à la release 0.6.0 (T-061)

### T-048 — Polices liées par le plugin, sans `@import` dans le thème intégré

- **Statut :** Abandonné (pas de gain mesurable, à chaud ni à froid)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-048)

**Critères d'acceptation**
- [ ] ~~Thème intégré sans `@import`, polices liées par le plugin~~ : essayé, pas gardé
- [x] Banc : moment où le thème s'applique et où la police est chargée (`themeMs`, `fontMs`), démarrage à froid (`--cold`)

**Notes techniques**
- **Essai** (non gardé) :
  - `tools/bundle-theme.mjs` retirait la ligne `@import url("https://fonts.googleapis.com/…")` de la copie intégrée (le fichier `nathflix.theme.css` la gardait) et refusait une copie qui en contiendrait encore un ;
  - `ensureFonts()` (dans `start()`, avant `builtInTheme.ensure()`) ajoutait `<link rel="preconnect" href="https://fonts.gstatic.com">` et la feuille des polices (`#dx-fonts`). `display=swap` gardé partout : le `optional` prévu pour la TV laisserait la police système pour toute une session au premier lancement.
- **Hypothèse** : dans une `<style>`, Chrome n'appliquerait rien du thème tant que la feuille importée de Google n'est pas revenue. **Fausse en pratique** : le thème s'applique au même moment avec ou sans `@import`, cache chaud comme cache vide.
- **Banc** :
  - `themeMs` : quand la variable `--dx-bg` du thème devient visible sur `<html>` ; `fontMs` : quand Inter 600 est chargée (`document.fonts`), depuis le début de la page ;
  - `--cold` : cache HTTP du navigateur vidé avant chaque run (`Network.clearBrowserCache`), comme un premier lancement de la journée.

**Vérification**
- Banc (invité, bridé ×6, 5 runs de chaque en alternance ; médianes en ms depuis le début de la page) :

  | | Avant | Après (essai) |
  |---|---|---|
  | TV, cache chaud : page prête / thème / police | 1 153 / 599 / 694 | 1 281 / 651 / 848 |
  | PC, cache chaud | 1 316 / 684 / 843 | 1 563 / 778 / 970 |
  | TV, cache vide (`--cold`) | 1 843 / 1 331 / 1 504 | 1 763 / 1 585 / 1 585 |
  | PC, cache vide | 1 885 / 1 436 / 1 620 | 2 041 / 1 548 / 1 920 |
  | Erreurs de Nathflix | 0 | 0 |

  Rien ne s'améliore (les écarts sont dans le bruit, plutôt en défaveur de l'essai) : le code de l'essai est retiré, le banc garde ses nouvelles mesures.
- Captures avec l'essai : identiques (hors bruit des badges). Sans objet une fois le code retiré.

**Livraison**
- Commits : `45c10d5`
- Version / CHANGELOG : —

### T-050 — Index des épisodes allégé sur TV et en mode léger

- **Statut :** Abandonné (pas de gain mesurable)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-050)

**Critères d'acceptation**
- [ ] ~~Index des épisodes allégé sur TV et en mode léger~~ : essayé, pas gardé
- [x] Banc : cache à reconstruire à chaque run (`--drop`)

**Notes techniques**
- **Essai** (non gardé), `lightIndex()` = `IN_TV || light()` :
  - `librarySeries()` : les 30 séries vues en dernier au lieu de 80 ;
  - `refresh()` : une requête à la fois au lieu de trois ; le premier `refresh()` sans `force` attendait 20 s après la première demande (le calendrier, qui demande avec `force`, n'attendait pas) ;
  - `get()` : la réponse traitée quand le navigateur est libre (`requestIdleCallback`, 1 s au plus).
- **Pourquoi aucun gain** : construire l'index coûte très peu au fil principal, même avec la bibliothèque de l'utilisateur et le cache vidé : 0 tâche longue au repos dans les deux cas. Le départ décalé de 20 s tombait pendant les touches du banc (après 10 s de stabilisation et 10 s de repos) : il déplace le travail sans le supprimer, et les touches en ont souffert.
- **Banc** : `--drop "nathflix.eps."` retire à chaque chargement les clés de `localStorage` qui commencent ainsi (ici le cache de l'index, gardé 6 h), pour mesurer un index à reconstruire.

**Vérification**
- Banc connecté au compte de l'utilisateur, TV bridée ×6, cache de l'index vidé à chaque run, 5 runs de chaque en alternance :

  | | Avant | Après (essai) |
  |---|---|---|
  | Tâches longues au repos (10 s) | 0 | 0 |
  | Tâches longues sur le parcours | 38, 7,23 s | 40, 7,12 s |
  | 20 touches : médiane / p90 / pire | 32 / 48 / 72 ms | 40 / 104 / 168 ms |
  | Page prête | 1 894 ms | 1 879 ms |
  | Erreurs de Nathflix | 0 | 0 |

  Le code de l'essai est retiré.
- Les tâches longues du parcours (≈ 7 s) ne se comparent pas aux mesures de T-046 à T-047 (12 à 15 s) : ces runs vident le cache des épisodes, ce qui change aussi l'état de l'Accueil, et l'état du compte a pu changer entre-temps.

**Livraison**
- Commits : `4f8d7f0`
- Version / CHANGELOG : —

### T-049 — Démarrage Android

- **Statut :** À valider (photo du démarrage sur la Fire TV avec la prochaine release)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-041
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> (chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-049)

Pas d'adb (la Fire TV n'est pas sur le réseau du PC) : les mesures sur l'appareil se font par des photos de la mesure à l'écran (« Mesure des performances »), au choix de l'utilisateur.

**Critères d'acceptation**
- [x] Temps de démarrage dans la mesure à l'écran (étape 1 du plan, adaptée aux photos)
- [x] Référence sur la Fire TV : release 0.7.0, 11414 ms (photo de l'utilisateur)
- [x] Étape 3 : mises à jour vérifiées après la page
- [x] Étape 2 (priorité du renderer) essayée, retirée : aucun effet mesurable
- [ ] Photo du démarrage après la prochaine release (étape 3 gardée seulement si elle ne fait pas pire)
- [ ] Étapes 4 (préchauffage) et 5 (intro 720p) : à essayer seulement si la photo le justifie

**Notes techniques**
- **Ligne « Démarrage »** (`startup`, juste avant `perfReadout`) : `startup.watch()` (dans `start()`) sonde toutes les 50 ms, 60 s au plus, jusqu'à ce que la page ait un écran à montrer (`SPLASH_READY`, ce qu'attend l'intro). Elle tourne même si la mesure à l'écran est coupée.
  - Nouvelle APK : « Démarrage : N ms depuis l'ouverture de l'app », de `MainActivity.onCreate` (`launchedAt`, par le bridge) à la page prête. Seulement pour la première page de l'app (page commencée moins de 30 s après l'ouverture), pas pour une page rechargée plus tard ;
  - APK plus ancienne (pas de `bridge.launchedAt`) : « Démarrage : N ms depuis le début de la page ».
- **Bridge** (nouvelle méthode, les quatre points de CLAUDE.md) : `Bridge.launchedAt(token): Long` (`@JavascriptInterface`, jeton vérifié), `native.launchedAt()` dans `nathflix-tv.js` (détectée : `bridge.launchedAt ? … : 0`), règles ProGuard déjà en place pour les méthodes annotées. `nathflix-tv.js` la pose une fois dans `__nathflixTv.launchedAt` ; le plugin vérifie que c'est un nombre.
- **Étape 3, faite (2026-11-01)** : `MainActivity.whenPageReady(block)` sonde `pageReady()` toutes les secondes (60 fois au plus, puis lance le bloc quand même) et lance le bloc une seule fois ; `Updates.checkPlugin` / `checkApp` partent 5 s après. Avant, ils partaient dans `onCreate` : `checkPlugin` télécharge à chaque lancement le plugin entier de la dernière release (1,6 Mo) et relit celui de l'appareil, en plein démarrage de Stremio. La build Dev ne vérifie jamais les mises à jour (`!debuggable`) : l'effet ne se voit qu'avec une release, sur la photo du démarrage.
- **Étape 2, essayée puis retirée** : `setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, false)`. Sur l'émulateur, le processus de la page a la même importance que l'app avec ou sans (au premier plan : `fg`/`BTOP` ; après Accueil : `prev`/`LAST`, comparé à la build Dev publiée). Le cas où elle compterait (la page couverte par le lecteur, et libérée par Android faute de mémoire) ne se teste pas sans lancer de film, et aucun redémarrage de la page après un film n'a été signalé. Rien ne la justifie au démarrage.
- **Étapes 4 et 5, pas essayées** : leur gain se mesure en démarrages sur l'appareil ; à faire seulement si la photo d'après la release reste loin de la cible (< 6 s).

**Vérification**
- `node --check` (plugin, `nathflix-tv.js`) : OK. Traductions : les deux nouveaux textes sont dans `frDictionary()` (`fr-missing.mjs` : seulement les deux faux positifs connus).
- Dans Chrome (TV, invité, mesure à l'écran activée) : « Start-up: 1994 ms from the page's start » s'affiche (Stremio en anglais pour l'invité).
- Kotlin : compilé par la CI `android-dev.yml` (voir le journal).
- Étape 3 (2026-11-01) : compilée localement (`assembleDebug`) ; sur l'émulateur Fire TV, avec un log temporaire (retiré avant le commit) à la place des vérifications, le bloc part une seule fois, 4571 ms après l'ouverture (démarrage à froid). CI `android-dev.yml` : voir le journal.
- Non vérifié : la Fire TV.
- **Référence Fire TV 4K** (photos de l'utilisateur, 2026-11-01, release 0.7.0, mode léger activé, 4 cœurs, 2 Go) : « Démarrage : 11414 ms depuis l'ouverture de l'app ». Pendant la navigation : réponse aux touches 40 à 112 ms en général, jusqu'à 1416 ms au pire ; 14 à 30 blocages en 10 s (3,7 à 6,6 s en tout, le plus long 0,9 à 1,1 s) ; 4683 à 5179 éléments. Sur l'émulateur, le même démarrage prend ~2,3 s.

**Livraison**
- Commits : `ed1ed1a` ; étape 3 : voir le journal
- Version / CHANGELOG : ligne « Démarrage » à la release 0.6.0 (T-061) ; étape 3 à la prochaine release

### T-065 — Bouton « Lecture » absent des pages de films ouvertes sans leurs flux

- **Statut :** À valider (sur l'appareil, avec un compte et des extensions de flux)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-012
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> Le bouton Lancer n'apparait pas sur les pages des films, sur les pages des séries ça marche mais pas les films, corrige ça

**Critères d'acceptation**
- [x] « Lecture » sur la page d'un film, quelle que soit la façon de l'ouvrir
- [x] Le bouton ouvre les flux du film et lance la première source, comme « Reprendre » sur une série

**Notes techniques**
- **Cause** : `episodePlay.ensure()` ne mettait le bouton que sur une page de flux (`#/detail/<type>/<id>/<vidéo>`) ou sur la page d'une série (`#/detail/series/<id>`). Un film ouvert à `#/detail/movie/<id>`, sans vidéo, n'était ni l'un ni l'autre. C'est l'adresse du bouton « Plus d'infos » du billboard (`data-href="/detail/${type}/${m.id}"`) et de certains catalogues d'extensions ; les cartes Cinemeta donnent `/<id>/<id>`, d'où un bouton présent ou non selon le chemin.
- **Correction** :
  - `onMovie` (film sans vidéo dans l'adresse) : le bouton s'affiche, libellé « Lecture » ;
  - au clic, `playMovie(id)` : la vidéo du film est `behaviorHints.defaultVideoId` de `meta_details` si Stremio la donne, sinon l'id du film ; `pending` est posé, puis `navigate()` ouvre la page des flux, qui lance la première source quand elles sont là (`playWhenReady`, comme pour une série).
- **Second défaut trouvé en testant** : `navigate()` change l'adresse par `history.pushState` + `popstate`, ce qui n'envoie pas de `hashchange`. La boucle de Nathflix ne repassait donc que si Stremio modifiait la page. Sur la page des flux d'un film sans extension de flux, rien ne bougeait, et la lecture en attente n'était jamais lancée. `navigate()` appelle maintenant `scheduleSync()`.
- Le message sans source dit « pour ce film » sur la page d'un film (`No streams found for this film.`, traduit) au lieu de « pour cet épisode ».

**Vérification**
- `node --check` : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus.
- Dans Chrome (invité, PC et TV ; `play-check.mjs`, scratchpad) :

  | Adresse | Avant | Après |
  |---|---|---|
  | `#/detail/movie/<id>/<id>` (cartes) | bouton | bouton |
  | `#/detail/movie/<id>` | **pas de bouton** | « Lecture » |
  | Clic sur « Lecture » | — | page des flux, « Recherche des flux… », puis « Aucun flux trouvé pour ce film. » (invité : pas d'extension de flux) |

- Non vérifié : la lecture réelle d'une source (compte avec extensions de flux) ; pas testé sur le compte de l'utilisateur pour ne pas lancer de film à sa place (sa progression changerait).

**Livraison**
- Commits : `6f318d3`
- Version / CHANGELOG : 0.6.0 (T-061)

### T-066 — Réglages de Nathflix synchronisés avec le compte Stremio

- **Statut :** À valider (sur deux appareils de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin, tools
- **Dépend de :** T-027
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> J'aimerais que mes paramètres de Nathflix dans l'app soient synchronisés avec mon compte. Je veux que les paramètres modifiés se reporte sur les autres appareils.

**Critères d'acceptation**
- [x] Un réglage changé sur un appareil arrive sur les autres appareils du même compte
- [x] Seuls les réglages changés à la main partent ; le plus récent gagne, réglage par réglage
- [x] Les réglages propres à un appareil restent sur l'appareil
- [x] Pas d'aller-retour : un réglage reçu n'est pas renvoyé comme un changement local
- [x] Worker redéployé avant la publication du plugin
- [ ] Vérifié sur deux appareils de l'utilisateur

**Notes techniques**
- **Entrée synchronisée `settings`** : `{ réglage: { v, t } }`, avec seulement les réglages changés à la main. Un réglage que personne n'a touché reste à la valeur par défaut de chaque appareil (par exemple les bandes-annonces, coupées par défaut sur TV). Ajoutée à `SYNCED` (plugin) et `KEY` (`tools/profile-sync/worker.js`).
- **`settingsSync`** (juste avant `SYNCED`) :
  - `noted()` : appelé à chaque enregistrement (`settingsSaved()`, l'ancien gestionnaire de `onSettingsSaved`, qui reçoit tous les réglages sur TV, sur l'app desktop et dans Stremio Enhanced). Compare avec `seen` (les réglages vus la dernière fois) plutôt qu'avec `settings`, que du code modifie parfois avant d'enregistrer (« Ne plus me demander »). Une valeur égale à celle reçue du compte n'est pas renvoyée ;
  - `merge()` / `same()` : la fusion de `cloudSync.merge` pour `settings`, réglage par réglage (le `t` le plus récent gagne) ; un réglage inconnu de cette version est ignoré ;
  - `apply()` : après une fusion qui change `settings`, une fois les réglages de l'appareil chargés (`settingsReady`) : enregistre les réglages reçus sur l'appareil (`API.saveSetting`), puis les applique tout de suite (`settingsSaved`), sans attendre que l'environnement prévienne.
- **`LOCAL`** (restent sur chaque appareil) : `lightMode`, `perfOverlay`, `profileSync`, `builtInTheme`, `convertVideo`, `enhance`, `tvTorrents`, `trailers`, `trailerDelay`, `externalPlayer`, `hdrSubtitles`, `tmdbKey`, `mdblistKey`. Ils dépendent du matériel ou de l'installation, ou ce sont des clés personnelles, qui ne partent pas vers le serveur.
- **Textes** : le réglage devient « Synchroniser les profils et les réglages avec mon compte Stremio », sa description dit ce qui reste sur l'appareil ; l'état devient « Profils et réglages synchronisés… ». Traduits.
- **Ordre de publication** (CLAUDE.md) : le Worker refuse toute la requête pour une seule clé inconnue. Il est redéployé avant que le plugin parte sur `main`, que Stremio Enhanced et les installeurs prennent directement.

**Vérification**
- `node --check` (plugin, Worker) : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus.
- `node tools/profile-sync/test.mjs` : tous les contrôles passent, dont le nouveau « Nathflix's settings are kept (settings) ».
- Deux appareils simulés (deux navigateurs Chrome, le vrai code du Worker en mémoire sur SQLite, un compte de test ; `settings-sync-e2e.mjs`, scratchpad) :
  - A change l'accent (rouge → bleu) et passe en mode léger ; A envoie ses réglages ;
  - B reçoit l'accent bleu, l'enregistre dans ses propres réglages, garde son mode léger (`auto`), et ne renvoie rien ;
  - B coupe le billboard et l'envoie ; A, relancé, le reçoit et garde son accent et son mode léger.
- Non vérifié : deux vrais appareils de l'utilisateur (ce test n'a pas touché à son compte).

**Livraison**
- Commits : `abde457`
- Worker : redéployé (`npx wrangler deploy`) avant de pousser ce commit
- Version / CHANGELOG : 0.6.0 (T-061)

### T-061 — Release 0.6.0 (performances, réglages synchronisés, bouton Lecture des films)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-042 à T-066
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-10-31
- **Branche :** `main`

**Demande**
> Après ces deux taches tu peux faire une vrai release

(les deux tâches : T-065, bouton Lecture des films ; T-066, réglages synchronisés)

**Critères d'acceptation**
- [x] `@version 0.6.0`, section `## 0.6.0` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] Worker de synchronisation redéployé avant la publication (clé `settings`, T-066)
- [x] CI verte, tag `v0.6.0`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 600, plugin embarqué en 0.6.0

**Notes techniques**
- **Contenu** :
  - gardées : T-042 (lectures du cœur), T-046 (flèches mesurées au survol), T-047 (ombres, mode léger), T-054 (Accueil TV par paquets, avancée), T-049 (temps de démarrage dans la mesure à l'écran, `Bridge.launchedAt`) ;
  - correctifs : T-063 (bouton Lecture aligné), T-064 (« Qui est-ce ? » sans voir l'Accueil), T-065 (Lecture sur les pages de films) ;
  - fonction : T-066 (réglages synchronisés) ;
  - abandonnées, sans gain mesurable : T-043, T-044, T-045, T-048, T-050.
- **Pas dans cette release** : T-049 (étapes de démarrage) et T-051 (rendu 720p sur TV) attendent des photos de la Fire TV ; T-052 et la suite sont en phase 2.
- **APK** : nouvelle méthode du bridge (`launchedAt`). Un plugin 0.6.0 dans une APK plus ancienne affiche le démarrage « depuis le début de la page ».

**Vérification**
- `node --check` (plugin, `nathflix-tv.js`, Worker), `node tools/profile-sync/test.mjs` : OK.
- Résultats du chantier sur le compte de l'utilisateur (TV bridée ×6, parcours du banc) : voir T-046, T-054 et T-047 (lectures du cœur ÷ 2, boucle de Nathflix 2,5 → 1,1 s, Accueil 7 000 → 4 600 éléments, pire tâche longue 1,1 → 0,6 s, dessin −22 %).
- CI sur `5a8332f` : Cross-platform, mpv Windows et build Android de test verts.
- Tag `v0.6.0` sur `5a8332f` ; run « Release » 41247091622 : tous les jobs verts du premier coup (publication, APK, Windows, Mac ×2, Linux ×2).
- `gh release view v0.6.0` : les 11 fichiers, les mêmes noms que pour la 0.5.2.
- APK :
  - `apksigner` : `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b`, identique à l'APK 0.5.2 ;
  - `aapt2` : versionCode 600, versionName 0.6.0 ;
  - plugin embarqué en 0.6.0 ; `nathflix-tv.js` avec `launchedAt`.
- Worker `nathflix-profiles` redéployé avant la publication (version `3bcdbda6`) : `OPTIONS /sync` 204, `POST` sans session ou avec une session inconnue 401.
- Non vérifié : la Fire TV et un téléphone avec cette release.

**Livraison**
- Commits : `5a8332f` (0.6.0)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.6.0

### T-067 — « Organiser l'Accueil » : textes des rangées superposés

- **Statut :** Terminé
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Corrige ce bug visuel sur le texte ici

(capture de « Organiser l'Accueil » avec une trentaine de rangées : le nom de chaque rangée chevauche la ligne « extension · type », et l'explication sous le titre a disparu)

**Critères d'acceptation**
- [x] Chaque ligne garde sa hauteur, la liste défile quand elle est plus haute que la fenêtre
- [x] L'explication sous le titre et les boutons du bas restent entiers

**Notes techniques**
- **Cause** : la feuille (`.dxe-sheet`) est une colonne flex de hauteur limitée, et la liste (`.dxe-list`) une autre colonne flex. Le CSS de Stremio donne `overflow: hidden` à tous les éléments (`* { overflow: hidden }` dans son `main.css`), et un élément flex qui coupe ce qui dépasse a une hauteur minimale de 0. Avec beaucoup de rangées, les lignes (`.dxe-row`) et l'en-tête (`.dxe-head`) étaient donc écrasés au lieu de faire défiler la liste.
- **Correction** : `flex: none` sur `.dxe-head`, `.dxe-row` et `.dxe-foot` (`EXTRAS_CSS`). Vaut aussi pour « Ordre et sauvegarde » des extensions, qui utilise les mêmes classes.

**Vérification**
- `node --check` : OK.
- Dans Chrome (invité, fenêtre de 420 px de haut pour forcer le défilement ; `arrange-check.mjs`, scratchpad) :

  | | Avant | Après |
  |---|---|---|
  | Ligne (hauteur / contenu) | 34 / 40 px | 46 / 46 px |
  | En-tête | 72 / 86 px | 86 / 86 px |
  | Liste (visible / contenu) | 231 / 231 px, sans défilement | 209 / 304 px, défile |

- « Ordre et sauvegarde » des extensions (même défaut, signalé aussi par l'utilisateur ; `addons-check.mjs`, fenêtre de 360 px) : ligne 22 / 34 px → 46 / 46 px, en-tête 59 / 86 px → 86 / 86 px, la liste défile.

**Livraison**
- Commits : `75a02f6`
- Version / CHANGELOG : à la prochaine release

### T-068 — TV : sélection à la télécommande (contour coupé, point de départ, déplacements) et défilement fluide

- **Statut :** À valider (sur la Fire TV, avec le compte de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-046, T-054
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Crée un émulateur de FireTV avec Android Studio […] teste l'application dessus et essaie d'identifier des bugs ou des ralentissement sur l'app pour les corriger.

> Sur TV la selection des éléments est pas trop intuitive et elle est souvent bugé(coupé, pas bien visible, mal placé par défault, etc..), teste bien tout ça pour le corriger

> Sur "Lecture", le bord gauche du contour est légèrement rogné […] C'est mineur mais il faut la corriger, c'est le genre d'exemple dont je parlais.

> Sur TV aussi quand on descend la page avec la touche de flèche vers le bas, ça scroll d'un coup, ce qui renforce l'impression de lags/freeze. Il faudrait un scroll fluide quand on descend/remonte la page.

> Quand on ouvre la page d'une série ou d'un film (ou sur les pages épisodes aussi) la sélection du bouton par défaut devrait être sur le bouton de lecture

**Critères d'acceptation**
- [x] Le contour de la sélection n'est jamais coupé (boutons, pastilles, lignes de Réglages, filtres, Calendrier…)
- [x] Une page s'ouvre avec quelque chose de sélectionné ; la page d'un film, d'une série ou d'un épisode sur son bouton de lecture
- [x] Les déplacements suivent ce qu'on attend (menu du haut ↔ page, menu de Réglages, retour sur une page)
- [x] La page défile de façon fluide, sans perte de réactivité mesurable
- [ ] Vérifié sur la Fire TV avec le compte de l'utilisateur

**Notes techniques**
- **Émulateur** : AVD `Fire_TV` (Android TV 14, x86, profil `tv_1080p`, 2 Go, WebView 113). Le SDK n'a pas d'image Amazon, et les images Android TV 9 et 11 ont une WebView trop ancienne (< 100). Build « Nathflix Dev » de la pré-release `dev`, avec le plugin de travail déposé dans `files/` en version 0.6.99, plus récente que celle de l'APK (`Updates.pluginSource`). Banc par CDP (`adb forward` vers `webview_devtools_remote_<pid>`), touches par `adb shell input keyevent`, processeur bridé ×12 (`Emulation.setCPUThrottlingRate`) pour approcher une clé TV.
- **Contour coupé** : sur TV, le contour de sélection est un `outline` à 3 px de l'élément, et Stremio coupe toutes ses boîtes (`* { overflow: hidden }`). `ringRoom` (TV, `focusin`/`focusout`) remonte les boîtes qui le coupent :
  - une boîte qui ne défile pas reçoit `dx-ring-room` (`overflow: clip` et `overflow-clip-margin: 10px`), plus `dx-ring-flow` (`display: flow-root`) si elle est en `block`, pour garder son propre contexte de formatage ;
  - dans une boîte qui défile, ou que l'élément remplit en largeur, le contour passe à l'intérieur (`dx-ring-in`). Il est resserré sur la partie visible quand l'élément dépasse une boîte fixe (icônes rondes Ma liste et Vu, `--dx-ring-in`) ;
  - les boîtes en `display: contents` sont ignorées : elles n'ont pas de boîte (les filtres de Découvrir prenaient un décalage de −261 px).
- **Point de départ** :
  - `keyNav.defaultPick()` essaie, dans l'ordre : le titre quitté sur cette page, le Play du billboard, le `.dx-play-ep` d'une fiche, sa première source, son premier épisode, la section de Réglages en cours, le premier titre. Chaque piste est testée seule, avant le parcours complet de la page.
  - `keyNav.land()` (étape de `sync()`, TV) le sélectionne à l'arrivée sur une page, une fois par visite, sauf sous « Qui est-ce ? », une fenêtre ou le splash.
  - Une fiche attend 1,5 s son bouton de lecture, qui arrive après les sources. Le parcours complet n'est fait qu'une fois par page, et une passe est programmée à 1,6 s pour une page qui ne bouge plus.
  - `remember()` garde le titre quitté (par son adresse) pour le retour sur la page.
- **Déplacements (TV)** :
  - du menu du haut vers le bas : `defaultPick` ;
  - vers le haut, le menu n'est atteint qu'une fois la page revenue en haut, et sur l'onglet de la page en cours. Le premier onglet `selected` trouvé était celui d'une page précédente, que Stremio garde cachée ;
  - sinon, `beyond()` va à l'élément le plus proche hors de l'écran, dans la boîte qui défile ;
  - menu de Réglages : la flèche bas ne quitte plus la liste par le bas, et la flèche droite mène en haut de la section affichée.
- **Défilement** : `SCROLL = "smooth"` partout. Il était instantané sur TV depuis dx.bi, quand une clé avait toutes les rangées et leurs effets à dessiner. Un `scroll-margin` (12 px, 2,5 rem pour un titre) garde visibles le contour et le haut de la rangée suivante.
- **Autres défauts trouvés** :
  - la loupe de recherche repliée (fenêtres de moins de 1450 px, donc toujours sur TV) sortait de sa case et passait sous l'avatar : le champ caché gardait ses 35 px de marge intérieure. Sur PC à 1280 px, elle était collée à l'icône des profils ;
  - l'anneau sur une affiche claire était peu visible : un trait sombre est ajouté à l'intérieur de l'anneau blanc ;
  - les barres de défilement étaient visibles sur TV : elles sont masquées ;
  - le bouton de lecture restait sur « Recherche des flux… » quand aucune extension n'avait de source : il affiche « Aucun flux trouvé » dès que Stremio affiche son message (`message-container` de la liste) ;
  - le bord gauche du contour de « Lecture » était rogné de 1 px, car le bouton grossit à la sélection : marge du billboard portée de 8 à 10 px.
- **Non traité** : dans le panneau d'informations de Stremio (fiche, Découvrir), la description, la distribution ou les réalisateurs sont coupés au bas de leur zone qui défile. Ce n'est pas lié à la sélection.

**Vérification**
- `node --check` : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus.
- Émulateur, en invité. Parcours aux flèches avec, à chaque appui, l'élément sélectionné, sa part visible et ce qui coupe son contour (`nav.mjs`, scratchpad).
  - **Avant** :
    - contour coupé sur « Lecture » (1 px), la note IMDb, les pastilles d'acteurs, « Profils », « Statistiques » et « Addons » (seul le trait du bas restait), « Connexion » du Calendrier (seuls les coins restaient), le curseur de zoom ;
    - rien de sélectionné à l'arrivée sur une fiche, Découvrir, Réglages, Extensions et Calendrier ;
    - du menu vers le billboard, la flèche bas menait à « Plus d'infos » ;
    - vers le haut depuis la page, la flèche menait à l'onglet le plus proche, en sautant ce qui était au-dessus hors de l'écran.
  - **Après**, tour complet de 67 appuis (Accueil, fiche d'un film, d'une série, d'un épisode, Découvrir et ses filtres, Extensions, Calendrier, Réglages, menu du haut) :
    - aucune sélection perdue, aucun contour coupé ; seules les icônes rondes Ma liste et Vu dépassent de leur pastille (dessin de Stremio), avec leur anneau resserré ;
    - arrivées : Accueil → Lecture, film → Lecture, série → « Lancer S1 É1 », épisode → « Lancer l'épisode », Découvrir → premier titre, Réglages → Général, Extensions → Installées, Calendrier → Connexion ;
    - Retour depuis une fiche → le titre quitté.
- Défilement de l'Accueil (6 appuis bas puis 6 haut), processeur bridé ×12, 4 passes de chaque, à la suite :

  | | Appui (médiane) | Pire appui | Images > 34 ms |
  |---|---|---|---|
  | Plugin d'avant (saut) | 48–64 ms | 80–112 ms | 3–5 |
  | Ce plugin (fluide) | 56–64 ms | 72–104 ms | 3–7 |

  Pas d'écart mesurable. Avec le même code et le défilement en saut : 64–72 ms, 6 à 9 images lentes.
- PC (Chrome, invité), barre du haut :
  - à 1280 px, la loupe est dans sa case : 1078–1106 px avant, 1043–1071 px après, pour une case de 1038–1075 px ;
  - à 1600 px : identique.
- Journal de la page pendant les tests : aucune erreur de Nathflix.
- Non vérifié : la Fire TV, et le compte de l'utilisateur (une trentaine de rangées, Continuer à regarder, profils).

**Livraison**
- Commits : `432118a`
- Version / CHANGELOG : à la prochaine release

### T-069 — TV : carte d'aperçu et bande-annonce sur le titre sélectionné ; défauts trouvés avec le compte de l'utilisateur

- **Statut :** À valider (sur la Fire TV)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-068
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Oui corrige tout les bugs que tu voit.
>
> Sinon sur PC, avec la souris sur une série/film […] ça agrandis la vue, et après quelques secondes ça lit même la bande d'annonce dans la petite fenêtre. Il faudrait avoir ça sur la version TV aussi quand on est sur la sélection d'un film/série. Ca aiderai en plus la lisibilité pour savoir ou se trouve le curseur.

(capture PC : le titre « Cette semaine » passe sous le logo de la barre du haut ; l'utilisateur s'est ensuite connecté lui-même sur l'émulateur, et a autorisé la validation de son profil en cours après chaque redémarrage)

**Critères d'acceptation**
- [x] Sur TV, le titre sélectionné s'agrandit en carte d'aperçu, avec un contour, puis sa bande-annonce démarre, muette
- [x] La sélection n'est plus perdue ni posée derrière un écran (« Qui est-ce ? », fenêtre de Stremio)
- [x] « Cette semaine » est accessible à la télécommande
- [x] Les textes du panneau d'informations ne sont plus coupés à mi-ligne sur TV
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- **Carte d'aperçu sur TV** : elle existait (`preview.onFocus`, 1,1 s après l'arrêt de la télécommande), mais le mode léger « auto » la coupait sur tout appareil modeste (`MODEST_DEVICE` : 4 cœurs ou moins, ou 2 Go ou moins), donc sur la Fire TV.
  - `previewsOn()` ne dépend plus du mode léger sur TV ; la description du mode léger le dit sur TV (traduite).
  - `trailers` est activé par défaut partout. Sur TV, la bande-annonce ne commence à se charger qu'après 2,5 s de carte ouverte.
  - Sur TV, la carte porte le contour de la sélection, et elle se ferme dès que la sélection bouge. Gardée jusqu'à l'ouverture de la suivante, elle montrait le titre quitté et laissait le titre sélectionné poussé de côté, à moitié hors de l'écran.
  - Sur TV, la flèche bas va toujours à la rangée suivante (sur PC, au clavier, elle entre dans les boutons de la carte).
- **« Qui est-ce ? »** :
  - la fenêtre promotionnelle de Stremio, ouverte dessous après la connexion, gardait la sélection ; les flèches allaient alors à sa croix, invisible. `#app` reçoit `inert` tant que l'écran est ouvert. `keyNav.layer()` ramène dans l'écran de Nathflix du dessus (Qui est-ce, fenêtre, pavé du code) une flèche partie d'ailleurs, et `land()` y remet la sélection à chaque passage (l'écran s'ouvre pendant le film d'intro natif, et la sélection posée alors ne tenait pas ; un passage est aussi programmé quand la WebView reprend le focus) ;
  - `render()` retrouve l'élément sélectionné après un nouveau dessin (les profils reçus du compte redessinaient l'écran).
- **Sélection perdue sur l'Accueil** :
  - `homeRows.ahead()` (T-054) comparait la position dans les rangées affichées à la liste des catalogues. Avec des rangées masquées en haut de la liste (13 sur 67 chez l'utilisateur), les rangées suivantes n'étaient jamais demandées. Il compte maintenant les rangées prêtes sous la sélection, sur la page ;
  - `keyNav` ne saute plus par-dessus des rangées en cours de chargement (`loadingBetween`, `loadingBelow`) : il allait à une rangée lointaine, redessinée aussitôt, ou à « Organiser l'Accueil » tout en bas ;
  - quand l'Accueil redessine le titre sélectionné, `land()` remet la sélection sur la même adresse, la copie la plus proche de l'ancienne place, même hors de l'écran (`nearestCopy`, `remember` garde aussi la position).
- **Défilement TV** : le défilement fluide du navigateur s'arrêtait au bout d'environ 400 ms quand l'Accueil changeait en route (rangées qui se remplissent), et laissait la rangée choisie en bas de l'écran. `reveal()` et `glideBy()` : glissade maison sur TV (chaque image couvre 30 % du reste, vers la position recalculée de l'élément ; arrêt au bout de 700 ms). Le PC garde `scrollIntoView` en `smooth`.
- **« Cette semaine »** : ses épisodes (`.dxe-air`) étaient sautés. Haut et bas y passent entre deux rangées (`airingBetween`), gauche et droite restent dedans, et l'anneau est dessiné sur leur couche `::after`, car l'image du dessous couvrait le contour ; pas de grossissement sur TV (la piste le coupait).
- **Panneau d'informations** (fiche, Découvrir) : quand son contenu dépasse, son bas s'estompe en fondu sur TV (`infoFade`, classe `dx-more`, `mask-image`), au lieu d'une ligne coupée.
- **Barre du haut** (capture PC) : son état opaque suivait n'importe quel cadre qui défilait, y compris ceux des pages que Stremio garde cachées, et une barre redessinée le perdait. `PAGE_SCROLLERS` : seul le cadre visible compte, et `checkScrolled()` le revérifie à chaque passage de `sync()`. Non reproduit en navigateur (il faut la rangée « Cette semaine » de l'utilisateur).

**Vérification**
- `node --check` : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus.
- Émulateur, compte de l'utilisateur (67 rangées dont 13 masquées, Continuer à regarder, Cette semaine, deux profils) :
  - « Qui est-ce ? » s'ouvre sur le profil en cours (avant : rien de sélectionné, et la flèche allait à la fenêtre de Stremio cachée dessous) ;
  - descente juste après le démarrage, deux fois : Lecture → Continuer à regarder → Cette semaine → rangées, sans perte (avant : sélection perdue, ou saut à « Organiser l'Accueil ») ;
  - tour de 55 appuis (Accueil, Découvrir, filtres) : aucune sélection perdue, appuis à 40 ms en médiane, 112 ms au pire ;
  - carte d'aperçu sur « The Invite » : ouverte, avec contour, puis bande-annonce lancée.
- Processeur bridé ×12, pause de 7 s sur 5 titres d'une rangée :

  | | Carte ouverte | Images > 100 ms | 95 % des images sous |
  |---|---|---|---|
  | Carte seule | 5 / 5 | 3 | 17 ms |
  | Carte et bande-annonce | 5 / 5 (vidéo lancée 4 fois sur 5 en 12 s) | 97 | 217 ms |

  La carte ne coûte presque rien ; c'est le chargement du lecteur YouTube qui fait saccader. D'où son départ retardé sur TV. Elle se coupe dans les réglages, appareil par appareil (`trailers` fait partie de `settingsSync.LOCAL`).
- Défilement de l'Accueil (compte de l'utilisateur, ×12, 2e et 3e passes) : 0.6.0 80–88 ms par appui, 9 à 11 images de plus de 50 ms ; ce plugin 72–96 ms, 8 à 12.
- PC (Chrome, invité) : barre opaque après défilement, survol d'une affiche sans décalage de la page.
- Non vérifié : la Fire TV ; la capture PC (« Cette semaine » sous le logo) n'a pas été reproduite.

**Livraison**
- Commits : `66156cc`
- Version / CHANGELOG : à la prochaine release

### T-070 — « Qui est-ce ? » au démarrage seulement ; fenêtres promotionnelles de Stremio supprimées ; contour de la carte d'aperçu TV

- **Statut :** À valider (sur la Fire TV)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-069
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> * On peut pas l'enlever cette fenêtre promotionnelle Hallowen au démarrage ? En plus elle vient de stremio et pas de Nathflix.
> * On perd l'encadrement blanc de selection de la série/film quand la carte grossit j'ai l'impression.
> * Quand je suis sur un épisode et que je veux revenir a l'accueil ça revient au choix du profil. Le choix du profil ne devrait apparaitre qu'une fois a l'ouverture de l'app et après ne plus se rouvrir(sauf manuellement) jusqu'à ce que l'app soit fermé et réouverte.

**Critères d'acceptation**
- [x] Les fenêtres promotionnelles de Stremio ne s'affichent plus
- [x] La carte d'aperçu TV garde le contour de la sélection
- [x] « Qui est-ce ? » ne s'affiche qu'au démarrage de l'app (ou depuis le menu du profil)
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- **Fenêtres promotionnelles** (Stremio, `event-modal`) : cachées par `EXTRAS_CSS` (`[class*="event-modal"] { display: none }`), et fermées dès qu'elles arrivent par leur propre croix (`stremioPromo`, étape de `sync()`), pour que Stremio ne les ramène pas et qu'aucune ne garde la sélection. Partout (PC compris).
- **Contour de la carte d'aperçu** : il était sur `.dx-preview-inner`, que `.dx-preview` (`overflow: hidden`) coupait entièrement. Il est sur `.dx-preview.is-open` (TV).
- **« Qui est-ce ? »** : `whoIsWatching.onVisible` (retour dans l'app après plus de 5 s) et `onRoute` (Accueil après un film) sont retirés. Il ne reste que le démarrage (`maybeShow({ start: true })`), le menu du profil, et le verrouillage après inactivité (`idleLock`, réglage `profileLock`). CLAUDE.md mis à jour.
- **Trouvés en vérifiant** :
  - avec le compte, la page d'une série ou d'un épisode s'ouvrait sans sélection : Stremio met le focus sur `#app`, que `land()` prenait pour une sélection. `keyNav.nothing()` traite `#app` comme le fond de page ;
  - au démarrage, les rangées sous « Cette semaine » ne se chargeaient qu'à l'approche de la télécommande, et la flèche bas attendait. Avec des rangées masquées parmi les premiers catalogues, l'Accueil n'avait presque rien de prêt sous le billboard. Une étape de `sync()` (TV, Accueil) appelle `homeRows.ahead()` depuis le billboard : moins de 3 rangées prêtes, et la suite est demandée.

**Vérification**
- `node --check` : OK.
- Émulateur, compte de l'utilisateur :
  - Accueil → série (« Lancer S1 É1 » sélectionné) → épisode (« Lancer l'épisode ») → Retour : Accueil, sur le titre quitté, sans « Qui est-ce ? » ;
  - Home du téléviseur pendant 8 s, puis retour dans l'app : pas de « Qui est-ce ? » ;
  - carte d'aperçu ouverte : contour blanc autour de la carte (capture) ;
  - descente 6 s après le démarrage : Lecture → Continuer à regarder → Cette semaine → rangées, sans attente.
- Fenêtre promotionnelle : elle ne s'est plus affichée sur l'émulateur, Stremio ayant retenu sa fermeture (fermée plusieurs fois par Retour pendant T-069). Le sélecteur `event-modal` vient des feuilles de style de Stremio ; la fermeture automatique n'a pas pu être vue en action.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : `c522bb0`
- Version / CHANGELOG : à la prochaine release

### T-071 — Retour depuis un épisode : la page de la série, pas l'Accueil

- **Statut :** À valider (sur la Fire TV)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Ah ouai je preferai que retour repasse par la série, c'est plus intuitif et ergonomique.

**Critères d'acceptation**
- [x] Depuis un épisode ouvert à partir de la page de sa série, Retour ramène à la série, puis à la page d'avant

**Notes techniques**
- **Cause** : en choisissant un épisode sur la page d'une série, Stremio remplace cette page dans l'historique par celle de l'épisode (longueur de l'historique inchangée), donc Retour sautait la série.
- **Correction** (`episodeBack`, clic et touche Entrée en capture) : depuis la page de la série elle-même (`#/detail/series/<id>`, sans épisode), une copie de cette page est ajoutée à l'historique (`history.pushState`) juste avant que Stremio ouvre l'épisode, et c'est elle que Stremio remplace. Si rien ne s'est ouvert au bout d'une seconde, la copie est retirée (`history.back()`). Un seul ajout par appui (OK envoie une touche et un clic). D'un épisode à l'autre, Stremio continue de remplacer.
- Partout (PC compris).

**Vérification**
- `node --check` : OK.
- Émulateur, compte de l'utilisateur : Accueil → série (OK sur une affiche) → épisode (OK sur un épisode) → Retour : la série, sélectionnée sur « Lancer S1 É1 » → Retour : l'Accueil, sur l'affiche quittée. Historique : 2 → 3 en ouvrant l'épisode (avant : 2 → 2).
- PC (Chrome, invité) : clic sur un épisode, historique 3 → 4, puis Retour du navigateur : la page de la série.
- **Incident pendant les tests** : un OK envoyé sans vérifier l'élément sélectionné a masqué « Outlander: Blood of My Blood » pour le profil de l'utilisateur (bouton « Ne plus jamais montrer »). Annulé aussitôt par le même bouton ; les listes `hidden:*` sont vides. Depuis, un OK n'est envoyé qu'après avoir vérifié la cible (`okon.mjs`, scratchpad).
- Non vérifié : la Fire TV.

**Livraison**
- Commits : `dc2b2cb`
- Version / CHANGELOG : à la prochaine release

### T-072 — Titres, résumés, genres et bandes-annonces en français (TMDB)

- **Statut :** À valider (sur l'appareil, avec la clé TMDB de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Les séries et films sont affichés en Anglais, avec la description en anglais aussi, et pareil pour les bande d'annonces. On pourrait pas avoir les descriptions et bande d'annonces en Français ou c'est que c'est lié aux catalogues ?

> Oui j'ai deja des clés TMDB et MDB que j'ai deja mis dans l'app deja.

> Si ça trouve aucune bande d'annonce en français il faudrait retomber sur la bande d'annonce en anglaise

**Critères d'acceptation**
- [x] Avec une clé TMDB et Nathflix en français : résumé du billboard, titre et genres de la carte d'aperçu, résumé de la fiche et du panneau de Découvrir en français
- [x] Bande-annonce française dans la carte d'aperçu et par le bouton « Bande-annonce » de la fiche, sinon l'anglaise
- [x] Sans clé, rien ne change et rien n'est demandé à TMDB
- [ ] Vérifié avec la vraie clé de l'utilisateur

**Notes techniques**
- **Cause** : Nathflix montre les données de l'extension de chaque rangée et complète avec Cinemeta, qui n'existe qu'en anglais. La fiche d'un titre vient de la première extension de la liste capable de la fournir (Cinemeta chez l'utilisateur).
- **`localMeta`** (avant `getInfo`) : actif si `LANG !== "en"` et une clé TMDB est réglée.
  - `get(type, imdb)` : `/find/<imdb>?external_source=imdb_id`, puis `/<movie|tv>/<id>?language=fr-FR&append_to_response=videos&include_video_language=fr` : titre, résumé (600 caractères au plus), genres, bande-annonce YouTube en français (« Trailer » avant « Teaser », officielles d'abord). Clé d'API ou jeton (`eyJ…`, en `Bearer`), comme pour les critiques. Gardé avec les autres métadonnées (`readCached`, 3 jours) ; un échec n'est pas gardé.
  - `apply()` : résumé, genres et bande-annonce par-dessus ; le titre traduit va dans `localName` (le nom d'origine reste celui envoyé à la bibliothèque par « Ma liste »).
- **Où** :
  - billboard : ses titres sont traduits avant d'être dessinés (3 s d'attente au plus) ;
  - carte d'aperçu et feuille titre du téléphone : `getInfo(type, id, true)`. Jamais pour les badges des affiches (`getInfo` sans le troisième argument) : une requête TMDB par affiche ;
  - fiche et panneau de Découvrir (`localMeta.page()`, étape de `sync()`) : le texte du `description-container` de Stremio est remplacé, comparé avant d'écrire ;
  - bouton « Bande-annonce » de la fiche (`localMeta.onTrailer`, clic et Entrée) : la bande-annonce française dans le lecteur plein écran de Nathflix (`theatre`) si elle est connue ; sinon celui de Stremio. Retour la ferme sur TV (`closePreview`).
- **Repli** : sans bande-annonce française, celle de l'extension ou de Cinemeta (anglaise) reste ; sans résumé français, l'anglais.
- Réglage « Clé TMDB (facultatif) » : libellé et description mis à jour (traduits). CLAUDE.md : `localMeta`.

**Vérification**
- `node --check` : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus.
- Chrome (PC, invité, Nathflix en français), TMDB simulé par le banc (`fr-test.mjs`, scratchpad : réponses françaises fabriquées, pas de vraie clé) :

  | | Résultat |
  |---|---|
  | Billboard | résumé français |
  | Carte d'aperçu | titre et genres français ; « Ma liste » garde le nom d'origine ; bande-annonce française |
  | Fiche, Découvrir | résumé français |
  | Bouton « Bande-annonce » | lecteur de Nathflix sur la bande-annonce française (« Trailer » choisi avant « Teaser ») |
  | Requêtes TMDB | 2 par titre (6 titres du billboard, puis les titres ouverts), toutes avec la clé, aucune en double |
  | Sans bande-annonce française | carte : celle de Cinemeta ; bouton : celui de Stremio ; résumé toujours en français |
  | Sans clé | tout comme avant, aucune requête TMDB |

- Non vérifié : la vraie clé TMDB de l'utilisateur (non lue), la TV.

**Livraison**
- Commits : `ad213ff`
- Version / CHANGELOG : à la prochaine release

### T-073 — Profils : un titre lancé sur un 2e profil apparaissait dans le 1er sur un autre appareil ; clés TMDB et MDBList synchronisées

- **Statut :** À valider (sur deux vrais appareils)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-027, T-066
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> * Il y a un bug avec les profils.
> Si je lis un film ou une série depuis un 2nd profil, il n'apparaît pas dans le premier profil. Le problème c'est que quand je vais sur un autre appareil, la il apparait sur le premier profil.
> Corrige ça, avec une synchro via mon compte.
> * Dans les réglages Nathflix synchronisé, je voudrais rajouté les clés TMDB et MDBList pour pas avoir a les retaper a chaque fois.
> Et au passage vérifie que ça se synchronise bien et que sur un nouvel appareil ça se charge bien aussi pour tout les paramètres synchro.

**Critères d'acceptation**
- [x] Un titre lancé sur le 2e profil n'apparaît pas dans le 1er profil sur un autre appareil, même resté ouvert
- [x] Les clés TMDB et MDBList suivent le compte, y compris celles déjà saisies avant
- [x] Un nouvel appareil reçoit profils, attributions des titres, titres lancés et réglages, et les garde après un redémarrage
- [ ] Vérifié sur deux vrais appareils

**Notes techniques**
- **Ce qui décide** : « Continuer à regarder » d'un profil ne montre un titre que s'il est à lui (`libraryOwners`, un titre sans propriétaire étant au premier profil) et, dès qu'un profil l'a lancé (`played:<profil>`), seulement aux profils qui l'ont lancé. Sans ces deux clés à jour, l'autre appareil le donne au premier profil.
- **Causes trouvées** (banc à trois appareils, `profiles-sync-e2e.mjs`, scratchpad : Worker en mémoire, compte de test) :
  1. un appareil resté ouvert ne relisait le compte qu'en revenant au premier plan, alors que Stremio relit la bibliothèque du compte dès que la fenêtre reprend le focus : le titre arrivait par Stremio sans son attribution ;
  2. ce qui restait à envoyer (`cloudSync.dirty`) n'existait qu'en mémoire : un rechargement de la page (langue de Stremio, mise à jour de Nathflix, page rendue après un plantage) ou l'app fermée avant l'envoi le perdait, et le titre lancé ne partait jamais ;
  3. `libraryOwners` était fusionné en bloc (le plus récent gagne tout), et le Worker ne renvoie pas à un appareil ce que son envoi remplace : deux appareils attribuant chacun un titre, l'une des attributions se perdait ;
  4. un titre lancé partait au bout de 20 s.
- **Corrections** (`cloudSync`) :
  - relecture du compte quand la fenêtre reprend le focus (au plus toutes les 15 s) et toutes les 5 min tant que Nathflix est à l'écran ;
  - `keepDirty()` : la liste à envoyer est gardée dans `localStorage` (`nathflix.syncDirty`) et reprise au démarrage ;
  - `libraryOwners` fusionné titre par titre ; avant d'envoyer une clé fusionnée élément par élément (`MERGED` : `libraryOwners`, `settings`, `stats:*`, `played:*`), `run()` lit d'abord ce que les autres appareils ont envoyé et le fusionne ;
  - `played:*` et `libraryOwners` partent au bout de 2 s ;
  - `viewerLibrary.forget` garde une liste vide au lieu d'effacer l'entrée (avec la fusion titre par titre, une entrée effacée reviendrait).
- **Clés TMDB et MDBList** : retirées de `settingsSync.LOCAL`. `settingsSync.seed()` envoie une fois celles qu'un appareil avait déjà et que le compte n'a pas (seuls les réglages changés à la main partaient). Description de la synchro (traduite), README du Worker et CLAUDE.md mis à jour. Le Worker n'a pas changé (la clé `settings` existait).

**Vérification**
- `node --check` : OK. Traductions : `fr-missing.mjs` ne signale que les deux faux positifs connus. `tools/profile-sync/test.mjs` : OK. Banc des réglages de T-066 (`settings-sync-e2e.mjs`) : 9/9.
- Banc à trois appareils (A : clés saisies avant, 2e profil qui lance un titre ; B : ouvert tout du long, jamais rechargé ; C : appareil neuf) :

  | Vérification | Avant (plugin publié) | Après |
  |---|---|---|
  | B reçoit les clés TMDB et MDBList | ✗ | ✓ |
  | A envoie le titre lancé | ✗ (perdu au rechargement de la page) | ✓ en 2 s |
  | B, resté ouvert, apprend que le titre est au 2e profil quand sa fenêtre reprend le focus | ✗ | ✓ |
  | B : le titre n'est pas dans « Continuer à regarder » du 1er profil | ✗ (le bug) | ✓ |
  | Deux appareils attribuent chacun un titre en même temps : les deux restent | ✗ | ✓ |
  | C (neuf) : profils, attributions, titres lancés, clés, accent ; mode léger de l'appareil gardé | ✗ en partie | ✓ |
  | C : réglages encore là après redémarrage | ✗ | ✓ |

  Après : 21 vérifications sur 21 ; avant : 10 échecs.
- Non vérifié : deux vrais appareils avec le compte de l'utilisateur.

**Livraison**
- Commits : `f9db738`
- Version / CHANGELOG : à la prochaine release

### T-074 — Release 0.6.1 (télécommande TV, carte d'aperçu, titres en français, synchro des profils)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-067 à T-073
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Push et tag une nouvelle release quand ça sera terminé

**Critères d'acceptation**
- [x] `@version 0.6.1`, section `## 0.6.1` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.6.1`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 601, plugin embarqué en 0.6.1

**Notes techniques**
- **Numéro** : 0.6.1, car la 0.7.0 reste réservée à la phase 2 du chantier performance (T-062, nouvelle APK). Depuis la 0.6.0, seul le plugin a changé (`tv/` et `app/` identiques, hors numéro de version).
- **Contenu** : T-067 (« Organiser l'Accueil »), T-068 et T-069 (sélection et défilement TV, carte d'aperçu et bande-annonce), T-070 (« Qui est-ce ? » au démarrage seulement, promotions de Stremio), T-071 (Retour depuis un épisode), T-072 (titres en français par TMDB), T-073 (synchro des profils, clés TMDB et MDBList).
- **Worker de synchro** : inchangé depuis la 0.6.0 (la clé `settings` porte aussi les clés TMDB et MDBList), rien à redéployer.

**Vérification**
- `node --check` : OK. Bancs de T-073 (21/21) et de T-066 (9/9), `tools/profile-sync/test.mjs` : OK.
- CI verte sur `6dddc16` (Cross-platform, mpv Windows, Android test build), puis tag `v0.6.1` : run Release 41322866523, tous les jobs verts (publish, android, desktop ×5).
- Release : les 11 fichiers (installeur et portable Windows, `.blockmap`, 2 dmg, 2 AppImage, APK, 3 `latest*.yml`).
- APK : signée `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b` (la même clé que la 0.6.0) ; `com.lavagnou.nathflix`, versionCode 601, versionName 0.6.1 ; plugin embarqué en 0.6.1.

**Livraison**
- Commits : `6dddc16` (`0.6.1: …`)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.6.1

### T-075 — Résumé resté en anglais sur la fiche d'un titre sans note IMDb

- **Statut :** À valider (sur l'appareil, avec la clé TMDB de l'utilisateur)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-072
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Parfois certaines description des séries sont bien en français mais dans la page details ça revient en anglais. Ca ne le fait pas sur tout.

(captures : VisionQuest, résumé français sur le billboard, anglais sur sa fiche ; la fiche montre les notes RT, Metacritic et Letterboxd, pas de note IMDb)

**Critères d'acceptation**
- [x] Le résumé de la fiche et du panneau de Découvrir passe en français même sans note IMDb sur la page

**Notes techniques**
- **Cause** : `localMeta.page()` ne trouvait l'identifiant IMDb du titre que dans le lien de sa note IMDb. Une fiche sans note IMDb (comme VisionQuest) n'en a pas, et son résumé restait en anglais ; le billboard, lui, connaît l'identifiant.
- **Correction** : `localMeta.titleOf(box)` prend le type et l'identifiant dans l'adresse de la page (`#/detail/<type>/<id>`) ou dans le lien vers la fiche (panneau de Découvrir), puis dans le lien IMDb en dernier recours. Les identifiants TMDB d'une extension (`tmdb:<n>`) sont aussi pris, sans recherche par IMDb (`fetch` lit directement le titre). Le bouton « Bande-annonce » passe par le même `titleOf`.

**Vérification**
- `node --check` : OK.
- Chrome, TMDB simulé (`fr-test-noimdb.mjs`, scratchpad : tous les liens IMDb retirés de la page, comme sur une fiche sans note IMDb) :

  | | 0.6.1 | Ce commit |
  |---|---|---|
  | Fiche d'un film | résumé anglais | résumé français |
  | Panneau de Découvrir | résumé anglais | résumé français |
  | Bouton « Bande-annonce » | bande-annonce française | bande-annonce française |

- Non vérifié : la vraie clé de l'utilisateur, un titre à identifiant `tmdb:` (aucun sur le compte invité).

**Livraison**
- Commits : `e81da46`
- Version / CHANGELOG : à la prochaine release

### T-076 — Lecteur TV comme celui de Netflix (prototype validé)

- **Statut :** À valider (sur la TV de l'utilisateur, avec la build Dev)
- **Priorité :** P1
- **Cible :** TV (APK), plugin
- **Dépend de :** —
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> L'interface du player sur TV est vraiment pas super niveau design, ça correspond pas du tout a celui de Netflix.

(photo du lecteur Netflix sur sa TV ; il ne veut pas des choix de langue de Netflix à cet endroit, mais les autres boutons du lecteur Nathflix). Prototype Claude Design « Lecteur TV Nathflix » validé : « Top, comme ça j'aime bien oui, mais enlève le paramètre de vitesse ».

**Critères d'acceptation**
- [x] En haut à gauche, Retour et « Revoir depuis le début » : rond blanc sur l'icône sélectionnée, son nom en capitales dessous
- [x] En haut à droite, la série en gras et l'épisode (« S1 : É3 « Titre » »)
- [x] En bas, gros bouton lecture, temps écoulé, barre rouge, durée ; puis Audio et sous-titres · Épisodes · Épisode suivant (pas de Vitesse)
- [x] Télécommande : sur lecture, gauche/droite = ±10 s, haut = icônes (ou « Passer l'intro »), bas = la rangée ; commandes masquées : comme avant (±10 s, OK = pause, haut/bas affichent les commandes)
- [x] Audio et sous-titres en deux colonnes, appliqué dès le choix ; Épisodes en cartes avec le résumé de l'épisode sélectionné ; Retour ferme le panneau et revient au bouton
- [x] Les sous-titres et « Passer l'intro » remontent au-dessus des commandes
- [x] « Épisode suivant » à la place de « Passer » pendant le générique et les 30 dernières secondes, et épisode suivant automatique, aussi sur TV (réglage `autoNext`)

**Notes techniques**
- `TvPlayerControls.kt` (nouveau) + `res/layout/player_tv_controls.xml` : dimensions du prototype divisées par 2 en dp (marges 56dp, icônes 36dp, lecture 56dp, pastilles 34dp/13sp, titre 22sp). Fonds au focus (`tc_*_bg`), couleur `color/tc_on_focus` (noir sur blanc au focus), icônes à trait `ic_tv_*`, barre `TvTimeBar` (dessinée, plus épaisse quand lecture a le focus). Masquage après 5 s pendant la lecture ; en pause, elles restent.
- `PlayerActivity` : sur TV, `useController = false` et `setUpTv()` (plus de `title_bar` ni de commandes Media3) ; `tvKey()` gère la télécommande quand les commandes sont masquées ; `skipShown()` / `hideSkip()` placent le focus ; « Passer l'intro » au style du prototype (`tc_skip_bg`, `ic_tv_next`, dimens TV). `episodesOf()` et `episodeLine()` (chaînes `player_episode_line*`).
- `PlayerTracks.kt` (nouveau) : `select`, `name`, `textOff`, partagés avec `MobilePlayerControls`.
- `nathflix-tv.js` : `mobilePlayback()` pour tous les appareils (épisodes, `next`), plus `season` / `episodeNumber` / `episodeTitle` ; `tv.tvNext = true`.
- Plugin : `autoNext` porte `tvNext: true` et s'affiche sur TV seulement si la couche TV a `tvNext` (une vieille APK n'a pas l'épisode suivant automatique).
- Nouvelles chaînes FR/EN : `player_restart`, `player_episode_line`, `player_episode_line_short`, `player_episode_card`, `player_back_to_video`.

**Vérification**
- `./gradlew assembleDebug` (JDK 17) : OK. `node --check` plugin et `nathflix-tv.js` : OK.
- Émulateur Fire_TV (1920×1080, 320 dpi), build Dev, lecteur ouvert par le bridge sur une vidéo de test publique (HLS Big Buck Bunny de test-streams.mux.dev, épisodes fictifs, sous-titres de test) : jamais un titre du compte ; la page, restée sur « Qui est-ce ? » sans validation, ignore le lecteur (pas de `handoff`) ; app arrêtée après.
  - commandes, focus sur lecture / icônes (nom dessous) / pastilles, « Passer l'intro » remonté puis atteint par haut : conformes au prototype ;
  - Audio et sous-titres : « English » appliqué tout de suite, coche déplacée, Retour revient sur « Audio et sous-titres » ;
  - sous-titres au-dessus des commandes, puis redescendus quand elles partent ;
  - « Revoir depuis le début » : retour à 0:00 ;
  - Épisodes : épisode en cours sélectionné avec sa progression, le résumé suit la carte ;
  - « Épisode suivant » sur TV dans les 30 dernières secondes ; fin de la vidéo : retour à la page.
  - Corrigé pendant le test (signalé aussi par l'utilisateur) : le bouton lecture agrandi au focus était coupé en haut (`clipChildren` de `tc_controls`).
- Non vérifié : la vraie TV (Fire TV), un vrai épisode et son passage à l'épisode suivant, les vignettes d'épisodes (celles de test sont refusées par leur serveur).

**Livraison**
- Commits : `ca8c08f` (CI verte : build Android avec R8, mpv, multiplateforme ; build Dev publiée dans la pré-release `dev`)
- Version / CHANGELOG : nouvelle APK, release 0.7.0 à proposer

### T-077 — Dégradé de « Qui est-ce ? » dans la couleur d'accent

- **Statut :** À valider
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> J'aimerais que la couleur rouge du dégradé circulaire du choix de profil soit lié a la couleur choisi dans les réglages de Nathflix (emeraude en ce moment)

**Critères d'acceptation**
- [x] Le dégradé circulaire de « Qui est-ce ? » (et de ses pages) prend la couleur d'accent
- [x] Avec l'accent rouge (par défaut), il reste exactement celui d'avant

**Notes techniques**
- `applyAccent()` pose `--dx-who-glow` (le dégradé) et `--dx-who-bar` (la barre floutée des pages d'un profil), calculés par `whoGlow()` / `whoGlowMix()` : l'accent mêlé au noir de la page (#0b0b0d), 42 % au centre jusqu'à 1 % au bord. Calcul en JS plutôt qu'en `color-mix()`, que les WebView TV anciennes (≥ 100) n'ont pas.
- Accent rouge : les variables sont retirées, et le CSS garde le dégradé dessiné à la main (`var(--dx-who-glow, …)`).

**Vérification**
- `node --check` : OK.
- Chrome, compte invité, « Qui est-ce ? » ouvert par l'avatar (`who-accent.mjs`, scratchpad) : Émeraude, dégradé vert (`rgb(19, 84, 43)` au centre) ; rouge, dégradé identique à avant (`rgb(100, 19, 27)` au centre).

**Livraison**
- Commits : dans la release 0.7.0 (T-078)
- Version / CHANGELOG : 0.7.0

### T-078 — Release 0.7.0 (lecteur TV comme Netflix, dégradé du choix de profil)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-075 à T-077
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Après ça tu peux publier une nouvelle release

**Critères d'acceptation**
- [x] `@version 0.7.0`, section `## 0.7.0` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.7.0`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 700, plugin embarqué en 0.7.0

**Notes techniques**
- **Numéro** : 0.7.0, car l'APK change (nouveau lecteur TV). La réservation de la phase 2 du chantier performance (T-062) passe à 0.8.0.
- **Contenu** : T-075 (résumé français sans note IMDb), T-076 (lecteur TV), T-077 (dégradé de « Qui est-ce ? »).
- **Worker de synchro** : inchangé, rien à redéployer.

**Vérification**
- `node --check` plugin et `nathflix-tv.js`, traductions (`fr-missing.mjs` : les 2 faux positifs connus) : OK.
- CI verte sur `b2cd1ce` (Cross-platform, mpv Windows, Android test build avec R8), puis tag `v0.7.0` : run Release 41329833883, tous les jobs verts (publish, android, desktop ×5).
- Release : les 11 fichiers (installeur et portable Windows, `.blockmap`, 2 dmg, 2 AppImage, APK, 3 `latest*.yml`).
- APK : signée `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b` (la même clé que la 0.6.x) ; `com.lavagnou.nathflix`, versionCode 700, versionName 0.7.0 ; plugin embarqué en 0.7.0 ; `TvTimeBar` présent dans le dex (gardé par R8 pour le layout).

**Livraison**
- Commits : `b2cd1ce` (`0.7.0: …`)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.7.0

### T-052 — Plugin chargé par adresse sur Android (cache de compilation)

- **Statut :** Abandonné (aucune des deux variantes ne raccourcit le démarrage)
- **Priorité :** P1
- **Cible :** tv
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Continue la suite du chantier

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-052)

**Critères d'acceptation**
- [x] Mesure de référence du temps de chargement du plugin
- [x] Variante A (`shouldInterceptRequest`) mesurée
- [x] Variante B (petit serveur local, cache HTTP) mesurée
- [x] Garder la variante qui gagne, sinon abandonner et le noter

**Notes techniques**
- **Mesure gardée** : `nathflix-tv.js` note la durée de compilation et d'exécution du plugin (`window.__nathflixTvPluginMs`) ; la mesure à l'écran l'affiche (« Plugin chargé en N ms ») et `window.__nathflix.startup()` donne le temps de démarrage. Servira à T-053.
- **Mesure sur l'émulateur Fire_TV** (Android TV 14 x86, 1920×1080), lancements à chaud (`launchperf.mjs`, scratchpad : arrêt de l'app, lancement, lecture des deux temps par le débogueur de la WebView, sans rien toucher au compte), à la place des photos de la Fire TV. L'émulateur est bien plus rapide qu'une Fire TV : les chiffres servent à comparer, pas à prédire.

  | | Plugin chargé (ms, lancements 2 à 6) | Démarrage (ms, médiane) |
  |---|---|---|
  | Référence : plugin écrit dans la page | 80–107 | 2410 |
  | A : même adresse que Stremio, servie par `shouldInterceptRequest` | 124–181 | 2295 |
  | B : `127.0.0.1:47811`, NanoHTTPD, cache HTTP (vérifié : 0 octet transféré au relancement) | 145–253 | 2517 |

- **Pourquoi aucun gain** : l'analyse du plugin (1,1 Mo) ne pèse que 15 à 40 ms sur l'émulateur (`new Function(source)`) ; le reste est son exécution. Chargé par adresse, le script s'exécute de façon asynchrone et attend son tour sur le fil principal, ce qui annule le gain du cache. Le démarrage ne bouge pas (écarts dans le bruit des mesures).
- Le code des deux variantes est gardé dans le scratchpad (`perf2/t052-variants-a-b.patch`) si un essai sur la vraie Fire TV paraît utile un jour.

**Vérification**
- `node --check` plugin et `nathflix-tv.js` ; traductions (`fr-missing.mjs` : les 2 faux positifs connus) ; Kotlin inchangé.
- La ligne « Plugin chargé en N ms » : lue sur l'émulateur par `__nathflixTvPluginMs`.

**Livraison**
- Commits : à venir (mesure seulement ; pas de changement de chargement)
- Version / CHANGELOG : à la prochaine release

### T-053 — Plugin minifié pour l'APK et l'app

- **Statut :** Abandonné (aucun gain mesurable au chargement)
- **Priorité :** P2
- **Cible :** tv, app, tools
- **Dépend de :** T-052
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Continue la suite du chantier

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-053)

**Critères d'acceptation**
- [x] Mesurer le gain avant de toucher à Gradle, à l'app et à la CI
- [x] Garder seulement si le chargement du plugin raccourcit

**Notes techniques**
- **Essai** (`perf2/minify-try.mjs`, scratchpad) : le plugin TV (intro et shaders retirés comme `Updates.forTv`), numéroté 0.7.99, tel quel puis minifié par terser 5 (`compress.passes: 2`, `mangle`), poussé dans `files/` de la build Dev sur l'émulateur Fire_TV ; 6 lancements à chaud chacun, alternés (`launchperf.mjs`).
- Taille : 1100 Ko → 764 Ko (−30 % seulement : le thème intégré, les images du logo et le dictionnaire français sont des chaînes que la minification ne réduit pas).

  | Série | Tel quel : plugin (ms) / démarrage (ms) | Minifié |
  |---|---|---|
  | 1 | 104 / 2687 | 102 / 2496 |
  | 2 | 103 / 2693 | 186 / 4375 (la machine ralentit pendant cette mesure) |
  | 3 (machine encore ralentie) | 176 / 4528 | 190 / 4359 |

- **Pourquoi** : comme vu dans T-052, l'analyse du script ne pèse que 15 à 40 ms sur l'émulateur ; le temps du plugin est son exécution, que la minification ne change pas. Les écarts sont dans le bruit. Pas de minification : le plugin reste lisible partout, et pas de nouvelle étape de build ni de risque (noms de fonctions, regex de `Updates.forTv`).

**Vérification**
- `node --check` de la sortie minifiée : OK ; elle a tourné sur l'émulateur (le temps de démarrage est relevé par le plugin lui-même).
- Plugin d'essai retiré de la build Dev après la mesure.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-055 — `keyNav` sans remesure

- **Statut :** Abandonné (le profil montre que les mesures de `keyNav` ne coûtent presque rien ; les deux pistes trouvées n'ont pas de gain mesurable)
- **Priorité :** P1
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Continue la suite du chantier

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-055)

**Critères d'acceptation**
- [x] Mesure avant (banc TV, 5 passes)
- [x] Trouver où va le temps d'une touche avant de changer `keyNav`
- [x] Garder seulement ce qui gagne

**Notes techniques**
- **Outils ajoutés (gardés)** :
  - compteur `keyNav` dans `perf` (`keyNav.onKey` chronomètre le gestionnaire de la télécommande) ;
  - banc : `keys.handlerMs` (temps du gestionnaire par touche) et `--profile` (profil CPU des 20 touches : les 15 fonctions du plugin les plus coûteuses, en temps propre et total ; documenté dans `tools/README.md`).
- **Où va le temps** (banc TV, processeur ralenti ×6) : une touche dure ~32 à 48 ms (Event Timing), dont ~10 à 17 ms dans le gestionnaire de Nathflix (selon les passes). Le profil montre que ce temps est surtout `go()` → `link.focus()` et ce qu'il déclenche (`onFocus`, ouverture de la carte d'aperçu, défilement), pas les mesures de `keyNav` (`inView`, `nearest`, `rows`, `getBoundingClientRect` n'apparaissent pas parmi les 15 plus coûteuses). Les étapes prévues (cache des mesures par image, `IntersectionObserver` sur les rangées, cache de `rows()`) viseraient donc à côté : non faites.
- **Pistes essayées puis retirées** :
  - `endsAtText` (« Se termine à » de la carte d'aperçu) en tête du profil (76 ms) : c'était le premier appel de `toLocaleTimeString`, qui charge les données de dates (~55 ms, ×6), payé de toute façon. Ensuite, 0,4 ms par appel contre ~0 avec un formateur gardé (`perf2/intl-cost.mjs`, scratchpad) : gain négligeable ;
  - flèches et traits de page des rangées mesurés à chaque image pendant le glissement d'une rangée sur TV, où ils ne se voient jamais (seulement au survol de la souris) : limité à la rangée survolée. Survol sur PC identique (`arrows-check.mjs`), mais aucune différence au banc.
  - Banc A/B dans le même navigateur (`--vs`, 6 passes alternées), avant / avec les deux pistes : médiane 32 / 32 ms, p90 52 / 52, pire 68 / 60, gestionnaire 9,7 / 9,6 ms. Retirées, selon la règle du chantier.

**Vérification**
- `node --check` : OK. Banc TV sans erreur `[Nathflix]` à chaque passe.

**Livraison**
- Commits : à venir (compteur et outils de mesure seulement)

### T-056 — Desktop : iframe de bande-annonce unique, observateurs du preload

- **Statut :** Abandonné (rien à gagner, et un risque pour l'iframe)
- **Priorité :** P2
- **Cible :** plugin, app
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Continue la suite du chantier

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-056)

**Critères d'acceptation**
- [x] Mesurer ce que coûtent les observateurs de la barre de titre avant de les changer
- [x] Ne rien changer au comportement

**Notes techniques**
- **Observateurs du preload** (`addTitleBar` dans `app/src/preload/index.ts`, `followPlayer` dans `ui/titleBar.ts`, tous deux `childList` + `subtree`) : recopiés tels quels et chronométrés dans Chromium sur Stremio + Nathflix (PC, processeur ralenti ×6 ; `perf2/observers-cost.mjs`, scratchpad, deux passes) :
  - au repos sur l'Accueil, 30 s : **0 appel** ;
  - en naviguant (fiche d'un film, retour, Découvrir, retour, défilement de l'Accueil) : environ 200 changements observés, 2 à 9 ms de rappels au total. La page s'est rechargée une fois pendant ce parcours (horloge de la page remise à zéro) : mesure partielle, mais même plusieurs fois plus, c'est négligeable.
  - Leurs rappels sortent tout de suite (`querySelector` d'un élément précis, puis retour) : rien à gagner à restreindre ce qu'ils observent, et un risque de manquer la barre de titre ou le lecteur.
- **Iframe de bande-annonce réutilisée** : changer le `src` d'une iframe gardée ajoute une entrée à l'historique de la fenêtre ; Retour reculerait d'abord dans les bandes-annonces de l'iframe au lieu de la page. Le gain se limite à la création d'un élément, quand le chargement de YouTube domine. Non fait.

**Vérification**
- Mesure seulement ; aucun code changé.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-057 — Desktop : fenêtre transparente seulement pendant mpv (étude)

- **Statut :** Abandonné (aucun écart entre fenêtre transparente et opaque)
- **Priorité :** P2
- **Cible :** app
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Oui tu peux lancer l'app

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-057 : étude, puis proposition seulement si l'écart est net, 2× ou plus au repos)

**Critères d'acceptation**
- [x] Mesurer le GPU et le processeur au repos, fenêtre transparente puis opaque
- [x] Proposer une voie seulement si l'écart est net

**Notes techniques**
- **Montage** : l'app construite depuis les sources (`npm ci`, `npm run dist`), lancée avec son propre dossier de données (`--user-data-dir` dans le scratchpad) pour ne toucher ni aux données ni au compte de l'app installée ; mode invité ; mpv coupé (`NATHFLIX_MPV=0`) ; la transparence réglée par le fichier `transparency` du dossier de données (comme quand mpv est actif). Accueil chargé (80 à 281 titres), 25 s de repos, puis 30 s de mesure : compteurs Windows « GPU Engine » (moteur 3D) des processus de l'app, et temps processeur (`perf2/t057-gpu.ps1`, scratchpad).
- **Résultats** (alternés, deux fois chacun) :

  | Fenêtre | GPU 3D de l'app (moyenne / pic) | Processeur de l'app |
  |---|---|---|
  | Opaque | 0,0 % / 0,1 % | 1,4 % d'un cœur |
  | Transparente | 0,0 % / 0,0 % | 1,9 % |
  | Opaque | 0,0 % / 0,1 % | 1,8 % |
  | Transparente | 0,3 % / 2,5 % | 3,6 % |

- Au repos, aucune des deux ne sollicite le GPU, et l'écart de processeur est dans le bruit : rien à proposer.

**Vérification**
- Mesure seulement ; aucun code changé. Processus de l'app arrêtés après chaque mesure.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-079 — Mesure à l'écran : la dernière minute, et la part de Nathflix par touche

- **Statut :** À valider (photo de la mesure sur la Fire TV)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> J'ai joints des photos de tests de performances sur ma FireTV 4K

**Critères d'acceptation**
- [x] Les lectures du cœur, la boucle principale et les enregistrements « par minute » portent sur la dernière minute
- [x] Une ligne donne le temps de Nathflix dans chaque touche de la télécommande

**Notes techniques**
- **Ce que montraient les photos** (Fire TV 4K, 0.7.0, mode léger) : « ctx 100, board 26 » lectures par minute, « boucle principale : 19 fois par minute, 1457 ms ». Ces valeurs étaient des moyennes **depuis le début de la page** : le démarrage, où Stremio lit son contexte des centaines de fois, les gonflait longtemps après.
- **Qui lit le cœur** : copie du plugin qui note l'appelant de chaque lecture (`perf2/make_callers.py`, scratchpad). Sur l'émulateur, compte de l'utilisateur (« Qui est-ce ? » validé sur Nathan, lecture seule) : au démarrage, 149 lectures de `ctx`, dont 146 par Stremio lui-même (`main.js`) et 3 par Nathflix ; ensuite, au repos, **aucune** lecture en 60 s, et quelques-unes en naviguant. En invité dans Chrome : aucune non plus au repos. Rien à réduire côté Nathflix.
- **Correction de la mesure** : `perfReadout.counters()` garde une copie des compteurs chaque seconde (`history`) et calcule les taux sur la dernière minute ; dans les 10 premières secondes de la mesure, la moyenne depuis le début reste la seule disponible. Nouvelle ligne « Part de Nathflix par touche : N ms » (compteur `keyNav` de T-055), traduite.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Chrome, TV, invité, mesure activée (`perf2/readout-test.mjs`, scratchpad) : à 15 s, « ctx 564 » (moyenne depuis le début, la rafale du démarrage) ; à 75 s, après 6 touches, « library 1 », « boucle principale : 0 fois par minute », « Part de Nathflix par touche : 1 ms ».
- Non vérifié : la Fire TV (prochaine photo).

**Livraison**
- Commits : `195bbc4` (la fiche a suivi dans un commit séparé : le script de PLAN.md avait échoué)
- Version / CHANGELOG : à la prochaine release

### T-080 — TV : OK maintenu ouvre le menu d'un titre ; 3 points masqués ; Retour sur la page d'un titre

- **Statut :** À valider (test sur la Fire TV ; Retour non reproduit)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-010
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> * Quand je laisse appuyer la touche ok/entrée de ma télécommande, ça fait le bruit du clic en boucle et fait freeze pendant plusieurs secondes l'app, ça devrait ouvrir le menu contextuel plutôt.
> * Sur la page d'une série, d'un épisode ou d'un film, le bouton retour de la télécommande ne fonctionne pas
> * Les 3 petits points qui ouvrent le menu contextuel sont encore visibles sur les apps.

**Critères d'acceptation**
- [x] OK maintenu sur un titre qui a un menu (Continuer de regarder) ouvre ce menu, sans ouvrir le titre ; la télécommande se déplace dans le menu, OK choisit, Retour le ferme et rend la sélection au titre
- [x] OK maintenu ailleurs n'appuie qu'une fois (plus de clics ni de sons en boucle)
- [x] Plus de 3 points sur les cartes (TV, téléphone, PC) ; appui long et clic droit ouvrent toujours le menu
- [ ] Retour sur la page d'un titre : cause non trouvée (non reproduit)
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- **Cause du clic en boucle et du gel** : la télécommande répète OK tant qu'il est enfoncé, et chaque répétition cliquait ce qui était sélectionné. Le « clic » est le son d'interface de Nathflix (`onSoundClick`, « select » sur une carte), joué à chaque clic. Le titre s'ouvrait en boucle ; sur sa page, les répétitions suivantes appuyaient sur « Lancer S1 É1 » / « Lecture » (`.dx-play-ep`, sélectionné à l'arrivée). Dans Chrome (TV, invité), Entrée maintenue sur un film : avant, le bouton passait à « Finding streams… » (un film allait démarrer) ; après, il reste intact.
- **WebView Android** : elle ne marque pas les répétitions (`e.repeat` reste faux), et après un appui long (`input keyevent --longpress` sur l'émulateur) elle n'envoie pas toujours le relâchement. `okHold` (après `cardPress`) tient donc son propre état : une touche OK qui arrive alors qu'OK est encore enfoncé, moins d'1 s après la précédente (`e.timeStamp`, l'heure d'envoi : une page occupée voit le vrai rythme), est une répétition, avalée (`preventDefault` + `stopImmediatePropagation`, écouteur `keydown` sur `window` en capture, avant ceux de Stremio et de `keyNav`).
- **Menu** : sur une carte avec menu (`cardPress.menuOf`), OK n'ouvre plus le titre à l'appui mais au relâchement. Maintenu, le menu s'ouvre à la première répétition (comme Android compte un appui long), ou après 600 ms : `menu.click()` sur le bouton caché de Stremio, son « menu », puis la sélection sur le premier choix (`[role="menu"] [role="menuitem"]`). Haut et bas passent d'un choix à l'autre (Stremio), OK choisit.
- **Retour dans le menu** : `cardPress.closeMenu()`, appelé par `closePreview()` (donc par le Retour de la couche TV). Le menu de Stremio se ferme sur Échap envoyé à l'intérieur (un clic sur son bouton ne le fermait pas) ; puis la sélection revient au lien du titre. Le sélecteur de menus de `tv.back` (`menu-container` + `open`) ne trouvait pas ce menu (`menu-container-…`, `role="menu"`) : sans cela, Retour serait allé à l'écran précédent.
- `okHold` tourne partout sauf sur téléphone et tablette (`IN_MOBILE`) : sur PC, Entrée maintenue au clavier ouvre aussi le menu, et une carte s'ouvre au relâchement.
- **3 points** : règle du thème (`menu-label-container` dans les cartes) en `visibility: hidden` et `pointer-events: none`. Le bouton garde sa place dans le coin bas droit : Stremio ouvre son menu à côté (le menu est dans un calque à part, `layer-…`, donc visible). Caché ainsi, il ne peut plus recevoir la sélection. La barre de progression ne s'arrête plus avant lui. Seules les cartes de la bibliothèque (Continuer de regarder) ont ce menu chez Stremio.
- **Retour sur la page d'un titre** : non reproduit sur l'émulateur Fire TV (compte de l'utilisateur, « Qui est-ce ? » validé sur Nathan), dans tous ces cas : page ouverte par OK sur une carte (tout de suite, après l'ouverture de la carte d'aperçu, après sa bande-annonce), par « Plus d'infos » du billboard, par l'adresse ; série, épisode, film ; sélection déplacée sur la page, jusqu'au champ « Rechercher des vidéos » ; épisode choisi puis Retour deux fois ; après un appui long ; processeur de la WebView bridé ×6. À chaque fois `tv.back()` fait `history.back()` et la page précédente revient. Rien de caché ne retient Retour (`modal-container`, menus, carte d'aperçu, « Qui est-ce ? »). Piste : les clics en boucle de l'appui long empilaient des pages (titre, puis épisode par « Lancer ») ; à revoir avec l'utilisateur, avec les étapes exactes.

**Vérification**
- `node --check` (plugin, `nathflix-tv.js`), `node tools/bundle-theme.mjs`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Émulateur Fire TV, compte de l'utilisateur, plugin poussé en 0.7.99, avec un garde-fou qui bloque et note tout clic sur les cartes de Continuer de regarder (`emu/holdmenu.mjs`, scratchpad) : appui long → menu ouvert, « Détails » sélectionné, aucun clic sur la carte ; droite sans effet ; Retour → menu fermé, sélection sur la carte ; OK court → un seul clic (bloqué) ; appui long puis OK sur « Détails » → page de l'épisode, Retour → Accueil. Capture : plus de 3 points, menu sous la carte avec « Détails » entouré.
- Émulateur, titre sans menu, appui long : la page s'ouvre une fois, rien n'y est pressé ensuite.
- Chrome (`perf2/okhold-pc.mjs`, scratchpad), invité, PC et TV : Entrée sur une carte ouvre le titre (un `pushState`) ; Entrée maintenue (16 répétitions) l'ouvre une seule fois.
- `tools/perf-bench.mjs --tv` et `--pc` (1 run) : parcours de contrôle sans erreur.
- Non vérifié : la Fire TV ; le téléphone (appui long inchangé, le bouton caché reçoit le même `click()` que sur TV).

**Livraison**
- Commits : à venir
- Version / CHANGELOG : à la prochaine release

### T-081 — Page d'un titre : genres et acteurs plus sélectionnables en mode léger ; bouton Partager retiré

- **Statut :** À valider (test sur la Fire TV)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-068
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Sur la page de détails des films/séries/épisodes on peut selectionner pleins d'éléments inutiles sur TV, j'ai fait une image que je te joint ou j'ai encadré en vert ce qui doit être sélectionnable et encadré en rouge ce qui ne doit pas l'être car c'est juste des éléments visuels.

(Image : en rouge, la durée, l'année, les notes, les genres et la distribution ; en vert, les boutons sous le résumé et la colonne des épisodes, avec les saisons et la recherche.)

> Enlève le bouton "Partager" au passage

Puis, le lendemain (2026-11-02) :

> J'avais dis que je voulais pas que les boutons des notes et genres soient cliquable, mais enfaite je veux garder cette feature mais juste la retirer pour les appareils en mode léger si ça peut optimiser.

**Critères d'acceptation**
- [x] ~~Sur TV, rien dans les détails d'un titre (notes, genres, distribution) ne prend la sélection~~ (remplacé le 2026-11-02)
- [x] En mode léger seulement (TV, téléphone, PC), rien dans les détails d'un titre ne prend la sélection de la télécommande ou du clavier ; sinon genres et acteurs restent sélectionnables
- [x] Les boutons, les saisons, la recherche, les épisodes et les sources restent sélectionnables
- [x] Plus de bouton « Partager » sur la page d'un titre (partout : TV, téléphone, PC ; confirmé par l'utilisateur le 2026-11-02 : « Oui retiré partout c'est bon »)
- [ ] Vérifié sur la Fire TV

**Notes techniques**
- Seuls les genres et les acteurs étaient sélectionnables dans la zone rouge : ce sont les liens de Stremio (`meta-links`, vers Découvrir et la Recherche) dans `meta-info-container` de `meta-preview`. Les notes (`.dx-score`) ne le sont pas.
- `keyNav.SKIP` réunit ce que la télécommande ne sélectionne jamais : `.dx-keyshield, .dx-arrow` comme avant, plus, sur TV seulement, tout ce qui est dans `#app [class*="meta-preview"] [class*="meta-info-container"]`. Utilisé par `inView()` (ce qui est à l'écran) et `beyond()` (plus loin, hors de l'écran). Sur PC, à la souris ou au clavier, rien ne change.
- Le même `meta-preview` sert au panneau de droite de Découvrir : sur TV, ses genres et acteurs ne sont plus sélectionnables non plus.
- Depuis « Lancer », Haut mène maintenant à la flèche de retour en haut à gauche (avant : aux acteurs).
- **2026-11-02, mode léger seulement** (demande de l'utilisateur) : `keyNav.SKIP_IN` (`#app [class*="meta-info-container"]`) ne vaut plus que quand `light()` est vrai. Le gain de performance est négligeable (quelques éléments de moins à mesurer par touche) : c'est surtout un choix d'interface.
- **Régression corrigée (2026-11-02)** : la première version ajoutait un sélecteur coûteux (`[class*=…] [class*=…]`) à `SKIP`, essayé par `closest()` sur chaque élément sélectionnable de la page et chacun de ses parents, à chaque touche (vu dans un profil : ~50 ms sur 10 touches, processeur ×6, dans `inView`). Désormais `skipping()` cherche les zones à ignorer une fois par touche, `skips(n, boxes)` les teste par `contains()`, et `inView()` ne le fait que pour ce qui est à l'écran (après la mesure de position).
- **Partager** : `hideButton.noShare(row)`, appelé par `hideButton.ensure()` sur la page d'un titre, avant même de savoir quel titre c'est. Le bouton de Stremio n'a pas de classe propre et son nom suit la langue de Stremio : il est reconnu par son icône (chemin SVG qui commence par `M396 459.9`), ou par son nom « Share » / « Partager ». Masqué en `style.display = "none"` (l'observateur principal ne suit pas les attributs, donc pas de boucle). Partout, pas seulement sur TV : la demande ne faisait pas de différence.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Émulateur Fire TV, compte de l'utilisateur, plugin poussé en 0.7.99 (`emu/walk.mjs`, scratchpad : flèches puis ce qui est sélectionné, avec une alerte si c'est dans les détails) : page d'une série (VisionQuest), d'un épisode et d'un film (6 liens dans les détails à chaque fois) ; 19 à 17 flèches dans tous les sens, jamais dans les détails. Boutons, épisodes et sources (à droite de la page d'un épisode) atteints. Aucun OK pressé sur une source.
- `tools/perf-bench.mjs --tv` (1 run) : parcours de contrôle sans erreur.
- 2026-11-02, émulateur, compte de l'utilisateur : mode léger actif (Auto, émulateur modeste : 2 cœurs, 2 Go), 11 flèches sur la page d'une série, jamais dans les détails ; mode léger coupé (réglage local, remis sur Auto après), Haut depuis « Lancer » mène aux acteurs puis aux genres, comme avant T-081.
- Partager, émulateur : plus de bouton sur la page d'une série, d'un épisode, d'un film (avec et sans ses flux) ; capture de la page d'un film. Chrome, PC, invité (`perf2/share-pc.mjs`, scratchpad) : le bouton est présent mais masqué, aucune erreur.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : `795dc37` (genres et acteurs), `7ed82ff` (bouton Partager), puis le mode léger seulement (voir le journal)
- Version / CHANGELOG : à la prochaine release

### T-051 — Réglage « Rendu allégé sur TV » (page dessinée en 720p puis agrandie)

- **Statut :** À valider (photos de la mesure à l'écran sur la Fire TV, réglage coupé puis activé ; puis choix du défaut)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-041
- **Créée le :** 2026-11-01
- **Mise à jour :** 2026-11-01
- **Branche :** `main`

**Demande**
> Je dois aller me coucher, avance tout le chantier en autonome au maximum pour demain quand je me réveillerais

(chantier performance, `docs/perf/2026-10-31-plan-performance.md`, T-051)

**Critères d'acceptation**
- [x] Réglage `tvLowRes` (Auto / Activé / Coupé) dans Performances, sur TV seulement, et seulement avec une APK qui sait le faire
- [x] Activé : la page est dessinée en 1280 pixels de large, puis agrandie à l'écran ; elle garde sa largeur de 1280 px CSS
- [x] Points de vigilance n° 6 du plan : sélection, « Qui est-ce ? », lecteur natif et retour, clavier à l'écran
- [x] Ligne « Page dessinée en L×H » dans la mesure à l'écran (lisible sur les photos)
- [ ] Mesure sur la Fire TV, coupé puis activé ; défaut `auto` seulement si la médiane des touches baisse d'au moins 20 % (plan), sinon `off`

**Notes techniques**
- **Pourquoi** : sur TV, la page fait 1280 px CSS de large, agrandie à l'écran par la WebView. Sur une Fire TV en 1080p (densité 2), Chromium dessine donc 1920×1080 pixels pour une page pensée en 1280×720 : 2,25 fois plus de pixels. Les blocages d'environ 1 s vus sur les photos (T-079) ne viennent pas du code de Nathflix (quelques ms par touche) : le dessin de la page en est un suspect.
- **Kotlin** (`MainActivity`) : `lowRes()` lit `tvLowRes` dans les SharedPreferences `plugin-settings` (clé `nathflix`, comme `Intro.kt` lit `accent`) une fois, au démarrage : `on`, ou `auto` sur une TV modeste (4 cœurs ou moins, `isLowRamDevice`, ou 2 Go de mémoire ou moins, comme le mode léger), jamais sur téléphone ou tablette. Si oui, `drawSmaller(largeur, hauteur)` (sur chaque changement de taille de la racine, via `post`) : WebView de `1280 × ceil(hauteur / échelle)` pixels, `pivotX/Y = 0`, `scaleX/Y = largeur / 1280`. Rien sur un écran de 1280 px ou moins.
- **Page** : `nathflix-tv.js` annonce `tv.lowRes = true`. Le plugin n'affiche le réglage que si `__nathflixTv.lowRes` existe : nouvelle propriété de `SCHEMA`, `tvFeature: "lowRes"`, vérifiée par `registerSchema()` (une vieille APK ignorerait le réglage). Réglage propre à l'appareil (`settingsSync.LOCAL`). Défaut `off` pour l'instant : le plan voulait `auto`, mais à décider d'après les photos.
- **Mesure à l'écran** : « Page dessinée en L×H » = `outerWidth × devicePixelRatio` (la taille de la vue de la page ; `innerWidth` reste 1280, la page étant zoomée pour tenir).
- **Le dessin est vraiment en 720p** : Chromium aurait pu redessiner à la taille agrandie (il connaît la transformation de la vue). Captures comparées, le texte est nettement plus doux réglage activé : la page est bien dessinée en 720p puis agrandie.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus). `assembleDebug` local.
- Émulateur Fire TV (1920×1080, densité 320), compte de l'utilisateur, plugin poussé en 0.7.99 :
  - coupé : WebView 1920×1080 (`outerWidth` 960, `devicePixelRatio` 2, `visualViewport.scale` 0,75) ;
  - activé (puis app relancée) : WebView 1280×720 (`dumpsys` : `0,0-1280,720`), `outerWidth` 640, `innerWidth` 1280, `visualViewport.scale` 0,5 ; la page remplit l'écran (capture) ;
  - contour de sélection bien placé sur une carte de Continuer de regarder (capture) ;
  - « Qui est-ce ? » affiché et validé (sur Nathan) au démarrage ;
  - lecteur natif avec la vidéo publique de test (`emu/playtest.mjs`, jamais un titre du compte), puis Retour : la page revient, toujours en 720p, voile retiré ;
  - clavier à l'écran : champ de Recherche, OK, le clavier s'ouvre, « dune » tapé arrive dans le champ.
- Réponse aux touches sur l'émulateur (`emu/scrollperf.mjs`, processeur ×4, 12 touches, 2 passages) : activé 56 / 56 ms de médiane, coupé 48 / 48 ms. Pas d'écart : l'émulateur dessine avec la carte graphique du PC, et le bridage ne ralentit que le processeur. Seule la Fire TV dira si le gain existe.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release (nouvelle APK nécessaire)

### T-082 — Défilement : moins de travail à chaque image (`onScroll`)

- **Statut :** À valider (Fire TV)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-041
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Je dois aller me coucher, avance tout le chantier en autonome au maximum pour demain quand je me réveillerais

(trouvée en mesurant pour T-059 : où va le temps quand on descend dans les rangées de l'Accueil)

**Critères d'acceptation**
- [x] Un évènement de défilement ne fouille plus toute la page quand il n'y a rien à remettre en place
- [x] Même comportement : carte d'aperçu, titres poussés de côté et couverts, barre du haut, flèches des rangées, fondu des détails
- [x] Gain mesuré

**Notes techniques**
- **Constat** (profil processeur, émulateur Fire TV, compte de l'utilisateur, Accueil, 10 × Bas, processeur ×6, `emu/rowsprof.mjs` du scratchpad) : `onScroll` tourne à chaque évènement de défilement, un par image quand une rangée ou la page glisse. À chaque fois : `preview.close(true)` cherchait dans toute la page les titres couverts (`[data-dx-covered]`) et poussés (`unpush()`, `[data-dx-pushed]`), même sans carte ouverte ; `setScrolled()` cherchait la barre du haut ; `onScroll` essayait trois sélecteurs `[class*=…]` sur l'élément. Ensemble : 350 à 450 ms sur les 10 touches.
- **Changements** :
  - `preview.marked` : posé quand une carte ouverte couvre son titre et pousse les autres (dans `open`, au moment où elle le fait) ; `close()` ne cherche et ne remet en place que s'il est posé, puis l'enlève ;
  - `setScrolled(on, fresh)` garde les barres trouvées (`scrolledNavs`) et ne les cherche à nouveau que si l'une a quitté la page, ou à chaque passage (`checkScrolled`) et à chaque changement de page (`updateRoute`) : Stremio redessine sa barre sur certaines pages ;
  - `scrollKind(target)` : ce qu'est l'élément qui défile (page, détails d'un titre, rangée, rien), calculé une fois par élément (`WeakMap`).

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Mesure, mêmes conditions, deux passages alternés : JS de Nathflix 1061 et 1059 ms avant, 799 et 641 ms après (−25 à −40 %). Les lignes `querySelectorAll < close / unpush / setScrolled < onScroll` disparaissent du profil.
- Émulateur, à la télécommande (`emu/pushcheck.mjs`) : carte ouverte → 9 titres poussés, le sien couvert ; déplacement → 0 poussé, 0 couvert ; plus bas puis repos → nouvelle carte, mêmes marques. Barre du haut : transparente tout en haut (défilement 0), opaque dès 42 px.
- Chrome, PC, invité, à la souris (`perf2/hover-pc.mjs`) : survol → 9 poussés, 1 couvert ; souris ailleurs → 0 ; nouveau survol puis molette → 0. (Les erreurs « TrustedScriptURL » de ce test viennent de l'outil de test, qui s'injecte aussi dans les iframes de YouTube et Google : identiques avec le plugin d'avant.)
- `tools/perf-bench.mjs --tv` et `--pc` (1 run) : parcours de contrôle sans erreur.
- Non vérifié : la Fire TV.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-083 — Continuer de regarder : une série ouvre sa page, pas son épisode

- **Statut :** À valider (sur les appareils)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-012
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> J'aimerais que par défaut quand on clique sur une série dans "Continuer à regarder" ça ouvre la série et pas l'épisode, vu qu'on a le bouton "Reprendre S1 E3" et que c'est suffisant 95% du temps car la première source est toujours bonne.

**Critères d'acceptation**
- [x] Une série de Continuer de regarder (rangée de l'Accueil et page « Voir tous ») ouvre la page de la série, avec « Reprendre S1 É3 »
- [x] Partout : TV (OK, appui court ou relâché après `okHold`), téléphone (toucher), PC (clic)
- [x] Films, menu ⋮, bouton lecture de Stremio sur la carte : inchangés

**Notes techniques**
- Stremio fait pointer ces cartes vers la page des sources de l'épisode (`#/detail/series/<id>/<épisode>`). `cwSeries.onClick` (écouteur `click` en capture sur `window`, avant Stremio) : un lien de série avec un épisode, dans une carte de `continue-watching` ou sur la route `continuewatching`, est remplacé par `navigate("/detail/series/<id>")`, avec le son « select » que l'écouteur des sons n'entend plus. Pas sur le menu caché, le bouton lecture ni la croix de la carte.
- Un clic déjà annulé (`defaultPrevented`, comme celui qui suit l'ouverture du menu par un doigt maintenu) est laissé tel quel.
- « Par défaut » compris comme le comportement normal : pas de réglage pour revenir à l'épisode (confirmé par l'utilisateur : « Non comme ça c'est bon »).

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Émulateur Fire TV, compte de l'utilisateur (`emu/cwopen.mjs`) ; les liens de Continuer de regarder ont été lus d'abord : tous mènent à la page d'un titre, aucun au lecteur. OK sur la 1re carte de la rangée (It's Always Sunny, lien vers S14 É10) → `#/detail/series/tt0472954`, bouton « Reprendre S14 É10 » ; Retour → Accueil, sélection sur la même carte. Page « Voir tous » : OK → `#/detail/series/tt35854736`, « Reprendre S1 É3 » ; Retour → la page, même carte.
- Non vérifié : téléphone et PC (même écouteur de clic).

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-084 — Billboard écrasé (seul le bas visible) quand l'Accueil est réorganisé

- **Statut :** À valider (PC et téléphone de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Sur PC, le billboard a le haut qui est coupé, on voit que le bas du billboard, il faudra corriger ça.
> En vue portrait sur mobile aussi c'est le cas mais pas autant.

**Critères d'acceptation**
- [x] Billboard à sa hauteur normale avec un Accueil réorganisé (PC, téléphone en portrait)
- [x] Ordre et rangées masquées d'« Organiser l'Accueil » inchangés

**Notes techniques**
- **Cause** : pas reproduit en invité (Accueil dans l'ordre de Stremio). L'utilisateur a réorganisé son Accueil : `homeArrange.apply()` passe alors le conteneur des rangées en `display: flex` colonne (`.dx-rows-arranged`, dans `EXTRAS_CSS`), pour ranger les rangées par `order`. Ce conteneur fait la hauteur de l'écran (il défile) : ses enfants rétrécissent pour y tenir, et le billboard, en `overflow: hidden`, n'a plus de hauteur minimale. Il était écrasé en bande, son contenu ancré en bas : on n'en voyait que le bas.
- Mesuré (Chrome, invité, la classe posée à la main) : PC 1920×1080, 886 px de haut → 390 px ; téléphone en portrait (390×844, app mobile), 551 → 394 px (412×915 : 551 → 446).
- **Correction** : `#app .dx-rows-arranged > * { flex-shrink: 0; }` : rien ne rétrécit pour tenir dans l'écran.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Après : PC 886 → 886 px, téléphone 551 → 551 px (390 et 412 de large), Accueil réorganisé ou non.
- Non vérifié : sur le compte de l'utilisateur (son app PC n'aura la correction qu'avec la prochaine release ; Stremio Enhanced la prend sur `main`).

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-085 — Ordre de l'Accueil pas répercuté sur les autres appareils

- **Statut :** À valider (sur les appareils de l'utilisateur ; cause exacte non trouvée)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-027
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Et j'ai réorganisé l'ordre de mes catalogues mais sur mes autres appareils c'est pas répercuté, la sync ne fonctionne pas

**Critères d'acceptation**
- [x] Un nouvel ordre de l'Accueil part dans les 2 s
- [x] Les autres appareils le prennent en ouvrant l'Accueil, ou en 2 minutes au plus à l'écran
- [x] Un appareil dont un envoi est refusé reçoit quand même ce que les autres ont changé
- [ ] Vérifié sur les appareils de l'utilisateur

**Notes techniques**
- **Ce qui marche** : l'émulateur Fire TV (compte de l'utilisateur, dernier plugin) a reçu l'ordre fait par l'utilisateur (`homeRows:main`, enregistré le 2026-11-01 à 22:17 UTC : 66 rangées ordonnées, 19 masquées) et l'applique : 63 rangées présentes, toutes reconnues et classées, 17 masquées. Le Worker a donc bien reçu l'ordre. Le code de synchronisation et d'« Organiser l'Accueil » est identique en 0.7.0 (comparé) : ce n'est pas une version en retard.
- **Causes possibles chez l'utilisateur** (non observables d'ici) :
  - délai : un appareil resté à l'écran ne relisait le compte que toutes les 5 minutes (ou en revenant au premier plan, ou quand la fenêtre reprend le focus), et l'ordre ne partait que 20 s après le changement ;
  - un appareil bloqué : envoi et réception passent par la même requête. Si le Worker refuse ce que l'appareil envoie (400 : une clé ou une heure qu'il refuse ; 413 : trop gros), l'appareil ne recevait plus rien, et réessayait au bout de 1, 5 puis 15 minutes ;
  - session refusée (401) : l'appareil s'arrête jusqu'à une nouvelle connexion (« Reconnectez-vous à Stremio… » dans les Réglages) ; pas changé ici.
- **Changements** (`cloudSync`) :
  - `changed()` : `homeRows:*` part au bout de 2 s (comme `played:*`) : il est enregistré une fois, quand « Organiser l'Accueil » se ferme ;
  - `onHome()`, appelé par `updateRoute()` à l'arrivée sur l'Accueil : une synchronisation si la dernière a plus de 30 s ;
  - toutes les 2 minutes (au lieu de 5) tant que Nathflix est à l'écran (le Worker accepte 30 synchronisations par minute et par compte) ;
  - envoi refusé (400 ou 413) : une requête sans rien envoyer suit, pour recevoir et fusionner ce que les autres ont changé.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Émulateur, compte de l'utilisateur : synchronisations au démarrage (1 s et 4 s) ; Réglages 40 s puis Accueil → une synchronisation (79 s) ; Découvrir puis Accueil dans les 30 s → aucune de plus. Requêtes lues dans `performance.getEntriesByType("resource")`.
- Chrome, faux compte et faux Worker (`perf2/sync-refused.mjs`, scratchpad) qui refuse tout envoi (400) et donne l'ordre de l'Accueil d'un autre appareil à une requête vide. Avant : un envoi refusé, rien de reçu. Après : l'envoi refusé est suivi d'une requête vide, et l'ordre de l'autre appareil est reçu (`homeRows:main`).
- Non vérifié : les appareils de l'utilisateur. Le statut de la synchronisation est dans Réglages (« Profils et réglages synchronisés… il y a N min », ou un message d'erreur) : à regarder sur l'appareil qui ne suit pas.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release (l'APK TV et l'app PC prennent le plugin de la release)

### T-058 — Thème découpé par appareil à la construction (étude)

- **Statut :** Abandonné (gain trop faible : ~30 ms par démarrage sur TV, ~0,5 s une fois par mise à jour)
- **Priorité :** P2
- **Cible :** plugin, tools
- **Dépend de :** T-041
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Je dois aller me coucher, avance tout le chantier en autonome au maximum pour demain quand je me réveillerais

> Continue tant qu'il reste des taches à faire. Si il y a des choix bloquants que j'ai à faire de mon coté pour certains changements, tu devras les noter mais continuer à avancer seul et me donner ces questions dans le message final

(phase 3 du plan, `docs/perf/2026-10-31-plan-performance.md` : « à proposer avant de commencer » ; l'utilisateur a demandé d'avancer seul)

**Critères d'acceptation**
- [x] Mesure avant : ce que coûte l'affûtage du thème (`sharpen`) au démarrage
- [x] Proposer le découpage seulement si le gain est net

**Notes techniques**
- **Copie affûtée en cache** (cas normal) : émulateur Fire TV, compte de l'utilisateur, processeur ×6 (`emu/sharpcost.mjs`, scratchpad) : `nathflix.sharp.dx-builtin-theme` (239 Ko, lecture + `JSON.parse` 4 ms, empreinte du texte d'origine 15,8 ms), `nathflix.sharp.dx-extras-style` (69 Ko, 0 + 12,1 ms). Environ 30 ms par démarrage, sur 11,4 s à la Fire TV.
- **Sans copie** (premier démarrage après une mise à jour de Stremio ou de Nathflix) : profil processeur d'un rechargement, ×6 (`emu/sharpcold.mjs`) : 535 ms sous `sharpen.run`, une fois.
- Le découpage à la construction ne ferait que la partie « règles des autres appareils » (`prune`) ; la traduction des sélecteurs en vraies classes de Stremio (`rules`) dépend de la feuille de style de Stremio en direct et resterait à faire sur l'appareil. Gain possible : une partie des 535 ms une fois par mise à jour, et les ~16 ms de l'empreinte (calculable à la construction pour le thème intégré, pas pour `EXTRAS_CSS` traduit). Trop peu pour trois variantes du thème dans le plugin et l'APK.

**Vérification**
- Mesures ci-dessus ; aucun code changé.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-059 — Ordonnanceur à budget (étude)

- **Statut :** Abandonné (rien à ordonnancer : le temps de Nathflix est sur le chemin de la touche, pas en tâches de fond)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-041
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Je dois aller me coucher, avance tout le chantier en autonome au maximum pour demain quand je me réveillerais

> Continue tant qu'il reste des taches à faire. Si il y a des choix bloquants que j'ai à faire de mon coté pour certains changements, tu devras les noter mais continuer à avancer seul et me donner ces questions dans le message final

(phase 3 du plan : « à proposer avant de commencer »)

**Critères d'acceptation**
- [x] Mesure avant : tâches longues et part de Nathflix en descendant 10 rangées (TV)
- [x] Proposer seulement si une file de tâches de fond peut gagner quelque chose

**Notes techniques**
- **Mesure** (émulateur Fire TV, compte de l'utilisateur, Accueil, 10 × Bas, processeur ×6, profil processeur `emu/rowsprof.mjs`). Première mesure : 12,9 s dont 11,6 s inoccupées, JS de Nathflix 345 ms, Stremio 142 ms, travail du navigateur 746 ms. Elle a montré `onScroll` et `preview.close()` qui fouillaient la page à chaque image : corrigé à part (T-082, −25 à −40 %).
- **Après T-082**, ce qui reste de Nathflix (641 à 799 ms sur 10 touches, ×6) : `focus()` et ce qu'il déclenche dans le navigateur (~140 ms), les pas du défilement doux de `keyNav` (`step`, ~120 ms), `onScroll` lui-même (~45 ms), la recherche des rangées (`rows`, `carouselRows`, `loadingBetween`, ~100 ms). Tout est sur le chemin de la touche : c'est ce qui la fait répondre. Ni `syncCards` ni `decorate` n'apparaissent en haut du profil pendant la navigation.
- Un ordonnanceur à budget étale des tâches de fond ; il n'y en a presque pas ici, et la réponse aux touches ne peut pas attendre. Le reste à gagner est du côté du dessin de la page sur l'appareil (T-051, à mesurer sur la Fire TV).

**Vérification**
- Mesures ci-dessus ; aucun code changé pour T-059 (le correctif trouvé est T-082).

**Livraison**
- Commits : aucun code (fiche seulement)

### T-060 — Étude : Stremio Web embarqué dans l'APK

- **Statut :** Terminé (décision de l'utilisateur : ne pas l'embarquer)
- **Priorité :** P3
- **Cible :** tv
- **Dépend de :** T-052
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Je dois aller me coucher, avance tout le chantier en autonome au maximum pour demain quand je me réveillerais

> Continue tant qu'il reste des taches à faire. Si il y a des choix bloquants que j'ai à faire de mon coté pour certains changements, tu devras les noter mais continuer à avancer seul et me donner ces questions dans le message final

(phase 3 du plan : étude seulement, sans implémentation ; décision de l'utilisateur)

**Critères d'acceptation**
- [x] Ce que coûte aujourd'hui le chargement de Stremio Web au démarrage
- [x] Faisabilité technique et licence
- [x] Recommandation écrite
- [x] Décision de l'utilisateur : « Tant que l'app fonctionne alors oui. » (d'accord pour ne pas l'embarquer)

**Notes techniques**
- **Aujourd'hui** (émulateur Fire TV, démarrage à chaud, compte de l'utilisateur ; `performance.getEntriesByType`) : la page est contrôlée par le service worker de Stremio (Workbox, `__WB_REVISION__`). Tous ses gros fichiers viennent de son cache, rien par le réseau (`transferSize` 0) : `main.js` 10 030 Ko en 481 ms, `main.css` 417 Ko en 259 ms, la page en 1189 ms (par le service worker). Embarquer Stremio Web ne gagnerait du téléchargement qu'au premier lancement et après chaque mise à jour de Stremio Web ; le temps d'analyse et d'exécution de `main.js` resterait le même.
- **Technique** : servir les fichiers depuis l'APK sur l'origine `https://web.stremio.com` (pour garder le `localStorage`, la session et les données de Nathflix) passe par `shouldInterceptRequest`. T-052 a mesuré ce chemin pour le seul plugin : plus lent que la référence (124 à 181 ms contre 80 à 107). Il faudrait aussi suivre chaque version de Stremio Web (nouvelle APK à chaque fois, ou téléchargement à part, ce qui revient au cache actuel), et l'APK prendrait ~15 Mo (bundle de 10 Mo, WASM de 5,7 Mo).
- **Licence** : Stremio Web est publié sous GPL ; Nathflix est sous GPL-3.0-or-later. Le redistribuer dans l'APK obligerait à suivre ses conditions (sources, mentions), en plus de la question de la marque Stremio. Pas bloquant en soi, mais une charge de plus.
- **Recommandation** : ne pas le faire. Le gain est nul au démarrage habituel (déjà en cache), le chemin technique est plus lent, et la maintenance suit chaque version de Stremio.

**Vérification**
- Mesures ci-dessus ; aucun code changé.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-062 — Release 0.8.0 (performances, OK maintenu, Continuer de regarder, billboard, synchro)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin, app
- **Dépend de :** T-049 à T-085
- **Créée le :** 2026-10-31
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> 1. Oui fait une release

(réponse à la question « Release 0.8.0 : je la publie quand tu me dis go »)

**Critères d'acceptation**
- [x] `@version 0.8.0`, section `## 0.8.0` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.8.0`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 800, plugin embarqué en 0.8.0

**Notes techniques**
- **Contenu** : T-049 (mises à jour vérifiées après la page), T-051 (réglage « Rendu allégé sur TV », coupé), T-079 (mesure à l'écran sur la dernière minute), T-080 (OK maintenu, 3 points masqués), T-081 (genres et acteurs en mode léger, Partager retiré), T-082 (défilement allégé), T-083 (Continuer de regarder ouvre la série), T-084 (billboard écrasé), T-085 (synchro de l'ordre de l'Accueil) ; et de T-052 / T-055 ce qui est resté (temps de chargement du plugin dans la mesure à l'écran, outils du banc).
- **Nouvelle APK** : T-049, T-051 (`MainActivity`, `nathflix-tv.js`). Un plugin 0.8.0 dans une APK 0.7.0 n'affiche pas le réglage de T-051 (`tvFeature`).
- **Worker de synchro** : inchangé (mêmes clés), rien à redéployer.

**Vérification**
- `node --check` plugin et `nathflix-tv.js`, traductions (`fr-missing.mjs` : les 2 faux positifs connus) : OK.
- CI verte sur `d70461e` (Cross-platform, mpv Windows, Android test build avec R8), puis tag `v0.8.0` : run Release 41363250144, tous les jobs verts (publish, android, desktop ×5).
- Release : les 11 fichiers (installeur et portable Windows, `.blockmap`, 2 dmg, 2 AppImage, APK, 3 `latest*.yml`), ni brouillon ni pré-release.
- APK : signée `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b` (la même clé que la 0.7.0) ; `com.lavagnou.nathflix`, versionCode 800, versionName 0.8.0 ; plugin embarqué en 0.8.0 ; T-051 présent après R8 (`tvLowRes` dans le dex, `tv.lowRes` dans `nathflix-tv.js`, `tvFeature` dans le plugin).

**Livraison**
- Commits : `d70461e` (`0.8.0: …`)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.8.0

### T-086 — Téléphone : bouton Bande-annonce dans la feuille d'un titre, appui long qui l'ouvre

- **Statut :** À valider (sur le téléphone de l'utilisateur)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Sur mobile il manque un bouton bande d'annonce sous le bouton Lecture quand on clique sur un une série/film , rajoute ça.

> Et quand je laisse appuyer sur une série/film ça doit ouvrir le menu contextuel aussi

**Critères d'acceptation**
- [x] Un bouton « Bande-annonce » sous « Lecture » dans la feuille d'un titre (téléphone et tablette), seulement si le titre a une bande-annonce
- [x] Il lance la bande-annonce en plein écran (lecteur de bandes-annonces de Nathflix), en français quand TMDB l'a ; Retour la ferme
- [x] Un appui long sur un film ou une série ouvre un menu, y compris sur les cartes de l'Accueil, de Découvrir et de la Recherche
- [x] Un toucher ouvre toujours la feuille ; « Continuer à regarder » et la Bibliothèque gardent le menu de Stremio
- [ ] Vérifié sur le téléphone de l'utilisateur

**Notes techniques**
- **Cause de l'appui long sans effet** : Stremio ne donne un menu ⋮ qu'aux cartes de « Continuer à regarder » et de la Bibliothèque. Les cartes des catalogues (Accueil, Découvrir, Recherche) n'en ont pas (Chrome en téléphone, Accueil : 60 cartes, aucune avec `menu-label-container`). `cardPress` ne trouvait rien à ouvrir.
- `cardPress.action()` : le menu de la carte s'il existe ; sinon, sur téléphone et tablette, la feuille du titre (`titleSheet.open`), qui a Lecture, Bande-annonce, Ma liste, Masquer et la page du titre. Utilisé par l'appui long (`onDown`) et par `contextmenu` (Android l'envoie sur un appui long : il est annulé sur ces cartes aussi).
- `titleSheet.infoFor(target)` : les conditions de `onClick` (routes, pas « Continuer à regarder », pas les boutons de Stremio), partagées avec `cardPress`.
- **Bande-annonce** : après les détails, `trailer.idFor(info, meta)` (la bande-annonce de TMDB en français déjà dans `meta` quand la clé TMDB est réglée, sinon celle de Cinemeta) ; le bouton `.dxm-trailer` est ajouté sous `.dxm-play` seulement s'il y en a une. Il ferme la feuille et ouvre `theatre` (logo du titre, « Lecture » et « Plus d'infos » vont à la page du titre).
- **`theatre` au doigt** :
  - CSS `html.dx-mobile` : marges, titre, boutons plus petits, pas d'infobulles ;
  - un toucher sur la vidéo quand les commandes sont masquées les réaffiche sans mettre en pause. Le réveil passe par `pointermove` de la souris seulement : un doigt envoie aussi un `mousemove` juste avant son clic, qui réveillait les commandes avant que le clic ne les voie masquées (la vidéo se mettait en pause). `byFinger` retient si le dernier appui vient d'un doigt.
- Nouvelle icône `ICONS.film`. Texte « Trailer » déjà traduit (« Bande-annonce »).

**Vérification**
- `node --check` plugin et `nathflix-tv.js`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Chrome en téléphone (412 × 915, tactile, invité ; `perf2/hold-mob.mjs`, scratchpad), Accueil, carte « Populaire - Film » :
  - avant : appui long de 900 ms → rien (ni menu ni feuille) ;
  - après : appui long → feuille ouverte, pas de clic au relâchement ; bouton « Bande-annonce » 7 px sous « Lecture » ; toucher → bande-annonce en lecture, feuille fermée ; toucher sur la vidéo aux commandes masquées → commandes visibles, toujours en lecture ; Retour (`closePreview`) → fermée ; un toucher simple ouvre toujours la feuille.
- Non vérifié : le téléphone de l'utilisateur (WebView Android).

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release (l'app Android prend le plugin de la dernière release)

### T-087 — Bande-annonce en plein écran : l'interface du lecteur YouTube ne se voit plus

- **Statut :** À valider (sur les appareils de l'utilisateur)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Dans la lecture de bande d'annonce, on a parfois l'UI du player de YouTube qui est visible sur la vidéo, on pourrait pas le cacher pour qu'il n'apparaisse pas ?

(capture du téléphone : bande-annonce en pause, avec le titre de la vidéo, la chaîne, le bouton lecture et le logo YouTube par-dessus)

**Critères d'acceptation**
- [x] Ni titre, ni bouton, ni logo de YouTube sur la vidéo : au démarrage, en pause, à la reprise, après un saut, à la fin
- [x] Les sous-titres automatiques de YouTube ne reviennent pas après une pause
- [ ] Vérifié sur les appareils de l'utilisateur

**Notes techniques**
- **Mesure** (Chrome, téléphone tactile, User-Agent de l'app Android et celui d'un téléphone : même résultat ; `perf2/trailer-ui.mjs`, scratchpad) : malgré `controls=0`, YouTube affiche son titre, la chaîne, un bouton pause, un lien et son logo pendant environ 4 s chaque fois qu'il passe en lecture : au démarrage, à la reprise après une pause, après un saut (encore là à +3,5 s, parti à +4,5 s depuis l'appui). En pause, chez l'utilisateur : titre, bouton lecture et logo. Rien ne se règle dans l'iframe (autre origine), et le bouton du milieu ne peut pas être rogné.
- **`theatre`** : sous l'iframe, une image (`.dxe-th-cover`) : le fond du titre (`cover`, MetaHub), sinon la miniature de la bande-annonce (`i.ytimg.com/…/hqdefault.jpg`). L'iframe n'est visible (`is-clear`, posé par `paint()`) que si la lecture dure depuis `REVEAL_MS` (4,3 s) : `since` est remis à l'heure quand YouTube passe en lecture (`yt`, son dernier état), à la reprise (`toggle`) et à chaque saut (`seekTo`). En pause, à la fin ou en chargement initial, l'image reste. L'iframe disparaît d'un coup (le fondu ne sert qu'à l'apparition) : en pause, avant que les boutons de YouTube n'arrivent.
- Le son continue pendant que l'image couvre la vidéo.
- `unloadModule` des sous-titres à chaque passage en lecture (ils revenaient après une reprise).
- `cover` passé par la feuille d'un titre (téléphone) et par le bouton Bande-annonce de la page d'un titre (`localMeta.onTrailer`) ; l'aperçu en plein écran d'une carte prend la miniature.

**Vérification**
- `node --check`, traductions (`fr-missing.mjs` : les 2 faux positifs connus).
- Chrome en téléphone, invité, User-Agent de l'app : captures à +1 / 3 / 4,5 / 5,5 s du démarrage, en pause (+0,3 et +1,5 s), +1 / 3 / 4,8 / 5,5 s après la reprise et après un saut. Avant : interface YouTube sur toutes les captures jusqu'à ~4 s. Après : l'image du titre, puis la vidéo seule (4,5 s : en fondu, sans interface) ; plus de sous-titres après la reprise.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : 0.8.1 (T-088)

### T-088 — Release 0.8.1 (bande-annonce et appui long sur téléphone, sans interface YouTube)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, plugin
- **Dépend de :** T-086, T-087
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Ensuite publie une 0.8.1 une fois que c'est ajouté

**Critères d'acceptation**
- [x] `@version 0.8.1`, section `## 0.8.1` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.8.1`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 801, plugin embarqué en 0.8.1

**Notes techniques**
- **Contenu** : T-086 (bouton Bande-annonce dans la feuille d'un titre, appui long), T-087 (interface YouTube cachée).
- Plugin seulement : l'APK 0.8.0 prend le plugin de la release (`Updates.checkPlugin`) ; l'APK 0.8.1 est publiée quand même (même code natif). Worker de synchro inchangé.

**Vérification**
- `node --check` plugin et `nathflix-tv.js`, traductions : OK.
- CI verte sur `a7f0667` (Cross-platform, mpv Windows, Android test build avec R8), puis tag `v0.8.1` : run Release 41438935849, tous les jobs verts (publish, android, desktop ×5).
- Release : les 11 fichiers, ni brouillon ni pré-release.
- APK : signée `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b` (la même clé que la 0.8.0) ; `com.lavagnou.nathflix`, versionCode 801, versionName 0.8.1 ; plugin embarqué en 0.8.1, avec T-087 (`dxe-th-cover`).

**Livraison**
- Commits : `a7f0667` (`0.8.1: …`)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.8.1

### T-089 — Étude : mémoire et démarrage sur TV

- **Statut :** Terminé (étude ; les changements sont T-090 et suivantes)
- **Priorité :** P1
- **Cible :** tv, plugin
- **Dépend de :** —
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Sur ma FireTV 4K, c'est beaucoup mieux qu'avant, c'est "utilisable" on va dire, mais très loin d'être confortable et fluide. Vu que le but que l'application tourne sur une FireTV pas 4K donc moins puissante, il y a encore beaucoup à optimisé avant d'en arrivé là.
>
> La sur le FireTV 4K, j'ai eu un message comme quoi l'app a planté deux fois d'affilié par manque de mémoire ram. Je pense que c'est pas trop normal pour une app TV de prendre autant de ram, sur toute les autres apps TV classique ça n'arrive jamais, même avec plusieurs apps ouverte les une après les autres.
>
> Lance une analyse pour essayer de trouver où on pourrait optimiser et ecenomiser la ram sur la version TV, et si il y a des choix à trancher pour ça, demande moi.
>
> Sur les version Windows et Android mobile/tablette ça fonctionne plutôt bien, c'est fluide, donc maintenant le soucis de performance concerne uniquement la version TV.

> Je remarque que le lag survient beaucoup au démarrage de l'app pendant 1 minute puis après ça se stabilise, il faudrait voir du côté du chargement initial de l'app et de l'accueil les optimisations possible également.

> J'avais pas précisé mais je suis une TV en résolution 4K aussi, ce qui joue sûrement sur les performances

**Critères d'acceptation**
- [x] Ce qui occupe la mémoire sur TV, par processus et par catégorie
- [x] La part de Nathflix (comparée à Stremio seul dans la même WebView)
- [x] Ce qui fait ramer la première minute
- [x] Les choix de l'utilisateur, posés et tranchés

**Notes techniques**
- **Message vu par l'utilisateur** : c'est celui de l'app (`crash_message`, `recoverFromCrash`) : le moteur de rendu de la WebView a été tué deux fois en moins de 3 minutes.
- **Banc** : émulateur Fire TV (2 Go, 1920×1080), build de test 0.8.1, compte de l'utilisateur, Accueil organisé (66 catalogues, 19 masqués, 13 de type `trakt` coupés par `videoRowsOnly`). Outils du scratchpad : `emu/mem.mjs` (dumpsys meminfo de chaque processus + chiffres de la page), `emu/memdump.mjs` (vidage memory-infra de Chromium par allocateur), `emu/heaps.mjs` (tas JS et mémoire WebAssembly des workers), `emu/startup2.mjs` (première minute, processeur ×6), `emu/memleak.mjs`, `emu/hashleak.mjs`, `emu/detached.mjs`, `emu/memtrailer.mjs`, `emu/memab.sh`.
- **Mémoire, Accueil au repos** : moteur de rendu 200-230 Mo (PSS), app 100 Mo. L'émulateur ne compte pas la mémoire du GPU : sur l'appareil, il faut y ajouter les images décodées pour le GPU (`gpu/transfer_cache`, jusqu'à 128 Mo, le plafond de Chromium) et les tuiles.
  - Moteur de rendu : malloc 55-94 Mo, cache web 46-53 Mo (scripts 17-35, images 12-33), V8 23-46 Mo, PartitionAlloc 20-48 Mo, tuiles 15-32 Mo, Oilpan 6-16 Mo ; mémoire WebAssembly du cœur de Stremio (worker) 37 Mo seul, 47 Mo avec les 66 catalogues (elle ne rend jamais rien).
  - App : malloc ≈ 77 Mo, images décodées GPU 31 Mo au repos, 127 Mo après avoir descendu les rangées.
- **Stremio seul** (plugin vide dans la même APK) : 7 rangées, 70 cartes, 72 images, 12 Mo d'images GPU, WASM 37 Mo. **Avec Nathflix** : 64 rangées, 594 cartes, 612 images (274 dans des rangées masquées), 127 Mo d'images GPU, WASM 47 Mo. Tas JS de la page à peu près égal (≈ 34 Mo) : la part de Nathflix vient de ce qu'il fait charger, pas de son propre code.
- **Cause n° 1 : Accueil organisé = tous les catalogues d'un coup.** Avec un ordre ou des rangées masquées, Stremio demande les rangées à l'écran par leur place dans la page, que `style.order` mélange : il demande tout (`LoadRange` 0-65, une dizaine de fois au démarrage), et `homeArrange` demandait aussi tout (`homeRows.preload(4, true)`). Au démarrage (processeur ×6, `startup2.mjs`) : 8,5 s de tâches longues sur les 15 premières secondes, ≈ 5 200 changements de la page, JS de Stremio 5,5 s, travail du navigateur 5,4 s, Nathflix 1,8 s, ramasse-miettes 1,3 s ; sur l'appareil, il faut y ajouter le décodage et l'envoi au GPU de 612 affiches (non bridés ici) : la minute de ralenti.
- **Bandes-annonces des aperçus** (`memtrailer.mjs`) : +41 Mo pendant qu'une joue, +62 Mo au passage à une deuxième ; retombe à +13 Mo une fois quittée.
- **Fiches ouvertes à la télécommande** (`memleak.mjs`) : +270 éléments détachés et +5 à 6 Mo par fiche, jamais rendus (200 → 258 Mo en 10 fiches). Ouvertes par leur adresse (`hashleak.mjs`), avec ou sans Nathflix : rien ne reste. Les éléments détachés sont retenus par des écouteurs d'événements (`bind`), pas du code de Nathflix. À trouver.
- **Affiches** : 500×750 à 680×1000 (TMDB, TVDB, top-posters, Kitsu), affichées à ≤ 228 px CSS (≈ 342 px sur un écran 1080p).
- **Images d'aperçu décodées d'avance** (`decodeImage`) : les 24 dernières (fond, logo, affiche des 8 dernières cartes).
- **Rendu allégé (720p, T-051)** : tuiles 32 → 11 Mo, images partagées 32 → 11 Mo (≈ 40 Mo) ; les images décodées GPU restent au plafond.
- **Pendant un film** (vidéo de test publique) : page ≈ 216 Mo derrière le lecteur, app ≈ 170 Mo (tas Java 55-61 Mo : la mémoire tampon par défaut de Media3, jusqu'à 50 s plafonnées à ≈ 144 Mo). Les deux processus restent au premier plan (`oom_score_adj` 0).
- **TV 4K** : selon l'appareil, Fire OS dessine les apps en 1080p ou en 4K ; la mesure à l'écran (« Page dessinée en L×H ») le dit. En 4K, les tuiles coûtent 4 fois plus.

**Choix de l'utilisateur**
- Bandes-annonces des aperçus sur TV : « Coupées par défaut (Recommandé) ».
- Mémoire tampon du lecteur : « Alors enfaîte, un des seul moment où l'application est vraiment fluide, c'est sur le player pendant la lecture d'un film ou d'une série, donc pour cette partie là il n'y a pas besoin d'optimiser. »
- Page derrière le lecteur : « Vu que le player ne lag pas je pense que c'est pas nécessaire »
- Rendu allégé sur TV : « Auto (Recommandé) » (activé sous 2 Go de RAM).

**Plan qui en découle** (sans changement visible, donc sans question) : T-090 (rangées au fil de la télécommande, jamais les masquées), affiches à la bonne taille sur TV, moins d'images d'aperçu décodées d'avance, la mémoire gardée après chaque fiche ; avec les choix : bandes-annonces des aperçus coupées sur TV, rendu allégé en Auto.

**Vérification**
- Mesures ci-dessus ; aucun code changé pour l'étude.

**Livraison**
- Commits : aucun code (fiche seulement)

### T-090 — Accueil organisé : rangées chargées au fil de la télécommande, jamais les masquées

- **Statut :** À valider (sur la Fire TV de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-089
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
(voir T-089 : mémoire de la TV et première minute de l'app)

**Critères d'acceptation**
- [x] Accueil organisé sur TV : les 6 premières rangées dans l'ordre de l'Accueil au démarrage, puis les suivantes avant que la télécommande y arrive
- [x] Les rangées masquées, et celles coupées par « Rangées de films et séries seulement », ne sont jamais demandées
- [x] Ailleurs (PC, téléphone) : toutes les rangées montrées, dans l'ordre
- [x] Une rangée pas encore chargée est déjà à sa place (et masquée si elle doit l'être)
- [ ] Vérifié sur la Fire TV de l'utilisateur

**Notes techniques**
- `homeRows.shown(board)` : les catalogues dans l'ordre de l'Accueil (`homeArrange.prefs()`), sans les masqués ni ceux coupés (`homeRows.off(key)`, d'après le type de la clé). `catalogKey(c)` : la clé d'un catalogue du core (`<addon>|<type>|<catalogue>`), la même que `homeArrange.keyOf(row)`.
- `homeRows.loadShown(entries, board)` : ceux qui n'ont pas été demandés, par suites de voisins dans l'ordre de Stremio (un `LoadRange` par suite ; **la fin du `LoadRange` de Stremio est incluse**).
- `homeRows.preload(force)` : Accueil organisé, `FIRST` (6) sur TV, tout ailleurs. `homeRows.ahead(row)` → `aheadShown(row)` : les `LOW` (3) rangées suivantes à l'écran (par `style.order`) ont-elles leurs titres ? sinon, les `BATCH` (6) suivantes de `shown()`.
- `homeRows.blocks(action)`, appelé par l'enveloppe `dispatch` de `viewerLibrary.patch` : Accueil organisé, les `LoadRange` de Stremio sont ignorés (ceux de Nathflix passent, `own`). Un `Load` de l'Accueil (addons changés) remet `homeArrange.preloaded` à faux : Nathflix redemande.
- `homeArrange.apply()` : une rangée sans lien « Voir tout » (pas encore chargée) prend sa clé par sa place parmi les rangées (`homeRows.keys`, lu dans le core, sans les catalogues vides, que Stremio n'affiche pas) ; relu quand le nombre de rangées change (`readKeys`). Une rangée coupée par `videoRowsOnly` est masquée d'après sa clé (`filterRows` ne la reconnaît qu'une fois chargée : sans ça, les 13 rangées Trakt de l'utilisateur restaient visibles, vides).
- Sur PC (Stremio Enhanced), la première demande de Stremio part avant le plugin : une rangée masquée peut être chargée une fois (sans effet visible).

**Vérification**
- `node --check`, traductions.
- Émulateur Fire TV, compte de l'utilisateur, plugin poussé en 0.8.99 :
  - avant : 66 catalogues demandés, 594 cartes, 612 images au démarrage ; après : 6 catalogues, 70 cartes, 88 images (comme Stremio seul) ;
  - 30 × Bas (`emu/downcheck.mjs`, 0,9 s entre deux) : jamais sur une rangée vide, les rangées arrivent avant la télécommande (6 → 10 → 14 → 18 → 22 → 23), toutes chargées en bas ;
  - démarrage, processeur ×6 (`startup2.mjs`) : tâches longues 8,5 → 5,3 s, changements de la page ≈ 5 200 → ≈ 650, JS de Stremio 5,5 → 1,9 s, ramasse-miettes 1,3 → 0,4 s ;
  - mémoire au repos : tas JS 36 → 23 Mo, Oilpan 10,6 → 6,2 Mo ; après avoir descendu toutes les rangées, les images GPU atteignent toujours le plafond (128 Mo).
- Chrome, invité, Accueil organisé (ordre inversé, première rangée masquée ; `perf2/arrange-pc.mjs`) : TV : 4 catalogues sur 6 demandés (ni la masquée ni YouTube, coupé), dans l'ordre ; PC : toutes les rangées montrées, dans l'ordre.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-091 — TV : affiches à la taille de l'écran, moins d'images d'aperçu décodées d'avance

- **Statut :** À valider (sur la Fire TV de l'utilisateur)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** T-089
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
(voir T-089 : mémoire de la TV)

**Critères d'acceptation**
- [x] Sur TV, les affiches TMDB et TheTVDB (v4) prennent la copie réduite de leur hébergeur ; l'originale revient si la copie manque
- [x] Sur TV, les images d'aperçu décodées d'avance sont celles des deux dernières cartes (au lieu de huit)
- [ ] Vérifié sur la Fire TV de l'utilisateur

**Notes techniques**
- Affichées à ≤ 228 px CSS, soit ≈ 342 px sur un écran 1080p. Mesures (une affiche) : TMDB `w600_and_h900_bestv2` 600×900, 230 Ko (2,2 Mo décodée) → `w342` 342×513, 79 Ko (0,7 Mo) ; TheTVDB v4 680×1000, 167 Ko (2,7 Mo) → `_t` 340×500, 55 Ko (0,7 Mo).
- `smallPoster.url(src)` : TMDB `w500|w780|w1280|original|w600_and_h900_bestv2` → `w342` ; TheTVDB `banners/v4/…/posters/x.jpg` → `x_t.jpg`. `smallPoster.apply(card)`, depuis `syncCards` (TV) quand une carte montre un nouveau titre : `data-dx-shown` retient l'adresse posée (Stremio en met une autre quand la carte change de titre), et une erreur de chargement remet l'originale.
- Pas de copie réduite connue : top-posters (les plus nombreuses chez l'utilisateur, 500×750 ; l'adresse contient sa clé, jamais écrite nulle part), anciennes affiches TheTVDB (`banners/posters/…`, pas de miniature : 403), Kitsu (390×554, déjà proche), MetaHub (`poster/small` 300×450 ; les fonds `background/medium` et `large` sont la même image 1920×1080).
- `decodeImage` : `DECODED_KEPT` = 6 sur TV (fond, logo et affiche des deux dernières cartes ; un fond seul fait 8 Mo décodé), 24 ailleurs.

**Vérification**
- `node --check`, traductions.
- Émulateur Fire TV, compte de l'utilisateur, 40 × Bas : 240 affiches, 151 chargées, aucune cassée, 51 remplacées par leur copie réduite (toutes celles de TMDB et TheTVDB v4 présentes).
- Les images décodées pour le GPU atteignent toujours le plafond de Chromium (128 Mo) après avoir descendu toutes les rangées : le gain est sur le décodage (moins de travail par affiche) et sur la place que chacune y prend.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-092 — TV : aperçus sans bande-annonce par défaut, rendu allégé en Auto par défaut

- **Statut :** À valider (sur la Fire TV de l'utilisateur ; le rendu allégé en Auto demande la prochaine APK)
- **Priorité :** P1
- **Cible :** plugin, tv
- **Dépend de :** T-089, T-051
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
Choix de l'utilisateur à l'étude T-089 :
> Bandes-annonces dans les aperçus sur TV : « Coupées par défaut (Recommandé) »

> « Rendu allégé sur TV » : « Auto (Recommandé) »

**Critères d'acceptation**
- [x] Sur TV, « Bandes-annonces dans les aperçus » est coupé par défaut (ailleurs inchangé) ; le réglage reste là pour les rallumer
- [x] « Rendu allégé sur TV » vaut Auto par défaut, dans le plugin et dans l'app
- [ ] Vérifié sur la Fire TV de l'utilisateur

**Notes techniques**
- `DEFAULTS.trailers = !IN_TV`. Le réglage est propre à chaque appareil (`settingsSync.LOCAL`) ; une TV où il a été allumé à la main le garde.
- `DEFAULTS.tvLowRes = "auto"`, et `MainActivity.lowRes()` lit `optString("tvLowRes", "auto")` : Auto active le rendu 720p sur une TV modeste (4 cœurs ou moins, ou 2 Go de mémoire ou moins : toutes les Fire TV Stick). Une APK 0.8.1 lit encore « off » quand le réglage n'a jamais été changé : il faut la prochaine APK.
- Mesures (T-089) : une bande-annonce dans un aperçu +41 à +62 Mo ; rendu 720p −40 Mo environ (tuiles et images partagées).
- MetaHub n'a pas de fond plus petit que 1920×1080 (`medium` = `large`) : le billboard n'a pas d'image 720p à prendre (contrairement à ce qui était annoncé dans la question).

**Vérification**
- `node --check` ; Kotlin : CI `android-dev.yml`.
- Émulateur Fire TV, compte de l'utilisateur : la télécommande reste 12 s sur une carte, aperçu ouvert, aucune bande-annonce (aucune iframe) ; moteur de rendu 205 Mo (241 Mo avec la bande-annonce avant).

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-093 — Chaque fiche de série quittée restait en mémoire (miniatures d'épisodes en attente)

- **Statut :** À valider (sur la Fire TV de l'utilisateur)
- **Priorité :** P1
- **Cible :** plugin
- **Dépend de :** T-089
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
(voir T-089 : « l'app a planté deux fois d'affilé par manque de mémoire ram »)

**Critères d'acceptation**
- [x] Une fiche quittée ne reste pas en mémoire
- [x] Mesuré : 10 fiches ouvertes et refermées à la télécommande, aucune page détachée gardée
- [ ] Vérifié sur la Fire TV de l'utilisateur (plus de « l'écran de Nathflix s'est arrêté deux fois »)

**Notes techniques**
- **Cause** : les miniatures d'épisodes d'une série (`thumbnail … blurred`, MetaHub `episodes`) sont en `loading="lazy"`. Celles pas encore chargées quand la fiche part gardent une « activité en attente » dans Chromium, qui garde l'image, et par elle toute la page détachée (`route-container`, ≈ 200 éléments, ≈ 5 Mo). Trouvé avec `emu/detroots.mjs` (arbres détachés par racine : 10 `route-container` pour 10 fiches), `emu/leakpath.mjs` (chemin depuis les racines : `C++ roots → Pending activities → HTMLImageElement → … → route-container`) et `emu/detimgs.mjs` (images détachées : `loading=lazy`, `complete=false`). Seules les fiches de séries (épisodes) fuyaient : d'où « rien » en ouvrant les premiers titres par leur adresse (des films).
- `lazyImages` (étape de `sync()`, toutes les pages) : chaque image `loading="lazy"` vue pas encore chargée est retenue par un `WeakRef` (vues une fois : `WeakSet`). 1,5 s après chaque changement d'adresse (Stremio retire l'ancienne page un instant après), `sweep()` retire `src` et `srcset` à celles toujours en attente qui ne sont plus dans la page : le chargement est abandonné et la page peut être libérée.
- Vaut partout (le défaut est dans Chromium) ; la TV est la seule à en souffrir vraiment.

**Vérification**
- `node --check`, traductions.
- Émulateur Fire TV, compte de l'utilisateur (`emu/memleak.mjs` : 10 titres des rangées, OK puis Retour) : éléments 4 117 → 6 807 avant, 4 117 → 4 226 après ; pages détachées gardées 10 → 0 ; moteur de rendu 193 → 213 Mo avant, 198 → 203 Mo après (sans bande-annonce d'aperçu, T-092).

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

### T-094 — Rendu allégé sans effet sur la Fire TV : appliqué dès que le réglage change

- **Statut :** À valider (sur la Fire TV de l'utilisateur : « Page dessinée en 1280×720 »)
- **Priorité :** P1
- **Cible :** tv, plugin
- **Dépend de :** T-051, T-092
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Ouais fait une nouvelle release, et oui la page est dessiné en 4K, même avec le rendu allégé sur TV activé

**Critères d'acceptation**
- [x] Changer « Rendu allégé sur TV » s'applique tout de suite, sans redémarrer l'app
- [x] Sur un écran 4K, rendu allégé actif : page dessinée en 1280×720
- [ ] Vérifié sur la Fire TV 4K de l'utilisateur

**Notes techniques**
- **Cause probable** : `MainActivity` ne lisait le réglage qu'au démarrage (`onCreate`). Sur une Fire TV, rouvrir l'app (après Accueil) ramène celle qui tourne encore, sans la redémarrer : le réglage changé ne prenait jamais effet. Correction (après coup) : l'émulateur ignore `wm size 3840x2160` (seule la densité a changé, `wm density 640`) : son écran est resté en 1920×1080, et la vérification « en 4K » n'en était pas une ; celle de la bascule (1920 ↔ 1280) reste valable. Sur la Fire TV de l'utilisateur, le réglage agit bien (« l'interface est beaucoup moins nette »), mais la mesure à l'écran annonce toujours 4K : voir T-097.
- `MainActivity.applyLowRes()` : met le rendu allégé dans l'état que dit le réglage (`lowRes()`) : écoute la taille de l'écran (`drawSmaller`) ou rend à la WebView tout l'écran (`MATCH_PARENT`, échelle 1). Appelé au démarrage et par `Bridge.saveSetting` quand `tvLowRes` change (`runOnUiThread`).
- `nathflix-tv.js` annonce `tv.lowResLive` ; la description du réglage dit « Prend effet tout de suite. » avec une telle app, « Prend effet au prochain démarrage de l'app. » avec une plus ancienne (texte découpé, traductions ajoutées).

**Vérification**
- `node --check`, traductions ; build de test compilée en local (`assembleDebug`).
- Émulateur, écran simulé en 4K, build locale, compte de l'utilisateur : au démarrage (Auto) 1280×720 ; réglage changé depuis la page (`saveSetting`) : Désactivé → WebView 1920×1080, Activé → 1280×720, Auto → 1280×720, sans redémarrer.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : 0.8.2

### T-095 — Release 0.8.2 (mémoire et démarrage sur TV)

- **Statut :** Terminé
- **Priorité :** P1
- **Cible :** release, tv, plugin
- **Dépend de :** T-090 à T-094
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Ouais fait une nouvelle release, et oui la page est dessiné en 4K, même avec le rendu allégé sur TV activé

**Critères d'acceptation**
- [x] `@version 0.8.2`, section `## 0.8.2` du CHANGELOG, `node tools/bundle-theme.mjs`
- [x] CI verte, tag `v0.8.2`, release avec ses 11 fichiers
- [x] APK signée par la clé de release, versionCode 802, plugin embarqué en 0.8.2

**Notes techniques**
- **Contenu** : T-090 (Accueil organisé chargé au fil de la télécommande), T-091 (affiches réduites sur TV), T-092 (aperçus sans bande-annonce et rendu allégé en Auto par défaut sur TV), T-093 (fiches de séries quittées libérées), T-094 (rendu allégé appliqué tout de suite).
- **Nouvelle APK** : T-092 (Auto par défaut côté app), T-094 (`applyLowRes`, `Bridge.saveSetting`). Worker de synchro inchangé.

**Vérification**
- `node --check` plugin et `nathflix-tv.js`, traductions (`fr-missing.mjs` : les 2 faux positifs connus) : OK.

- CI sur `e01de85` : Cross-platform Windows et Linux, mpv Windows, Android test build (avec R8) verts. Cross-platform macOS jamais lancé : GitHub n'a pas trouvé de machine macOS (« The job was not acquired by Runner of type hosted even after multiple attempts », trois fois en 1 h 30). L'utilisateur a choisi de publier quand même : « Publie quand même, j'utilise pas les version Linux et macOS ».
- Tag `v0.8.2` : run Release 41521560134 ; publish, android, desktop Windows, Linux ×2 et Mac Intel verts ; Mac Apple Silicon en attente d'une machine au moment de clôturer (son .dmg s'ajoutera à la release quand il passera).
- Release : 10 fichiers sur 11 (tout sauf `nathflix-mac-arm64.dmg`), ni brouillon ni pré-release.
- APK : signée `CN=Nathflix, O=lavagnou`, SHA-256 `22ad1773…fe962b` (la même clé que la 0.8.1) ; `com.lavagnou.nathflix`, versionCode 802, versionName 0.8.2 ; plugin embarqué en 0.8.2 (avec `aheadShown`, `lazyImages`) ; `tv.lowResLive` dans `nathflix-tv.js`.

**Livraison**
- Commits : `e01de85` (`0.8.2: …`)
- Release : https://github.com/Lavagnou/dxbi-stremio-android/releases/tag/v0.8.2

### T-096 — TV : le billboard tourne aussi en mode léger et avec la télécommande sur ses boutons

- **Statut :** À valider (sur la Fire TV de l'utilisateur)
- **Priorité :** P2
- **Cible :** plugin
- **Dépend de :** —
- **Créée le :** 2026-11-02
- **Mise à jour :** 2026-11-02
- **Branche :** `main`

**Demande**
> Au passage,sur ma TV le billboard reste bloqué sur le premier slide, ça ne passe jamais au suivant, vérifie ça.

Choix de l'utilisateur :
> Billboard sur TV en mode léger : « Qu'il tourne, en sobre (Recommandé) »

> Télécommande sur ses boutons : « Tourner quand même (Recommandé) »

**Critères d'acceptation**
- [x] Sur TV, le billboard passe au slide suivant même en mode léger (fondu simple, sans le zoom lent, comme toujours sur TV)
- [x] Avec la télécommande sur « Lecture » ou « Plus d'infos », il tourne aussi, et la sélection passe au même bouton du nouveau slide
- [x] Une touche pressée sur ses boutons relance l'attente (il ne change jamais au moment d'appuyer)
- [ ] Vérifié sur la Fire TV de l'utilisateur

**Notes techniques**
- **Cause** : `hero.interval()` rendait 0 en mode léger (Auto l'active sur une Fire TV) : billboard figé (`is-static`), voulu à l'époque pour la clé. Et `tick()` gardait le slide tant qu'un de ses boutons avait la sélection.
- `interval()` : 0 en mode léger sauf sur TV. Le zoom lent reste coupé sur TV (`show()`, thème `.dx-tv .dx-hero-backdrop`).
- `tick()` sur TV : le bouton sélectionné (sa place parmi `.dx-btn`) est retenu ; après `show()`, `followFocus()` le donne au même bouton du nouveau slide 500 ms plus tard (un bouton encore caché ne peut pas prendre la sélection), sauf si la télécommande est allée ailleurs.
- `hero.onKey` (keydown, TV) : sélection dans le billboard → `schedule()` repart de zéro, et la progression du point actif aussi.
- Ailleurs (PC, téléphone) : inchangé.

**Vérification**
- `node --check`, traductions.
- Émulateur Fire TV (mode léger actif), compte de l'utilisateur : slides 4 → 5 → 0 toutes les 9-10 s sans toucher à rien ; sélection mise sur « Plus d'infos » : slides 4 → 5 → 1 → 2, la sélection toujours sur « Plus d'infos » du slide affiché ; Gauche/Droite toutes les 2 s pendant 24 s : reste sur le slide 2, puis passe au 3 une dizaine de secondes après la dernière touche.

**Livraison**
- Commits : voir le journal
- Version / CHANGELOG : à la prochaine release

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
| 2026-11-01 | T-075 | Créée et faite : résumé en français aussi sur les fiches sans note IMDb (`localMeta.titleOf`) |
| 2026-11-01 | T-076 | Créée et faite : lecteur TV comme celui de Netflix (`TvPlayerControls`), Épisodes et Épisode suivant sur TV |
| 2026-11-01 | T-077 | Créée et faite : dégradé de « Qui est-ce ? » dans la couleur d'accent |
| 2026-11-01 | T-078 | Créée : release 0.7.0 ; réservation de T-062 passée à 0.8.0 |
| 2026-11-01 | T-078 | Terminé : release 0.7.0 publiée (11 fichiers, APK 700 signée) |
| 2026-11-01 | T-052 | Abandonné : plugin par adresse (variantes A et B) sans gain sur l'émulateur ; mesure « Plugin chargé en N ms » gardée |
| 2026-11-01 | T-053 | Abandonné : plugin minifié (−30 %) sans gain de chargement sur l'émulateur |
| 2026-11-01 | — | Test mpv (Windows) en échec sur `2f13276` : l'image de l'Accueil prise pendant un fondu du billboard (238 points changés au lieu de 0) ; relancé, il passe. `tools/mpv-test.mjs` remesure l'Accueil 2,5 s plus tard s'il bouge |
| 2026-11-01 | T-055 | Abandonné : le profil des touches montre que les mesures de `keyNav` ne coûtent presque rien ; deux pistes essayées sans gain ; compteur `keyNav` et `--profile` du banc gardés |
| 2026-11-01 | T-056 | Abandonné : observateurs du preload sans coût mesurable (0 appel au repos) ; iframe réutilisée écartée (historique de Retour) |
| 2026-11-01 | T-049 | Référence Fire TV 4K (photos, 0.7.0) : démarrage 11414 ms |
| 2026-11-01 | T-057 | Abandonné : app PC au repos, fenêtre transparente ou opaque, GPU ~0 % et processeur identiques |
| 2026-11-01 | T-079 | Créée et faite : mesure à l'écran sur la dernière minute, part de Nathflix par touche ; les lectures de `ctx` sont celles de Stremio au démarrage |
| 2026-11-01 | T-080 | Créée et faite : OK maintenu ouvre le menu d'un titre et n'appuie plus en boucle, 3 points masqués ; Retour sur la page d'un titre non reproduit |
| 2026-11-01 | T-081 | Créée et faite : sur TV, genres et acteurs de la page d'un titre plus sélectionnables (`keyNav.SKIP`) |
| 2026-11-01 | T-081 | Bouton « Partager » retiré de la page d'un titre (`hideButton.noShare`) |
| 2026-11-01 | T-049 | Étape 3 faite (mises à jour vérifiées 5 s après la page) ; étape 2 essayée et retirée ; À valider avec la photo d'après la prochaine release |
| 2026-11-01 | T-051 | Créée et faite : réglage « Rendu allégé sur TV » (720p agrandi), coupé par défaut ; À valider avec des photos de la Fire TV |
| 2026-11-02 | T-081 | Genres et acteurs à nouveau sélectionnables, sauf en mode léger (demande de l'utilisateur) ; filtre moins coûteux |
| 2026-11-02 | T-082 | Créée et faite : défilement allégé (`preview.marked`, barres gardées, `scrollKind`), JS de Nathflix −25 à −40 % en descendant les rangées |
| 2026-11-02 | T-083 | Créée et faite : une série de Continuer de regarder ouvre sa page (« Reprendre ») |
| 2026-11-02 | T-084 | Créée et faite : billboard écrasé sur un Accueil réorganisé (`flex-shrink: 0`) |
| 2026-11-02 | T-085 | Créée et faite : ordre de l'Accueil envoyé en 2 s, synchro en ouvrant l'Accueil et toutes les 2 min, réception même après un envoi refusé ; cause exacte chez l'utilisateur non trouvée |
| 2026-11-02 | T-058 | Abandonné : l'affûtage du thème coûte ~30 ms par démarrage (cache) et ~0,5 s une fois par mise à jour |
| 2026-11-02 | T-059 | Abandonné : le temps de Nathflix est sur le chemin de la touche, rien à ordonnancer ; le correctif trouvé est T-082 |
| 2026-11-02 | T-060 | Étude faite : Stremio Web vient déjà du cache de son service worker ; recommandation : ne pas l'embarquer (décision de l'utilisateur) |
| 2026-11-02 | T-060 | Terminé : l'utilisateur est d'accord pour ne pas embarquer Stremio Web |
| 2026-11-02 | T-062 | En cours : release 0.8.0 demandée par l'utilisateur |
| 2026-11-02 | T-062 | Terminé : release 0.8.0 publiée (11 fichiers, APK 800 signée par la clé de release, plugin 0.8.0) |
| 2026-11-02 | T-086 | Créée et faite : bouton Bande-annonce dans la feuille d'un titre, appui long sur une carte sans menu de Stremio qui ouvre la feuille |
| 2026-11-02 | T-087 | Créée et faite : la vidéo reste sous l'image du titre tant que YouTube montre ses boutons (4,3 s après chaque lecture, et en pause) |
| 2026-11-02 | T-088 | En cours : release 0.8.1 demandée par l'utilisateur |
| 2026-11-02 | T-088 | Terminé : release 0.8.1 publiée (11 fichiers, APK 801 signée par la clé de release, plugin 0.8.1) |
| 2026-11-02 | T-089 | Étude faite : page 200-300 Mo, Accueil organisé = 66 catalogues et 612 affiches d'un coup, aperçus +40-60 Mo, +5 Mo par fiche ; choix de l'utilisateur notés |
| 2026-11-02 | T-090 | Créée et faite : Accueil organisé chargé au fil de la télécommande (6 catalogues au démarrage au lieu de 66), jamais les rangées masquées |
| 2026-11-02 | T-091 | Créée et faite : affiches TMDB et TheTVDB en copie réduite sur TV, images d'aperçu décodées d'avance 24 → 6 |
| 2026-11-02 | T-092 | Créée et faite : sur TV, aperçus sans bande-annonce et rendu allégé en Auto par défaut (choix de l'utilisateur) |
| 2026-11-02 | T-093 | Créée et faite : les miniatures d'épisodes en attente gardaient chaque fiche de série quittée en mémoire (≈ 5 Mo chacune) ; abandonnées après le départ de la page |
| 2026-11-02 | T-094 | Créée et faite : le rendu allégé ne se lisait qu'au démarrage (jamais sur une Fire TV, qui reprend l'app) ; appliqué dès que le réglage change |
| 2026-11-02 | T-095 | En cours : release 0.8.2 demandée par l'utilisateur |
| 2026-11-02 | T-095 | Terminé : release 0.8.2 publiée (11 fichiers, APK 802 signée par la clé de release, plugin 0.8.2) |
| 2026-11-02 | T-096 | Créée et faite : sur TV, le billboard tourne en mode léger et avec la télécommande sur ses boutons (choix de l'utilisateur) |
| 2026-11-01 | T-074 | Fait : release v0.6.1 publiée (11 fichiers, APK signée vérifiée, versionCode 601) |
| 2026-11-01 | T-074 | Release 0.6.1 en cours : version montée, CHANGELOG |
| 2026-11-01 | T-073 | Créée et faite : synchro des profils fiabilisée (focus, envois gardés, fusion titre par titre, envoi rapide), clés TMDB et MDBList synchronisées ; banc à trois appareils 21/21 |
| 2026-11-01 | T-072 | Créée et faite : titres, résumés, genres et bandes-annonces en français par TMDB (`localMeta`), anglais en repli ; à valider avec la vraie clé |
| 2026-11-01 | T-071 | Créée et faite : Retour depuis un épisode ramène à la série (`episodeBack`) ; à valider sur la Fire TV |
| 2026-11-01 | T-070 | Créée et faite : « Qui est-ce ? » au démarrage seulement, promotions de Stremio supprimées, contour de la carte d'aperçu ; sélection sur `#app`, rangées préparées au démarrage ; à valider sur la Fire TV |
| 2026-11-01 | T-069 | Créée et faite : carte d'aperçu et bande-annonce sur TV ; Qui est-ce ?, rangées masquées (T-054), Cette semaine, glissade, panneau d'informations, barre du haut ; à valider sur la Fire TV |
| 2026-11-01 | T-068 | Créée et faite : émulateur Fire TV ; sélection TV (contour jamais coupé, point de départ, déplacements), défilement fluide, loupe, « Aucun flux trouvé » ; à valider sur la Fire TV |
| 2026-11-01 | T-067 | Créée et faite : lignes de « Organiser l'Accueil » écrasées (`flex: none`) |
| 2026-10-31 | T-061 | Fait : release v0.6.0 publiée (11 fichiers, APK signée vérifiée, versionCode 600) |
| 2026-10-31 | T-061 | Release 0.6.0 en cours : version montée, CHANGELOG ; Worker redéployé (clé `settings`) |
| 2026-10-31 | T-066 | Créée et faite : réglages synchronisés avec le compte (clé `settings`, le plus récent gagne, réglages d'appareil exclus) ; testée sur deux appareils simulés avec le Worker en local |
| 2026-10-31 | T-065 | Créée et corrigée : « Lecture » sur les pages de films sans vidéo dans l'adresse ; `navigate()` relance la boucle de Nathflix |
| 2026-10-31 | T-049 | Temps de démarrage dans la mesure à l'écran (`Bridge.launchedAt`) ; bloqué en attendant les photos de la Fire TV |
| 2026-10-31 | T-050 | Abandonné : l'index des épisodes coûte très peu, même cache vidé ; le départ décalé déplaçait le travail sur les touches. Banc : `--drop` |
| 2026-10-31 | T-048 | Abandonné : le thème s'applique au même moment sans `@import`, à chaud comme à froid. Banc : `themeMs`, `fontMs`, `--cold` |
| 2026-10-31 | T-047 | Fait : plus de grandes ombres floues sur TV et en mode léger, effets TV aussi en mode léger (`html.dx-light`) ; dessin −22 % sur TV, −29 % en mode léger sur téléphone |
| 2026-10-31 | T-054 | Avancée et faite : Accueil TV chargé par paquets de 6 rangées devant la télécommande (7 000 → 4 600 éléments, tâches longues 14,9 → 12,6 s, pire 1,1 → 0,6 s). Banc : `--trace`, `--descend` |
| 2026-10-31 | T-046 | Fait : banc connecté au compte de l'utilisateur (`--login`, `--vs`) ; au repos Nathflix ne fait rien, la boucle principale coûte surtout dans la mesure des flèches des rangées → mesurées seulement au survol (boucle 2,48 → 1,14 s sur le parcours) |
| 2026-10-31 | T-064 | Créée et corrigée : cache noir pendant que « Qui est-ce ? » se prépare (Accueil visible 0 ms au lieu de ≈ 0,4 s sur PC) ; à valider sur la Fire TV |
| 2026-10-31 | T-045 | Abandonné : une écriture de moins par visite sur le banc, et une perte possible si le renderer plante ; à reprendre si la Fire TV montre beaucoup d'enregistrements. Banc : `switching`, `--loss-test` |
| 2026-10-31 | T-044 | Abandonné : le filtre par profil coûte quelques ms par minute (27 → 20 ms en TV bridée, 8 → 10 ms sur PC). Gardé : son chronomètre (`filterMs`) |
| 2026-10-31 | T-043 | Abandonné : « Se termine à » et calendrier ne lisent le cœur que pendant `sync()`, qui ne tourne pas au repos ; mémo sans gain mesurable (9 → 8 lectures sur le parcours). Banc : `--dwell`, reprise des runs |
| 2026-10-31 | T-042 | Fait : `coreEvents` (enveloppe de `window.onCoreEvent`), `coreMeta` ne lit plus que les modèles changés de la route : lectures divisées par deux sur le parcours |
| 2026-10-31 | T-063 | Fait : bouton « Lecture » aligné sur ses voisins (PC, TV) |
| 2026-10-31 | T-041 | Fait : banc `tools/perf-bench.mjs`, compteurs `perf` et mesure de référence (TV et PC). Chantier performance lancé (`docs/perf/`), T-061 et T-062 réservées aux releases 0.6.0 et 0.7.0 |
| 2026-10-30 | T-040 | Fait : release v0.5.2 publiée (11 fichiers, APK signée vérifiée) ; T-039 à valider sur la Fire TV |
| 2026-10-30 | T-039, T-040 | Audit de performance (rapport) puis corrections : CSS allégé et `sharpen` partout, billboard, télécommande, mode léger, mesure à l'écran ; testées (Playwright avant/après). Release 0.5.2 en cours |
| 2026-10-30 | T-038 | Fait : release v0.5.1 publiée (dmg Mac x64 refait après une panne réseau du runner) |
| 2026-10-30 | T-038 | Créée : release 0.5.1 ; version montée, en cours (CI, puis tag) |
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
