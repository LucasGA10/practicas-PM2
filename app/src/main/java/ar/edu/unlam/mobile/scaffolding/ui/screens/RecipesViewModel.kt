package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.util.Log
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository
    ) : ViewModel() {

    // Simulación de una fuente de todas las recetas. En una app real, vendría de un repositorio.
    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    private val _selectedFilterTags = MutableStateFlow<List<Category>>(emptyList())
    val selectedFilterTags: StateFlow<List<Category>> = _selectedFilterTags.asStateFlow()

    // Este es el StateFlow que tu UI observará para la lista filtrada de recetas
    val recipes: StateFlow<List<Recipe>> =
        combine(_allRecipes, _selectedFilterTags) { allRecipes, tags ->
            if (tags.isEmpty()) {
                allRecipes
            } else {
                allRecipes.filter { recipe ->
                    tags.all { selectedTag -> recipe.tags.any { recipeTag -> recipeTag == selectedTag } }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var fetchRecipesJob: Job? = null

    init {
        fetchRecipe()
    }

    private fun fetchRecipe() {
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

    fun updateSelectedFilterTags(newSelectedTags: List<Category>) {
        _selectedFilterTags.value = newSelectedTags.distinct()
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
                fetchRecipe() // ESTA ES LA LLAMADA CLAVE DEL WORKAROUND

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
