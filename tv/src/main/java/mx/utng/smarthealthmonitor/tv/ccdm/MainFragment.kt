package mx.utng.smarthealthmonitor.tv.ccdm
import android.os.Bundle
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import mx.utng.smarthealthmonitor_shared_ccdm.db.MockData
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

class MainFragment : BrowseSupportFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del BrowseFragment
        title        = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // Color de la marca en el sidebar
        brandColor = resources.getColor(R.color.sh_primary, null)

        cargarFilas()
    }

    private fun cargarFilas() {
        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

        // ── Fila 1: Estado actual (FC + Pasos) ───────────
        val estadoAdapter = ArrayObjectAdapter(FCCardPresenter())
        // Datos simulados — en Ej.03 vendrán de Room
        estadoAdapter.add(LecturaFC(id=0, valorBpm=88, hora="Ahora"))
        estadoAdapter.add(LecturaFC(id = 1, valorBpm = 4250, hora = "Pasos"))
        rowsAdapter.add(ListRow(HeaderItem("Estado actual"), estadoAdapter))

        // ── Fila 2: Historial de FC ────────────────────
        val histAdapter = ArrayObjectAdapter(FCCardPresenter())
        MockData.historialFC.forEach { histAdapter.add(it) }
        rowsAdapter.add(ListRow(HeaderItem("Historial FC"), histAdapter))

        // ── Fila 3: Alertas recientes ────────────────────
        val alertasAdapter = ArrayObjectAdapter(FCCardPresenter())

        val alertas = listOf(
            LecturaFC(
                id = 10,
                valorBpm = 120,
                hora = "FC alta - 21:30"
            ),
            LecturaFC(
                id = 11,
                valorBpm = 45,
                hora = "FC baja - 20:15"
            ),
            LecturaFC(
                id = 12,
                valorBpm = 0,
                hora = "Sin señal - 19:50"
            )
        )

        alertas.forEach {
            alertasAdapter.add(it)
        }

        rowsAdapter.add(
            ListRow(
                HeaderItem("Alertas recientes"),
                alertasAdapter
            )
        )
        this.adapter = rowsAdapter
    }

}
