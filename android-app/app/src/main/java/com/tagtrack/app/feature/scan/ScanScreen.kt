package com.tagtrack.app.feature.scan

import androidx.activity.ComponentActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.tagtrack.app.R

@Composable
fun ScanScreen(
    hostActivity: ComponentActivity?,
    onStartScan: (ComponentActivity) -> Unit,
    onErrorDismissed: () -> Unit,
    errorUid: String?,
) {
    LaunchedEffect(hostActivity) {
        hostActivity?.let(onStartScan)
    }
    Text(text = stringResource(id = R.string.scan_prompt))
    if (errorUid != null) {
        val message = if (errorUid.isBlank()) {
            stringResource(id = R.string.nfc_unavailable_message)
        } else {
            stringResource(id = R.string.scan_not_found, errorUid)
        }
        AlertDialog(
            onDismissRequest = onErrorDismissed,
            confirmButton = {
                TextButton(onClick = onErrorDismissed) {
                    Text(text = stringResource(id = R.string.action_register_tag))
                }
            },
            dismissButton = {
                TextButton(onClick = onErrorDismissed) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            text = { Text(text = message) }
        )
    }
}
