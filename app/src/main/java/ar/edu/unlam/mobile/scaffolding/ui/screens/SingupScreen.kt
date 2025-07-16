package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.ui.components.TopBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingupScreen(
    viewModel: SingupViewModel = hiltViewModel(),
    navController: NavController,
) {
    val back: (() -> Unit)? = { navController.popBackStack() }

    Scaffold(
        topBar = {
            TopBar(
                "Crear cuenta",
                back,
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            Text("SingupScreen")
        }
    }
}
