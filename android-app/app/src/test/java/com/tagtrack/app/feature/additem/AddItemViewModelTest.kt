package com.tagtrack.app.feature.additem

import com.tagtrack.app.core.domain.model.Item
import com.tagtrack.app.core.domain.repository.ItemRepository
import com.tagtrack.app.core.nfc.NfcReaderManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

private class FakeItemRepository : ItemRepository {
    private val itemsFlow = MutableStateFlow<List<Item>>(emptyList())
    override fun getItems(): Flow<List<Item>> = itemsFlow
    override fun getItemByUid(nfcUid: String): Flow<Item?> = flowOf(itemsFlow.value.firstOrNull { it.nfcUid == nfcUid })
    override fun getItemById(id: String): Flow<Item?> = flowOf(itemsFlow.value.firstOrNull { it.id == id })
    override suspend fun refresh() {}
    override suspend fun create(item: Item) {
        itemsFlow.value = itemsFlow.value + item
    }
    override suspend fun update(item: Item) {}
    override suspend fun delete(itemId: String) {}
    override suspend fun markWorn(itemId: String) {}
}


@OptIn(ExperimentalCoroutinesApi::class)
class AddItemViewModelTest {
    private lateinit var repository: FakeItemRepository
    private lateinit var viewModel: AddItemViewModel

    @Before
    fun setup() {
        repository = FakeItemRepository()
        viewModel = AddItemViewModel(repository, NfcReaderManager())
    }

    @Test
    fun save_withoutScan_setsValidationError() = runTest {
        viewModel.save {}
        assertEquals(AddItemError.VALIDATION, viewModel.uiState.value.error)
    }

    @Test
    fun save_withValidItem_resetsState() = runTest {
        viewModel.updateState(viewModel.uiState.value.copy(scannedUid = "ABC123", name = "Jacket"))
        viewModel.save {}
        assertNull(viewModel.uiState.value.scannedUid)
        assertEquals("", viewModel.uiState.value.name)
    }
}
