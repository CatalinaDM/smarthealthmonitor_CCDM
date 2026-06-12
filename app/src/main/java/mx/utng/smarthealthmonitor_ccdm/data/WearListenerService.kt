package mx.utng.smarthealthmonitor_ccdm.data

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
    class WearListenerService : WearableListenerService() {

        companion object {
            const val PATH_FC    = "/smarthealthmonitor/fc"
            const val PATH_PASOS = "/smarthealthmonitor/pasos"

            const val PATH_SPO2 = "/smarthealthmonitor/spo2"
            private const val TAG = "WearListener"
        }

        private val serviceScope = CoroutineScope(Dispatchers.IO)

        override fun onMessageReceived(messageEvent: MessageEvent) {
            val data = String(messageEvent.data)
            val path = messageEvent.path

            when (path) {
                PATH_FC -> {
                    val bpm = data.toIntOrNull() ?: return
                    serviceScope.launch {
                        SmartHealthRepository.actualizarFC(bpm)
                    }
                }

                PATH_PASOS -> {
                    val pasos = data.toIntOrNull() ?: return
                    serviceScope.launch {
                        SmartHealthRepository.actualizarPasos(pasos)
                    }
                }

                PATH_SPO2 -> {
                    val spo2 = data.toIntOrNull() ?: return
                    serviceScope.launch {
                        SmartHealthRepository.actualizarSpO2(spo2)
                    }
                }
            }
        }
    }