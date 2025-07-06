package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.core.copy
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreparationViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle, // Correctamente inyectado
        private val recipesRepository: RecipesRepository
    ) : ViewModel() {
    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val recipeId: Int? = savedStateHandle.get<Int>("recipeId") // "recipeId" debe coincidir con el nombre del argumento
        recipeId?.let {
            loadRecipeDetails(it)
        } ?: run {
            _error.value = "ID de receta no encontrado"
        }
    }

    fun loadRecipeDetails(recipeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí llamarías a tu caso de uso o repositorio para obtener los detalles de la receta
                // Por ejemplo:
                val details = recipesRepository.getRecipeById(recipeId)
                _recipe.value = details
            } catch (e: Exception) {
                _error.value = "Error al cargar los detalles de la receta: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            val currentRecipe = _recipe.value
            if (currentRecipe != null && _recipe.value?.id == id) {
                val updatedRecipe = currentRecipe.copy(isFavorite = !currentRecipe.isFavorite)
                recipesRepository.updateRecipe(updatedRecipe) // Guarda el cambio en el repositorio
                _recipe.value = updatedRecipe // Actualiza el StateFlow localmente
            }
        }
    }

    fun updateRecipeRating(recipeId: Int, newRating: Float) {
        viewModelScope.launch {
            val currentRecipe = _recipe.value
            if (currentRecipe != null && currentRecipe.id == recipeId) {
                // Validación opcional del rating (ej. entre 1 y 5)
                val validRating = newRating.coerceIn(0f, 5f) // Asegura que esté en rango (0 si no se califica)
                val updatedRecipe = currentRecipe.copy(rating = validRating)
                recipesRepository.updateRecipe(updatedRecipe) // Guarda el cambio en el repositorio
                _recipe.value = updatedRecipe // Actualiza el StateFlow para reflejar el cambio
            }
        }
    }

    }