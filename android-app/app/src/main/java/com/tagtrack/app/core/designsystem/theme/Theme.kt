package com.tagtrack.app.core.designsystem.theme

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.view.ViewCompat
import com.tagtrack.app.core.common.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor
)

@Composable
fun TagTrackTheme(
    language: String,
    theme: AppTheme = AppTheme.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val darkTheme = when (theme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }

    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    SideEffect {
        val activity = LocalView.current.context as? android.app.Activity ?: return@SideEffect
        val window = activity.window
        window.statusBarColor = colorScheme.primary.toArgb()
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = !darkTheme
    }

    val layoutDirection = if (language == "he") LayoutDirection.Rtl else LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

val Configuration.isRtl: Boolean
    get() = layoutDirection == android.util.LayoutDirection.RTL
