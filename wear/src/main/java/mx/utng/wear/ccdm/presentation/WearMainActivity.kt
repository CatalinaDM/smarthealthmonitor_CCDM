/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package mx.utng.wear.ccdm.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import mx.utng.wear.ccdm.presentation.theme.SmartHealthMonitorTheme
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

class WearMainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var wearDataSender: WearDataSender
    private var isRegistered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("PRUEBA_CATI", "ENTRO A ONCREATE")
        super.onCreate(savedInstanceState)


        // Inicializar el repositorio compartido en el módulo wear
        SmartHealthRepository.init(applicationContext)

        wearDataSender = WearDataSender(applicationContext)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        val permissions = arrayOf(
            android.Manifest.permission.BODY_SENSORS,
            android.Manifest.permission.ACTIVITY_RECOGNITION,
        )


        val allGranted = permissions.all {
            androidx.core.content.ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            registerHealthServices()
        } else {
            permissionLauncher.launch(permissions)
        }

        setContent {
            SmartHealthMonitorTheme {
                // Reemplazado con WearNavGraph
                SmartHealthWearNavGraph()
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->

        result.forEach { (permiso, concedido) ->
            Log.e(
                "PRUEBA_CATI",
                "$permiso -> $concedido"
            )
        }

        val allGranted = result.values.all { it }

        if (allGranted) {
            registerHealthServices()
        }
    }
    private fun registerHealthServices() {
        if (isRegistered) return
        isRegistered = true

        // Registrar el HealthDataService para monitorear el sensor de FC en segundo plano
        lifecycleScope.launch {
            try {
                HealthDataService.registrar(applicationContext)
            } catch (e: Exception) {
            }
        }

        // Registrar el SensorEventListener para actualización en tiempo real e inmediata
        heartRateSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: Log.w("WearMainActivity", "El dispositivo no tiene sensor de ritmo cardíaco")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_HEART_RATE) {
            val bpm = event.values[0].toInt()
            lifecycleScope.launch {
                // Actualizar el repositorio local para que la UI del reloj cambie de inmediato
                SmartHealthRepository.actualizarFC(bpm)
                // Enviar el dato en tiempo real a la app del celular
                wearDataSender.enviarFC(bpm)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            Log.d("WearMainActivity", "SensorEventListener desregistrado")
        }
    }
}