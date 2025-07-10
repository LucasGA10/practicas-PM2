package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreparationViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle, // Correctamente inyectado
        private val recipesRepository: RecipesRepository,
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
                _error.value = null // Limpiar errores previos
                recipesRepository.getRecipeById(recipeId)
                    .catch { e ->
                        _error.value = "Error al cargar los detalles de la receta: ${e.message}"
                        _isLoading.value = false
                        _recipe.value = null // Asegúrate que la receta quede nula si hay error
                        // Log.e("PreparationViewModel", "Error loading recipe details for ID $recipeId", e)
                    }
                    .collectLatest { recipeDetails -> // collectLatest es bueno si el ID puede cambiar y quieres la última
                        _recipe.value = recipeDetails
                        _isLoading.value = false
                        if (recipeDetails == null && _error.value == null) {
                            // Esto podría pasar si el Flow emite null porque la receta no existe,
                            // pero no hubo una excepción en el Flow.
                            _error.value = "No se encontraron detalles para la receta con ID: $recipeId."
                        }
                    }
            }
        }

        fun toggleFavorite(recipeIdToToggle: Int) {
            viewModelScope.launch {
                val currentRecipeValue = _recipe.value
                if (currentRecipeValue != null && currentRecipeValue.id == recipeIdToToggle) {
                    val newFavoriteState = !currentRecipeValue.isFavorite
                    try {
                        recipesRepository.setRecipeFavoriteStatus(currentRecipeValue.id, newFavoriteState)
                    } catch (e: Exception) {
                        _error.value = "Error al actualizar favorito: ${e.message}"
                        // Considera revertir el estado visual si la UI lo actualizó optimistamente,
                        // o mostrar un error más prominente.
                    }
                }
            }
        }

        fun updateRecipeRating(
            recipeIdToUpdate: Int,
            newRating: Float,
        ) {
            viewModelScope.launch {
                val currentRecipeValue = _recipe.value
                if (currentRecipeValue != null && currentRecipeValue.id == recipeIdToUpdate) {
                    val validRating = newRating.coerceIn(0f, 5f)
                    val updatedRecipe = currentRecipeValue.copy(rating = validRating)
                    try {
                        recipesRepository.updateRecipe(updatedRecipe)
                    } catch (e: Exception) {
                        _error.value = "Error al actualizar el rating: ${e.message}"
                    }
                }
            }
        }
    }
