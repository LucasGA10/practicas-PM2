package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.animation.core.copy
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

)

// Eventos que la UI puede enviar al ViewModel
sealed class DietFormEvent {
    data class WeightChanged(val weight: String) : DietFormEvent()
    data class HeightChanged(val height: String) : DietFormEvent()
    data class AgeChanged(val age: String) : DietFormEvent()
    data class GenderSelected(val gender: Gender) : DietFormEvent()
    data class DietGoalSelected(val dietGoal: DietGoal) : DietFormEvent()
    object SaveClicked : DietFormEvent()
    object ErrorMessageShown : DietFormEvent() // Para limpiar el mensaje de error
    object ResetSaveSuccess : DietFormEvent() // Para limpiar el flag de éxito
    object ToggleDietarySection : DietFormEvent()
    data class RestrictionToggled(val restrictionId: String) : DietFormEvent()

}

@HiltViewModel
class DietFormViewModel
@Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    var uiState by mutableStateOf(DietFormUiState())
        private set

    private val currentActiveUserId: Int = 1

    init {
        loadExistingData()
    }

    private fun loadExistingData() {
        viewModelScope.launch {
            uiState =
                uiState.copy(isLoading = true) // Opcional: mostrar un loader general para datos iniciales
            val user = userRepository.getUserById(currentActiveUserId)
            // Si tuvieras un Flow en userRepository.getCurrentUser() podrías hacer:
            // val user = userRepository.getCurrentUser().firstOrNull { it?.id == currentActiveUserId }
            if (user != null) {
                uiState = uiState.copy(
                    userName = user.userName.ifEmpty { "Usuario" },
                    userImageUrl = user.imageUrl, // Asumiendo que User tiene profileImageUrl

                    // Datos del formulario
                    weight = user.weightKg?.toString()
                        ?: uiState.weight, // Mantener valor actual si no hay dato guardado
                    height = user.heightCm?.toString() ?: uiState.height,
                    age = user.age?.toString() ?: uiState.age,
                    selectedGender = user.gender ?: uiState.selectedGender,
                    selectedDietGoal = user.dietGoal ?: uiState.selectedDietGoal,
                    isLoading = false
                )
                // Cargar restricciones dietéticas seleccionadas
                val updatedRestrictions = uiState.dietaryRestrictions.map { option ->
                    option.copy(isSelected = user.selectedDietaryRestrictions?.contains(option.id) == true)
                }

                uiState = uiState.copy(
                    // ...
                    dietaryRestrictions = updatedRestrictions,
                    // ...
                )
                validateForm() // Revalidar el formulario con los datos cargados


            } else {
                // Manejar el caso de que el usuario no se encuentre o no se pueda cargar
                uiState = uiState.copy(
                    errorMessage = "No se pudieron cargar los datos del usuario.",
                    userName = "Error",
                )
            }
        }
    }

    fun onEvent(event: DietFormEvent) {
        when (event) {
            is DietFormEvent.WeightChanged -> uiState =
                uiState.copy(weight = event.weight, saveSuccess = false)

            is DietFormEvent.HeightChanged -> uiState =
                uiState.copy(height = event.height, saveSuccess = false)

            is DietFormEvent.AgeChanged -> uiState =
                uiState.copy(age = event.age, saveSuccess = false)

            is DietFormEvent.GenderSelected -> uiState =
                uiState.copy(selectedGender = event.gender, saveSuccess = false)

            is DietFormEvent.DietGoalSelected -> uiState =
                uiState.copy(selectedDietGoal = event.dietGoal, saveSuccess = false)

            is DietFormEvent.SaveClicked -> saveDietInfo()
            is DietFormEvent.ErrorMessageShown -> uiState = uiState.copy(errorMessage = null)
            is DietFormEvent.ResetSaveSuccess -> uiState = uiState.copy(saveSuccess = false)
            is DietFormEvent.ToggleDietarySection -> {
                uiState = uiState.copy(isDietarySectionVisible = !uiState.isDietarySectionVisible)
            }
            is DietFormEvent.RestrictionToggled -> {
                val updatedRestrictions = uiState.dietaryRestrictions.map {
                    if (it.id == event.restrictionId) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it
                    }
                }
                uiState = uiState.copy(dietaryRestrictions = updatedRestrictions, saveSuccess = false)
                // No necesitas llamar a validateForm() aquí a menos que la validez dependa de esto.
            }
        }
        validateForm()
    }

    private fun validateForm() {
        val weightValid =
            uiState.weight.toFloatOrNull()?.let { it > 0 && it < 500 } ?: false // Rangos básicos
        val heightValid =
            uiState.height.toFloatOrNull()?.let { it > 50 && it < 300 } ?: false // Rangos básicos
        val ageValid =
            uiState.age.toIntOrNull()?.let { it > 0 && it < 150 } ?: false // Rangos básicos
        val genderValid = uiState.selectedGender != null
        val dietGoalValid = uiState.selectedDietGoal != null

        uiState = uiState.copy(
            isFormValid = weightValid && heightValid && ageValid && genderValid && dietGoalValid
        )
    }

    private fun saveDietInfo() {
        if (!uiState.isFormValid) {
            uiState = uiState.copy(errorMessage = "Por favor, completa todos los campos correctamente.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val weightKg = uiState.weight.toFloat()
                val heightCm = uiState.height.toFloat() // Cambiado a toInt como en tu validación
                val ageYears = uiState.age.toInt()
                val gender = uiState.selectedGender!!
                val dietGoal = uiState.selectedDietGoal!!
                val selectedRestrictionIds = uiState.dietaryRestrictions
                    .filter { it.isSelected }
                    .map { it.id }


                val result = userRepository.updateUserDietProfile(
                    userId = currentActiveUserId,
                    weightKg = weightKg,
                    heightCm = heightCm,
                    age = ageYears,
                    gender = gender,
                    dietGoal = dietGoal,
                    selectedRestrictions = selectedRestrictionIds
                )

                result.fold(
                    onSuccess = {
                        uiState = uiState.copy(isLoading = false, saveSuccess = true)
                    },
                    onFailure = { exception ->
                        uiState = uiState.copy(
                            isLoading = false,
                            errorMessage = "Error al guardar: ${exception.message}"
                        )
                    }
                )

            } catch (e: NumberFormatException) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Por favor, ingresa números válidos para peso, altura y edad."
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Ocurrió un error inesperado: ${e.message}"
                )
            }
        }
    }
}