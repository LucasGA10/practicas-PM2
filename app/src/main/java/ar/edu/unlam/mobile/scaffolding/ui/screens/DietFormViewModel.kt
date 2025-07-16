package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietaryRestriction
import ar.edu.unlam.mobile.scaffolding.domain.model.user.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import ar.edu.unlam.mobile.scaffolding.domain.model.user.allDietaryRestrictions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DietFormUiState(
    val userName: String = "Usuario",
    val userImageUrl: String? = "",
    val weight: String = "",
    val height: String = "",
    val age: String = "",
    val selectedGender: Gender? = null,
    val selectedDietGoal: DietGoal? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFormValid: Boolean = false,
    val saveSuccess: Boolean = false, // Para navegar o mostrar mensaje de éxito
    val dietaryRestrictions: List<DietaryRestriction> = allDietaryRestrictions,
    val isDietarySectionVisible: Boolean = false, // Para controlar la expansión
    val isGenderGoalSectionVisible: Boolean = true, // O false si quieres que empiece colapsada
    val desiredCalories: String = "", // Se guarda como String para el TextField
    val desiredCaloriesLabel: String = "Calorías Deseadas", // Etiqueta dinámica
    val showDesiredCaloriesField: Boolean = false, // Para controlar la visibilidad
)

// Eventos que la UI puede enviar al ViewModel
sealed class DietFormEvent {
    data class WeightChanged(val weight: String) : DietFormEvent()

    data class HeightChanged(val height: String) : DietFormEvent()

    data class AgeChanged(val age: String) : DietFormEvent()

    data class GenderSelected(val gender: Gender) : DietFormEvent()

    data class DietGoalSelected(val dietGoal: DietGoal) : DietFormEvent()

    data class DesiredCaloriesChanged(val calories: String) : DietFormEvent() // Nuevo evento

    object SaveClicked : DietFormEvent()

    object ErrorMessageShown : DietFormEvent() // Para limpiar el mensaje de error

    object ResetSaveSuccess : DietFormEvent() // Para limpiar el flag de éxito

    object ToggleDietarySection : DietFormEvent()

    object ToggleGenderGoalSection : DietFormEvent()

    data class RestrictionToggled(val restrictionId: String) : DietFormEvent()
}

