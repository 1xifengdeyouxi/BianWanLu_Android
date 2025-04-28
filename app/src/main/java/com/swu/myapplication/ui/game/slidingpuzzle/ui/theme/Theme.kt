package com.swu.myapplication.ui.game.slidingpuzzle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 定义颜色
val PrimaryColor = Color(0xFF4CAF50)
val PrimaryVariant = Color(0xFF388E3C)
val SecondaryColor = Color(0xFF03A9F4)
val Background = Color(0xFFF0F0F0)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF212121)
val TileColor = Color(0xFF8BC34A)
val EmptyTileColor = Color(0xFFE0E0E0)
val TileText = Color(0xFF212121)
val TileLightText = Color(0xFFFFFFFF)

private val DarkColorPalette = darkColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryVariant,
    secondary = SecondaryColor,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryVariant,
    secondary = SecondaryColor,
    background = Background,
    surface = Surface,
    onSurface = OnSurface
)

@Composable
fun SlidingPuzzleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}