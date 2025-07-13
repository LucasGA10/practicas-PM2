package ar.edu.unlam.mobile.scaffolding.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.scaffolding.NavDestinations

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentUserId = "1" // ID de usuario fijo para testeo

    NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
        // --- Home Item ---
        val homeRoute = "home"
        val isHomeSelected = currentDestination?.hierarchy?.any { it.route == homeRoute } == true
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = {
                if (!isHomeSelected) {
                    controller.navigate(homeRoute) {
                        val startDestinationRoute = controller.graph.findNode(controller.graph.startDestinationId)?.route
                        if (startDestinationRoute != null) {
                            popUpTo(startDestinationRoute)
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                ),
            label = {
                if (isHomeSelected) {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            },
            alwaysShowLabel = false,
        )

        // --- Progreso Item ---
        val userProgressRoutePattern = NavDestinations.USER_PROGRESS_ROUTE_WITH_ARG

        val isProgressSelected = currentDestination?.hierarchy?.any { navDest ->
            navDest.route == userProgressRoutePattern
        } == true

        NavigationBarItem(
            //Usa la logica simplificada para verificar el ID.
            selected = isProgressSelected,
            onClick = {
                val routeToNavigate = userProgressRoutePattern.replace("{id}", currentUserId)
                // Solo navega si la ruta actual (con su argumento resuelto si es el mismo patrón)
                // Esta condición de onClick podría necesitar refinamiento para evitar navegación innecesaria
                // si ya estás en userProgress/1.
                var currentlyOnTarget = false
                if (currentDestination?.route == userProgressRoutePattern) {
                    val currentEntryId = navBackStackEntry?.arguments?.getInt("id")?.toString()
                    if (currentEntryId == currentUserId) {
                        currentlyOnTarget = true
                    }
                }

                if (!currentlyOnTarget) {
                    //isProgressSelected no verifica el ID.
                    controller.navigate(routeToNavigate) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Leaderboard,
                    contentDescription = "Progreso",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                ),
            label = {
                if (isProgressSelected) {
                    Text(
                        text = "Dieta",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            },
            alwaysShowLabel = false,
        )

        // --- Recipes Item ---
        val recipesRoute = "recipes"
        val isRecipesSelected = currentDestination?.hierarchy?.any { it.route == recipesRoute } == true
        NavigationBarItem(
            selected = isRecipesSelected,
            onClick = {
                if (!isRecipesSelected) {
                    controller.navigate(recipesRoute) {
                        launchSingleTop = true
                        restoreState = true
                        // Considera popUpTo
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Recipes",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                ),
            label = {
                if (isRecipesSelected) {
                    Text(
                        text = "Recetas",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            },
            alwaysShowLabel = false,
        )

        // --- Historial Item ---
        val historyRoute = NavDestinations.HISTORY_ROUTE
        val isHistorialSelected = currentDestination?.hierarchy?.any { it.route == historyRoute } == true
        NavigationBarItem(
            selected = isHistorialSelected,
            onClick = {
                if (!isHistorialSelected) {
                    controller.navigate(historyRoute) {
                        launchSingleTop = true
                        restoreState = true
                        // Considera popUpTo
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = "Historial",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                ),
            label = {
                if (isHistorialSelected) {
                    Text(
                        text = "Historial",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                    )
                }
            },
            alwaysShowLabel = false,
        )
    }
}