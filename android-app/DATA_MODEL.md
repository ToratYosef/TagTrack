# Data model

All records are scoped per Firebase Auth user ID. The `nfcUid` is immutable and unique within a user namespace.

## Item

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `String` | UUID primary key |
| `nfcUid` | `String` | Uppercase hex, no separators. Immutable. |
| `name` | `String` | Required. |
| `type` | `String` | Garment category (e.g., shirt, pants). |
| `color` | `String?` | Optional. |
| `brand` | `String?` | Optional. |
| `size` | `String?` | Optional. |
| `tags` | `List<String>` | Custom user tags. |
| `notes` | `String?` | Freeform notes. |
| `photoUri` | `String?` | Local URI (MediaStore/Photo Picker). |
| `photoCloudUrl` | `String?` | Firebase Storage download URL (when cloud backup enabled). |
| `createdAt` | `Instant` | Creation timestamp. |
| `lastWorn` | `Instant?` | Updated via “Record worn”. |

## Outfit

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `String` | UUID primary key. |
| `name` | `String` | Required. |
| `itemIds` | `List<String>` | References `Item.id`. |
| `createdAt` | `Instant` | Creation timestamp. |

## Settings (Datastore)

| Key | Type | Notes |
| --- | --- | --- |
| `language` | `String` | `en` or `he`. |
| `theme` | `Int` | 0 system, 1 light, 2 dark. |
| `authenticated` | `Int` | 0/1 flag, mirrors auth state for offline gating. |
| `cloud_backup` | `Int` | 0/1 toggle for Storage uploads. |

## Constraints

- `nfcUid` must be unique per user and never editable after creation.
- Deleting an item frees its UID for reuse (client + server enforcement).
- To reuse a tag, the user must delete the linked item first; no reassignment path.
