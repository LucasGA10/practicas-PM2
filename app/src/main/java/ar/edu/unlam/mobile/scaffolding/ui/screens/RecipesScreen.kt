package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.domain.model.SortCriterion
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import ar.edu.unlam.mobile.scaffolding.ui.components.RecipeCard
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar
import com.ar.unlam.ddi.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecipeScreen(
    viewModel: RecipesViewModel = hiltViewModel(),
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

    LaunchedEffect(currentSortCriterion, selectedFilterTags) {
        if (recipes.isNotEmpty()) { // Solo hacer scroll si hay items
            coroutineScope.launch {
                lazyListState.animateScrollToItem(index = 0) // O scrollToItem(index = 0) para ir sin animación
            }
        }
    }

    // La altura total del header que puede colapsar (Row + FlowRow si está visible)
    val totalCollapsibleHeaderHeightPx by remember(headerRowHeightPx, filterChipsHeightPx, showFilters) {
        derivedStateOf {
            headerRowHeightPx + if (showFilters) filterChipsHeightPx else 0f
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
                    // Solo reaccionar al scroll si el header tiene alguna altura
                    if (totalCollapsibleHeaderHeightPx == 0f) return Offset.Zero

                    val newOffset = headerOffsetPx + delta
                    val oldOffset = headerOffsetPx
                    headerOffsetPx = newOffset.coerceIn(-totalCollapsibleHeaderHeightPx, 0f)

                    // Devolver la cantidad de scroll consumida por el header
                    // para que la lista no scrollee esa cantidad.
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

    Scaffold(
        topBar = { TopBar("Recetas") },
    ) { scaffoldPaddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddingValues)
                    .nestedScroll(nestedScrollConnection),
        ) {
            // Contenido de la Lista (LazyColumn)
            // Su padding superior se ajustará para dejar espacio al header
            LazyColumn(
                state = lazyListState,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(horizontal = 5.dp),
                contentPadding =
                    PaddingValues(
                        top =
                            with(localDensity) {
                                (totalCollapsibleHeaderHeightPx + headerOffsetPx).toDp() + 6.dp // 6.dp es tu padding original
                            },
                        bottom = scaffoldPaddingValues.calculateBottomPadding() + 6.dp,
                    ),
            ) {
                if (recipes.isEmpty()) {
                    item { // Necesitas envolver el contenido de "lista vacía" en un item o items
                        Column(
                            modifier =
                                Modifier
                                    .fillParentMaxSize() // Ocupa el total del espacio del viewport de LazyColumn
                                    .padding(horizontal = 3.dp),
                            // Ajusta según el padding del LazyColumn
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            if (selectedFilterTags.isNotEmpty()) {
                                Text(
                                    "No hay recetas que coincidan con todas las tags seleccionadas.",
                                    textAlign = TextAlign.Center,
                                )
                            } else {
//                                Text(
//                                    "No hay recetas disponibles o cargando...",
//                                    textAlign = TextAlign.Center,
//                                )
                                Box {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(recipe.id)
                            },
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

            // Header completo (Row de Filtros/Resultados + FlowRow de Chips)
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(x = 0, y = headerOffsetPx.toInt()) }
                        .background(MaterialTheme.colorScheme.surface) // Fondo para el header
                        .zIndex(1f),
                // Asegura que el header esté sobre la lista si hay superposición durante la animación
            ) {
                // Row de Filtros y Resultados
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(PrimaryGreen.copy(alpha = 0.8f))
                            .onSizeChanged { size ->
                                headerRowHeightPx = size.height.toFloat()
                            }
                            .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { showFilters = !showFilters },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 1.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                    ) {
                        val filterButtonText =
                            if (selectedFilterTags.isNotEmpty()) {
                                "Filtros (${selectedFilterTags.size})"
                            } else {
                                "Filtros"
                            }
                        Text(
                            text = filterButtonText,
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = if (showFilters) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (showFilters) "Ocultar filtros" else "Mostrar filtros",
                            tint = Color.White,
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    // Botón de Sort con DropdownMenu
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = !showSortMenu },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.White),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                        ) {
                            Text(
                                text =
                                    if (currentSortCriterion != SortCriterion.NONE) {
                                        getSortCriterionText(currentSortCriterion).take(10) + "..."
                                    } else {
                                        "Ordenar"
                                    },
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White,
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                // Cambiar el icono del botón Sort aquí
                                imageVector = if (showSortMenu) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (showSortMenu) "Ocultar opciones de orden" else "Mostrar opciones de orden",
                                tint = Color.White,
                            )
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
                                        showSortMenu = false // Cerrar el menú
                                    },
                                    trailingIcon = {
                                        if (currentSortCriterion == criterion) {
                                            Icon(Icons.Filled.Check, "Seleccionado")
                                        }
                                    },
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f)) // Para empujar los resultados a la derecha

                    Text(
                        text = "${recipes.size} ${if (recipes.size == 1) "Resultado" else "Resultados"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                    )
                }
                // FlowRow con FilterChips
                AnimatedVisibility(
                    visible = showFilters,
                    enter =
                        slideInVertically(
                            initialOffsetY = { -it },
                            animationSpec = tween(durationMillis = 150), // Reduce la duración (ej: 150ms)
                        ),
                    exit =
                        slideOutVertically(
                            targetOffsetY = { -it },
                            animationSpec = tween(durationMillis = 100), // La salida puede ser incluso más rápida (ej: 100ms)
                        ),
                ) {
                    // Envuelve el FlowRow en un Box para medir su altura
                    Box(modifier = Modifier.onSizeChanged { size -> filterChipsHeightPx = size.height.toFloat() }) {
                        if (allAvailableCategories.isNotEmpty()) {
                            FlowRow(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 4.dp,
                                            bottom = 8.dp,
                                            start = 16.dp,
                                            end = 16.dp,
                                        ),
                            ) {
                                allAvailableCategories.forEach { category ->
                                    FilterChip(
                                        selected = selectedFilterTags.contains(category),
                                        onClick = { viewModel.toggleFilterTag(category) },
                                        label = { Text(category.name) },
                                        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
                                        border = BorderStroke(1.dp, PrimaryGreen),
                                    )
                                }
                            }
                        } else {
                            Text(
                                "No hay categorías de filtro disponibles.",
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
