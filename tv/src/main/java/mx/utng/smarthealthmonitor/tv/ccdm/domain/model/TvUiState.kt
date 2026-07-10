package mx.utng.smarthealthmonitor.tv.ccdm.domain.model
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

data class TvUiState(
    val lecturas    : List<LecturaFC> = emptyList(),
    val fcActual    : Int             = 0,
    val isLoading   : Boolean         = true,
    val error       : String?         = null,
)
