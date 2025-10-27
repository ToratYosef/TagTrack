# Architecture

TagTrack uses a clean architecture separation with MVVM view models and repository abstractions.

## Modules & layers

- **core/common** — cross-cutting types (app state, theming).
- **core/data** — repository implementations (Room, Firestore, Firebase Auth) and persistence modules.
- **core/domain** — pure Kotlin models and repository interfaces used by features.
- **core/nfc** — NFC reader mode helpers and adapter state observers.
- **core/designsystem** — theme and typography definitions.
- **feature/** — presentation logic per feature. Each feature exposes a UI state, a Hilt-enabled ViewModel, and Compose UI.
- **navigation** — a single `NavHost` wiring destinations.

## Data flow

1. UI events are emitted from Compose screens to their feature ViewModel.
2. ViewModels call domain repositories (interfaces) to mutate or read data.
3. Repository implementations orchestrate Room (offline cache) and Firestore (cloud). Writes go to Room first, then sync to Firestore.
4. NFC scans are handled by `NfcReaderManager.enableReaderMode`, which emits normalized uppercase hexadecimal UIDs.
5. Duplicate protection happens before writes: the `AddItemViewModel` queries `ItemRepository.getItemByUid` and blocks creation on conflicts.

## Persistence

- **Room**: `TagTrackDatabase` stores `ItemEntity` and `OutfitEntity`, serializing tags (`List<String>`) and timestamps.
- **Firestore**: Data mirrors the local schema. Cloud Functions/Rules (not included) must enforce uniqueness of `nfcUid` per user.
- **Datastore**: Stores language, theme, authentication flags, and cloud-backup preference.
- **WorkManager**: Ready for background sync / upload tasks (scheduling scaffolding only).

## Firebase

- Auth: Email/password + optional email link (`sendMagicLink`).
- Firestore: per-user collections for items and outfits. Client code writes to `/users/{uid}/items/{itemId}` (set via security rules).
- Storage: Optional per-user folder for photo uploads (`/users/{uid}/photos/{itemId}`) guarded by Storage rules.

## Navigation

The navigation graph enforces the following entry points:

- `onboarding` → `auth` → `home` once authenticated.
- `home` hosts browse/search with access to `add_item`, `scan`, `item_detail`, and `settings`.
- `add_item` always starts in NFC scan mode. When a duplicate is detected, navigation can jump directly to `item_detail`.
- `scan` is a reader-first shortcut to detail screens. Missing items offer re-registration.

## Accessibility & localization

- Compose theme flips to RTL for Hebrew via `LocalLayoutDirection`.
- Material 3 components supply accessible defaults and respect dynamic type.
- Strings are localized in `values/strings.xml` and `values-he/strings.xml`.

## Testing & CI

- JVM tests cover critical ViewModel logic (`AddItemViewModelTest`).
- Instrumentation tests use Compose Testing with the Hilt runner (`AddItemScreenTest`).
- GitHub Actions (not provided) should run lint, unit tests, UI tests, and assembleRelease.
