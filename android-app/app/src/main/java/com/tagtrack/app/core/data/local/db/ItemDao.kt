package com.tagtrack.app.core.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun observeItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE nfc_uid = :nfcUid LIMIT 1")
    fun observeItemByUid(nfcUid: String): Flow<ItemEntity?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeItemById(id: String): Flow<ItemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ItemEntity)

    @Update
    suspend fun update(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ItemEntity?
}
