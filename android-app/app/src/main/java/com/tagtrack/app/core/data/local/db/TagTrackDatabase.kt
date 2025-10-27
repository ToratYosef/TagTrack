package com.tagtrack.app.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ItemEntity::class, OutfitEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class TagTrackDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun outfitDao(): OutfitDao
}
