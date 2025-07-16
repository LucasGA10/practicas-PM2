package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import ar.edu.unlam.mobile.scaffolding.domain.model.user.CompletedRecipeInfo
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.BarChartData.Bar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
        private val ioDispatcher: CoroutineDispatcher,
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

        private val _weeklyCaloriesChartData = MutableStateFlow<BarChartData?>(null)
        val weeklyCaloriesChartData: StateFlow<BarChartData?> = _weeklyCaloriesChartData.asStateFlow()

        init {
            Log.d("UserProgressViewModel", "Init ViewModel")
            viewModelScope.launch {
                currentUser.collectLatest { user ->
                    if (user != null) {
                        Log.d("UserProgressViewModel", "Usuario actual detectado: ${user.id}")
                        calculateTotalNutritionalValue(user)
                        calculateTodayNutritionalValue(user)
                    } else {
                        Log.d(
                            "UserProgressViewModel",
                            "Usuario actual es null, reseteando valores nutricionales.",
                        )
                        _totalNutritionalValue.value = null
                        _todayNutritionalValue.value = null
                    }
                }
            }
            updateWeeklyCaloriesChart(currentUser.value?.recipeHistory!!)
        }

        private fun calculateTotalNutritionalValue(user: User) {
            viewModelScope.launch {
                if (user.recipeHistory.isEmpty()) {
                    _totalNutritionalValue.value = NutritionalValue()
                    return@launch
                }
                Log.d(
                    "UserProgressViewModel",
                    "Calculando valor nutricional: ${_totalNutritionalValue.value}",
                )
                var accumulatedNutritionalValue = NutritionalValue()

                for (completedRecipeInfo in user.recipeHistory) {
                    // Obtiene el NutritionalValue para cada receta en el historial
                    val recipeNutritionalValue =
                        recipesRepository.getNutritionalValueByRecipeId(completedRecipeInfo.recipeId)

                    // Suma el valor obtenido al acumulado (usando la función `plus` que definimos)
                    accumulatedNutritionalValue += recipeNutritionalValue
                    Log.d(
                        "UserProgressViewModel",
                        "Valor nutricional acumulado: ${_totalNutritionalValue.value}",
                    )
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
                                    completionDateCalendar.get(Calendar.DAY_OF_YEAR) ==
                                    today.get(
                                        Calendar.DAY_OF_YEAR,
                                    )
                                ) {
                                    completedRecipeInfo
                                } else {
                                    null
                                }
                            } else {
                                Log.e(
                                    "UserProgressViewModel",
                                    "dateFormat.parse devolvió null para: ${completedRecipeInfo.completionDate}",
                                )
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
                    Log.d(
                        "UserProgressViewModel",
                        "No hay recetas consumidas HOY válidas, estableciendo valor nutricional de hoy a ceros.",
                    )
                    _todayNutritionalValue.value =
                        NutritionalValue() // Asegúrate que NutritionalValue() por defecto tiene ceros
                    return@launch
                }

                Log.d(
                    "UserProgressViewModel",
                    "PROCEDIENDO a calcular suma nutricional para ${recipesToday.size} recetas de HOY.",
                )
                var accumulatedNutritionalValueToday = NutritionalValue() // Inicializa a ceros

                for (completedRecipeInfo in recipesToday) {
                    try {
                        Log.d(
                            "UserProgressViewModel",
                            "Procesando en bucle receta de HOY ID: ${completedRecipeInfo.recipeId}",
                        )
                        val nutritionalValue =
                            recipesRepository.getNutritionalValueByRecipeId(completedRecipeInfo.recipeId)

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
                Log.d(
                    "UserProgressViewModel",
                    "Valor FINAL de HOY asignado a StateFlow: ${_todayNutritionalValue.value}",
                )
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
                            Log.d(
                                "UserProgressViewModel",
                                "Receta más frecuente ID: $mostFrequentRecipeId, Conteo: $count",
                            )

                            // 2. Obtener los detalles de ESA receta desde el repositorio
                            // recipesRepository.getRecipeListItemById(recipeId) debería devolver Flow<RecipeListItem?>
                            recipesRepository.getRecipeListItemById(mostFrequentRecipeId)
                                .map { recipeListItem ->
                                    if (recipeListItem != null) {
                                        Log.d(
                                            "UserProgressViewModel",
                                            "Detalles encontrados para la receta más frecuente: ${recipeListItem.name}",
                                        )
                                        MostFrequentRecipeUiState(
                                            recipe = recipeListItem,
                                            count = count,
                                        )
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

        fun addCustomNutritionalValue(customValue: NutritionalValue) {
            viewModelScope.launch(ioDispatcher) {
                val currentToday =
                    _todayNutritionalValue.value ?: NutritionalValue() // Si es null, empieza desde cero
                _todayNutritionalValue.value = currentToday + customValue

                Log.d(
                    "UserProgressViewModel",
                    "Custom value añadido. Nuevo todayNutrition: ${_totalNutritionalValue.value}",
                )

                // Opcional: Si quieres que esto afecte el total acumulado y potencialmente se guarde:
                // 1. Actualizar el total general en memoria
                val currentTotal = _totalNutritionalValue.value ?: NutritionalValue()
                _totalNutritionalValue.value = currentTotal + customValue

                // 2. (Más avanzado) Persistir esta entrada manual.
                // Esto requeriría una nueva función en UserRepository, por ejemplo,
                // `addManualEntryToHistory(userId: Int, nutritionalValue: NutritionalValue)`
                // y CompletedRecipeInfo podría necesitar una forma de distinguir entradas manuales.
                // Por ahora, solo actualizamos el estado en memoria.
                // _user.value?.let { currentUser ->
                //     val result = userRepository.addManualNutritionalEntry(currentUser.id, customValue)
                //     if (result.isFailure) {
                //         Log.e("UserProgressViewModel", "Error al guardar entrada manual: ${result.exceptionOrNull()?.message}")
                //     }
                // }
            }
        }

        fun updateWeeklyCaloriesChart(history: List<CompletedRecipeInfo>) { // currentUser ya es un StateFlow en el ViewModel
            val currentUserData = currentUser.value // Obtener el valor actual del usuario
            if (currentUserData == null) {
                Log.w("ChartLogic", "Usuario actual es null, no se puede generar gráfico con colores de objetivo.")
                _weeklyCaloriesChartData.value =
                    BarChartData( // Podrías mostrar barras grises por defecto
                        bars =
                            (0 until 7).map { i ->
                                val date = LocalDate.now().minusDays((6 - i).toLong())
                                Bar(label = date.format(DateTimeFormatter.ofPattern("EEE", Locale.getDefault())), value = 0f, color = Color.Gray)
                            },
                    )
                return
            }

            val desiredCalories = currentUserData.desiredCalories!!.toFloat() // Asumiendo que es Int o Double
            val dietGoal = currentUserData.dietGoal!!.name

            Log.d("ChartLogic", "updateWeeklyCaloriesChart llamado. Goal: $dietGoal, Desired Cals: $desiredCalories")
            viewModelScope.launch(ioDispatcher) {
                val today = LocalDate.now()
                val daysToShow = 7
                val chartBars = mutableListOf<Bar>()

                val historyDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

                val dailyCaloriesMap = mutableMapOf<LocalDate, Float>()
                for (i in (daysToShow - 1) downTo 0) {
                    val date = today.minusDays(i.toLong())
                    dailyCaloriesMap[date] = 0f
                }

                val recentHistory =
                    history.filter { completedRecipeInfo ->
                        try {
                            val completionDateParsed: Date = historyDateFormat.parse(completedRecipeInfo.completionDate)
                            // Convertir java.util.Date a java.time.LocalDate
                            val completionLocalDate: LocalDate =
                                completionDateParsed.toInstant()
                                    .atZone(ZoneId.systemDefault()) // Usar el ZoneId del sistema
                                    .toLocalDate()

                            // Comprobar si la fecha está dentro de los últimos 'daysToShow' (7 días)
                            // Incluyendo hoy y los 6 días anteriores.
                            val startDate = today.minusDays(daysToShow - 1L) // El primer día del rango (hace 6 días)

                            !completionLocalDate.isBefore(startDate) && !completionLocalDate.isAfter(today)
                        } catch (e: Exception) {
                            // Si hay un error al parsear la fecha, se excluye este elemento del historial.
                            Log.e("ChartLogic_Filter", "Error parseando fecha para filtrar: ${completedRecipeInfo.completionDate}", e)
                            false
                        }
                    }
                Log.d("ChartLogic", "Historial reciente filtrado: ${recentHistory.size} entradas.")

                for (completedRecipe in recentHistory) {
                    try {
                        // ... (lógica para parsear fecha y obtener nutritionalValue) ...
                        val nutritionalValue = recipesRepository.getNutritionalValueByRecipeId(completedRecipe.recipeId)

                        if (nutritionalValue != null) {
                            val completionLocalDate =
                                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                                    .parse(completedRecipe.completionDate)!!
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()

                            val currentCalories = dailyCaloriesMap[completionLocalDate] ?: 0f
                            dailyCaloriesMap[completionLocalDate] = currentCalories + nutritionalValue.calories.toFloat()
                        }
                    } catch (e: Exception) {
                        Log.e("ChartLogic", "Error procesando receta ${completedRecipe.recipeId}: ", e)
                    }
                }

                val sortedDailyCalories = dailyCaloriesMap.entries.sortedBy { it.key }

                // Definir los colores (puedes ponerlos en un companion object o constantes)
                val defaultBarColor = Color(0xFF6200EE) // Morado por defecto (o tu MaterialTheme.colorScheme.primary si lo pasas)
                val targetMetOrSurplusColor = Color(0xFF4CAF50) // Verde (bueno para GAIN_WEIGHT si se alcanza/supera)
                val deficitColor = Color(0xFFF44336) // Rojo (malo para GAIN_WEIGHT, bueno para LOSE_WEIGHT si es un déficit)
                val warningNearTargetColor = Color(0xFFFFEB3B) // Amarillo (cerca del objetivo para MANTENER)

                if (sortedDailyCalories.isNotEmpty()) {
                    sortedDailyCalories.forEach { (date, dailyTotalCalories) ->
                        val barColor =
                            determineBarColor(
                                dailyCalories = dailyTotalCalories,
                                desiredCalories = desiredCalories,
                                dietGoal = dietGoal,
                                defaultColor = defaultBarColor,
                                targetMetColor = targetMetOrSurplusColor,
                                deficitColor = deficitColor,
                                warningColor = warningNearTargetColor,
                            )

                        chartBars.add(
                            Bar(
                                label = date.format(dayOfWeekFormatter),
                                value = dailyTotalCalories,
                                color = barColor,
                            ),
                        )
                    }
                    _weeklyCaloriesChartData.value = BarChartData(bars = chartBars)
                    Log.d("ChartLogic", "_weeklyCaloriesChartData actualizado con ${chartBars.size} barras y colores de objetivo.")
                } else {
                    // ... (lógica para barras vacías/de cero, podrían ser todas Color.Gray)
                    val emptyBars =
                        (0 until daysToShow).map { i ->
                            val dateValue = today.minusDays((daysToShow - 1 - i).toLong())
                            Bar(label = dateValue.format(dayOfWeekFormatter), value = 0f, color = Color.Gray)
                        }
                    _weeklyCaloriesChartData.value = BarChartData(bars = emptyBars)
                }
            }
        }

        private fun determineBarColor(
            dailyCalories: Float,
            desiredCalories: Float,
            dietGoal: String, // O tu tipo Enum: DietGoal
            defaultColor: Color,
            targetMetColor: Color,
            deficitColor: Color,
            warningColor: Color,
            tolerance: Float = 50f, // Tolerancia de +/- 50 calorías para MANTENER y otros
        ): Color {
            return when (dietGoal) { // Asume que dietGoal es un String como "LOSE_WEIGHT", "GAIN_WEIGHT", "MAINTAIN_WEIGHT"
                "LOSE_WEIGHT" -> {
                    if (dailyCalories > desiredCalories) {
                        deficitColor // Se pasó (rojo)
                    } else {
                        targetMetColor // Está en o por debajo del objetivo (verde)
                    }
                }
                "GAIN_WEIGHT" -> {
                    if (dailyCalories < desiredCalories) {
                        deficitColor // No alcanzó (rojo)
                    } else {
                        targetMetColor // Alcanzó o superó (verde)
                    }
                }
                "MAINTAIN_WEIGHT" -> { // Y cualquier otro caso, o puedes ser más explícito
                    when {
                        dailyCalories > desiredCalories + tolerance -> warningColor // Se pasó significativamente (amarillo)
                        dailyCalories < desiredCalories - tolerance -> warningColor // Le faltó significativamente (amarillo)
                        else -> targetMetColor // Dentro de la tolerancia (verde)
                    }
                }
                else -> defaultColor // Caso por defecto si dietGoal no coincide
            }
        }
    }
