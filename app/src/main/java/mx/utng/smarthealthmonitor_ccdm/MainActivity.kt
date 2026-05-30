package mx.utng.smarthealthmonitor_ccdm
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Warning
import mx.utng.smarthealthmonitor_ccdm.ui.theme.SmartHealthMonitorTheme
import mx.utng.smarthealthmonitor_ccdm.navigation.SmartHealthNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthNavGraph()

        }
    }


@Preview(name = "Login - Light", showBackground = true,
    showSystemUi = true, device = "id:pixel_6")
@Preview(name = "Login - Dark", showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Login - Big Font", showBackground = true,
    fontScale = 1.5f)

@Composable
private fun LoginScreenPreview() {
    SmartHealthMonitorTheme {
        LoginScreen()
    }
}

    @Composable
    fun ThemePreview() {
        SmartHealthMonitorTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "SmartHealth Monitor",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}


