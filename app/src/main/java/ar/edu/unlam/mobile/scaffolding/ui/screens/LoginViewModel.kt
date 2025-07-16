package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        private val _loginResult = MutableStateFlow<Result<User>?>(null)
        val loginResult: StateFlow<Result<User>?> = _loginResult.asStateFlow()

        val currentUser: StateFlow<User?> =
            userRepository.getCurrentUser()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = null,
                )

        fun attemptLogin(
            emailValue: String,
            passwordValue: String,
        ) {
            viewModelScope.launch {
                // Actualizar estados internos si los tienes aquí
                // email = emailValue
                // password = passwordValue

                _isLoading.value = true
                _loginResult.value = null // Limpiar resultado anterior

                if (emailValue.isBlank() || passwordValue.isBlank()) {
                    _loginResult.value = Result.failure(IllegalArgumentException("Email y contraseña no pueden estar vacíos."))
                    _isLoading.value = false
                    return@launch
                }

                // Simular un pequeño retraso para ver el indicador de carga
                // delay(1000)

                val result = userRepository.loginUser(emailValue, passwordValue)
                _loginResult.value = result
                // No establezcas currentUser aquí directamente; deja que el Flow de getCurrentUser() lo haga.
                _isLoading.value = false
            }
        }

        // Para limpiar el resultado después de que la UI lo ha consumido (ej. mostrado Toast)
        fun clearLoginResult() {
            _loginResult.value = null
        }
    }
