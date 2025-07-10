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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.screens.HistoryScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.PreparationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.RecipeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserScreen
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
    const val RECIPE_LIST_ROUTE = "Recipes"
    const val PREPARATION_ROUTE_PREFIX = "Preparation_screen" // Prefijo para la ruta
    const val RECIPE_ARGUMENT = "recipeId" // Nombre del argumento
    const val HISTORIAL = "Historial" // Nombre del argumento
    const val PREPARATION_ROUTE_WITH_ARG = "$PREPARATION_ROUTE_PREFIX/{$RECIPE_ARGUMENT}" // Ruta completa con el argumento
}

@Composable
fun MainScreen() {
    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
    // a través del back stack
    val controller = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(controller = controller) },
    ) { paddingValue ->
        // NavHost es el componente que funciona como contenedor de los otros componentes que
        // podrán ser destinos de navegación.
        NavHost(
            navController = controller,
            startDestination = "home",
            modifier = Modifier.padding(paddingValue),
        ) {
            // composable es el componente que se usa para definir un destino de navegación.
            // Por parámetro recibe la ruta que se utilizará para navegar a dicho destino.
            composable("home") {
                // Home es el componente en sí que es el destino de navegación.
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    CodiaMainView()
                }
            }
            composable(
                route = "user/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("id") ?: "1"
                UserScreen(userId = id)
            }
            composable(NavDestinations.RECIPE_LIST_ROUTE) {
                RecipeScreen(navController = controller)
            }
            composable(
                route = NavDestinations.PREPARATION_ROUTE_WITH_ARG,
                arguments =
                    listOf(
                        navArgument(NavDestinations.RECIPE_ARGUMENT) { // NavDestinations.RECIPE_ARGUMENT es "recipeId"
                            type = NavType.IntType // El tipo se define directamente aquí
                        },
                    ),
            ) { // El backStackEntry ya no se usa directamente aquí si el ViewModel lo maneja
                PreparationScreen(navController = controller)
            }
            composable(route = NavDestinations.HISTORIAL) {
                // Aquí podrías agregar una pantalla de favoritos si es necesario
                HistoryScreen(navController = controller)
            }
        }
    }
}
