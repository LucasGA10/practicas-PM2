package ar.edu.unlam.mobile.scaffolding

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.res.ResourcesCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ar.edu.unlam.mobile.scaffolding.ui.components.BottomBar
import ar.edu.unlam.mobile.scaffolding.ui.screens.AuthViewModel
import ar.edu.unlam.mobile.scaffolding.ui.screens.DietFormScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.LoginScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.SingupScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.UserAuthState
import ar.edu.unlam.mobile.scaffolding.ui.screens.recipes.HistoryScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.recipes.PreparationScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.recipes.RecipeScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserProgressScreen
import ar.edu.unlam.mobile.scaffolding.ui.screens.user.UserScreen
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
    const val LOGIN_ROUTE = "login"
    const val CREATE_ACCOUNT_ROUTE = "SingupScreen"
    const val LOADING_ROUTE = "loading"
    const val DIET_FORM_ROUTE = "dietForm"
    const val HOME_ROUTE = "home"
    const val RECIPE_LIST_ROUTE = "recipes" // O como la hayas llamado
    const val PREPARATION_ROUTE_WITH_ARG = "preparation/{recipeId}"
    const val RECIPE_ARGUMENT = "recipeId"
    const val HISTORY_ROUTE = "historial"
    const val USER_PROFILE_ROUTE = "user"
    const val USER_PROGRESS_ROUTE_WITH_ARG = "userProgress/{id}"
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(), // Renombrado de 'controller' a 'navController' por convención
    authViewModel: AuthViewModel = hiltViewModel(), // Inyecta el AuthViewModel
) {
    val userAuthState by authViewModel.userAuthState.collectAsStateWithLifecycle()

    val startDestination = remember(userAuthState) { // Se recalcula si userAuthState cambia
        when (userAuthState) {
            is UserAuthState.Unauthenticated -> NavDestinations.LOGIN_ROUTE
            is UserAuthState.AuthenticatedWithoutDiet -> NavDestinations.DIET_FORM_ROUTE
            is UserAuthState.AuthenticatedWithDiet -> NavDestinations.HOME_ROUTE
            UserAuthState.Loading -> null // Aún no está listo
        }
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Lógica de BottomBar (la mantengo como la tenías)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val routesWithoutBottomBar =
            setOf(
                NavDestinations.LOGIN_ROUTE,
                NavDestinations.CREATE_ACCOUNT_ROUTE,
                NavDestinations.DIET_FORM_ROUTE,
                NavDestinations.PREPARATION_ROUTE_WITH_ARG
            )
        val showBottomBar =
            currentRoute != null &&
                !routesWithoutBottomBar.any { routePattern ->
                    if (currentRoute == routePattern) {
                        true
                    } else if (routePattern.contains("{") && routePattern.contains("}")) {
                        val regexPattern = routePattern.replace(Regex("\\{.*?\\}"), "[^/]+")
                        currentRoute.matches(Regex(regexPattern))
                    } else {
                        false
                    }
                }

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomBar(controller = navController)
                }
            },
        ) { paddingValue ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValue),
            ) {
                // Ruta de carga (puede ser simple si el NavHost solo se muestra después de cargar)
                composable(NavDestinations.LOADING_ROUTE) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                composable(NavDestinations.LOGIN_ROUTE) {
                    LoginScreen(navController = navController)
                }

                composable(NavDestinations.CREATE_ACCOUNT_ROUTE) {
                    SingupScreen(navController = navController)
                }

                composable(NavDestinations.HOME_ROUTE) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        CodiaMainView(/* navController si lo necesita */)
                    }
                }

                composable(NavDestinations.DIET_FORM_ROUTE) {
                    DietFormScreen(
                        navController = navController,
                        onSaveSuccess = {
                            navController.navigate(NavDestinations.HOME_ROUTE) {
                                popUpTo(NavDestinations.DIET_FORM_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                    )
                }

                composable(
                    route = NavDestinations.USER_PROGRESS_ROUTE_WITH_ARG,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { navBackStackEntry ->
                    UserProgressScreen(navController = navController)
                }

                composable(NavDestinations.RECIPE_LIST_ROUTE) {
                    RecipeScreen(navController = navController)
                }

                composable(
                    route = NavDestinations.PREPARATION_ROUTE_WITH_ARG,
                    arguments = listOf(navArgument(NavDestinations.RECIPE_ARGUMENT) { type = NavType.IntType }),
                ) {
                    PreparationScreen(navController = navController)
                }

                composable(NavDestinations.HISTORY_ROUTE) {
                    HistoryScreen(navController = navController)
                }

                composable(NavDestinations.USER_PROFILE_ROUTE) {
                    UserScreen(navController = navController)
                }
                // Añade el resto de tus rutas aquí
            }
        }
    }
}
