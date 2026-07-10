package mx.utng.smarthealthmonitor.tv.ccdm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.TvCatalogScreen
import mx.utng.smarthealthmonitor.tv.ccdm.sync.TvSyncClient
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SmartHealthRepository.init(applicationContext)
        TvSyncClient.start(lifecycleScope)

        setContent {
            TvCatalogScreen()
        }
    }
}