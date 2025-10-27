package com.tagtrack.app.core.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Query("SELECT * FROM outfits")
    fun observeOutfits(): Flow<List<OutfitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(outfit: OutfitEntity)

    @Update
    suspend fun update(outfit: OutfitEntity)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun delete(id: String)
}
