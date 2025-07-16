package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.util.Log
import androidx.activity.result.launch
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
class UserViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {

    private val _logoutState = MutableStateFlow<LogoutEvent?>(null)
    val logoutState = _logoutState.asStateFlow()

    sealed class LogoutEvent {
        object NavigateToLogin : LogoutEvent()
    }
    val currentUser: StateFlow<User?> =
            userRepository.getCurrentUser() // Debe ser Flow<User?>
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L), // Mantén el flow activo mientras haya suscriptores
                    initialValue = null, // O un valor inicial apropiado
                )

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.clearCurrentUserSession()

            _logoutState.value = LogoutEvent.NavigateToLogin
            Log.d("UserViewModel", "Usuario ha cerrado sesión, evento NavigateToLogin emitido.")
        }
    }

    fun onLogoutEventConsumed() {
        _logoutState.value = null
    }

    }


