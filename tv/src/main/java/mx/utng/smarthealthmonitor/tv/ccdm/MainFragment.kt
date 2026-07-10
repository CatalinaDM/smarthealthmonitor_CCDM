package mx.utng.smarthealthmonitor.tv.ccdm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC

class MainFragment : BrowseSupportFragment() {

    private val viewModel: TvViewModel by viewModels()

    private lateinit var histAdapter: ArrayObjectAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = "SmartHealth TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        brandColor = resources.getColor(R.color.sh_primary, null)

        cargarFilas()
        observarDatos()
        // Listener cuando el usuario da clic en una tarjeta
        setOnItemViewClickedListener { itemViewHolder, item, rowViewHolder, row ->

            if (item is LecturaFC) {

                val detail = DetailFragment.newInstance(item.id)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, detail)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    private fun cargarFilas() {

        val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())


        // Fila que será actualizada con Room
        histAdapter = ArrayObjectAdapter(FCCardPresenter())


        rowsAdapter.add(
            ListRow(
                HeaderItem("Historial FC"),
                histAdapter
            )
        )


        this.adapter = rowsAdapter
    }



    private fun observarDatos() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(
                Lifecycle.State.STARTED
            ) {

                viewModel.historial.collect { lecturas ->

                    histAdapter.clear()

                    lecturas.forEach { lectura ->
                        histAdapter.add(lectura)
                    }

                }
            }
        }
    }
}