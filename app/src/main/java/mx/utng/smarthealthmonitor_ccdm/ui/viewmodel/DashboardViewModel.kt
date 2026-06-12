// ui/viewmodel/DashboardViewModel.kt
package mx.utng.smarthealthmonitor_ccdm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mx.utng.smarthealthmonitor_ccdm.data.models.MockData
import mx.utng.smarthealthmonitor_ccdm.data.SmartHealthRepository
import mx.utng.smarthealthmonitor_ccdm.data.db.LecturaFC


class DashboardViewModel : ViewModel() {

    // FC: viene del wearable real vía Repository.
    // Si es 0 (sin dato aún), usar valor simulado.
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) MockData.fcActual else it }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000), MockData.fcActual)

    // ← NUEVO: historial desde Room (Flow reactivo)
    val historial: StateFlow<List<LecturaFC>> =
        SmartHealthRepository.obtenerHistorial()
            .stateIn(
                scope        = viewModelScope,
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val pasos: StateFlow<Int> = SmartHealthRepository.pasosFlow
        .map { if (it == 0) MockData.pasosActual else it }
        .stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = MockData.pasosActual
        )
    val spO2: StateFlow<Int> = SmartHealthRepository.spO2Flow
        .map { if (it == 0) 98 else it } // valor por defecto
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 98
        )
}
