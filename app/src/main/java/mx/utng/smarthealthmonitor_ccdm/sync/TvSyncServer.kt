package mx.utng.smarthealthmonitor_ccdm.sync

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository
import java.net.ServerSocket
import java.net.Socket

object TvSyncServer {
    private const val PORT = 8090
    private var serverSocket: ServerSocket? = null
    private var running = false

    fun start(scope: CoroutineScope) {
        if (running) return
        running = true
        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(PORT)
                Log.d("TvSyncServer", "Escuchando en puerto $PORT")
                while (running) {
                    val client = serverSocket?.accept() ?: break
                    handleClient(client)
                }
            } catch (e: Exception) {
                Log.e("TvSyncServer", "Error en servidor: ${e.message}")
            }
        }
    }

    private suspend fun handleClient(client: Socket) {
        try {
            val lecturas = SmartHealthRepository.obtenerHistorial().first()
            val writer = client.getOutputStream().bufferedWriter()
            lecturas.forEach { l ->
                writer.write("${l.valorBpm}|${l.timestamp}|${l.hora}|${l.esNormal}")
                writer.newLine()
            }
            writer.flush()
        } catch (e: Exception) {
            Log.e("TvSyncServer", "Error con cliente: ${e.message}")
        } finally {
            client.close()
        }
    }

    fun stop() {
        running = false
        serverSocket?.close()
    }
}