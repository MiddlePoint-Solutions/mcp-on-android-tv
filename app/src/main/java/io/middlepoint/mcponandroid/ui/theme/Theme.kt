package io.middlepoint.mcponandroid.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.lightColorScheme

@Composable
fun McpAndroidTheme(content: @Composable () -> Unit) {
    val colorScheme =
        remember {
            lightColorScheme(
                onPrimary = Color.White,
                background = Purple40,
                onBackground = Color.White,
            )
        }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
