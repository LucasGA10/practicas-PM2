package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ar.edu.unlam.mobile.scaffolding.NavDestinations
import ar.edu.unlam.mobile.scaffolding.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navController: NavController,
    // onLoginClicked: (String, String) -> Unit,
    // onNavigateToRegister: () -> Unit
) {
    // Estado para los campos de texto (idealmente manejado por un ViewModel)
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    // Observar currentUser para reaccionar si ya está logueado o después de un login exitoso
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Efecto para manejar el resultado del login (éxito o error)
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()
                Toast.makeText(
                    context,
                    "Login exitoso: ¡Bienvenido ${user?.userName ?: ""}!",
                    Toast.LENGTH_SHORT
                ).show()
                // La navegación se manejará al observar currentUser
            } else {
                val exception = result.exceptionOrNull()
                Toast.makeText(
                    context,
                    "Error de login: ${exception?.message ?: "Error desconocido"}",
                    Toast.LENGTH_LONG
                ).show()
            }
            viewModel.clearLoginResult() // Limpia el resultado para no volver a mostrar el Toast en recomposiciones
        }
    }

    // Efecto para navegar cuando currentUser cambia (después de login exitoso)
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            if (currentUser != null) {
                val destination =
                    if (currentUser!!.dietGoal == null) {
                        NavDestinations.DIET_FORM_ROUTE
                    } else {
                        NavDestinations.HOME_ROUTE // O NavDestinations.USER_PROFILE_ROUTE si es el destino final post-dieta
                    }
                navController.navigate(destination) {
                    popUpTo(NavDestinations.LOGIN_ROUTE) { inclusive = true }
                    launchSingleTop =
                        true // Importante para evitar múltiples instancias del destino
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoverde),
                contentDescription = "Logo de la aplicación",
                modifier =
                    Modifier
                        .width(250.dp)
                        .height(200.dp)
                        .padding(top = 16.dp),
                contentScale = ContentScale.Fit,
            )

            Text(
                text = "Ingresa con tu Email",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            )

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Email") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .padding(top = 12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                isError = loginResult?.isFailure
                    ?: false, // Marcar como error si el último login falló
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("password") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .padding(top = 16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = loginResult?.isFailure ?: false,
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
            } else {
                Button(
                    onClick = {
                        viewModel.attemptLogin(emailInput.trim(), passwordInput)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                    enabled = !isLoading, // Deshabilitar botón mientras carga
                ) {
                    Text("Continuar")
                }
            }

            TextButton(
                onClick = {
                    navController.navigate("SingupScreen") // Reemplaza con tu ruta de registro
                },
                modifier = Modifier.padding(top = 24.dp),
                enabled = !isLoading,
            ) {
                Text("No tenes cuenta? Registrate ahora")
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- Botones de Login Social ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón de Google
                Button(
                    onClick = {
                        Toast.makeText(context, "Login con Google (TODO)", Toast.LENGTH_SHORT)
                            .show()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp) // Altura unificada
                            // .padding(top = 30.dp) // El padding se manejará por el Spacer y el Column
                            .padding(bottom = 8.dp), // Espacio entre Google y Apple
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                    enabled = !isLoading,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.googlelogo),
                        contentDescription = "Logo de Google",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google", fontSize = 16.sp)
                }

                // Botón de Apple
                Button(
                    onClick = {
                        Toast.makeText(context, "Login con Apple (TODO)", Toast.LENGTH_SHORT).show()
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp) // Altura unificada
                            .padding(top = 8.dp), // Espacio entre Google y Apple
                    // .padding(top = 16.dp, bottom = 16.dp), // Ajustado
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black,
                        ),
                    enabled = !isLoading,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.applelogo),
                        contentDescription = "Logo de Apple",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Apple", fontSize = 16.sp)
                }
            }
            // --- Fin Botones de Login Social ---

            // Spacer opcional en la parte inferior para dar un poco de aire
            Spacer(modifier = Modifier.height(24.dp)) // Ajusta esta altura según necesites
        }


    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLoginScreenCompose() {
    // Deberías envolverlo en tu tema de la app si tienes uno
    // DietAppV2Theme {
    LoginScreen(
        hiltViewModel(),
        navController = NavController(LocalContext.current),
    )
    // }
}
