package mx.utng.smarthealthmonitor_ccdm
import android.app.Application
import mx.utng.smarthealthmonitor_ccdm.data.SmartHealthRepository

class SmartHealthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Room al abrir la aplicación
        SmartHealthRepository.init(this) // inicializar Room
    }
}