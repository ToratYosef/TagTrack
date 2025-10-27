package com.tagtrack.app.feature.additem

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R

@Composable
fun AddItemScreen(
    state: AddItemUiState,
    hostActivity: ComponentActivity?,
    onStartScan: (ComponentActivity) -> Unit,
    onStateChanged: (AddItemUiState) -> Unit,
    onSave: () -> Unit,
    onViewExisting: (String) -> Unit,
    onDuplicateDismiss: () -> Unit,
) {
    LaunchedEffect(hostActivity, state.isWaitingForScan) {
        if (hostActivity != null && state.isWaitingForScan) {
            onStartScan(hostActivity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.add_item_title))
        Spacer(modifier = Modifier.height(16.dp))
        if (state.isWaitingForScan) {
            Text(text = stringResource(id = R.string.add_item_waiting_for_scan))
        } else {
            Text(text = stringResource(id = R.string.add_item_tag_ready, state.scannedUid!!))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = { name -> onStateChanged(state.copy(name = name)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_name)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.type,
            onValueChange = { type -> onStateChanged(state.copy(type = type)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_type)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.color,
            onValueChange = { color -> onStateChanged(state.copy(color = color)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_color)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.brand,
            onValueChange = { brand -> onStateChanged(state.copy(brand = brand)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_brand)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.size,
            onValueChange = { size -> onStateChanged(state.copy(size = size)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_size)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.tags,
            onValueChange = { tags -> onStateChanged(state.copy(tags = tags)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_tags_hint)) },
            singleLine = false
        )
        OutlinedTextField(
            value = state.notes,
            onValueChange = { notes -> onStateChanged(state.copy(notes = notes)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.field_notes)) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onSave, enabled = !state.isSaving && !state.isWaitingForScan) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.height(24.dp))
                } else {
                    Text(text = stringResource(id = R.string.action_save))
                }
            }
        }
    }

    if (state.isDuplicate) {
        AlertDialog(
            onDismissRequest = onDuplicateDismiss,
            confirmButton = {
                TextButton(onClick = { state.duplicateItemId?.let(onViewExisting) }) {
                    Text(text = stringResource(id = R.string.action_open_item))
                }
            },
            dismissButton = {
                TextButton(onClick = onDuplicateDismiss) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            title = { Text(text = stringResource(id = R.string.duplicate_tag_title)) },
            text = {
                Text(text = stringResource(id = R.string.duplicate_tag_message, state.duplicateItemName ?: ""))
            }
        )
    }
}
