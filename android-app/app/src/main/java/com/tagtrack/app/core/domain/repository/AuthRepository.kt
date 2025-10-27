package com.tagtrack.app.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun sendMagicLink(email: String)
    suspend fun signOut()
}
