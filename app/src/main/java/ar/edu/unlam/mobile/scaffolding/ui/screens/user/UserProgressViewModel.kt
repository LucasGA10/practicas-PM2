package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProgressViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
        private val userRepository: UserRepository,
        savedStateHandle: SavedStateHandle,
    ) : ViewModel() {
        val currentUser: StateFlow<User?> =
            userRepository.getCurrentUser()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L),
                    initialValue = null,
                )
        private val _totalNutritionalValue = MutableStateFlow<NutritionalValue?>(null)
        val totalNutritionalValue: StateFlow<NutritionalValue?> = _totalNutritionalValue.asStateFlow()

        init {
            // Observar cambios en el usuario actual
            Log.d("UserProgressViewModel", "Observando cambios en el usuario actual")
            viewModelScope.launch {
                currentUser.collectLatest { user ->
                    if (user != null) {
                        Log.d("UserProgressViewModel", "Usuario actual: ${user.recipeHistory}")
                        calculateTotalNutritionalValue(user)
                    } else {
                        _totalNutritionalValue.value = null // Resetea si no hay usuario
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
    }
