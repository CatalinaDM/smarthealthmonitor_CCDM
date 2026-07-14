package mx.utng.smarthealthmonitor_shared_ccdm.repository

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFCDao
import mx.utng.smarthealthmonitor_shared_ccdm.db.SmartHealthDB

/**
 * Repositorio singleton que centraliza los datos de salud.
 * El WearListenerService escribe aquí.
 * El ViewModel lee de aquí.
 */
object SmartHealthRepository {


    // FC actual del wearable (bpm)
    private val _fcFlow = MutableStateFlow(0)
    val fcFlow: StateFlow<Int> = _fcFlow.asStateFlow()

    // Pasos del día actual
    private val _pasosFlow = MutableStateFlow(0)
    val pasosFlow: StateFlow<Int> = _pasosFlow.asStateFlow()

    private val _spO2Flow = MutableStateFlow(0)
    val spO2Flow: StateFlow<Int> = _spO2Flow.asStateFlow()

    private var dao: LecturaFCDao? = null



    fun init(context: Context) {
        dao = SmartHealthDB.getDatabase(context).lecturaDao()
    }

    suspend fun actualizarFC(bpm: Int) {
        val estado = when { bpm < 60 -> "FC Baja"; bpm > 100 -> "FC Alta"; else -> "Normal" }
        val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        dao?.insertar(LecturaFC(bpm = bpm, estado = estado, hora = hora))
        _fcFlow.value = bpm
    }

fun actualizarPasos(pasos: Int) {
        _pasosFlow.value = pasos
    }
    fun actualizarSpO2(spO2: Int) {
        _spO2Flow.value = spO2
    }
    // Flow del historial desde Room
    fun obtenerHistorial(): Flow<List<LecturaFC>> =
        dao?.obtenerUltimas() ?: emptyFlow()


}