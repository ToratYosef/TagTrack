package com.tagtrack.app.core.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tagtrack.app.core.common.AppTheme
import com.tagtrack.app.core.data.AppSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "tagtrack_settings")

@Singleton
class DefaultAppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppSettingsRepository {

    private val nfcPromptChannel = Channel<Unit>(Channel.BUFFERED)

    override val language: String
        get() = runBlocking { languageFlow.first() }

    override val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.LANGUAGE] ?: DEFAULT_LANGUAGE
    }

    override val appTheme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        when (preferences[Keys.THEME] ?: 0) {
            1 -> AppTheme.LIGHT
            2 -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }

    override val isAuthenticated: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.AUTHENTICATED] == 1
    }

    override val cloudBackupEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.CLOUD_BACKUP] == 1
    }

    val nfcPrompts = nfcPromptChannel.receiveAsFlow()

    override suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language
        }
    }

    override suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME] = when (theme) {
                AppTheme.SYSTEM -> 0
                AppTheme.LIGHT -> 1
                AppTheme.DARK -> 2
            }
        }
    }

    override suspend fun setCloudBackup(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CLOUD_BACKUP] = if (enabled) 1 else 0
        }
    }

    override suspend fun setAuthenticated(isAuthenticated: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTHENTICATED] = if (isAuthenticated) 1 else 0
        }
    }

    override suspend fun emitNfcSettingsPrompt() {
        nfcPromptChannel.send(Unit)
    }

    private object Keys {
        val LANGUAGE: Preferences.Key<String> = stringPreferencesKey("language")
        val THEME: Preferences.Key<Int> = intPreferencesKey("theme")
        val AUTHENTICATED: Preferences.Key<Int> = intPreferencesKey("authenticated")
        val CLOUD_BACKUP: Preferences.Key<Int> = intPreferencesKey("cloud_backup")
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "en"
    }
}
