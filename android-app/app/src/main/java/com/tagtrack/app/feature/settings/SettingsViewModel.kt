package com.tagtrack.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.common.AppTheme
import com.tagtrack.app.core.data.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        combine(
            appSettingsRepository.languageFlow,
            appSettingsRepository.appTheme,
            appSettingsRepository.cloudBackupEnabled
        ) { language, theme, cloudBackup ->
            SettingsUiState(language = language, theme = theme, cloudBackupEnabled = cloudBackup)
        }.onEach { state ->
            _uiState.value = state
        }.launchIn(viewModelScope)
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { appSettingsRepository.setLanguage(language) }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { appSettingsRepository.setTheme(theme) }
    }

    fun setCloudBackup(enabled: Boolean) {
        viewModelScope.launch { appSettingsRepository.setCloudBackup(enabled) }
    }
}
