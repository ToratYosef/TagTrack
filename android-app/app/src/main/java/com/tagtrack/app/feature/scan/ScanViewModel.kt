package com.tagtrack.app.feature.scan

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.domain.repository.ItemRepository
import com.tagtrack.app.core.nfc.NfcReaderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val nfcReaderManager: NfcReaderManager,
) : ViewModel() {
    private val _scannedItemId = MutableSharedFlow<String>()
    val scannedItemId: SharedFlow<String> = _scannedItemId.asSharedFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun startScan(activity: ComponentActivity) {
        viewModelScope.launch {
            try {
                nfcReaderManager.enableReaderMode(activity).collect { uid ->
                    val normalized = uid.uppercase()
                    val item = itemRepository.getItemByUid(normalized).first()
                    if (item != null) {
                        _scannedItemId.emit(item.id)
                    } else {
                        _error.value = normalized
                    }
                    return@launch
                }
            } catch (ex: Exception) {
                _error.value = ex.message ?: ""
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
