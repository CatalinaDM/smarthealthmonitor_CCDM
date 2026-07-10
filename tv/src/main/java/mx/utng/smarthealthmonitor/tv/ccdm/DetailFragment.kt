package mx.utng.smarthealthmonitor.tv.ccdm


import android.os.Bundle
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import androidx.leanback.widget.OnActionClickedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.filament.View
import kotlinx.coroutines.launch
import mx.utng.smarthealthmonitor_shared_ccdm.db.LecturaFC
import mx.utng.smarthealthmonitor_shared_ccdm.db.SmartHealthDB

class DetailFragment : DetailsSupportFragment(),
    OnActionClickedListener {

    companion object {
        const val ARG_LECTURA_ID = "lectura_id"
        const val ACTION_PLAY    = 1L
        const val ACTION_BACK    = 2L

        fun newInstance(lecturaId: Int): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().also {
                    it.putInt(ARG_LECTURA_ID, lecturaId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getInt(ARG_LECTURA_ID) ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            // Buscar la lectura en Room por ID
            val lectura = SmartHealthDB.getDatabase(requireContext())
                .lecturaDao().obtenerPorId(id)
            lectura?.let { construirDetalle(it) }
        }
    }

    private fun construirDetalle(lectura: LecturaFC) {
        val selector = ClassPresenterSelector()

        val dpPresenter = FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter()
        )
        dpPresenter.setOnActionClickedListener(this)
        selector.addClassPresenter(DetailsOverviewRow::class.java, dpPresenter)

        val row = DetailsOverviewRow(lectura)
        // Ícono de corazón como imagen del detalle
        val iconRes = if (lectura.esNormal)
            android.R.drawable.ic_menu_compass  // placeholder OK
        else
            android.R.drawable.ic_dialog_alert  // placeholder error
        row.imageDrawable = ContextCompat.getDrawable(requireContext(), iconRes)

        // Botones de acción
        val actions = ArrayObjectAdapter()
        actions.add(Action(ACTION_PLAY, "▶ Reproducir alerta"))
        actions.add(Action(ACTION_BACK, "← Volver al historial"))
        row.actionsAdapter = actions

        val adapter = ArrayObjectAdapter(selector)
        adapter.add(row)
        this.adapter = adapter
    }

    override fun onActionClicked(action: Action) {
        when (action.id) {
            ACTION_PLAY -> {
                // TODO Ej.02: navegar al PlaybackFragment
                Toast.makeText(context, "Reproducir", Toast.LENGTH_SHORT).show()
            }
            ACTION_BACK -> requireActivity().onBackPressed()
        }
    }
}
