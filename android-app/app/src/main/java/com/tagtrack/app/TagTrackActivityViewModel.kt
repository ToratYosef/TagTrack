package com.tagtrack.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.common.AppState
import com.tagtrack.app.core.data.AppSettingsRepository
import com.tagtrack.app.core.nfc.NfcStateObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TagTrackActivityViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val nfcStateObserver: NfcStateObserver,
) : ViewModel() {

    private val _language = MutableStateFlow(appSettingsRepository.language)
    val language: StateFlow<String> = _language

    private val _appState = MutableStateFlow(AppState())
    val appState: StateFlow<AppState> = _appState

    init {
        viewModelScope.launch {
            appSettingsRepository.languageFlow.collect { _language.value = it }
        }
        viewModelScope.launch {
            nfcStateObserver.nfcState.collect { state ->
                _appState.value = _appState.value.copy(
                    nfcSupported = state.isSupported,
                    nfcEnabled = state.isEnabled
                )
            }
        }
        viewModelScope.launch {
            appSettingsRepository.appTheme.collect { theme ->
                _appState.value = _appState.value.copy(theme = theme)
            }
        }
        viewModelScope.launch {
            appSettingsRepository.isAuthenticated.collect { authenticated ->
                _appState.value = _appState.value.copy(isAuthenticated = authenticated)
            }
        }
    }

    fun onNfcSettingsRequested() {
        viewModelScope.launch {
            appSettingsRepository.emitNfcSettingsPrompt()
        }
    }
}
