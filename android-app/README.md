# TagTrack Android

TagTrack is a native Android wardrobe manager built with Kotlin and Jetpack Compose. Every clothing item is linked to a physical NFC tag—there is no way to enter or edit tag IDs manually. The application is designed for a single private user with optional cloud sync for photos.

## Project structure

The project follows a clean architecture split into three layers:

- **core** — shared domain models, repositories, NFC utilities, and data access.
- **feature** — UI state, view models, and Compose screens for individual features (onboarding, auth, add item, scan, home, detail, settings).
- **navigation** — central navigation graph wiring screens together.

```
app/
  core/
  feature/
  navigation/
```

### Technology choices

- **Language & UI**: Kotlin, Jetpack Compose Material 3.
- **DI**: Hilt.
- **Persistence**: Room for offline cache.
- **Cloud**: Firebase Auth, Firestore, Storage (optional for photos).
- **Media**: CameraX (scaffolding), Android Photo Picker, Coil for rendering.
- **NFC**: Reader mode only, normalized uppercase hex UIDs.
- **Work manager**: Reserved for sync tasks (not yet wired).

## Getting started

1. Open the project in Android Studio Koala+.
2. Create a Firebase project and add `google-services.json` to `app/`.
3. Configure Firebase Auth (email/password, email link), Firestore, and Storage. See `ARCHITECTURE.md` for rule samples.
4. Enable NFC, Camera, and Photo Picker permissions in your test device settings.
5. Build and run: `./gradlew :app:assembleDebug` or from Android Studio.

### Local testing

```bash
./gradlew lint
./gradlew test
./gradlew connectedDebugAndroidTest
```

Instrumented tests require an emulator or device with Play Services (for Firebase). The Hilt test runner is registered as the default instrumentation runner.

### Release builds

- Update versionCode/versionName in `app/build.gradle.kts`.
- Use Play App Signing; the release keystore should only sign the upload bundle.
- Build the release bundle: `./gradlew :app:bundleRelease`.

## NFC workflow

- Adding an item starts in reader mode. The UI stays on the scan screen until a tag is tapped.
- Detected UIDs are normalized (uppercase, no separators) and checked for duplicates locally before the item form opens.
- Duplicate scans show a dialog to open the existing item; no edit path can change the UID.
- Deleting an item frees the tag for reuse.

## Accessibility & localization

- English and Hebrew translations ship by default. Hebrew switches Compose to RTL automatically.
- Compose components respect dynamic type and include content descriptions where needed.
- Colors meet WCAG AA contrast through Material 3 defaults.

## Optional cloud sync

Cloud backup for photos is opt-in. When enabled, uploads use Firebase Storage; WorkManager is the staging point for retries. Disable the toggle to keep photos local.

## Contributing

1. Fork and clone the repository.
2. Create feature branches off `main`.
3. Run the test suite before opening a PR.
4. Follow the Kotlin style guide and keep feature modules isolated.

Please review `PRIVACY.md` and `DATA_MODEL.md` for more details on data handling.
