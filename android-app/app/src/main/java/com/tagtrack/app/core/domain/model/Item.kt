package com.tagtrack.app.core.domain.model

import java.time.Instant

data class Item(
    val id: String,
    val nfcUid: String,
    val name: String,
    val type: String,
    val color: String?,
    val brand: String?,
    val size: String?,
    val tags: List<String>,
    val notes: String?,
    val photoUri: String?,
    val photoCloudUrl: String?,
    val createdAt: Instant,
    val lastWorn: Instant?,
)
