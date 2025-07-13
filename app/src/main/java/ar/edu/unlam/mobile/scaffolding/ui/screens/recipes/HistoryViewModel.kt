package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.SortCriterion
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
    ) : ViewModel() {
        private val _recipeListItems = MutableStateFlow<List<RecipeListItem>>(emptyList())

        private val _selectedFilterTags = MutableStateFlow<List<Category>>(emptyList())
        val selectedFilterTags: StateFlow<List<Category>> = _selectedFilterTags.asStateFlow()

        private val _sortCriterion = MutableStateFlow(SortCriterion.NONE)
        val sortCriterion: StateFlow<SortCriterion> = _sortCriterion.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        val recipes: StateFlow<List<RecipeListItem>> =
            combine(
                _recipeListItems,
                _selectedFilterTags,
                _sortCriterion,
            ) { sourceItems, tags, sortOrder ->
                Log.d("RecipesViewModel", "Combining ListItems. Count: ${sourceItems.size}, Tags: ${tags.size}, Sort: $sortOrder")

                val filteredItems =
                    if (tags.isEmpty()) {
                        sourceItems
                    } else {
                        // Asegúrate que RecipeListItem tenga 'tags' y que sean comparables a Category
                        sourceItems.filter { recipeItem ->
                            // Si recipeItem.tags es List<Category>
                            tags.all { selectedTag -> recipeItem.tags.any { itemTag -> itemTag == selectedTag } }
                            // Si recipeItem.tags es List<String> y Category tiene un campo 'tagName' String
                            // tags.all { selectedTag -> recipeItem.tags.any { itemTagName -> itemTagName == selectedTag.tagName } }
                        }
                    }

                // Asegúrate que RecipeListItem tenga 'name', 'rating', 'difficulty'
                when (sortOrder) {
                    SortCriterion.NAME_ASC -> filteredItems.sortedBy { it.name.lowercase() }
                    SortCriterion.NAME_DESC -> filteredItems.sortedByDescending { it.name.lowercase() }
                    SortCriterion.RATING_ASC -> filteredItems.sortedBy { it.rating }
                    SortCriterion.RATING_DESC -> filteredItems.sortedByDescending { it.rating }
                    SortCriterion.DIFFICULTY_ASC -> filteredItems.sortedBy { mapDifficultyToSortableValue(it.difficulty) }
                    SortCriterion.DIFFICULTY_DESC -> filteredItems.sortedByDescending { mapDifficultyToSortableValue(it.difficulty) }
                    SortCriterion.NONE -> filteredItems
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = emptyList<RecipeListItem>(),
            )

        private var fetchJob: Job? = null

        init {
            loadRecipeListItems()
        }

        fun loadRecipeListItems() {
            fetchJob?.cancel()
            fetchJob =
                viewModelScope.launch {
                    _isLoading.value = true
                    // recipesRepository.getRecipeListItems() DEBE devolver un Flow
                    // que observa la base de datos para que esto funcione automáticamente.
                    recipesRepository.getRecipeListItems()
                        .catch { exception ->
                            Log.e("RecipesViewModel", "Error fetching recipe list items", exception)
                            _recipeListItems.value = emptyList()
                            _isLoading.value = false
                        }
                        .collectLatest { items ->
                            Log.d("RecipesViewModel", "Fetched recipe list items. Count: ${items.size}")
                            _recipeListItems.value = items // Actualiza el Flow fuente
                            _isLoading.value = false
                        }
                }
        }

        private fun mapDifficultyToSortableValue(difficulty: Difficulty): Int {
            return when (difficulty) {
                Difficulty.Fácil -> 1
                Difficulty.Media -> 2
                Difficulty.Difícil -> 3
            }
        }

        fun toggleFilterTag(tag: Category) {
            val currentTags = _selectedFilterTags.value.toMutableList()
            if (currentTags.contains(tag)) {
                currentTags.remove(tag)
            } else {
                currentTags.add(tag)
            }
            _selectedFilterTags.value = currentTags.toList() // Esta línea es la que emite el nuevo estado
            Log.d("RecipesViewModel", "Toggled filter. New selected tags: ${_selectedFilterTags.value}")
        }

        fun setSortCriterion(criterion: SortCriterion) {
            _sortCriterion.update { criterion }
            Log.d("RecipesViewModel", "Sort criterion set to: $criterion")
        }

        // No pude hacer que funcione bien, no muestra el cambio al instante
        fun toggleFavorite(recipeId: Int) {
            viewModelScope.launch {
                // Primero, necesitamos saber el estado actual de isFavorite para invertirlo.
                // La forma más robusta es obtener el estado actual del objeto que ya tienes en la UI
                // o, si es necesario, obtenerlo de la lista actual.
                // PERO, idealmente, el Flow ya nos dará el estado.

                // Lo que necesitas hacer es llamar al repositorio para que actualice la BD.
                // No necesitas obtener la receta completa aquí si solo vas a cambiar el favorito.

                // Necesitamos saber el estado actual para poder invertirlo.
                // La forma más directa es encontrar el item en la lista actual que se está mostrando.
                val currentRecipeItem = _recipeListItems.value.find { it.id == recipeId }

                if (currentRecipeItem != null) {
                    val newFavoriteState = !currentRecipeItem.isFavorite
                    try {
                        recipesRepository.setRecipeFavoriteStatus(recipeId, newFavoriteState)
                        Log.d("RecipesViewModel", "Toggled favorite for ID $recipeId to $newFavoriteState. DB update initiated.")
                    } catch (e: Exception) {
                        Log.e("RecipesViewModel", "Error toggling favorite for ID $recipeId", e)
                        // Considera mostrar un mensaje de error al usuario
                    }
                } else {
                    Log.w("RecipesViewModel", "Recipe with ID $recipeId not found in current list for toggling favorite.")
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            fetchJob?.cancel()
        }
    }
