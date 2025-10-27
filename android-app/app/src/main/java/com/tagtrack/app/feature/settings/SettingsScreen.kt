package com.tagtrack.app.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R
import com.tagtrack.app.core.common.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onThemeChanged: (AppTheme) -> Unit,
    onLanguageChanged: (String) -> Unit,
    onCloudBackupChanged: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(id = R.string.settings_theme))
        ThemeSelector(selected = state.theme, onThemeChanged = onThemeChanged)
        Text(text = stringResource(id = R.string.settings_language))
        LanguageSelector(selected = state.language, onLanguageChanged = onLanguageChanged)
        Text(text = stringResource(id = R.string.settings_cloud_backup))
        Switch(
            checked = state.cloudBackupEnabled,
            onCheckedChange = onCloudBackupChanged,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSelector(selected: AppTheme, onThemeChanged: (AppTheme) -> Unit) {
    val options = AppTheme.values()
    val expanded = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        TextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.settings_theme)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            modifier = Modifier.menuAnchor()
        )
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            options.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(text = theme.name) },
                    onClick = {
                        onThemeChanged(theme)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(selected: String, onLanguageChanged: (String) -> Unit) {
    val options = listOf("en" to R.string.language_en, "he" to R.string.language_he)
    val expanded = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = { expanded.value = it }) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(id = R.string.settings_language)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            modifier = Modifier.menuAnchor()
        )
        DropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            options.forEach { (code, labelRes) ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = labelRes)) },
                    onClick = {
                        onLanguageChanged(code)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
