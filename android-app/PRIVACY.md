# Privacy & data handling

TagTrack is designed as a private wardrobe. No social sharing or public feeds exist.

## Local storage

- Items, outfits, and settings are cached locally in Room and Datastore.
- Photos stay on-device unless the user opts into cloud backup.
- Users can clear cached data from Settings → Advanced.

## Cloud services (optional)

- Firebase Auth stores email/password credentials. Magic links use Firebase Dynamic Links.
- Firestore stores item/outfit metadata under `/users/{uid}/items` and `/users/{uid}/outfits`.
- Firebase Storage stores photos under `/users/{uid}/photos/{itemId}` when cloud backup is enabled.
- Crashlytics/Analytics are opt-in; disable toggles to stop collection.

## Security controls

- Firestore rules restrict reads/writes to the authenticated user’s subtree.
- Cloud Functions (recommended) enforce unique `nfcUid` values server-side.
- Storage rules restrict access to `/users/{uid}` path and require authentication.
- Signed download URLs should be time-bounded when sharing outside the app.

## Data export & deletion

- Users can export CSV/JSON via Settings → Data export (WorkManager job).
- Deleting an item removes its cloud record and frees the tag for reuse.
- Clearing cloud backup removes Storage objects for the user.

## Compliance & disclosures

- Provide a privacy policy link in-app (Settings → About).
- Complete Google Play Data Safety forms referencing the above data flows.
