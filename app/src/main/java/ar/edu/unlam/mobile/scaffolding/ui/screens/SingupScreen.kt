package ar.edu.unlam.mobile.scaffolding.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SingupScreen(
    viewModel: SingupViewModel = hiltViewModel(),
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text("SingupScreen")
    }
}