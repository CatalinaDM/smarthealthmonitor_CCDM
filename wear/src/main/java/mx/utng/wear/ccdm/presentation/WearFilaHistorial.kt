package mx.utng.wear.ccdm.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

@Composable
fun WearFilaHistorial(lectura: LecturaFC) {
    val color = if (lectura.estado == "Normal")
        MaterialTheme.colors.primary
    else
        MaterialTheme.colors.error

    Chip(
        label = { Text("${lectura.bpm} bpm",
            color = color) },
        secondaryLabel = { Text(lectura.hora) },
        onClick = { },
        colors = ChipDefaults.secondaryChipColors(),
        modifier = Modifier.fillMaxWidth()
    )
}


