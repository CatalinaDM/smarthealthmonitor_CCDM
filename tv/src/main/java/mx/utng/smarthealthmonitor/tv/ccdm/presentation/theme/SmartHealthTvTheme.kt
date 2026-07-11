package mx.utng.smarthealthmonitor.tv.ccdm.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val SmartHealthTvColorScheme = darkColorScheme(
    primary = Color(0xFF1565C0),
    background = Color(0xFF0D1B4A),
    surface = Color(0xFF1565C0)
)

@Composable
fun SmartHealthTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartHealthTvColorScheme,
        content = content
    )
}