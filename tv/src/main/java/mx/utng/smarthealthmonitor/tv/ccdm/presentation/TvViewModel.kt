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

    init {
        // Observar historial reactivo del Room DAO
        viewModelScope.launch {
            SmartHealthRepository.obtenerHistorial()
                .catch { e -> _state.update{it.copy(error=e.message,isLoading=false)} }
                .collect { lecturas ->
                    _state.update { it.copy(lecturas=lecturas, isLoading=false) }
                }
        }
        
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

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}

