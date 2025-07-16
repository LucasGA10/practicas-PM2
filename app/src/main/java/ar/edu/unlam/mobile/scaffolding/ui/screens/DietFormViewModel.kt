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
import ar.edu.unlam.mobile.scaffolding.domain.model.user.allDietaryRestrictions
import dagger.hilt.android.lifecycle.HiltViewModel
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

        private val currentActiveUserId: Int = 1

        init {
            loadExistingData()
        }

        private fun loadExistingData() {
            viewModelScope.launch {
                _uiState.value =
                    uiState.copy(isLoading = true) // Opcional: mostrar un loader general para datos iniciales
                val user = userRepository.getUserById(currentActiveUserId)
                // Si tuvieras un Flow en userRepository.getCurrentUser() podrías hacer:
                // val user = userRepository.getCurrentUser().firstOrNull { it?.id == currentActiveUserId }
                if (user != null) {
                    _uiState.value =
                        uiState.copy(
                            userName = user.userName.ifEmpty { "Usuario" },
                            userImageUrl = user.imageUrl, // Asumiendo que User tiene profileImageUrl
                            // Datos del formulario
                            weight =
                                user.weightKg?.toString()
                                    ?: uiState.weight,
                            // Mantener valor actual si no hay dato guardado
                            height = user.heightCm?.toString() ?: uiState.height,
                            age = user.age?.toString() ?: uiState.age,
                            selectedGender = user.gender ?: uiState.selectedGender,
                            selectedDietGoal = user.dietGoal ?: uiState.selectedDietGoal,
                            isLoading = false,
                        )
                    // Cargar restricciones dietéticas seleccionadas
                    val updatedRestrictions =
                        uiState.dietaryRestrictions.map { option ->
                            option.copy(isSelected = user.selectedDietaryRestrictions?.contains(option.id) == true)
                        }
                    _uiState.value =
                        uiState.copy(
                            // ...
                            dietaryRestrictions = updatedRestrictions,
                            // ...
                        )
                    validateForm() // Revalidar el formulario con los datos cargados
                } else {
                    // Manejar el caso de que el usuario no se encuentre o no se pueda cargar
                    _uiState.value =
                        uiState.copy(
                            errorMessage = "No se pudieron cargar los datos del usuario.",
                            userName = "Error",
                        )
                }
            }
        }

        fun onEvent(event: DietFormEvent) {
            when (event) {
                is DietFormEvent.WeightChanged ->
                    _uiState.value = uiState.copy(weight = event.weight, saveSuccess = false)

                is DietFormEvent.HeightChanged ->
                    _uiState.value = uiState.copy(height = event.height, saveSuccess = false)

                is DietFormEvent.AgeChanged ->
                    _uiState.value = uiState.copy(age = event.age, saveSuccess = false)

                is DietFormEvent.GenderSelected ->
                    _uiState.value = uiState.copy(selectedGender = event.gender, saveSuccess = false)

                is DietFormEvent.DietGoalSelected -> {
                    val newLabel =
                        when (event.dietGoal) {
                            DietGoal.LOSE_WEIGHT -> "Calorías Máximas"
                            DietGoal.GAIN_WEIGHT -> "Calorías Mínimas"
                            else -> "Calorías Deseadas"
                        }
                    _uiState.value =
                        uiState.copy(
                            selectedDietGoal = event.dietGoal,
                            showDesiredCaloriesField = true, // Mostrar siempre el campo al seleccionar un objetivo
                            desiredCaloriesLabel = newLabel,
                            isFormValid = validateForm(),
                        )
                }
                is DietFormEvent.DesiredCaloriesChanged -> {
                    _uiState.value =
                        uiState.copy(
                            desiredCalories = event.calories,
                            isFormValid = validateForm(),
                        )
                }
                is DietFormEvent.RestrictionToggled -> {
                    val updatedRestrictions =
                        uiState.dietaryRestrictions.map {
                            if (it.id == event.restrictionId) it.copy(isSelected = !it.isSelected) else it
                        }
                    _uiState.value = uiState.copy(dietaryRestrictions = updatedRestrictions)
                    // No es necesario revalidar el formulario aquí a menos que las restricciones afecten la validez
                }

                is DietFormEvent.SaveClicked -> saveDietInfo()
                is DietFormEvent.ErrorMessageShown -> _uiState.value = uiState.copy(errorMessage = null)
                is DietFormEvent.ResetSaveSuccess -> _uiState.value = uiState.copy(saveSuccess = false)
                is DietFormEvent.ToggleGenderGoalSection -> {
                    _uiState.value = uiState.copy(isGenderGoalSectionVisible = !uiState.isGenderGoalSectionVisible)
                }
                is DietFormEvent.ToggleDietarySection -> {
                    _uiState.value = uiState.copy(isDietarySectionVisible = !uiState.isDietarySectionVisible)
                }
                // Otros eventos...
            }
            validateForm()
        }

        private fun validateForm(): Boolean {
            val weightValid = uiState.weight.toDoubleOrNull()?.let { it > 0 } ?: false
            val heightValid = uiState.height.toIntOrNull()?.let { it > 0 } ?: false
            val ageValid = uiState.age.toIntOrNull()?.let { it > 0 } ?: false
            val genderSelected = uiState.selectedGender != null
            val dietGoalSelected = uiState.selectedDietGoal != null
            // Validación para calorías deseadas si el campo es visible
            val desiredCaloriesValid =
                if (uiState.showDesiredCaloriesField) {
                    uiState.desiredCalories.toDoubleOrNull()?.let { it > 0 } ?: false
                } else {
                    true // Si no se muestra, se considera válido para este campo
                }

            return weightValid && heightValid && ageValid && genderSelected && dietGoalSelected && desiredCaloriesValid
        }

        private fun saveDietInfo() {
            if (!uiState.isFormValid) {
                _uiState.value = uiState.copy(errorMessage = "Por favor, completa todos los campos correctamente.")
                return
            }

            viewModelScope.launch {
                _uiState.value = uiState.copy(isLoading = true, errorMessage = null)
                try {
                    val weightKg = uiState.weight.toFloat()
                    val heightCm = uiState.height.toFloat() // Cambiado a toInt como en tu validación
                    val ageYears = uiState.age.toInt()
                    val gender = uiState.selectedGender!!
                    val dietGoal = uiState.selectedDietGoal!!
                    val selectedRestrictionIds =
                        uiState.dietaryRestrictions
                            .filter { it.isSelected }
                            .map { it.id }
                    val desiredCalories = uiState.desiredCalories.toDouble()

                    val result =
                        userRepository.updateUserDietProfile(
                            userId = currentActiveUserId,
                            weightKg = weightKg,
                            heightCm = heightCm,
                            age = ageYears,
                            gender = gender,
                            dietGoal = dietGoal,
                            selectedRestrictions = selectedRestrictionIds,
                            desiredCalories = desiredCalories,
                        )

                    result.fold(
                        onSuccess = {
                            _uiState.value = uiState.copy(isLoading = false, saveSuccess = true)
                        },
                        onFailure = { exception ->
                            _uiState.value =
                                uiState.copy(
                                    isLoading = false,
                                    errorMessage = "Error al guardar: ${exception.message}",
                                )
                        },
                    )
                } catch (e: NumberFormatException) {
                    _uiState.value =
                        uiState.copy(
                            isLoading = false,
                            errorMessage = "Por favor, ingresa números válidos para peso, altura y edad.",
                        )
                } catch (e: Exception) {
                    _uiState.value =
                        uiState.copy(
                            isLoading = false,
                            errorMessage = "Ocurrió un error inesperado: ${e.message}",
                        )
                }
            }
        }
    }
