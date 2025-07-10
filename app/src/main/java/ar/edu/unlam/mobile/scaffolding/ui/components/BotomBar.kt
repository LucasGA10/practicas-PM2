package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.scaffolding.NavDestinations
import ar.edu.unlam.mobile.scaffolding.R

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    NavigationBar(containerColor = Color(0xFF185529)) {
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "home" } == true,
            onClick = { controller.navigate("home") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE2FAE8),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color(0xFF34A853),
                ),
            label = {
                androidx.compose.material3.Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        if (navBackStackEntry?.destination?.hierarchy?.any { it.route == "home" } == true) {
                            Color(
                                0xFFE2FAE8,
                            )
                        } else {
                            Color(0xFF34A853)
                        },
                )
            },
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "user/{id}" } == true,
            onClick = { controller.navigate("user/test") },
            icon = {
                Icon(
                    painter = painterResource(id = com.ar.unlam.ddi.R.drawable.image_2027680),
                    contentDescription = "Progreso",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE2FAE8),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color(0xFF34A853),
                ),
            label = {
                androidx.compose.material3.Text(
                    text = "Progreso",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        if (navBackStackEntry?.destination?.hierarchy?.any { it.route == "user/{id}" } == true) {
                            Color(
                                0xFFE2FAE8,
                            )
                        } else {
                            Color(0xFF34A853)
                        },
                )
            },
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "Recipes" } == true,
            onClick = { controller.navigate("recipes") },
            icon = {
                Icon(
                    painter = painterResource(id = com.ar.unlam.ddi.R.drawable.image_2027673),
                    contentDescription = "Recipes",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE2FAE8),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color(0xFF34A853),
                ),
            label = {
                androidx.compose.material3.Text(
                    text = "Recipes",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        if (navBackStackEntry?.destination?.hierarchy?.any { it.route == "Recipes" } == true) {
                            Color(
                                0xFFE2FAE8,
                            )
                        } else {
                            Color(0xFF34A853)
                        },
                )
            },
        )

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "Historial" } == true,
            onClick = { controller.navigate(NavDestinations.HISTORIAL) },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = "Historial",
                )
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE2FAE8),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color(0xFF34A853),
                ),
            label = {
                androidx.compose.material3.Text(
                    text = "Historial",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        if (navBackStackEntry?.destination?.hierarchy?.any { it.route == "Recetas" } == true) {
                            Color(
                                0xFFE2FAE8,
                            )
                        } else {
                            Color(0xFF34A853)
                        },
                )
            },
        )
    }
}
