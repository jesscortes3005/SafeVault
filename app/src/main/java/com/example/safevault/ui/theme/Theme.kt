package com.example.safevault.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val VaultColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = VaultDark,
    primaryContainer = VaultSurface,
    onPrimaryContainer = VaultPrimary,
    secondary = VaultAccent,
    onSecondary = VaultDark,
    background = VaultDark,
    surface = VaultSurface,
    onBackground = VaultTextPrimary,
    onSurface = VaultTextPrimary,
    error = VaultError
)

@Composable
fun SafeVaultTheme(
    darkTheme: Boolean = true, // Force dark for vault feel
    content: @Composable () -> Unit
) {
    val colorScheme = VaultColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context.findActivity())?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
