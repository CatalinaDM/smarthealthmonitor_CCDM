package mx.utng.smarthealthmonitor_ccdm.navigation
sealed class Screen(val route: String) {
    object Login     : Screen("login")
    object Dashboard : Screen("dashboard")
    object Historial : Screen("historial")
    object Alerta    : Screen("alerta")
}




