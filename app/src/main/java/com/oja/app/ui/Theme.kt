package com.oja.app.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeSkin(val displayName: String, val scheme: ColorScheme) {
    NEUTRAL(
        displayName = "Neutral",
        scheme = darkColorScheme()
    ),
    MARKET_DAWN(
        displayName = "Market Dawn",
        scheme = darkColorScheme(
            primary = Color(0xFFEF6C00),
            secondary = Color(0xFFFFB74D),
            tertiary = Color(0xFF00897B),
            background = Color(0xFF1A1A1A),
            surface = Color(0xFF212121)
        )
    ),
    RIVERINE_BREEZE(
        displayName = "Riverine Breeze",
        scheme = darkColorScheme(
            primary = Color(0xFF26A69A),
            secondary = Color(0xFF4DD0E1),
            tertiary = Color(0xFF9CCC65),
            background = Color(0xFF121417),
            surface = Color(0xFF182024)
        )
    )
}

@Composable
fun OjaTheme(skin: ThemeSkin = ThemeSkin.NEUTRAL, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = skin.scheme, content = content)
}
