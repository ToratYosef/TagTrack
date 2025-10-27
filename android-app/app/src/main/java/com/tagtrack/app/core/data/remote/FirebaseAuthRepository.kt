package com.tagtrack.app.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.tagtrack.app.core.data.AppSettingsRepository
import com.tagtrack.app.core.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val appSettingsRepository: AppSettingsRepository,
) : AuthRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val isAuthenticated: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val authenticated = auth.currentUser != null
            scope.launch { appSettingsRepository.setAuthenticated(authenticated) }
            trySend(authenticated)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        appSettingsRepository.setAuthenticated(true)
    }

    override suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        appSettingsRepository.setAuthenticated(true)
    }

    override suspend fun sendMagicLink(email: String) {
        firebaseAuth.sendSignInLinkToEmail(email, com.google.firebase.auth.ActionCodeSettings.newBuilder()
            .setHandleCodeInApp(true)
            .setUrl("https://tagtrack.app/magic")
            .setAndroidPackageName("com.tagtrack.app", true, "1")
            .build()).await()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        appSettingsRepository.setAuthenticated(false)
    }

