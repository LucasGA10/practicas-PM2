package ar.edu.unlam.mobile.scaffolding.ui.components

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
    val currentUserId = "1"

    NavigationBar(containerColor = Color(0xFF185529)) {
        // --- Home Item ---
        val isHomeSelected = currentDestination?.hierarchy?.any { it.route == "home" } == true
        NavigationBarItem(
            selected = isHomeSelected,
            onClick = { controller.navigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            },
            // Puedes personalizar el color del icono aquí
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                ),
            label = {
                // Solo muestra el texto si esta seleccionado
                if (isHomeSelected) {
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White, // Color del texto cuando está seleccionado
                    )
                }
            },
            alwaysShowLabel = false, // Importante para que no reserve espacio si no hay label
        )

        // --- Progreso Item ---
        val isProgresoSelected =
            currentDestination?.hierarchy?.any {
                it.route == "userProgress/$currentUserId" || it.route?.startsWith("userProgres/") == true
            } == true
        NavigationBarItem(
            selected = isProgresoSelected,
            onClick = { controller.navigate("userProgress/$currentUserId") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Leaderboard,
                    // painter = painterResource(id = com.ar.unlam.ddi.R.drawable.image_2027680),
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
                if (isProgresoSelected) {
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
        val isRecipesSelected = currentDestination?.hierarchy?.any { it.route == "recipes" } == true
        NavigationBarItem(
            selected = isRecipesSelected,
            onClick = { controller.navigate("recipes") },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    // painter = painterResource(id = com.ar.unlam.ddi.R.drawable.image_2027673),
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
        val isHistorialSelected = currentDestination?.hierarchy?.any { it.route == NavDestinations.HISTORIAL_ROUTE } == true
        NavigationBarItem(
            selected = isHistorialSelected,
            onClick = { controller.navigate(NavDestinations.HISTORIAL_ROUTE) },
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
