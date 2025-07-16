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

        @OptIn(ExperimentalCoroutinesApi::class)
        val completedRecipes: StateFlow<List<RecipeHistoryItem>> =
            currentUser.transformLatest { user ->
                if (user == null) {
                    _error.value = "Aún no has completado ninguna receta."
                    _isLoading.value = false
                    emit(emptyList())
                    return@transformLatest
                }
                if (user.recipeHistory.isEmpty()) {
                    _isLoading.value = false
                    // Considera un mensaje específico si el historial está vacío pero el usuario existe.
                    // _error.value = "Tu historial de recetas está vacío."
                    emit(emptyList())
                    return@transformLatest
                }

                _isLoading.value = true
                _error.value = null

                try {
                    // 1. Obtener los IDs únicos de las recetas en el historial para la consulta al repo
                    val uniqueRecipeIdsInHistory = user.recipeHistory.map { it.recipeId }.distinct()
                    val recipesFromRepoMap =
                        recipesRepository.getRecipeHistoryItemsByIds(uniqueRecipeIdsInHistory)
                            .associateBy { it.id } // Crear un mapa de ID -> RecipeListItem para búsqueda rápida

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

                    // 2. Mapear CADA entrada del historial del usuario
                    val historyItems =
                        user.recipeHistory.mapNotNull { userHistoryEntry ->
                            recipesFromRepoMap[userHistoryEntry.recipeId]?.let { recipeListItem ->
                                RecipeHistoryItem(
                                    // Necesitamos un ID único para cada instancia en la UI si vas a tener keys
                                    // Podrías usar recipeId + completionDate como un ID compuesto si es necesario,
                                    // o simplemente depender del índice en la lista para keys de LazyColumn.
                                    // Por ahora, mantendremos el ID original de la receta.
                                    id = recipeListItem.id,
                                    name = recipeListItem.name,
                                    imageUrl = recipeListItem.imageUrl,
                                    category = recipeListItem.category,
                                    portions = recipeListItem.portions,
                                    tags = recipeListItem.tags,
                                    isFavorite = recipeListItem.isFavorite, // El estado de favorito vendrá del RecipeListItem
                                    completionDate = userHistoryEntry.completionDate, // La fecha específica de ESTA entrada
                                )
                            }
                        }

                    // 3. Ordenar por fecha de finalización (ya lo tienes y está bien)
                    val sortedHistoryItems =
                        historyItems.sortedByDescending { historyItem ->
                            try {
                                dateFormat.parse(historyItem.completionDate)?.time ?: 0L
                            } catch (e: Exception) {
                                Log.e("HistoryViewModel", "Error al parsear fecha: ${historyItem.completionDate}", e)
                                0L
                            }
                        }

                    this.emit(sortedHistoryItems)
                } catch (e: Exception) {
                    _error.value = "Error al cargar recetas del historial: ${e.message}"
                    Log.e("HistoryViewModel", "Error en completedRecipes flow", e)
                    emit(emptyList())
                } finally {
                    _isLoading.value = false
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun toggleFavorite(recipeId: Int) {
            viewModelScope.launch {
                // Importante: Si tienes recetas duplicadas en la lista `completedRecipes`
                // (misma receta, diferentes fechas), este toggleFavorite afectará
                // al estado base de la receta en el repositorio.
                // La UI debería reflejar esto consistentemente para todas las instancias de esa receta.
                // La lógica actual de `toggleFavoriteRecipeUseCase` debería manejar esto correctamente
                // a nivel de datos. El `isFavorite` en `RecipeHistoryItem` se actualiza porque
                // `recipesFromRepoMap` se actualiza cuando el repositorio emite nuevos datos.

                // Encuentra la primera instancia para obtener el estado actual (o cualquier instancia)
                val currentRecipeItem = completedRecipes.value.find { it.id == recipeId }

                if (currentRecipeItem != null) {
                    val newFavoriteState = !currentRecipeItem.isFavorite
                    try {
                        toggleFavoriteRecipeUseCase(recipeId, newFavoriteState)
                        Log.d("HistoryViewModel", "Toggled favorite for ID $recipeId to $newFavoriteState. DB update initiated.")
                    } catch (e: Exception) {
                        Log.e("HistoryViewModel", "Error toggling favorite for ID $recipeId", e)
                    }
                } else {
                    Log.w("HistoryViewModel", "Recipe with ID $recipeId not found in current list for toggling favorite.")
                }
            }
        }
    }
