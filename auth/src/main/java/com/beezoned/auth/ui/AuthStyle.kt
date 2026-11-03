package com.beezoned.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Optional look-and-feel overrides so each host app can theme the auth screen. */
data class AuthStyle(
    val background: Brush? = null,
    /** Colour of the title and subtitle; defaults to the Material onBackground colour. */
    val contentColor: Color? = null,
    val primaryButton: List<Color>? = null,
    val logo: (@Composable () -> Unit)? = null,
    val subtitle: String? = null,
)
