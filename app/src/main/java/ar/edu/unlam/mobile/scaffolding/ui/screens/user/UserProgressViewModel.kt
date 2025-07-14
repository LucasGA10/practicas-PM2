package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserProgressViewModel
    @Inject
    constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
        private val _userId = MutableStateFlow<Int?>(savedStateHandle.get<Int>("userId"))
        val userId: StateFlow<Int?> = _userId
    }
