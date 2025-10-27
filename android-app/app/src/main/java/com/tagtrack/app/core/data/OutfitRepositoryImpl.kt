package com.tagtrack.app.core.data

import com.google.firebase.firestore.FirebaseFirestore
import com.tagtrack.app.core.data.local.db.OutfitDao
import com.tagtrack.app.core.data.local.db.OutfitEntity
import com.tagtrack.app.core.domain.model.Outfit
import com.tagtrack.app.core.domain.repository.OutfitRepository
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
class OutfitRepositoryImpl @Inject constructor(
    private val outfitDao: OutfitDao,
    private val firestore: FirebaseFirestore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : OutfitRepository {
    override fun getOutfits(): Flow<List<Outfit>> =
        outfitDao.observeOutfits().map { list -> list.map { it.toDomain() } }

    override suspend fun create(outfit: Outfit) {
        withContext(dispatcher) {
            val entity = outfit.copy(
                id = outfit.id.ifEmpty { UUID.randomUUID().toString() },
                createdAt = outfit.createdAt.takeIf { it != Instant.EPOCH } ?: Instant.now()
            ).toEntity()
            outfitDao.upsert(entity)
            firestore.collection(COLLECTION_OUTFITS).document(entity.id).set(entity.toRemote()).await()
        }
    }

    override suspend fun update(outfit: Outfit) {
        withContext(dispatcher) {
            val entity = outfit.toEntity()
            outfitDao.update(entity)
            firestore.collection(COLLECTION_OUTFITS).document(entity.id).set(entity.toRemote()).await()
        }
    }

    override suspend fun delete(outfitId: String) {
        withContext(dispatcher) {
            outfitDao.delete(outfitId)
            firestore.collection(COLLECTION_OUTFITS).document(outfitId).delete().await()
        }
    }

    private fun OutfitEntity.toDomain(): Outfit = Outfit(
        id = id,
        name = name,
        itemIds = itemIds,
        createdAt = createdAt
    )

    private fun Outfit.toEntity(): OutfitEntity = OutfitEntity(
        id = id,
        name = name,
        itemIds = itemIds,
        createdAt = createdAt
    )

    private fun OutfitEntity.toRemote(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "itemIds" to itemIds,
        "createdAt" to createdAt.toEpochMilli()
    )

    companion object {
        private const val COLLECTION_OUTFITS = "outfits"
    }
}
