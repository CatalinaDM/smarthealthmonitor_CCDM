package mx.utng.smarthealthmonitor.tv.ccdm.domain.model
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

data class TvUiState(
    val lecturas    : List<LecturaFC> = emptyList(),
    val estadisticas: List<LecturaFC> = emptyList(),
    val analisisAvanzado: List<LecturaFC> = emptyList(),
    val fcActual    : Int             = 0,
    val fcEstado    : String          = "Normal",
    val ultimaHora  : String          = "",
    val isLoading   : Boolean         = true,
    val error       : String?         = null,
)
