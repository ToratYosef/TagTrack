package com.tagtrack.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tagtrack.app.core.domain.model.Item
import com.tagtrack.app.core.domain.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var allItems: List<Item> = emptyList()

    init {
        viewModelScope.launch {
            itemRepository.getItems().collect { items ->
                allItems = items
                _uiState.update { state ->
                    state.copy(items = applyFilters(allItems, state))
                }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { state ->
            val newState = state.copy(query = query)
            newState.copy(items = applyFilters(allItems, newState))
        }
    }

    fun setFilters(type: String?, color: String?, tags: Set<String>) {
        _uiState.update { state ->
            val newState = state.copy(filterType = type, filterColor = color, filterTags = tags)
            newState.copy(items = applyFilters(allItems, newState))
        }
    }

    private fun applyFilters(items: List<Item>, state: HomeUiState): List<Item> {
        return items.filter { item ->
            val matchesQuery = state.query.isBlank() ||
                item.name.contains(state.query, ignoreCase = true) ||
                item.brand?.contains(state.query, ignoreCase = true) == true
            val matchesType = state.filterType?.let { item.type.equals(it, ignoreCase = true) } ?: true
            val matchesColor = state.filterColor?.let { item.color?.equals(it, ignoreCase = true) == true } ?: true
            val matchesTags = if (state.filterTags.isEmpty()) {
                true
            } else {
                state.filterTags.all { tag -> item.tags.any { it.equals(tag, ignoreCase = true) } }
            }
            matchesQuery && matchesType && matchesColor && matchesTags
        }
    }

}
