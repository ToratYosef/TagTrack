package com.tagtrack.app.core.domain.model

import com.tagtrack.app.core.common.AppTheme
import java.time.Instant

data class UserSettings(
    val cloudBackupEnabled: Boolean,
    val language: String,
    val theme: AppTheme,
    val lastBackupAt: Instant?,
)
