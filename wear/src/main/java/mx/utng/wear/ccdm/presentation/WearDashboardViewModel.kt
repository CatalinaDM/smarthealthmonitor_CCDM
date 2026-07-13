package mx.utng.wear.ccdm.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import mx.utng.wear.ccdm.mqtt.MqttWearPublisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearDashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val mqttPublisher = MqttWearPublisher(application)

    // Reutiliza el mismo Repository del módulo app
    val fc: StateFlow<Int> = SmartHealthRepository.fcFlow
        .map { if (it == 0) 72 else it }  // valor por defecto
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5_000), 72)

    val historial: StateFlow<List<LecturaFC>> = SmartHealthRepository.obtenerHistorial()
            .stateIn(viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            mqttPublisher.connect()
        }
        viewModelScope.launch {
            fc.collect { bpm ->
                val estado = when { bpm < 60 -> "FC Baja"; bpm > 100 -> "FC Alta"; else -> "Normal" }
                mqttPublisher.publishFC(bpm, estado)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mqttPublisher.disconnect()
    }
}


