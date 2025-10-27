package com.tagtrack.app.core.common

data class AppState(
    val nfcSupported: Boolean = true,
    val nfcEnabled: Boolean = true,
    val isAuthenticated: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
)

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}
