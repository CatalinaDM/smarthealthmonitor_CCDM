package mx.utng.smarthealthmonitor_ccdm.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.utng.smarthealthmonitor_ccdm.ui.theme.SmartHealthMonitorTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsWalk

@Composable
fun TarjetaDato(
    valor: String,                        // "78" o "4,250"
    unidad: String,                       // "bpm" o "pasos"
    label: String,                        // "Frecuencia cardíaca"
    colorValor: Color,                    // MaterialTheme.colorScheme.error
    modifier: Modifier = Modifier,         // ← siempre en Composables reutilizables
    icono: ImageVector? = null,
    mostrarEstadoSalud: Boolean = false
) {
    // Convertir el valor a Int
    val fc = valor.toIntOrNull()

    // Validar rango normal
    val esNormal = fc != null && fc in 60..100
    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Mostrar icono solo si no es null
                if (icono != null) {
                    Icon(
                        imageVector = icono,
                        contentDescription = label,
                        tint = colorValor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = valor,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorValor
                    )
                    Text(
                        text = unidad,
                        style = MaterialTheme.typography.titleSmall,
                        color = colorValor,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            // ── CHIP DE ESTADO ─────────────────────
            if (mostrarEstadoSalud && fc != null) {

                Spacer(Modifier.height(12.dp))

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (esNormal)
                                "Normal"
                            else
                                "Consulta al médico"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor =
                            if (esNormal)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer,

                        labelColor =
                            if (esNormal)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                    )
                )
            }
        }
    }
}

// Preview con datos de prueba
@Preview(showBackground = true, name = "TarjetaDato - FC")
@Composable
private fun TarjetaDatoPreview() {
    SmartHealthMonitorTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TarjetaDato(
                valor = "130", unidad = "bpm",
                label = "Frecuencia cardíaca",
                colorValor = MaterialTheme.colorScheme.error,
                icono = Icons.Default.Favorite
            )
            TarjetaDato(
                valor = "4,250", unidad = "pasos",
                label = "Pasos del día",
                colorValor = MaterialTheme.colorScheme.primary,
                icono = Icons.Default.DirectionsWalk
            )
        }
    }
}

