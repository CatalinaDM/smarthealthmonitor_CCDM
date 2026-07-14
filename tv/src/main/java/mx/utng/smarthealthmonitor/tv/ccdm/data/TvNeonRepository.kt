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

    // --- Consultas SQL Avanzadas (Reto Extra) ---

    /** Alertas de FC fuera de rango (últimas 24 horas) */
    suspend fun obtenerAlertasFc(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT id, bpm, estado, dispositivo, hora, created_at FROM lecturas_fc
WHERE (bpm < 60 OR bpm > 100)
  AND created_at > NOW() - INTERVAL '24 hours'
ORDER BY created_at DESC LIMIT 5""".trimIndent()
                )
            ).rows
        }

    /** Promedio de FC por hora del día */
    suspend fun obtenerPromedioPorHora(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT 
    0 AS id,
    ROUND(AVG(bpm))::integer AS bpm,
    'Promedio' AS estado,
    'Hora: ' || EXTRACT(HOUR FROM created_at) AS dispositivo,
    MAX(hora) AS hora,
    '' AS created_at
FROM lecturas_fc
GROUP BY EXTRACT(HOUR FROM created_at)
ORDER BY EXTRACT(HOUR FROM created_at) DESC LIMIT 5""".trimIndent()
                )
            ).rows
        }

    /** Lectura más reciente de cada dispositivo */
    suspend fun obtenerMasRecientePorDispositivo(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT DISTINCT ON (dispositivo)
       id, bpm, estado, dispositivo, hora, created_at
FROM lecturas_fc
ORDER BY dispositivo, created_at DESC""".trimIndent()
                )
            ).rows
        }

    /** Detección de taquicardia sostenida (>100 bpm por 3+ lecturas seguidas) */
    suspend fun obtenerTaquicardiaSostenida(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            NeonClient.api.executeQuery(
                connStr = NeonClient.CONN_STRING,
                request = NeonRequest(
                    query = """SELECT 
    0 AS id,
    COUNT(*)::integer AS bpm,
    'Taquicardia' AS estado,
    'Alerta 1h' AS dispositivo,
    MAX(hora) AS hora,
    '' AS created_at
FROM lecturas_fc
WHERE bpm > 100
  AND created_at > NOW() - INTERVAL '1 hour'
HAVING COUNT(*) >= 3""".trimIndent()
                )
            ).rows
        }

    /** Combinar análisis avanzado en una sola lista para la UI */
    suspend fun obtenerAnalisisAvanzado(): List<LecturaFcDto> =
        withContext(Dispatchers.IO) {
            val resultados = mutableListOf<LecturaFcDto>()
            
            try { resultados.addAll(obtenerTaquicardiaSostenida()) } catch(e: Exception) {}
            try { 
                resultados.addAll(obtenerMasRecientePorDispositivo().map { 
                    it.copy(estado = "Reciente: " + it.estado) 
                }) 
            } catch(e: Exception) {}
            try { resultados.addAll(obtenerPromedioPorHora()) } catch(e: Exception) {}
            try { 
                resultados.addAll(obtenerAlertasFc().map { 
                    it.copy(dispositivo = "Alerta: " + it.dispositivo) 
                }) 
            } catch(e: Exception) {}
            
            return@withContext resultados
        }
}
