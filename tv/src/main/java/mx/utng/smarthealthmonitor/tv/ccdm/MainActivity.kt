package mx.utng.smarthealthmonitor.tv.ccdm

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.TvCatalogScreen
import mx.utng.smarthealthmonitor_shared_ccdm.repository.SmartHealthRepository
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.TvDetailScreen
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.TvPlaybackScreen
import mx.utng.smarthealthmonitor.tv.ccdm.presentation.theme.SmartHealthTvTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SmartHealthRepository.init(applicationContext)
        setContent {
            // S12: Theme de Compose for TV envolviendo toda la navegación
            SmartHealthTvTheme {
                // S12: NavController reemplaza la llamada directa a TvCatalogScreen()
                val navController = rememberNavController()

                NavHost(navController, startDestination = "catalog") {

                    // S12: ruta del catálogo — ahora recibe onCardClick para navegar al detalle
                    composable("catalog") {
                        TvCatalogScreen(onCardClick = { lecturaId ->
                            navController.navigate("detail/$lecturaId")
                        })
                    }

                    // S12: nueva ruta — pantalla de detalle con argumento lecturaId
                    composable(
                        route = "detail/{lecturaId}",
                        arguments = listOf(navArgument("lecturaId") { type = NavType.IntType })
                    ) { backStack ->
                        val id = backStack.arguments?.getInt("lecturaId") ?: return@composable
                        TvDetailScreen(lecturaId = id, navController = navController)
                    }

                    // S12: nueva ruta — reproductor ExoPlayer
                    composable("playback") {
                        TvPlaybackScreen(navController = navController)
                    }
                }
            }
        }
    }
}
