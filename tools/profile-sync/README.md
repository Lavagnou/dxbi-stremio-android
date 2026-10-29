# Synchronisation des profils (Cloudflare Worker + D1)

Ce petit serveur garde les profils Nathflix de chaque personne et les donne à tous ses appareils :
- profils, avatars et codes (chiffrés) ;
- rangées de l'Accueil, titres masqués ;
- « Continuer de regarder » retirés ;
- propriétaires de la bibliothèque, titres autorisés aux enfants, code parental ;
- statistiques par profil.

Il n'y a pas de compte à créer : chacun est reconnu par son **compte Stremio**.

- L'app envoie sa clé de session Stremio. Le Worker demande à Stremio à quel compte elle appartient (`POST https://api.strem.io/api/getUser`, comme les apps de Stremio), puis range les données sous l'identifiant de ce compte.
- **La clé n'est jamais enregistrée**, ni dans la base ni dans les journaux. Le Worker se souvient seulement, en mémoire et pour 10 minutes, du compte qui correspond à l'empreinte (SHA-256) d'une clé.
- Les caches (fiches, classifications d'âge, épisodes…) et les réglages de l'appareil ne sont pas envoyés.

## Limites (pour rester dans l'offre gratuite)

| | Limite |
|---|---|
| Taille par compte | 1 Mo |
| Taille d'une valeur | 100 Ko (au-delà, les stats d'un profil restent sur l'appareil) |
| Entrées par requête | 200 |
| Requêtes | 60 par minute et par adresse, 30 par minute et par compte |

**Côté appareil**, une synchro a lieu :
- au démarrage ;
- au retour au premier plan, au plus une fois par minute ;
- 20 s après un changement (60 s au plus).

Seules les données modifiées sont écrites. L'offre gratuite de Cloudflare permet 100 000 requêtes par jour, et pour D1, 100 000 lignes écrites et 5 millions de lignes lues par jour : une famille et des amis en sont très loin.

## Mise en place (une seule fois, environ 10 minutes)

Il faut un ordinateur (Windows, Mac ou Linux).

1. **Créer un compte Cloudflare gratuit** : https://dash.cloudflare.com/sign-up, puis confirmer l'e-mail reçu. Aucune carte bancaire n'est demandée pour l'offre gratuite.
2. **Installer Node.js** (version « LTS ») depuis https://nodejs.org, avec les options par défaut.
3. **Ouvrir un terminal dans ce dossier** (`tools/profile-sync` du dépôt) :
   - Windows : clic droit sur le dossier → *Ouvrir dans le terminal* ;
   - Mac : clic droit → *Nouveau terminal au dossier*.
4. **Se connecter à Cloudflare** :

   ```
   npx wrangler login
   ```

   Accepter l'installation de wrangler si elle est proposée. Le navigateur s'ouvre : cliquer sur *Allow*.
5. **Créer la base** :

   ```
   npx wrangler d1 create nathflix-profiles
   ```

   La commande affiche un `database_id` (une suite de lettres et de chiffres). Ouvrir `wrangler.toml` et le coller à la place de `REPLACE-WITH-YOUR-DATABASE-ID`.
6. **Créer la table** :

   ```
   npx wrangler d1 execute nathflix-profiles --remote --file=schema.sql
   ```

7. **Mettre le Worker en ligne** :

   ```
   npx wrangler deploy
   ```

   Il affiche son adresse, par exemple `https://nathflix-profiles.ton-nom.workers.dev`.
8. **Brancher Nathflix dessus** : mettre cette adresse suivie de `/sync` dans `SYNC_ENDPOINT`, en haut de la partie « Report a problem » de `nathflix.plugin.js` :

   ```js
   const SYNC_ENDPOINT = "https://nathflix-profiles.ton-nom.workers.dev/sync";
   ```

   Ensuite, faire une release : le plugin se met à jour seul. Tant que `SYNC_ENDPOINT` est vide, rien n'est synchronisé et rien ne change.

Après une modification de `worker.js`, il suffit de relancer `npx wrangler deploy`.

## Voir ou effacer des données

Tableau de bord Cloudflare → *Storage & Databases* → *D1 SQL Database* → *nathflix-profiles* → *Console*.

- Combien de comptes : `SELECT COUNT(DISTINCT account) FROM entries;`
- Effacer les données d'un compte (son identifiant Stremio) : `DELETE FROM entries WHERE account = 'identifiant';`

Chacun peut aussi couper la synchro sur ses appareils : Réglages Nathflix → Accueil → *Synchroniser les profils avec mon compte Stremio*.

## Tester sans Cloudflare

```
node tools/profile-sync/test.mjs
```

Il fait tourner le vrai `worker.js` avec la base SQLite de Node (`d1-sqlite.mjs`) à la place de D1, et une réponse simulée de Stremio. Il faut Node 22 ou plus.

Pour essayer le Worker en local avec Cloudflare : `npx wrangler dev`, puis des requêtes `POST` sur `http://127.0.0.1:8787/sync`.
