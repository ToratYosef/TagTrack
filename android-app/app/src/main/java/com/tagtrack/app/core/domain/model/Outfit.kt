package com.tagtrack.app.core.domain.model

import java.time.Instant

data class Outfit(
    val id: String,
    val name: String,
    val itemIds: List<String>,
    val createdAt: Instant,
)
