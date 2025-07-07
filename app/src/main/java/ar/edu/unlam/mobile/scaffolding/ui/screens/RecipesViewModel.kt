package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
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

enum class SortCriterion {
    NONE, // Podría ser el estado por defecto o "Por Defecto" (orden del repositorio)
    NAME_ASC,
    NAME_DESC,
    RATING_ASC,
    RATING_DESC,
    DIFFICULTY_ASC, // Asumiendo que dificultad puede ser ordenada (ej: Fácil < Media < Difícil)
    DIFFICULTY_DESC
}


@HiltViewModel
class RecipesViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository
    ) : ViewModel() {

    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())

    private val _selectedFilterTags = MutableStateFlow<List<Category>>(emptyList())
    val selectedFilterTags: StateFlow<List<Category>> = _selectedFilterTags.asStateFlow()

    private val _sortCriterion = MutableStateFlow(SortCriterion.NONE)
    val sortCriterion: StateFlow<SortCriterion> = _sortCriterion.asStateFlow()

    val recipes: StateFlow<List<Recipe>> =
        combine(
            _allRecipes,
            _selectedFilterTags,
            _sortCriterion // 3. Incluir el criterio de ordenación en el combine
        ) { allRecipes, tags, sortOrder ->
            Log.d("RecipesViewModel", "Combining. AllRecipes: ${allRecipes.size}, Tags: ${tags.size}, Sort: $sortOrder")
            val filteredRecipes = if (tags.isEmpty()) {
                allRecipes
            } else {
                allRecipes.filter { recipe ->
                    tags.all { selectedTag -> recipe.tags.any { recipeTag -> recipeTag == selectedTag } }
                }
            }

            // 4. Aplicar la ordenación
            when (sortOrder) {
                SortCriterion.NAME_ASC -> filteredRecipes.sortedBy { it.name.lowercase() }
                SortCriterion.NAME_DESC -> filteredRecipes.sortedByDescending { it.name.lowercase() }
                SortCriterion.RATING_ASC -> filteredRecipes.sortedBy { it.rating } // Asumiendo que 'rating' existe en Recipe y es Comparable
                SortCriterion.RATING_DESC -> filteredRecipes.sortedByDescending { it.rating }
                SortCriterion.DIFFICULTY_ASC -> filteredRecipes.sortedBy { mapDifficultyToSortableValue(it.difficulty) } // Necesitas una función para mapear dificultad
                SortCriterion.DIFFICULTY_DESC -> filteredRecipes.sortedByDescending { mapDifficultyToSortableValue(it.difficulty) }
                SortCriterion.NONE -> filteredRecipes // Sin ordenación específica o por defecto
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private var fetchRecipesJob: Job? = null

    init {
        fetchRecipes()
    }

    private fun fetchRecipes() {
        fetchRecipesJob?.cancel()
        fetchRecipesJob = viewModelScope.launch {
            recipesRepository.getRecipes()
                .catch { exception ->
                    Log.e("RecipesViewModel", "Error fetching recipes", exception)
                    _allRecipes.value = emptyList() // O un estado de error
                }
                .collectLatest { recipesFromRepo ->
                    Log.d("RecipesViewModel", "Fetched all recipes. Count: ${recipesFromRepo.size}")
                    _allRecipes.value = recipesFromRepo
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

    //No pude hacer que funcione bien, no muestra el cambio al instante
    fun toggleFavorite(recipeId: Int) {
        viewModelScope.launch {
            val currentRecipe = _allRecipes.value.find { it.id == recipeId }
            if (currentRecipe != null) {
                Log.d("RecipesViewModel", "Toggling favorite for: ${currentRecipe.name}, current isFavorite: ${currentRecipe.isFavorite}")
                val updatedRecipe = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)

                recipesRepository.updateRecipe(updatedRecipe)
                Log.d("RecipesViewModel", "Updated recipe in repo: ${updatedRecipe.name}, new isFavorite: ${updatedRecipe.isFavorite}")

                // WORKAROUND: Después de actualizar en el repo, vuelve a obtener TODAS las recetas.
                Log.d("RecipesViewModel", "Forcing refresh of all recipes after toggleFavorite.")
                fetchRecipes() // ESTA ES LA LLAMADA CLAVE DEL WORKAROUND

            } else {
                Log.w("RecipesViewModel", "Recipe with ID $recipeId not found for toggling favorite.")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fetchRecipesJob?.cancel()
    }

    }
