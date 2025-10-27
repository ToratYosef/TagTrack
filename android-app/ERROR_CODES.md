# Error codes & UI copy

| Code | Trigger | User-facing message |
| --- | --- | --- |
| `NFCSCAN_DENIED` | NFC permission denied or adapter disabled | Banner prompts the user to enable NFC in system settings. |
| `NFC_UNAVAILABLE` | Device lacks NFC hardware | Read-only mode — Add Item button disabled, explanatory dialog. |
| `NFC_TIMEOUT` | Scan session expired without reading | Inline text prompts to retry scanning. |
| `DUPLICATE_UID` | Scanned tag already linked | Dialog: “Tag already linked to {ItemName}. Open item / Cancel.” |
| `NETWORK_ERROR` | Firestore write failed | Persistent snackbar with “Retry” action. |
| `STORAGE_UPLOAD_FAILED` | Firebase Storage upload failure | Snackbar linking to retry upload from item detail. |
| `PERMISSION_DENIED` | Camera/Photo picker permissions missing | Dialog with deep link to app settings. |
