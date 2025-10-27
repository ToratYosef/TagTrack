package com.tagtrack.app.core.domain.repository

import com.tagtrack.app.core.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItems(): Flow<List<Item>>
    fun getItemByUid(nfcUid: String): Flow<Item?>
    fun getItemById(id: String): Flow<Item?>
    suspend fun refresh()
    suspend fun create(item: Item)
    suspend fun update(item: Item)
    suspend fun delete(itemId: String)
    suspend fun markWorn(itemId: String)
}
