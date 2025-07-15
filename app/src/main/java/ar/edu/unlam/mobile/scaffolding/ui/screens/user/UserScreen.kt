package ar.edu.unlam.mobile.scaffolding.ui.screens.user

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun UserScreen(
    navController: NavHostController,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (currentUser == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                        // TODO: Navegar a la pantalla de edición de perfil
                        // navController.navigate("edit_profile_route")
                        println("Edit profile clicked for ${user.userName}")
                    },
                )

                // Sección de detalles del usuario
                UserDetailsCard(user = user)

                // Aquí podrías añadir más secciones, como:
                // - Configuración de la cuenta
                // - Botón de cerrar sesión
                // - Enlaces a "Acerca de", "Ayuda", etc.
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Lógica para cerrar sesión */ },
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
}

@Composable
fun UserProfileHeader(
    user: User,
    onEditClick: () -> Unit, // Callback para el botón de editar
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier =
                Modifier
                    .size(136.dp),
        ) {
            // Imagen de Perfil
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
                // Centra la imagen dentro de esta Box interna
                contentScale = ContentScale.Crop,
            )

            // Botón de Editar (Lápiz)
            IconButton(
                onClick = onEditClick,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
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
