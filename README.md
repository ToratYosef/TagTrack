# TagTrack

TagTrack is a React Native (Expo) wardrobe manager that links every clothing item to a physical NFC tag. Adding an item always begins with a scan, ensuring NFC UIDs are immutable and unique per user. The app ships with Firebase backend integrations, opt-in cloud backup, and TypeScript-first architecture.

## Table of contents
- [Features](#features)
- [Architecture](#architecture)
- [Getting started](#getting-started)
- [Firebase setup](#firebase-setup)
- [Running the app](#running-the-app)
- [Testing](#testing)
- [CI/CD](#cicd)
- [Data model](#data-model)
- [Security rules](#security-rules)
- [Wireframes](#wireframes)
- [API reference](#api-reference)
- [Troubleshooting](#troubleshooting)

## Features
- NFC-first add flow with duplicate prevention and UID immutability
- Firebase Auth (email/password + magic link), Firestore, and Storage integration
- Opt-in cloud backup and analytics toggles with GDPR-conscious defaults
- Multilingual (English + Hebrew) with RTL support and accessibility-friendly UI
- Offline read-only mode with clear messaging when actions are blocked
- Automated unit tests, Detox E2E scaffolding, and GitHub Actions workflows

## Architecture
```
.
├── App.tsx                     # Entry point and navigation stack
├── src
│   ├── components              # Shared UI components
│   ├── hooks                   # Reusable hooks (reserved for future work)
│   ├── screens                 # Feature screens (Home, AddItem, Scan, etc.)
│   ├── services                # Firebase, NFC, and API abstractions
│   ├── state                   # React Context providers for auth/settings
│   ├── styles                  # Theming helpers
│   ├── types                   # Shared TypeScript types
│   ├── utils                   # Helper utilities (error mapping, etc.)
│   └── i18n                    # Translation bootstrap and locale files
├── __tests__                   # Jest unit/UI tests
├── e2e                         # Detox config and E2E scenarios
├── .github/workflows           # CI jobs (lint/test/build)
└── scripts                     # Tooling helpers (reserved)
```

State is handled with React Context + React Query to keep network cache consistent. Firebase functions sit behind typed services so alternative backends can be swapped with minimal friction.

## Getting started
1. Install the Expo CLI and dependencies:
   ```bash
   npm install --global expo-cli eas-cli
   npm install
   ```
2. Copy the environment template:
   ```bash
   cp .env.example .env
   ```
3. Fill in Firebase credentials in `.env` or Expo Config (`app.config.ts`).
4. Install iOS/Android prerequisites per Expo docs.

## Firebase setup
1. Create a Firebase project with Auth, Firestore, Storage, and (optional) Analytics.
2. Enable email/password and passwordless sign-in in **Authentication > Sign-in method**.
3. Create Firestore collections by running the app once; security rules enforce per-user scoping.
4. Storage rules must restrict access to `user_photos/{userId}/**`.
5. Deploy Cloud Functions (see `functions/` placeholder) to enforce unique NFC UIDs server-side.

### Firestore rules (sample)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### Storage rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /user_photos/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Running the app
```bash
# Install dependencies (needed once per clone)
npm install

# Start the Expo dev server
npm run start

# Run on Android device/emulator
npm run android

# Run on iOS simulator/device
npm run ios
```

NFC requires a physical device. For development without NFC hardware, enable the debug toggle (`EXPO_PUBLIC_FEATURE_ENABLE_DEBUG_NFC_SIMULATION=true`) to simulate scans (never enable in production builds).

## Testing
- **Unit/UI tests**: `npm test`
- **Coverage**: `npm run test:coverage`
- **Detox E2E**: `npm run e2e`

Detox requires building binaries via EAS. Provide artifacts under `bin/` for local runs or integrate with EAS Build.

## CI/CD
GitHub Actions workflows cover lint, typecheck, tests, and Expo Application Services builds.

- `.github/workflows/ci.yml`: lint + test + typecheck
- `.github/workflows/build.yml`: kicks off EAS preview builds on the `staging` branch

Configure repository secrets:
- `EXPO_TOKEN`
- `FIREBASE_SERVICE_ACCOUNT`
- `SENTRY_AUTH_TOKEN` (optional)

## Data model
- `users/{userId}` – profile + privacy settings
- `users/{userId}/items/{itemId}` – wardrobe items keyed by immutable `nfcUID`
- `users/{userId}/outfits/{outfitId}` – outfit groupings
- `users/{userId}/logs/{logId}` – audit events

Each item document:
```json
{
  "name": "Navy Wool Blazer",
  "type": "Outerwear",
  "color": "Navy",
  "brand": "Acme",
  "size": "M",
  "tags": ["Winter", "Formal"],
  "notes": "Dry clean only",
  "photoURL": "https://...",
  "nfcUID": "04A224F9C53280",
  "createdAt": "2024-05-09T00:00:00Z",
  "lastWorn": "2024-05-11T00:00:00Z"
}
```

## Security rules
- All reads/writes restricted to authenticated user’s subtree
- Cloud Function `enforceUniqueNfcUid` deletes conflicting documents and returns a failure payload
- Storage restricted per user folder
- Audit logs written on add/edit/delete actions

## Wireframes
Static PNG wireframes are located in [`docs/wireframes`](docs/wireframes) (placeholder). Replace with Figma exports when available.

## API reference
| Function | Description |
|----------|-------------|
| `scanNfcAndStartAdd()` | Prompts for NFC scan, returns normalized UID |
| `createItem(payload)` | Saves item metadata with immutable `nfcUID` |
| `getItemByNfc(uid)` | Fetches item by NFC UID |
| `listItems()` | Lists latest items ordered by creation date |
| `deleteItem(itemId)` | Deletes item, freeing NFC tag |
| `recordWorn(itemId)` | Updates `lastWorn` timestamp |
| `exportItems(format)` | Returns serialized wardrobe data |

## Troubleshooting
- **NFC not supported**: Device must have NFC hardware. App automatically enters view-only mode.
- **Duplicate tag**: Cloud Function rejects duplicates. User is redirected to the existing item.
- **Photo upload failures**: Retry from Settings > Pending sync.
- **Expo + NFC on iOS**: Requires Expo prebuild (`npx expo prebuild`) to inject native modules.
- **`expo: not found` when running npm scripts**: Install dependencies with `npm install` or use the global Expo CLI directly via `npx expo <command>`.

## Additional resources
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- [docs/QA.md](docs/QA.md) *(placeholder manual QA checklist)*
