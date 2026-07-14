package mx.utng.smarthealthmonitor.tv.ccdm.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items


@Composable
fun TvCatalogScreen(
    viewModel: TvViewModel = viewModel(),
    onCardClick: (Int) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(Color(0xFF0D1B4A))) {

        if (state.isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
            return@Box
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(48.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            item {
                RowSection(title = "⚡ Estado Actual (3 dispositivos)") {
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                        items(state.estadisticas) { lectura ->
                            FcCardItem(lectura=lectura, onClick={ onCardClick(lectura.id) })
                        }
                    }
                }
            }

            item {
                RowSection(title = "📋 Historial FC") {
                    LazyRow(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                        items(state.lecturas) { lectura ->
                            FcCardItem(lectura=lectura, onClick={ onCardClick(lectura.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style=MaterialTheme.typography.headlineSmall,
            color=Color.White, fontWeight= FontWeight.Bold)
        content()
    }
}