package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.screens.HistoryScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.PreparationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.RecipeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserProgressScreen
import ar.edu.unlam.mobile.scaffolding.ui.theme.DietappV2Theme
import com.ar.unlam.ddi.ui.CodiaMainView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.teal_200, null)
        setContent {
            DietappV2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

object NavDestinations {
    const val HOME_ROUTE = "home"
    const val RECIPE_LIST_ROUTE = "recipes" // O como la hayas llamado
    const val PREPARATION_ROUTE_WITH_ARG = "preparation/{recipeId}"
    const val RECIPE_ARGUMENT = "recipeId"
    const val HISTORIAL_ROUTE = "historial"

    // Añade la ruta para userProgres aquí también si quieres consistencia
    const val USER_PROGRESS_ROUTE_WITH_ARG = "userProgress/{id}"
}

@Composable
fun MainScreen() {
    val controller = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Aqui se definen las rutas donde NO se mostrara la BottomBar
    // Usa las plantillas de ruta completas, incluyendo los placeholders de argumentos
    val routesWithoutBottomBar =
        setOf(
            NavDestinations.PREPARATION_ROUTE_WITH_ARG,
            // Puedes añadir más rutas aquí si es necesario
            // "otra_pantalla_sin_bottom_bar"
        )
    val showBottomBar =
        currentRoute != null &&
            !routesWithoutBottomBar.any { routePattern ->
                // Si la ruta actual es exactamente igual a una de las rutas sin argumentos
                if (currentRoute == routePattern) {
                    true
                }
                // Si el patrón de ruta contiene un placeholder de argumento
                else if (routePattern.contains("{") && routePattern.contains("}")) {
                    // Crea una expresión regular para que coincida con el patrón de ruta
                    // Ej: "preparation/{recipeId}" se convierte en "preparation/[^/]+"
                    val regexPattern = routePattern.replace(Regex("\\{.*?\\}"), "[^/]+")
                    currentRoute.matches(Regex(regexPattern))
                }
                // Si no hay coincidencia
                else {
                    false
                }
            }

    Scaffold(
        bottomBar = {
            if (showBottomBar) { // Usa la variable corregida
                BottomBar(controller = controller)
            }
        },
    ) { paddingValue ->
        NavHost(
            navController = controller,
            startDestination = NavDestinations.HOME_ROUTE,
            modifier = Modifier.padding(paddingValue),
        ) {
            composable(NavDestinations.HOME_ROUTE) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CodiaMainView()
                }
            }

            composable(
                route = NavDestinations.USER_PROGRESS_ROUTE_WITH_ARG,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { navBackStackEntry ->
                val userId = navBackStackEntry.arguments?.getInt("id")
                if (userId != null) {
                    UserProgressScreen(navController = controller)
                } else {
                    Text("Error: ID de usuario no encontrado para Progreso.")
                }
            }

            composable(NavDestinations.RECIPE_LIST_ROUTE) {
                RecipeScreen(navController = controller)
            }

            composable(
                route = NavDestinations.PREPARATION_ROUTE_WITH_ARG,
                arguments = listOf(navArgument(NavDestinations.RECIPE_ARGUMENT) { type = NavType.IntType }),
            ) {
                PreparationScreen(navController = controller)
            }

            composable(NavDestinations.HISTORIAL_ROUTE) {
                HistoryScreen(navController = controller)
            }
        }
    }
}
