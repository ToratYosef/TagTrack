package com.tagtrack.app.core.data

import com.tagtrack.app.core.common.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val language: String
    val languageFlow: Flow<String>
    val appTheme: Flow<AppTheme>
    val isAuthenticated: Flow<Boolean>
    val cloudBackupEnabled: Flow<Boolean>

    suspend fun setLanguage(language: String)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setCloudBackup(enabled: Boolean)
    suspend fun setAuthenticated(isAuthenticated: Boolean)
    suspend fun emitNfcSettingsPrompt()
}
