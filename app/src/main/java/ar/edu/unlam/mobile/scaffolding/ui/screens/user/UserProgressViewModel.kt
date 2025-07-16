package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class MostFrequentRecipeUiState(
    val recipe: RecipeListItem? = null,
    val count: Int = 0,
    val message: String? = null,
)

@HiltViewModel
class UserProgressViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        val currentUser: StateFlow<User?> =
            userRepository.getCurrentUser()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = null,
                )

        // Para el total acumulado
        private val _totalNutritionalValue = MutableStateFlow<NutritionalValue?>(null)
        val totalNutritionalValue: StateFlow<NutritionalValue?> = _totalNutritionalValue.asStateFlow()

        // Para el valor nutricional de hoy
        private val _todayNutritionalValue = MutableStateFlow<NutritionalValue?>(null)
        val todayNutritionalValue: StateFlow<NutritionalValue?> = _todayNutritionalValue.asStateFlow()

        init {
            Log.d("UserProgressViewModel", "Init ViewModel")
            viewModelScope.launch {
                currentUser.collectLatest { user ->
                    if (user != null) {
                        Log.d("UserProgressViewModel", "Usuario actual detectado: ${user.id}")
                        calculateTotalNutritionalValue(user)
                        calculateTodayNutritionalValue(user)
                    } else {
                        Log.d("UserProgressViewModel", "Usuario actual es null, reseteando valores nutricionales.")
                        _totalNutritionalValue.value = null
                        _todayNutritionalValue.value = null
                    }
                }
            }
        }

        private fun calculateTotalNutritionalValue(user: User) {
            viewModelScope.launch {
                if (user.recipeHistory.isEmpty()) {
                    _totalNutritionalValue.value = NutritionalValue()
                    return@launch
                }
                Log.d("UserProgressViewModel", "Calculando valor nutricional: ${_totalNutritionalValue.value}")
                var accumulatedNutritionalValue = NutritionalValue()

                for (completedRecipeInfo in user.recipeHistory) {
                    // Obtiene el NutritionalValue para cada receta en el historial
                    val recipeNutritionalValue = recipesRepository.getNutritionalValueByRecipeId(completedRecipeInfo.recipeId)

                    // Suma el valor obtenido al acumulado (usando la función `plus` que definimos)
                    accumulatedNutritionalValue += recipeNutritionalValue
                    Log.d("UserProgressViewModel", "Valor nutricional acumulado: ${_totalNutritionalValue.value}")
                }
                _totalNutritionalValue.value = accumulatedNutritionalValue
            }
        }

        // Calcular el valor nutricional de HOY
        private fun calculateTodayNutritionalValue(user: User) {
            viewModelScope.launch { // Cada cálculo en su propio launch
                val today = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                Log.d("UserProgressViewModel", "--- Iniciando cálculo para HOY ---")

                val recipesToday =
                    user.recipeHistory.mapNotNull { completedRecipeInfo ->
                        try {
                            val parsedDate: Date? = dateFormat.parse(completedRecipeInfo.completionDate)
                            if (parsedDate != null) {
                                val completionTimestamp: Long = parsedDate.time
                                val completionDateCalendar =
                                    Calendar.getInstance().apply {
                                        timeInMillis = completionTimestamp
                                    }
                                if (completionDateCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                                    completionDateCalendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                                ) {
                                    completedRecipeInfo
                                } else {
                                    null
                                }
                            } else {
                                Log.e("UserProgressViewModel", "dateFormat.parse devolvió null para: ${completedRecipeInfo.completionDate}")
                                null
                            }
                        } catch (e: java.text.ParseException) {
                            Log.e(
                                "UserProgressViewModel",
                                "Formato de fecha inválido (ParseException) para recipeId ${completedRecipeInfo.recipeId}: '${completedRecipeInfo.completionDate}'",
                                e,
                            )
                            null
                        } catch (e: Exception) {
                            Log.e(
                                "UserProgressViewModel",
                                "Error procesando fecha para recipeId ${completedRecipeInfo.recipeId}: '${completedRecipeInfo.completionDate}'",
                                e,
                            )
                            null
                        }
                    }

                if (recipesToday.isEmpty()) {
                    Log.d("UserProgressViewModel", "No hay recetas consumidas HOY válidas, estableciendo valor nutricional de hoy a ceros.")
                    _todayNutritionalValue.value = NutritionalValue() // Asegúrate que NutritionalValue() por defecto tiene ceros
                    return@launch
                }

                Log.d("UserProgressViewModel", "PROCEDIENDO a calcular suma nutricional para ${recipesToday.size} recetas de HOY.")
                var accumulatedNutritionalValueToday = NutritionalValue() // Inicializa a ceros

                for (completedRecipeInfo in recipesToday) {
                    try {
                        Log.d("UserProgressViewModel", "Procesando en bucle receta de HOY ID: ${completedRecipeInfo.recipeId}")
                        val nutritionalValue = recipesRepository.getNutritionalValueByRecipeId(completedRecipeInfo.recipeId)

                        if (nutritionalValue != null) {
                            accumulatedNutritionalValueToday += nutritionalValue
                        } else {
                            Log.w(
                                "UserProgressViewModel",
                                "Valor nutricional NO encontrado para receta ID (HOY): ${completedRecipeInfo.recipeId}",
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "UserProgressViewModel",
                            "Error obteniendo detalles o sumando para receta ID (HOY): ${completedRecipeInfo.recipeId}",
                            e,
                        )
                    }
                }
                _todayNutritionalValue.value = accumulatedNutritionalValueToday
                Log.d("UserProgressViewModel", "Valor FINAL de HOY asignado a StateFlow: ${_todayNutritionalValue.value}")
            }
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        val mostFrequentRecipeState: StateFlow<MostFrequentRecipeUiState> =
            currentUser
                .filterNotNull()
                .flatMapLatest { user ->
                    if (user.recipeHistory.isEmpty()) {
                        // Si el historial está vacío, emitir un estado con mensaje
                        Log.d("UserProgressViewModel", "Historial vacío para receta más frecuente.")
                        MutableStateFlow(MostFrequentRecipeUiState(message = "Tu historial de recetas está vacío."))
                    } else {
                        // 1. Encontrar el recipeId más frecuente
                        // Agrupa por recipeId y cuenta las ocurrencias
                        val frequencyMap =
                            user.recipeHistory
                                .groupBy { it.recipeId } // Resultado: Map<Int, List<CompletedRecipeInfo>>
                                .mapValues { entry -> entry.value.size } // Resultado: Map<Int, Int> (recipeId a conteo)

                        // Encuentra la entrada (recipeId, conteo) con el mayor conteo
                        val mostFrequentEntry = frequencyMap.maxByOrNull { it.value }

                        if (mostFrequentEntry == null) {
                            Log.w(
                                "UserProgressViewModel",
                                "No se pudo determinar la entrada más frecuente aunque el historial no está vacío.",
                            )
                            MutableStateFlow(MostFrequentRecipeUiState(message = "No se pudo determinar la receta más frecuente."))
                        } else {
                            val mostFrequentRecipeId = mostFrequentEntry.key
                            val count = mostFrequentEntry.value
                            Log.d("UserProgressViewModel", "Receta más frecuente ID: $mostFrequentRecipeId, Conteo: $count")

                            // 2. Obtener los detalles de ESA receta desde el repositorio
                            // recipesRepository.getRecipeListItemById(recipeId) debería devolver Flow<RecipeListItem?>
                            recipesRepository.getRecipeListItemById(mostFrequentRecipeId).map { recipeListItem ->
                                if (recipeListItem != null) {
                                    Log.d(
                                        "UserProgressViewModel",
                                        "Detalles encontrados para la receta más frecuente: ${recipeListItem.name}",
                                    )
                                    MostFrequentRecipeUiState(recipe = recipeListItem, count = count)
                                } else {
                                    Log.w(
                                        "UserProgressViewModel",
                                        "Detalles no encontrados en el repo para la receta más frecuente ID: $mostFrequentRecipeId",
                                    )
                                    MostFrequentRecipeUiState(message = "Detalles de la receta más frecuente no encontrados.")
                                }
                            }
                        }
                    }
                }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    MostFrequentRecipeUiState(
                        message = "Calculando receta más frecuente...",
                    ),
                )
    }
