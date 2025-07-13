package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.domain.model.SortCriterion
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import ar.edu.unlam.mobile.scaffolding.domain.usecases.GetRecipeListItemsUseCase
import ar.edu.unlam.mobile.scaffolding.domain.usecases.ToggleFavoriteRecipeUseCase
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
class RecipesViewModel
    @Inject
    constructor(
        private val getRecipeListItemsUseCase: GetRecipeListItemsUseCase,
        private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    ) : ViewModel() {
        private val recipeListItemsSource = MutableStateFlow<List<RecipeListItem>>(emptyList())
        private val _selectedFilterTags = MutableStateFlow<List<Category>>(emptyList())
        val selectedFilterTags: StateFlow<List<Category>> = _selectedFilterTags.asStateFlow()

        private val _sortCriterion = MutableStateFlow(SortCriterion.NONE)
        val sortCriterion: StateFlow<SortCriterion> = _sortCriterion.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading

        val recipes: StateFlow<List<RecipeListItem>> =
            combine(
                recipeListItemsSource,
                _selectedFilterTags,
                _sortCriterion,
            ) { sourceItems, tags, sortOrder ->
                Log.d("RecipesViewModel", "Combining ListItems. Count: ${sourceItems.size}, Tags: ${tags.size}, Sort: $sortOrder")

                val filteredItems =
                    if (tags.isEmpty()) {
                        sourceItems
                    } else {
                        sourceItems.filter { recipeItem ->
                            tags.all { selectedTag -> recipeItem.tags.any { itemTag -> itemTag == selectedTag } }
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
                initialValue = emptyList(),
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
                    getRecipeListItemsUseCase() // USAR CASO DE USO
                        .catch { exception ->
                            Log.e("RecipesViewModel", "Error fetching recipe list items", exception)
                            recipeListItemsSource.value = emptyList() // Actualiza la fuente interna
                            _isLoading.value = false
                        }
                        .collectLatest { items ->
                            Log.d("RecipesViewModel", "Fetched recipe list items. Count: ${items.size}")
                            recipeListItemsSource.value = items // Actualiza la fuente interna
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

        fun toggleFavorite(recipeId: Int) {
            viewModelScope.launch {
                val currentRecipeItem = recipeListItemsSource.value.find { it.id == recipeId } // Busca en la fuente interna

                if (currentRecipeItem != null) {
                    val newFavoriteState = !currentRecipeItem.isFavorite
                    try {
                        toggleFavoriteRecipeUseCase(recipeId, newFavoriteState) // USAR CASO DE USO
                        Log.d("RecipesViewModel", "Toggled favorite for ID $recipeId to $newFavoriteState. DB update initiated.")
                        // El Flow debería actualizarse automáticamente si el repositorio emite cambios
                    } catch (e: Exception) {
                        Log.e("RecipesViewModel", "Error toggling favorite for ID $recipeId", e)
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
