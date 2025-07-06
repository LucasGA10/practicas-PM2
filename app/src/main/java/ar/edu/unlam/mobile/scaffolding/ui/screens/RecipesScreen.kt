package ar.edu.unlam.mobile.scaffolding.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.ui.components.RecipeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category

object NavDestinations {
    const val RECIPE_ID_ARG = "recipeId"
}

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
    navController: NavController
) {
    val recipes: List<Recipe> by viewModel.recipes.collectAsState()
    val selectedFilterTags: List<Category> by viewModel.selectedFilterTags.collectAsState()

    val back: (() -> Unit)? = {
        navController.popBackStack()
    }

    val allAvailableCategories: List<Category> = Category.entries
    // Estado para controlar la visibilidad de los filtros
    var showFilters by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopBar("Recetas", back) }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.2f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { showFilters = !showFilters }) { // Alterna la visibilidad
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = if (showFilters) "Ocultar filtros" else "Mostrar filtros"
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (showFilters) "Ocultar" else "Filtrar")
                }
                Text(
                    text = "${recipes.size} ${if (recipes.size == 1) "Resultado" else "Resultados"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            // Mostrar FlowRow con FilterChips solo si showFilters es true
            if (showFilters) {
                if (allAvailableCategories.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 8.dp), // Ajusta padding si es necesario
                    ) {
                        allAvailableCategories.forEach { category ->
                            FilterChip(
                                selected = selectedFilterTags.contains(category),
                                onClick = { viewModel.toggleFilterTag(category) },
                                label = { Text(category.name) },
                                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        "No hay categorías de filtro disponibles.",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (recipes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 8.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedFilterTags.isNotEmpty()) {
                        Text(
                            "No hay recetas que coincidan con todas las tags seleccionadas.",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            "No hay recetas disponibles o cargando...", // Puede tener un indicador de carga aquí
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 5.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onFavoriteClick = { /* viewModel.toggleFavorite(it) */ },
                            onClickAction = {
                                navController.navigate("Preparation_screen/${recipe.id}")
                            }
                        )

                        if (recipes.indexOf(recipe) < recipes.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp),
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }

                }
            }
        }
    }
}