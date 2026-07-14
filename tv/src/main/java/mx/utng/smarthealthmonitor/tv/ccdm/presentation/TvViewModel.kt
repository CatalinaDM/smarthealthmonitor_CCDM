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
        viewModelScope.launch {
            try { neonRepo.enviarLecturaTvMock() } catch (e: Exception) {}
            cargarDatos()
        }
        
        mqttSubscriber.connect()
        // Observar mensajes MQTT y actualizar el estado de la UI
        viewModelScope.launch {
            mqttFlow.collect { tvMsg ->
                tvMsg ?: return@collect
                
                _state.update { currentState ->
                    // Crear un registro local simulado para actualizar la UI sin asfixiar la Base de Datos
                    val nuevaLectura = mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC(
                        id = (currentState.lecturas.maxOfOrNull { it.id } ?: 0) + 1,
                        bpm = tvMsg.bpm,
                        estado = tvMsg.estado,
                        hora = tvMsg.hora,
                        dispositivo = "wear"
                    )
                    
                    val nuevasLecturas = (listOf(nuevaLectura) + currentState.lecturas).take(50)
                    
                    // Actualizar la primera fila (estadisticas/estado actual)
                    // Se actualizan "wear" y "app" porque la app refleja el mismo estado del reloj
                    val estadisticasActualizadas = currentState.estadisticas.map { stat ->
                        if (stat.dispositivo.equals("wear", ignoreCase = true) || stat.dispositivo.equals("app", ignoreCase = true)) {
                            stat.copy(bpm = tvMsg.bpm, estado = tvMsg.estado, hora = tvMsg.hora)
                        } else stat
                    }.toMutableList()
                    
                    if (estadisticasActualizadas.none { it.dispositivo.equals("wear", ignoreCase = true) }) {
                        estadisticasActualizadas.add(0, nuevaLectura)
                    }
                    if (estadisticasActualizadas.none { it.dispositivo.equals("app", ignoreCase = true) }) {
                        estadisticasActualizadas.add(0, nuevaLectura.copy(dispositivo = "app"))
                    }
                    
                    currentState.copy(
                        fcActual = tvMsg.bpm,
                        fcEstado = tvMsg.estado,
                        ultimaHora = tvMsg.hora,
                        lecturas = nuevasLecturas,
                        estadisticas = estadisticasActualizadas,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun cargarDatos(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) {
                _state.update { it.copy(isLoading=true) }
            }
            try {
                val lecturas = neonRepo.obtenerHistorialCompleto(50)
                val stats = neonRepo.obtenerEstadisticas()
                _state.update { it.copy(
                    lecturas = lecturas.map { dto -> dto.toLecturaFC() },
                    estadisticas = stats.map { dto -> dto.toLecturaFC() },
                    isLoading = false
                )}
            } catch (e: Exception) {
                if (!silent) {
                    _state.update { it.copy(error=e.message, isLoading=false) }
                }
            }
        }
    }

    fun refresh() = cargarDatos()

    override fun onCleared() {
        super.onCleared()
        mqttSubscriber.disconnect()
    }
}

