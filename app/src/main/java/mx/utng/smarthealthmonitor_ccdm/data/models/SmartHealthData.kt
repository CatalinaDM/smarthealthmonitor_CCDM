package mx.utng.smarthealthmonitor_ccdm.data.models
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

//data class LecturaFC(
//    val id: Int,
//    val valorBpm: Int,
//    val hora: String,
//    val esNormal: Boolean = valorBpm in 60..100
//)
//
// Datos de prueba para desarrollo (mock data)
object MockData {
    val historialFC = listOf(
        LecturaFC(1, 78, 1717410000000, "11:00"),
        LecturaFC(2, 82, 1717410000000,"10:30"),
        LecturaFC(3, 76, 1717410000000,"10:00"),
        LecturaFC(4, 95, 1717410000000,"09:30", false),  // fuera de rango
        LecturaFC(5, 71, 1717410000000,"09:00"),
        LecturaFC(6, 80, 1717410000000,"08:30"),
        LecturaFC(7, 74, 1717410000000,"08:00")
    )
    var fcActual = 78
    var pasosActual = 4250
}


