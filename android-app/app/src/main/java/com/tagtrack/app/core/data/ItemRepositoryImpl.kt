package com.tagtrack.app.core.data

import com.google.firebase.firestore.FirebaseFirestore
import com.tagtrack.app.core.data.local.db.ItemDao
import com.tagtrack.app.core.data.local.db.ItemEntity
import com.tagtrack.app.core.domain.model.Item
import com.tagtrack.app.core.domain.repository.ItemRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.UUID

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ItemRepository {

    override fun getItems(): Flow<List<Item>> = itemDao.observeItems().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getItemByUid(nfcUid: String): Flow<Item?> =
        itemDao.observeItemByUid(nfcUid).map { it?.toDomain() }

    override fun getItemById(id: String): Flow<Item?> =
        itemDao.observeItemById(id).map { it?.toDomain() }

    override suspend fun refresh() {
        // Placeholder: production app would sync with Firestore via WorkManager
    }

    override suspend fun create(item: Item) {
        withContext(dispatcher) {
            val entity = item.copy(
                id = item.id.ifEmpty { UUID.randomUUID().toString() },
                createdAt = item.createdAt.takeIf { it != Instant.EPOCH } ?: Instant.now()
            ).toEntity()
            itemDao.upsert(entity)
            firestore.collection(COLLECTION_ITEMS).document(entity.id).set(entity.toRemote()).await()
        }
    }

    override suspend fun update(item: Item) {
        withContext(dispatcher) {
            val entity = item.toEntity()
            itemDao.update(entity)
            firestore.collection(COLLECTION_ITEMS).document(entity.id).set(entity.toRemote()).await()
        }
    }

    override suspend fun delete(itemId: String) {
        withContext(dispatcher) {
            itemDao.delete(itemId)
            firestore.collection(COLLECTION_ITEMS).document(itemId).delete().await()
        }
    }

    override suspend fun markWorn(itemId: String) {
        withContext(dispatcher) {
            val entity = itemDao.getById(itemId) ?: return@withContext
            val updated = entity.copy(lastWorn = Instant.now())
            itemDao.update(updated)
            firestore.collection(COLLECTION_ITEMS).document(updated.id).update("lastWorn", updated.lastWorn?.toEpochMilli()).await()
        }
    }

    private fun ItemEntity.toDomain(): Item = Item(
        id = id,
        nfcUid = nfcUid,
        name = name,
        type = type,
        color = color,
        brand = brand,
        size = size,
        tags = tags,
        notes = notes,
        photoUri = photoUri,
        photoCloudUrl = photoCloudUrl,
        createdAt = createdAt,
        lastWorn = lastWorn
    )

    private fun Item.toEntity(): ItemEntity = ItemEntity(
        id = id,
        nfcUid = nfcUid,
        name = name,
        type = type,
        color = color,
        brand = brand,
        size = size,
        tags = tags,
        notes = notes,
        photoUri = photoUri,
        photoCloudUrl = photoCloudUrl,
        createdAt = createdAt,
        lastWorn = lastWorn
    )

    private fun ItemEntity.toRemote(): Map<String, Any?> = mapOf(
        "id" to id,
        "nfcUid" to nfcUid,
        "name" to name,
        "type" to type,
        "color" to color,
        "brand" to brand,
        "size" to size,
        "tags" to tags,
        "notes" to notes,
        "photoUri" to photoUri,
        "photoCloudUrl" to photoCloudUrl,
        "createdAt" to createdAt.toEpochMilli(),
        "lastWorn" to lastWorn?.toEpochMilli()
    )

    companion object {
        private const val COLLECTION_ITEMS = "items"
    }
}
