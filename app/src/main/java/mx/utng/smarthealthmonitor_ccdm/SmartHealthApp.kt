package mx.utng.smarthealthmonitor_ccdm
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import mx.utng.smarthealthmonitor_ccdm.sync.TvSyncServer
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

import mx.utng.smarthealthmonitor_ccdm.mqtt.MqttAppService

class SmartHealthApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob())
    lateinit var mqttService: MqttAppService

    override fun onCreate() {
        super.onCreate()

        // Inicializar Room al abrir la aplicación
        SmartHealthRepository.init(this) // inicializar Room

        mqttService = MqttAppService(context = this)
        mqttService.connect()

        // Arrancar el servidor de sincronización para la TV
        TvSyncServer.start(appScope)

    }
}