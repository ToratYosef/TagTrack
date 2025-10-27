package com.tagtrack.app.core.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("nfc_uid") val nfcUid: String,
    val name: String,
    val type: String,
    val color: String?,
    val brand: String?,
    val size: String?,
    val tags: List<String>,
    val notes: String?,
    @ColumnInfo("photo_uri") val photoUri: String?,
    @ColumnInfo("photo_cloud_url") val photoCloudUrl: String?,
    @ColumnInfo("created_at") val createdAt: Instant,
    @ColumnInfo("last_worn") val lastWorn: Instant?,
)
