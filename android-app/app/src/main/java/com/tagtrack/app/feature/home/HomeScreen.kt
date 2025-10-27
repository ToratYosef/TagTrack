package com.tagtrack.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.material3.icons.filled.Nfc
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R
import com.tagtrack.app.core.domain.model.Item

@Composable
fun HomeScreen(
    state: HomeUiState,
    onQueryChanged: (String) -> Unit,
    onAddItem: () -> Unit,
    onScan: () -> Unit,
    onItemClick: (Item) -> Unit,
    isAddEnabled: Boolean,
    nfcSupported: Boolean,
    nfcEnabled: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.home_title)) },
                actions = {
                    IconButton(onClick = onScan) {
                        Icon(Icons.Default.Nfc, contentDescription = stringResource(id = R.string.action_scan))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddItem,
                enabled = isAddEnabled,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(text = stringResource(id = R.string.action_add_item)) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!nfcSupported) {
                Text(text = stringResource(id = R.string.nfc_unavailable_message), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
            } else if (!nfcEnabled) {
                Text(text = stringResource(id = R.string.nfc_disabled_message), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
            }
            TextField(
                value = state.query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                label = { Text(text = stringResource(id = R.string.action_search)) },
                singleLine = true
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.items, key = { it.id }) { item ->
                    ItemCard(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
private fun ItemCard(item: Item, onClick: () -> Unit) {
    androidx.compose.material3.Card(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            item.brand?.let { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
            item.tags.takeIf { it.isNotEmpty() }?.let { tags ->
                Text(text = tags.joinToString(separator = ", "), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
