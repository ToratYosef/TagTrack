# Contributing to TagTrack

We welcome contributions that improve accessibility, NFC UX, or documentation. Before opening a pull request:

1. Fork the repository and create a branch per feature.
2. Run `npm install` and `npm run prepare` to set up Husky hooks.
3. Ensure your changes pass linting, type checks, unit tests, and Detox smoke tests.
4. Update README/docs if behavior changes.
5. Follow the commit style `feat:`, `fix:`, `docs:`, etc.

## Development tips
- Use Expo Go for quick iteration, but switch to a dev client for NFC testing.
- Mock Firebase in unit tests; do not commit credentials.
- Keep UI accessible (WCAG AA) and provide English + Hebrew translations for new strings.
