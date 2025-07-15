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
    data object Loading : UserAuthState

    data class Authenticated(val user: User) : UserAuthState

    data object Unauthenticated : UserAuthState
}

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        // Este flow determina el estado de autenticación y si el dietGoal está presente
        val userAuthState: StateFlow<UserAuthState> =
            userRepository.getCurrentUser()
                .map { user ->
                    if (user != null) {
                        UserAuthState.Authenticated(user)
                    } else {
                        UserAuthState.Unauthenticated // O podrías tener otra lógica si un usuario "existe" pero está incompleto
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = UserAuthState.Loading,
                )
    }
