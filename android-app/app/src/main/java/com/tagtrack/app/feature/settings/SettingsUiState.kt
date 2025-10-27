package com.tagtrack.app.feature.settings

import com.tagtrack.app.core.common.AppTheme

data class SettingsUiState(
    val language: String = "en",
    val theme: AppTheme = AppTheme.SYSTEM,
    val cloudBackupEnabled: Boolean = false,
)
