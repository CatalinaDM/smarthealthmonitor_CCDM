package mx.utng.smarthealthmonitor.tv.ccdm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.TvCatalogScreen
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SmartHealthRepository.init(applicationContext)

        // Sembrar datos de prueba solo si la base de datos local del módulo tv está vacía
        lifecycleScope.launch {
            val historialActual = SmartHealthRepository.obtenerHistorial().first()
            if (historialActual.isEmpty()) {
                val lecturasDePrueba = listOf(72, 75, 68, 80, 77,22,67,88,99,)
                lecturasDePrueba.forEach { bpm ->
                    SmartHealthRepository.actualizarFC(bpm)
                }
            }
        }

        setContent {
            TvCatalogScreen()
        }
    }
}