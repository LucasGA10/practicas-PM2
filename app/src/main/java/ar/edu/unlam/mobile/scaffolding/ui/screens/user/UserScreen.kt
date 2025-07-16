package ar.edu.unlam.mobile.scaffolding.ui.screens.user

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Boy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Girl
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.domain.model.user.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navController: NavHostController,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val logoutEvent by viewModel.logoutState.collectAsStateWithLifecycle() // Observa el estado de logout
    val scrollState = rememberScrollState()

    LaunchedEffect(logoutEvent) {
        if (logoutEvent == UserViewModel.LogoutEvent.NavigateToLogin) {
            Log.d("UserScreen", "Recibido LogoutEvent.NavigateToLogin, navegando...")
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true // También elimina la pantalla de inicio (homeScreenRoute) de la pila
                }
                // Asegúrate de que la pantalla de login sea la única instancia en la pila
                launchSingleTop = true
            }
            viewModel.onLogoutEventConsumed() // Resetea el evento para evitar re-navegaciones
        }
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { // Mantén un Box para centrar el indicador de carga
            CircularProgressIndicator()
        }
    } else {
        val user = currentUser!!
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 70.dp), // Espacio para el FAB si se superpone con contenido al final
        ) {
            UserProfileHeader(
                user = user,
                onEditClick = {
                    println("Edit profile clicked for ${user.userName}")
                },
            )

            // Sección de detalles del usuario
            UserDetailsCard(user = user)

            Spacer(modifier = Modifier.height(24.dp)) // Un poco más de espacio antes de los botones de acción

            // Botón para ir a DietFormScreen
            Button(
                onClick = { navController.navigate("dietForm") },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                Text("Actualizar Preferencias de Dieta") // O el texto que prefieras
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    Log.d("UserScreen", "Botón Cerrar Sesión clickeado, llamando a viewModel.logoutUser()")
                    viewModel.logoutUser()
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                // colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun UserProfileHeader(
    user: User,
    onEditClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        // .height(200.dp) // Define una altura para el header o usa aspectRatio para el fondo
        // El padding superior se puede quitar si el fondo verde ya crea el espacio deseado
        // .padding(top = 32.dp, bottom = 16.dp),
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.fondoverde),
            contentDescription = "Fondo de perfil",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(170.dp),
            contentScale = ContentScale.Crop,
        )

        // Contenido superpuesto (imagen de perfil y botón de editar)
        Column( // Usamos una Columna para centrar el contenido verticalmente si es necesario, o para apilar más cosas
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(top = 32.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box( // Contenedor para la imagen de perfil y el botón de editar
                modifier = Modifier.size(136.dp), // Tamaño del contenedor de la imagen de perfil y el botón
            ) {
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(user.imageUrl ?: R.drawable.ic_no_profile_image_round)
                            .crossfade(true)
                            .error(R.drawable.ic_no_profile_image_round)
                            .placeholder(R.drawable.ic_no_profile_image_round)
                            .build(),
                    contentDescription = "Foto de perfil de ${user.userName}",
                    modifier =
                        Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.Center),
                    contentScale = ContentScale.Crop,
                )
                IconButton(
                    onClick = onEditClick,
                    modifier =
                        Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f), CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar perfil",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
fun UserDetailsCard(user: User) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Mi Información",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            UserInfoRow(icon = Icons.Filled.Person, label = "Nombre", value = user.userName)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            UserInfoRow(icon = Icons.Filled.Email, label = "Email", value = user.email)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            UserInfoRow(
                icon =
                    when (user.gender) {
                        Gender.MALE -> Icons.Filled.Boy
                        Gender.FEMALE -> Icons.Filled.Girl
                        Gender.OTHER -> Icons.Filled.People
                        null -> Icons.Filled.People
                    },
                label = "Genero",
                value = "${user.gender?.displayName}",
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            UserInfoRow(icon = Icons.AutoMirrored.Filled.TrendingUp, label = "Nivel", value = user.level.toString())
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            user.dietGoal?.let { goal ->
                UserInfoRow(icon = Icons.Filled.Star, label = "Objetivo Actual", value = goal.displayName) // Usa un ícono apropiado
            }
        }
    }
}

@Composable
fun UserInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}
