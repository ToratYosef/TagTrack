package com.tagtrack.app.feature.detail

import com.tagtrack.app.core.domain.model.Item

data class ItemDetailUiState(
    val item: Item? = null,
    val isLoading: Boolean = true,
    val showDeleteConfirmation: Boolean = false,
)
