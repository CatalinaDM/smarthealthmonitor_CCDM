package mx.utng.smarthealthmonitor.tv.ccdm.sync

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository
import java.net.Socket

object TvSyncClient {
    private const val HOST = "localhost"
    private const val PORT = 8090
    private const val INTERVALO_MS = 5000L

    fun start(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    Socket(HOST, PORT).use { socket ->
                        val lineas = socket.getInputStream().bufferedReader().readLines()
                        lineas.forEach { linea ->
                            val partes = linea.split("|")
                            val bpm = partes.getOrNull(0)?.toIntOrNull()
                            if (bpm != null) {
                                SmartHealthRepository.actualizarFC(bpm)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w("TvSyncClient", "Sin conexión al teléfono todavía: ${e.message}")
                }
                delay(INTERVALO_MS)
            }
        }
    }
}