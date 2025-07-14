package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun UserScreen(
    viewModel: UserViewModel = hiltViewModel(),
    navController: NavController,
)  {
    Scaffold { innerPadding ->
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = { navController.navigate("dietForm") },
            ) {
                Text(text = "Ir a formulario de dieta (testeo)")
            }
        }
    }
}
