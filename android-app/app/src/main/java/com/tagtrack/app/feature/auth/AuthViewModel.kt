package com.tagtrack.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun signIn(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            try {
                authRepository.signIn(state.email.trim(), state.password)
                _uiState.value = AuthUiState()
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = state.copy(isLoading = false, error = ex.message)
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            try {
                authRepository.signUp(state.email.trim(), state.password)
                _uiState.value = AuthUiState()
                onSuccess()
            } catch (ex: Exception) {
                _uiState.value = state.copy(isLoading = false, error = ex.message)
            }
        }
    }

    fun sendMagicLink() {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                authRepository.sendMagicLink(state.email.trim())
            } catch (_: Exception) {
                // ignore for now
            }
        }
    }
}
