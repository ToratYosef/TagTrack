package com.tagtrack.app.core.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo("item_ids") val itemIds: List<String>,
    @ColumnInfo("created_at") val createdAt: Instant,
)
