package com.tagtrack.app.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.domain.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val itemRepository: ItemRepository,
) : ViewModel() {
    private val itemId: String = checkNotNull(savedStateHandle["itemId"])

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    init {
        itemRepository.getItemById(itemId)
            .onEach { item ->
                _uiState.value = ItemDetailUiState(item = item, isLoading = item == null)
            }
            .launchIn(viewModelScope)
    }

    fun onRecordWorn() {
        viewModelScope.launch { itemRepository.markWorn(itemId) }
    }

    fun onDelete() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
    }

    fun onDeleteConfirmed(onFinished: () -> Unit) {
        viewModelScope.launch {
            itemRepository.delete(itemId)
            onFinished()
        }
    }

    fun onDeleteDismissed() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
    }
}
