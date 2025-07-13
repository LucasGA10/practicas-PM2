package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.model.SortCriterion
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import ar.edu.unlam.mobile.scaffolding.ui.components.HistoryRecipeCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    navController: NavController,
) {
    val recipes: List<RecipeListItem> by viewModel.recipes.collectAsState()
    val selectedFilterTags: List<Category> by viewModel.selectedFilterTags.collectAsState()
    val currentSortCriterion: SortCriterion by viewModel.sortCriterion.collectAsState() // Obtener estado de ordenación

    val allAvailableCategories: List<Category> = Category.entries
    var showFilters by remember { mutableStateOf(false) }

    var showSortMenu by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope() // Necesario para llamar a scrollToItem
    var headerRowHeightPx by remember { mutableFloatStateOf(0f) } // Altura del Row de Filtros/Resultados
    var filterChipsHeightPx by remember { mutableFloatStateOf(0f) } // Altura del FlowRow de FilterChips
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }
    val localDensity = LocalDensity.current

    val totalCollapsibleHeaderHeightPx by remember(headerRowHeightPx, filterChipsHeightPx, showFilters) {
        derivedStateOf {
            headerRowHeightPx + if (showFilters) filterChipsHeightPx else 0f
        }
    }

    LaunchedEffect(currentSortCriterion, selectedFilterTags, recipes.size) { // Observa cambios
        if (recipes.isNotEmpty()) { // Solo hacer scroll si hay items
            // Considera si quieres que esto se ejecute cada vez que recipes.size cambie también
            lazyListState.animateScrollToItem(index = 0)
        }
    }

    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    val delta = available.y
                    if (totalCollapsibleHeaderHeightPx == 0f) return Offset.Zero
                    val newOffset = headerOffsetPx + delta
                    val oldOffset = headerOffsetPx
                    headerOffsetPx = newOffset.coerceIn(-totalCollapsibleHeaderHeightPx, 0f)
                    return Offset(0f, headerOffsetPx - oldOffset)
                }
            }
        }

    fun getSortCriterionText(criterion: SortCriterion): String {
        return when (criterion) {
            SortCriterion.NONE -> "Por Defecto"
            SortCriterion.NAME_ASC -> "Nombre (A-Z)"
            SortCriterion.NAME_DESC -> "Nombre (Z-A)"
            SortCriterion.RATING_ASC -> "Rating (Menor)"
            SortCriterion.RATING_DESC -> "Rating (Mayor)"
            SortCriterion.DIFFICULTY_ASC -> "Dificultad (Asc)"
            SortCriterion.DIFFICULTY_DESC -> "Dificultad (Desc)"
        }
    }

    Scaffold { scaffoldPaddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddingValues) // Aplicar padding del Scaffold al Box externo
                    .nestedScroll(nestedScrollConnection),
        ) {
            // Contenido de la Lista (LazyColumn)
            LazyColumn(
                state = lazyListState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 5.dp),
                // Padding horizontal para el contenido de la lista
                contentPadding =
                    PaddingValues(
                        // El padding superior ahora solo necesita ser el espacio *adicional* al header
                        // cuando el header está completamente visible, más tu padding original.
                        // O, más simple, el espacio total que el header ocupará.
                        top = with(localDensity) { totalCollapsibleHeaderHeightPx.toDp() + 6.dp }, // Espacio para el header + padding
                        bottom = 6.dp, // Padding inferior general, el del scaffold ya se aplicó al Box
                    ),
            ) {
                // ... (tu lógica de items: isEmpty, items(recipes), etc.)
                if (recipes.isEmpty()) {
                    item {
                        Column(
                            modifier =
                                Modifier
                                    .fillParentMaxSize()
                                    .padding(horizontal = 3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                if (selectedFilterTags.isNotEmpty()) "No hay recetas que coincidan." else "No hay recetas disponibles.",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                } else {
                    items(recipes, key = { it.id }) { recipe ->
                        HistoryRecipeCard(
                            recipe = recipe,
                            onFavoriteClick = { viewModel.toggleFavorite(recipe.id) },
                            onClickAction = {
                                navController.navigate("preparation/${recipe.id}")
                            },
                        )
                        if (recipes.indexOf(recipe) < recipes.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                }
            }

            // --- ESTE ES EL HEADER QUE SE MUEVE ---
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter) // Asegura que esté en la parte superior del Box
                        .graphicsLayer { // Usa graphicsLayer para moverlo sin afectar el layout
                            translationY = headerOffsetPx
                        }
                        .background(MaterialTheme.colorScheme.surface) // Darle un fondo para que no sea transparente
                        .padding(horizontal = 16.dp, vertical = 8.dp), // Padding interno del header
            ) {
                // Row para "Filtros", "Resultados", botón de ordenar
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .onSizeChanged { size -> // Obtener la altura de esta fila
                                headerRowHeightPx = size.height.toFloat()
                            }
                            .padding(bottom = if (showFilters) 8.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Resultados: ${recipes.size}", style = MaterialTheme.typography.bodyMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { showFilters = !showFilters }) {
                            Text(if (showFilters) "Ocultar Filtros" else "Mostrar Filtros")
                        }
                        Spacer(Modifier.width(8.dp))
                        // Botón de Ordenar y DropdownMenu (ejemplo)
                        Box {
                            TextButton(onClick = { showSortMenu = true }) {
                                Text("Ordenar: ${getSortCriterionText(currentSortCriterion)}")
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Ordenar")
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                            ) {
                                SortCriterion.entries.forEach { criterion ->
                                    DropdownMenuItem(
                                        text = { Text(getSortCriterionText(criterion)) },
                                        onClick = {
                                            viewModel.setSortCriterion(criterion)
                                            showSortMenu = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }

                // FlowRow para los FilterChips (si showFilters es true)
                // Usar AnimatedVisibility para una mejor transición
                AnimatedVisibility(visible = showFilters) {
                    FlowRow( // ExperimentalLayoutApi
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .onSizeChanged { size ->
                                    filterChipsHeightPx = size.height.toFloat()
                                }
                                .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // En Compose se llama verticalArrangement
                    ) {
                        allAvailableCategories.forEach { category ->
                            FilterChip(
                                selected = selectedFilterTags.contains(category),
                                onClick = { viewModel.toggleFilterTag(category) },
                                label = { Text(category.name) }, // Asumiendo que Category tiene displayName
                            )
                        }
                    }
                }
                HorizontalDivider() // Divisor al final del header
            }
        }
    }

    /*val tabs = listOf("Recetas", "Favoritas")

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            contentColor = PrimaryGreen,
            containerColor = Color.White,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> HistoryRecipeScreen(viewModel, navController)
                1 -> FavoritesScreen(viewModel, navController)
            }
        }
    }*/
}

@Composable
fun HistoryRecipeScreen(
    viewModel: HistoryViewModel,
    navController: NavController,
) {
    /*Scaffold(
    ) { scaffoldPaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPaddingValues)
                .nestedScroll(nestedScrollConnection)
        ) {
            // Contenido de la Lista (LazyColumn)
            // Su padding superior se ajustará para dejar espacio al header
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 5.dp),
                contentPadding = PaddingValues(
                    top = with(localDensity) {
                        (totalCollapsibleHeaderHeightPx + headerOffsetPx).toDp() + 6.dp // 6.dp es tu padding original
                    },
                    bottom = scaffoldPaddingValues.calculateBottomPadding() + 6.dp
                )
            ) {
                if (recipes.isEmpty()) {
                    item { // Necesitas envolver el contenido de "lista vacía" en un item o items
                        Column(
                            modifier = Modifier
                                .fillParentMaxSize() // Ocupa todo el espacio del viewport de LazyColumn
                                .padding(horizontal = 3.dp), // Ajusta según el padding del LazyColumn
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (selectedFilterTags.isNotEmpty()) {
                                Text(
                                    "No hay recetas que coincidan con todas las tags seleccionadas.",
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    "No hay recetas disponibles o cargando...",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(recipes, key = { it.id }) { recipe ->
                        HistoryRecipeCard(
                            recipe = recipe,
                            onFavoriteClick = { recipeToToggle ->
                                viewModel.toggleFavorite(recipeToToggle.id)
                            },
                            onClickAction = {
                                navController.navigate("Preparation_screen/${recipe.id}")
                            }
                        )
                        if (recipes.indexOf(recipe) < recipes.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                thickness = 0.8.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }

        }
    }*/
}

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
