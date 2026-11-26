package com.drively.app.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
private val Colors = darkColorScheme(
    primary=Blue, onPrimary=TextHigh,
    background=Bg, onBackground=TextHigh,
    surface=Surface1, onSurface=TextHigh,
    surfaceVariant=Surface2, error=Red
)
@Composable
fun DrivelyTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = Colors, content = content)
