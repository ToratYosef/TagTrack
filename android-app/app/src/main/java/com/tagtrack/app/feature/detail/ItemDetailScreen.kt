package com.tagtrack.app.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R

@Composable
fun ItemDetailScreen(
    state: ItemDetailUiState,
    onRecordWorn: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onDeleteDismissed: () -> Unit,
) {
    val item = state.item
    if (item == null) {
        Text(text = stringResource(id = R.string.loading), modifier = Modifier.padding(16.dp))
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = item.name, style = MaterialTheme.typography.headlineSmall)
        item.photoUri?.let { uri -> Text(text = stringResource(id = R.string.photo_placeholder, uri)) }
        Text(text = stringResource(id = R.string.detail_type, item.type))
        item.brand?.let { Text(text = stringResource(id = R.string.detail_brand, it)) }
        item.color?.let { Text(text = stringResource(id = R.string.detail_color, it)) }
        item.size?.let { Text(text = stringResource(id = R.string.detail_size, it)) }
        if (item.tags.isNotEmpty()) {
            Text(text = stringResource(id = R.string.detail_tags, item.tags.joinToString(", ")))
        }
        item.notes?.let { Text(text = stringResource(id = R.string.detail_notes, it)) }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRecordWorn, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_record_worn))
        }
        OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_edit))
        }
        OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_delete))
        }
    }

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDeleteDismissed,
            confirmButton = {
                TextButton(onClick = onDeleteConfirmed) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismissed) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            title = { Text(text = stringResource(id = R.string.delete_confirmation_title)) },
            text = { Text(text = stringResource(id = R.string.delete_confirmation_message)) }
        )
    }
}
