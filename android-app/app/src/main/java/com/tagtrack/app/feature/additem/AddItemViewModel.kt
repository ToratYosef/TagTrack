package com.tagtrack.app.feature.additem

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.domain.model.Item
import com.tagtrack.app.core.domain.repository.ItemRepository
import com.tagtrack.app.core.nfc.NfcReaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val nfcReaderManager: NfcReaderManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    private var scanJob: Job? = null

    fun startScan(activity: ComponentActivity) {
        if (_uiState.value.isWaitingForScan.not()) return
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(error = null, isWaitingForScan = true)
            nfcReaderManager.enableReaderMode(activity).collect { uid ->
                handleTagScanned(uid)
            }
        }
    }

    private suspend fun checkDuplicate(uid: String): Item? = itemRepository.getItemByUid(uid).first()

    private fun handleTagScanned(uid: String) {
        viewModelScope.launch {
            val normalized = uid.uppercase()
            val duplicate = checkDuplicate(normalized)
            if (duplicate != null) {
                _uiState.value = _uiState.value.copy(
                    isWaitingForScan = true,
                    scannedUid = null,
                    isDuplicate = true,
                    duplicateItemName = duplicate.name,
                    duplicateItemId = duplicate.id,
                    error = AddItemError.NFC_DUPLICATE
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isWaitingForScan = false,
                    scannedUid = normalized,
                    isDuplicate = false,
                    duplicateItemName = null,
                    duplicateItemId = null,
                    error = null
                )
            }
        }
    }

    fun onFieldChanged(transform: (AddItemUiState) -> AddItemUiState) {
        _uiState.value = transform(_uiState.value)
    }

    fun updateState(state: AddItemUiState) {
        _uiState.value = state
    }

    fun save(onComplete: (String) -> Unit) {
        val state = _uiState.value
        if (state.scannedUid == null || state.name.isBlank()) {
            _uiState.value = state.copy(error = AddItemError.VALIDATION)
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            val item = Item(
                id = UUID.randomUUID().toString(),
                nfcUid = state.scannedUid,
                name = state.name.trim(),
                type = state.type.trim(),
                color = state.color.takeIf { it.isNotBlank() }?.trim(),
                brand = state.brand.takeIf { it.isNotBlank() }?.trim(),
                size = state.size.takeIf { it.isNotBlank() }?.trim(),
                tags = state.tags.split(',').mapNotNull { token -> token.trim().takeIf { it.isNotBlank() } },
                notes = state.notes.takeIf { it.isNotBlank() }?.trim(),
                photoUri = state.photoUri,
                photoCloudUrl = null,
                createdAt = Instant.now(),
                lastWorn = null
            )
            try {
                itemRepository.create(item)
                _uiState.value = AddItemUiState()
                onComplete(item.id)
            } catch (ex: Exception) {
                _uiState.value = state.copy(isSaving = false, error = AddItemError.NETWORK)
            }
        }
    }

    fun resetDuplicateError() {
        _uiState.value = _uiState.value.copy(isDuplicate = false, error = null)
    }
}