@HiltViewModel
class DietFormViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
    private val _uiState = mutableStateOf(DietFormUiState())
    val uiState: DietFormUiState by _uiState

    init {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { loggedInUser ->
                if (loggedInUser != null) {
                    loadDataForCurrentUser(loggedInUser)
                } else {
                    _uiState.value = uiState.copy(
                        isLoading = false,
                        errorMessage = "No hay un usuario activo.",
                        userName = "Error",
                        // Resetea otros campos si es necesario
                    )
                }
            }
        }
    }

    private fun loadDataForCurrentUser(currentUser: User) {
        _uiState.value = uiState.copy(isLoading = true)

        _uiState.value = uiState.copy(
            userName = currentUser.userName.ifEmpty { "Usuario" },
            userImageUrl = currentUser.imageUrl,
            weight = currentUser.weightKg?.toString() ?: "",
            height = currentUser.heightCm?.toString() ?: "",
            age = currentUser.age?.toString() ?: "",
            selectedGender = currentUser.gender,
            selectedDietGoal = currentUser.dietGoal,
            desiredCalories = if (currentUser.dietGoal != null) currentUser.desiredCalories?.toString() ?: "" else "",
            showDesiredCaloriesField = currentUser.dietGoal != null,
            desiredCaloriesLabel = when (currentUser.dietGoal) {
                DietGoal.LOSE_WEIGHT -> "Calorías Máximas"
                DietGoal.GAIN_WEIGHT -> "Calorías Mínimas"
                else -> "Calorías Deseadas"
            },
            isLoading = false,
            errorMessage = null
        )

        val updatedRestrictions = allDietaryRestrictions.map { option ->
            option.copy(isSelected = currentUser.selectedDietaryRestrictions?.contains(option.id) == true)
        }
        _uiState.value = uiState.copy(
            dietaryRestrictions = updatedRestrictions,
            isFormValid = checkFormValidity(uiState) // Validar con el estado actualizado
        )
    }

    fun onEvent(event: DietFormEvent) {
        var newState: DietFormUiState
        when (event) {
            is DietFormEvent.WeightChanged -> newState = uiState.copy(weight = event.weight)
            is DietFormEvent.HeightChanged -> newState = uiState.copy(height = event.height)
            is DietFormEvent.AgeChanged -> newState = uiState.copy(age = event.age)
            is DietFormEvent.GenderSelected -> newState = uiState.copy(selectedGender = event.gender)
            is DietFormEvent.DietGoalSelected -> {
                val newLabel = when (event.dietGoal) {
                    DietGoal.LOSE_WEIGHT -> "Calorías Máximas"
                    DietGoal.GAIN_WEIGHT -> "Calorías Mínimas"
                    else -> "Calorías Deseadas"
                }
                newState = uiState.copy(
                    selectedDietGoal = event.dietGoal,
                    showDesiredCaloriesField = true,
                    desiredCaloriesLabel = newLabel
                )
            }
            is DietFormEvent.DesiredCaloriesChanged -> newState = uiState.copy(desiredCalories = event.calories)
            is DietFormEvent.RestrictionToggled -> {
                val updatedRestrictions = uiState.dietaryRestrictions.map {
                    if (it.id == event.restrictionId) it.copy(isSelected = !it.isSelected) else it
                }
                newState = uiState.copy(dietaryRestrictions = updatedRestrictions)
            }
            is DietFormEvent.SaveClicked -> {
                saveDietInfo()
                return // saveDietInfo maneja el estado de la UI, incluyendo isFormValid
            }
            is DietFormEvent.ErrorMessageShown -> newState = uiState.copy(errorMessage = null)
            is DietFormEvent.ResetSaveSuccess -> newState = uiState.copy(saveSuccess = false)
            is DietFormEvent.ToggleGenderGoalSection -> newState = uiState.copy(isGenderGoalSectionVisible = !uiState.isGenderGoalSectionVisible)
            is DietFormEvent.ToggleDietarySection -> newState = uiState.copy(isDietarySectionVisible = !uiState.isDietarySectionVisible)
        }
        _uiState.value = newState.copy(
            saveSuccess = false, // Cualquier cambio en el formulario resetea el éxito de guardado
            isFormValid = checkFormValidity(newState)
        )
    }

    private fun checkFormValidity(state: DietFormUiState): Boolean {
        val weightValid = state.weight.toFloatOrNull()?.let { it > 0 } ?: false
        val heightValid = state.height.toFloatOrNull()?.let { it > 0 } ?: false
        val ageValid = state.age.toIntOrNull()?.let { it > 0 } ?: false
        val genderSelected = state.selectedGender != null
        val dietGoalSelected = state.selectedDietGoal != null
        val desiredCaloriesValid =
            if (state.showDesiredCaloriesField) {
                state.desiredCalories.toFloatOrNull()?.let { it > 0 } ?: false
            } else {
                true
            }
        return weightValid && heightValid && ageValid && genderSelected && dietGoalSelected && desiredCaloriesValid
    }

    private fun saveDietInfo() {
        val currentFormState = uiState.copy(isFormValid = checkFormValidity(uiState))
        _uiState.value = currentFormState

        if (!currentFormState.isFormValid) {
            _uiState.value = currentFormState.copy(errorMessage = "Por favor, completa todos los campos correctamente.")
            return
        }

        viewModelScope.launch {
            // --- CORRECCIÓN AQUÍ ---
            // getCurrentUser() devuelve Flow<User?>, usamos .first() para obtener el primer (y único) valor emitido.
            // Esto es seguro si getCurrentUser() siempre emite el estado actual del usuario (incluido null).
            val userToUpdate: User? = userRepository.getCurrentUser().first()
            // -----------------------

            if (userToUpdate == null) {
                _uiState.value = uiState.copy(isLoading = false, errorMessage = "Error: Usuario no encontrado para guardar.")
                return@launch
            }

            _uiState.value = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val weightKg = uiState.weight.toFloat()
                val heightCm = uiState.height.toFloat()
                val ageYears = uiState.age.toInt()
                val gender = uiState.selectedGender!! // Asegúrate de que no sean null por la validación
                val dietGoal = uiState.selectedDietGoal!! // Asegúrate de que no sean null por la validación
                val selectedRestrictionIds = uiState.dietaryRestrictions.filter { it.isSelected }.map { it.id }
                val desiredCalories = if (uiState.showDesiredCaloriesField) uiState.desiredCalories.toDouble() else 0.0

                val result = userRepository.updateUserDietProfile(
                    userId = userToUpdate.id, // USA EL ID DEL USUARIO LOGUEADO
                    weightKg = weightKg,
                    heightCm = heightCm,
                    age = ageYears,
                    gender = gender,
                    dietGoal = dietGoal,
                    selectedRestrictions = selectedRestrictionIds,
                    desiredCalories = desiredCalories
                )

                result.fold(
                    onSuccess = { _uiState.value = uiState.copy(isLoading = false, saveSuccess = true) },
                    onFailure = { exception ->
                        _uiState.value = uiState.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar: ${exception.message}"
                        )
                    }
                )
            } catch (e: NumberFormatException) {
                _uiState.value = uiState.copy(
                    isLoading = false,
                    errorMessage = "Por favor, ingresa números válidos."
                )
            } catch (e: Exception) {
                _uiState.value = uiState.copy(
                    isLoading = false,
                    errorMessage = "Ocurrió un error inesperado: ${e.message}"
                )
            }
        }
    }
}