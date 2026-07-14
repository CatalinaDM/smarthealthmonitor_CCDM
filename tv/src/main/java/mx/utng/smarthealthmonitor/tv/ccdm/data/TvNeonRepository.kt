package mx.utng.smarthealthmonitor.tv.ccdm.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.utng.smarthealthmonitor_shared_ccdm.data.remote.NeonClient
import mx.utng.smarthealthmonitor_shared_ccdm.data.remote.NeonRequest
import mx.utng.smarthealthmonitor_shared_ccdm.data.remote.LecturaFcDto

class TvNeonRepository {
    /** Obtener historial completo de los 3 dispositivos */
    suspend fun obtenerHistorialCompleto(limite: Int = 50): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT id,bpm,estado,dispositivo,hora,created_at
FROM lecturas_fc
ORDER BY created_at DESC
LIMIT $1""".trimIndent(),
                    params = listOf(limite)
                )
            ).rows
        }

    /** Estadísticas por dispositivo */
    suspend fun obtenerEstadisticas(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT dispositivo,
ROUND(AVG(bpm)) AS bpm,
'Promedio' AS estado,
MAX(hora) AS hora
FROM lecturas_fc
GROUP BY dispositivo""".trimIndent()
                )
            ).rows
        }

    /** Enviar lectura mock de TV */
    suspend fun enviarLecturaTvMock() {
        withContext(Dispatchers.IO) {
            val hora = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """INSERT INTO lecturas_fc (bpm, estado, dispositivo, hora) VALUES (72, 'Normal', 'tv', $1)""",
                    params = listOf(hora)
                )
            )
        }
    }
}
