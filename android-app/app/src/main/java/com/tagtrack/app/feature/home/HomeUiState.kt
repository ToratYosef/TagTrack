package com.tagtrack.app.feature.home

import com.tagtrack.app.core.domain.model.Item

data class HomeUiState(
    val items: List<Item> = emptyList(),
    val query: String = "",
    val filterType: String? = null,
    val filterColor: String? = null,
    val filterTags: Set<String> = emptySet(),
)
