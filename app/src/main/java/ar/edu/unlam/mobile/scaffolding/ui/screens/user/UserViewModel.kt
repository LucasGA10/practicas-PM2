package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {

        val currentUser: StateFlow<User?> =
            userRepository.getCurrentUser() // Debe ser Flow<User?>
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000L), // Mantén el flow activo mientras haya suscriptores
                    initialValue = null, // O un valor inicial apropiado
                )
    }

// Button(
// onClick = { navController.navigate("dietForm") },
// ) {
//    Text(text = "Ir a formulario de dieta (testeo)")
// }
