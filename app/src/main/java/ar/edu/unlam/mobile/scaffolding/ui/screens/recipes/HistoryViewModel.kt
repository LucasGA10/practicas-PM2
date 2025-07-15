package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import ar.edu.unlam.mobile.scaffolding.domain.usecases.ToggleFavoriteRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val recipesRepository: RecipesRepository,
        private val toggleFavoriteRecipeUseCase: ToggleFavoriteRecipeUseCase,
    ) : ViewModel() {
        private val _isLoading = MutableStateFlow(true)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error.asStateFlow()

        private val currentUser: StateFlow<User?> =
            userRepository
                .getCurrentUser()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        @OptIn(ExperimentalCoroutinesApi::class) // Necesario para transformLatest
        val completedRecipes: StateFlow<List<RecipeHistoryItem>> = // Cambiado de RecipeHistoryItem a RecipeListItem
            currentUser.transformLatest { user ->
                if (user == null) {
                    _error.value = "Aún no has completado ninguna receta."
                    _isLoading.value = false
                    emit(emptyList()) // Emitir el tipo correcto
                    return@transformLatest
                }
                if (user.recipeHistory.isEmpty()) {
                    _isLoading.value = false
                    emit(emptyList()) // Emitir el tipo correcto
                    return@transformLatest
                }

                _isLoading.value = true
                _error.value = null

                try {
                    val recipeIdsInHistory = user.recipeHistory.map { it.recipeId }
                    val recipesFromRepo = recipesRepository.getRecipeListItemsByIds(recipeIdsInHistory)

                    val completionDateMap = user.recipeHistory.associate { it.recipeId to it.completionDate }
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

                    val historyItems =
                        recipesFromRepo.mapNotNull { recipeListItem ->
                            completionDateMap[recipeListItem.id]?.let { dateString ->
                                RecipeHistoryItem(
                                    id = recipeListItem.id,
                                    name = recipeListItem.name,
                                    imageUrl = recipeListItem.imageUrl,
                                    category = recipeListItem.category,
                                    portions = recipeListItem.portions,
                                    tags = recipeListItem.tags,
                                    isFavorite = recipeListItem.isFavorite,
                                    completionDate = dateString, // El String de la fecha original
                                )
                            }
                        }
                    val sortedHistoryItems =
                        historyItems.sortedByDescending { historyItem ->
                            try {
                                dateFormat.parse(historyItem.completionDate)?.time ?: 0L // Parsear a Date y obtener milisegundos
                            } catch (e: Exception) {
                                Log.e("HistoryViewModel", "Error al parsear fecha: ${historyItem.completionDate}", e)
                                0L // Poner al final si hay error de parseo
                            }
                        }

                    this.emit(sortedHistoryItems)
                } catch (e: Exception) {
                    _error.value = "Error al cargar recetas del historial: ${e.message}"
                    Log.e("HistoryViewModel", "Error en completedRecipes flow", e)
                    emit(emptyList()) // Emitir el tipo correcto en caso de error
                } finally {
                    _isLoading.value = false
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()) // Valor inicial

        fun toggleFavorite(recipeId: Int) {
            viewModelScope.launch {
                val currentRecipeItem = completedRecipes.value.find { it.id == recipeId } // Busca en la fuente interna

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
    }
