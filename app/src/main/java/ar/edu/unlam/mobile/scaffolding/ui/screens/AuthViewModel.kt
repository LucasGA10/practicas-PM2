package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface UserAuthState {
    object Loading : UserAuthState // Estado inicial mientras se carga el usuario

    object Unauthenticated : UserAuthState // Nadie ha iniciado sesión

    data class AuthenticatedWithoutDiet(val user: User) : UserAuthState // Logueado, pero sin datos de dieta

    data class AuthenticatedWithDiet(val user: User) : UserAuthState // Logueado y con datos de dieta
}

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        val userAuthState: StateFlow<UserAuthState> =
            userRepository.getCurrentUser()
                .map { user ->
                    if (user == null) {
                        UserAuthState.Unauthenticated
                    } else {
                        if (user.dietGoal == null) { // Ajusta esta condición según tu modelo
                            UserAuthState.AuthenticatedWithoutDiet(user)
                        } else {
                            UserAuthState.AuthenticatedWithDiet(user)
                        }
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = UserAuthState.Loading, // Correcto: estado inicial es Loading
                )
    }
