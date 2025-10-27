package com.tagtrack.app.feature.additem

data class AddItemUiState(
    val isWaitingForScan: Boolean = true,
    val scannedUid: String? = null,
    val name: String = "",
    val type: String = "",
    val color: String = "",
    val brand: String = "",
    val size: String = "",
    val tags: String = "",
    val notes: String = "",
    val photoUri: String? = null,
    val error: AddItemError? = null,
    val isSaving: Boolean = false,
    val isDuplicate: Boolean = false,
    val duplicateItemName: String? = null,
    val duplicateItemId: String? = null,
)

enum class AddItemError {
    NFC_DUPLICATE,
    VALIDATION,
    NETWORK,
}
