package com.tagtrack.app.core.domain.repository

import com.tagtrack.app.core.domain.model.Outfit
import kotlinx.coroutines.flow.Flow

interface OutfitRepository {
    fun getOutfits(): Flow<List<Outfit>>
    suspend fun create(outfit: Outfit)
    suspend fun update(outfit: Outfit)
    suspend fun delete(outfitId: String)
}
