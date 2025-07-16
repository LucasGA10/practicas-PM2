package ar.edu.unlam.mobile.scaffolding.ui.screens.recipes

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import ar.edu.unlam.mobile.scaffolding.domain.usecases.GetIngredientsByIdsUseCase
import ar.edu.unlam.mobile.scaffolding.domain.usecases.GetRecipeByIdUseCase
import ar.edu.unlam.mobile.scaffolding.domain.usecases.ToggleFavoriteRecipeUseCase
import ar.edu.unlam.mobile.scaffolding.domain.usecases.UpdateRecipeRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiUsedIngredient(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val typeName: String,
    val quantity: String,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PreparationViewModel
    @Inject
    constructor(
        private val getRecipeUseCase: GetRecipeByIdUseCase,
        private val getIngredientsUseCase: GetIngredientsByIdsUseCase,
        private val toggleFavoriteUseCase: ToggleFavoriteRecipeUseCase,
        private val updateRecipeRatingUseCase: UpdateRecipeRatingUseCase,
        private val userRepository: UserRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        private val recipeId: StateFlow<Int?> = MutableStateFlow(savedStateHandle.get<Int>("recipeId"))

        private val _showRatingDialog = MutableStateFlow(false)
        val showRatingDialog: StateFlow<Boolean> = _showRatingDialog.asStateFlow()

        private val _currentRatingForDialog = MutableStateFlow(0f) // Rating temporal para el diálogo
        val currentRatingForDialog: StateFlow<Float> = _currentRatingForDialog.asStateFlow()

        private val currentUserId: StateFlow<User?> =
            userRepository.getCurrentUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

        private val _error = MutableStateFlow<String?>(null)
        val error: StateFlow<String?> = _error.asStateFlow()

        val recipe: StateFlow<Recipe?> =
            recipeId
                .filterNotNull()
                .flatMapLatest { id ->
                    Log.d("PrepViewModel", "RecipeIdFlow emitió: $id. Obteniendo receta...")
                    getRecipeUseCase(id) // Esto devuelve Flow<Recipe?>
                }.map { domainRecipe ->
                    if (domainRecipe == null) {
                        Log.d("PrepViewModel", "GetRecipeUseCase devolvió null.")
                    } else {
                        Log.d(
                            "PrepViewModel",
                            "GetRecipeUseCase devolvió receta: ${domainRecipe.name}. Ingredientes usados simples: ${domainRecipe.usedIngredients.size}",
                        )
                        // Loguea los IDs de los ingredientes usados simples
                        domainRecipe.usedIngredients.forEach {
                            Log.d(
                                "PrepViewModel",
                                "  -> UsedIng simple: id=${it.id}, qty=${it.quantity}",
                            )
                        }
                    }
                    domainRecipe
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = null,
                )

        val uiUsedIngredients: StateFlow<List<UiUsedIngredient>> =
            recipe // Depende del Flow<Recipe?>
                .filterNotNull() // Solo procede si tenemos una receta
                .flatMapLatest { domainRecipe -> // domainRecipe es tu modelo de dominio Recipe
                    Log.d(
                        "PrepViewModel",
                        "uiUsedIngredients: Receta recibida: ${domainRecipe.name}. Procesando ${domainRecipe.usedIngredients.size} ingredientes usados simples.",
                    )

                    if (domainRecipe.usedIngredients.isEmpty()) {
                        Log.d("PrepViewModel", "uiUsedIngredients: No hay ingredientes usados simples en la receta. Emitiendo lista vacía.")
                        flowOf(emptyList<UiUsedIngredient>())
                    } else {
                        val ingredientIds = domainRecipe.usedIngredients.map { it.id }.distinct()
                        Log.d("PrepViewModel", "uiUsedIngredients: IDs de ingredientes a buscar: $ingredientIds")

                        if (ingredientIds.isEmpty()) { // Puede pasar si todos los usedIngredients tuvieran un ID inválido o duplicado que se eliminó con distinct
                            Log.d(
                                "PrepViewModel",
                                "uiUsedIngredients: Lista de IDs de ingredientes está vacía después de distinct. Emitiendo lista vacía.",
                            )
                            flowOf(emptyList<UiUsedIngredient>())
                        } else {
                            getIngredientsUseCase(ingredientIds) // Flow<Map<Int, Ingredient>>
                                .map { ingredientsMap ->
                                    Log.d(
                                        "PrepViewModel",
                                        "uiUsedIngredients: Mapa de ingredientes detallados recibido. Tamaño: ${ingredientsMap.size}",
                                    )
                                    ingredientsMap.forEach {
                                            (id, ing) ->
                                        Log.d("PrepViewModel", "  -> Detalle Ing: id=$id, nombre=${ing.name}")
                                    }

                                    val mappedUiIngredients =
                                        domainRecipe.usedIngredients.mapNotNull { usedIngredientSimple ->
                                            val ingredientDetails = ingredientsMap[usedIngredientSimple.id]
                                            if (ingredientDetails != null) {
                                                Log.d(
                                                    "PrepViewModel",
                                                    "  -> Mapeando: simpleId=${usedIngredientSimple.id} con detalleId=${ingredientDetails.id}",
                                                )
                                                UiUsedIngredient(
                                                    id = ingredientDetails.id,
                                                    name = ingredientDetails.name,
                                                    imageUrl = ingredientDetails.imageUrl,
                                                    typeName = ingredientDetails.type.displayName, // Asume que Ingredient.type es IngredientType? o IngredientType
                                                    quantity = usedIngredientSimple.quantity,
                                                )
                                            } else {
                                                Log.w(
                                                    "PrepViewModel",
                                                    "uiUsedIngredients: Detalles COMPLETOS NO encontrados para ingredientId: ${usedIngredientSimple.id} en la receta: ${domainRecipe.name}",
                                                )
                                                null
                                            }
                                        }
                                    Log.d(
                                        "PrepViewModel",
                                        "uiUsedIngredients: Mapeo completado. ${mappedUiIngredients.size} UiUsedIngredients creados.",
                                    )
                                    mappedUiIngredients
                                }
                        }
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = emptyList(),
                )

        fun toggleFavorite(recipeIdToToggle: Int) { // El ID es suficiente aquí
            viewModelScope.launch {
                val currentRecipeValue = recipe.value // _recipe es StateFlow<Recipe?>
                if (currentRecipeValue != null && currentRecipeValue.id == recipeIdToToggle) {
                    val newFavoriteState = !currentRecipeValue.isFavorite
                    try {
                        toggleFavoriteUseCase(recipeIdToToggle, newFavoriteState)
                        // Opcional: El _recipe StateFlow se actualizará automáticamente si
                        // la fuente de datos subyacente (base de datos) emite cambios.
                        // Si no, podrías necesitar forzar una recarga o actualizar el _recipe localmente
                        // de forma optimista (aunque esto es más complejo de manejar con errores).
                    } catch (e: Exception) {
                        _error.value = "Error al actualizar favorito: ${e.message}"
                    }
                }
            }
        }

        fun onRecipeCompletedClicked() {
            _currentRatingForDialog.value = 0f
            _showRatingDialog.value = true
        }

        fun onRatingDialogDismiss() {
            _showRatingDialog.value = false
        }

        fun onRatingSubmitted(rating: Float) {
            _showRatingDialog.value = false
            val recipeValue = recipe.value
            val userId = currentUserId.value?.id

            if (recipeValue != null && userId != null) {
                viewModelScope.launch {
                    // 1. Actualizar el rating "general" de la receta (si es diferente de la puntuación del historial)
                    updateRecipeRatingUseCase(recipeValue.id, rating)

                    // 2. Añadir al historial del usuario
                    val result = userRepository.addRecipeToHistory(userId, recipeValue.id)
                    result.fold(
                        onSuccess = {
                            Log.d("PrepViewModel", "Receta ${recipeValue.id} añadida al historial con rating $rating")
                            // Opcional: Mostrar un mensaje de éxito
                        },
                        onFailure = { e ->
                            _error.value = "Error al guardar en historial: ${e.message}"
                            Log.e("PrepViewModel", "Error al guardar en historial", e)
                        },
                    )
                }
            } else {
                _error.value = "No se pudo guardar: receta o usuario no disponibles."
            }
        }

        fun onDialogRatingChanged(newRating: Float) {
            _currentRatingForDialog.value = newRating.coerceIn(0f, 5f)
        }
    }
