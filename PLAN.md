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
| — | *Aucune tâche pour le moment* | | | | | |

---

## Tâches

*Aucune tâche pour le moment. Les fiches s'ajoutent ici, la plus récente en bas, à partir du modèle ci-dessous.*

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
| — | | | |

---

## Questions ouvertes (globales)

Questions pour l'utilisateur qui ne concernent pas une seule tâche.

- —

---

## Journal

Une ligne par changement d'état ou par session de travail, la plus récente en haut.

| Date | Tâche | Événement |
|---|---|---|
| 2026-10-28 | — | Création de PLAN.md (aucune tâche pour le moment) |
