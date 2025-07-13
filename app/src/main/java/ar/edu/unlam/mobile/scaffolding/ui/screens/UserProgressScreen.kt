package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProgressScreen(
    viewModel: UserProgressViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
) {
    val userId by viewModel.userId.collectAsState()

    Scaffold(
        topBar = { TopBar("Mi Progreso") },
    ) { innerPadding -> // Renombrado para claridad, sigue siendo PaddingValues
        Box(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            // ASEGURA QUE EL BOX OCUPE ESPACIO
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "progreso de $userId")

            Button(onClick = {
                navController.navigate("dietForm")
            }) {
                Text(text = "Ir a formulario de dieta (testeo)")
            }
        }
    }
}
