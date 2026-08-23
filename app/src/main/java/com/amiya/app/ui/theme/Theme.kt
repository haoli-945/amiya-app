package com.amiya.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 品牌色
val AmiyaPink = Color(0xFFFF6B9D)
val AmiyaPinkLight = Color(0xFFFFB3CC)
val AmiyaPinkDark = Color(0xFFCC3366)
val AmiyaGreen = Color(0xFF4CAF50)
val AmiyaOrange = Color(0xFFFF9800)

private val LightColorScheme = lightColorScheme(
    primary = AmiyaPink,
    onPrimary = Color.White,
    primaryContainer = AmiyaPinkLight,
    secondary = AmiyaGreen,
    tertiary = AmiyaOrange,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
)

@Composable
fun AmiyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
