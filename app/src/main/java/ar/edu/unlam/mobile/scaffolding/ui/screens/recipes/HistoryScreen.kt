package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.ui.components.HistoryRecipeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavController,
) {
    val completedRecipes: List<RecipeHistoryItem> by viewModel.completedRecipes.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val error: String? by viewModel.error.collectAsState()

    Scaffold(
        topBar = { TopBar(title = "Historial") },
    ) { scaffoldPaddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddingValues) // Aplicar padding del Scaffold
                    .background(MaterialTheme.colorScheme.background),
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                    )
                }
                completedRecipes.isEmpty() -> {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            "Aún no has completado ninguna receta.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            "¡Anímate a cocinar y marca tus recetas como hechas!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(completedRecipes, key = { it.id }) { recipe ->
                            HistoryRecipeCard(
                                recipe = recipe,
                                onFavoriteClick = { viewModel.toggleFavorite(recipe.id) },
                                onClickAction = {
                                    navController.navigate("preparation/${recipe.id}")
                                },
                            )
                            if (completedRecipes.indexOf(recipe) < completedRecipes.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 0.8.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
/*
@Composable
fun FavoritesScreen(
    viewModel: HistoryViewModel,
    navController: NavController,
) {
    val favoriteRecipes: List<RecipeListItem> by viewModel.recipes.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favoriteRecipes) { recipe ->
            HistoryRecipeCard(
                recipe = recipe,
                onFavoriteClick = { viewModel.toggleFavorite(recipe.id) },
                onClickAction = { navController.navigate("preparation/${recipe.id}") },
            )
        }
    }
}
*/
