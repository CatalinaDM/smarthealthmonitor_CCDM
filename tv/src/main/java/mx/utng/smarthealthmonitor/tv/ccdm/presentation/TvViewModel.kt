package mx.utng.smarthealthmonitor.tv.ccdm.presentation

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*;
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor.tv.ccdm.domain.model.TvUiState
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import mx.utng.smarthealthmonitor.mqtt.TvMessage
import mx.utng.smarthealthmonitor.tv.ccdm.mqtt.MqttTvSubscriber

class TvViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(TvUiState())
    val state: StateFlow<TvUiState> = _state.asStateFlow()

    // Flow de mensajes MQTT entrantes
    private val mqttFlow = MutableStateFlow<TvMessage?>(null)
    private val mqttSubscriber = MqttTvSubscriber(application, mqttFlow)

    private val neonRepo = mx.utng.smarthealthmonitor.tv.ccdm.data.TvNeonRepository()

    init {
        cargarDatos()
        
        mqttSubscriber.connect()
        // Observar mensajes MQTT y actualizar el estado de la UI
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                _state.update { it.copy(
                    fcActual = tvMsg.bpm,
                    fcEstado = tvMsg.estado,
                    ultimaHora = tvMsg.hora,
                    isLoading = false
                )}
            }
        }
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading=true) }
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                val stats = neonRepo.obtenerEstadisticas()
                _state.update { it.copy(
                    lecturas = lecturas.map { dto -> dto.toLecturaFC() },
                    estadisticas = stats.map { dto -> dto.toLecturaFC() },
                    isLoading = false
                )}
            } catch (e: Exception) {
                _state.update { it.copy(error=e.message, isLoading=false) }
            }
        }
    }

    fun refresh() = cargarDatos()

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}

