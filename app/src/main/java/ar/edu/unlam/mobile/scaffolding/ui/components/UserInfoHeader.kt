package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import ar.edu.unlam.mobile.scaffolding.ui.theme.gold

@Composable
fun UserInfoHeader(user: User) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp),
    ) {
        // Imagen de Fondo
        Image(
            painter = painterResource(R.drawable.alimentos_verdes),
            contentDescription = "Encabezado de perfil de usuario",
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .blur(radius = 20.dp),
            contentScale = ContentScale.Crop,
        )
        // Color negro con 35% de opacidad
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
        )
        // Contenido sobre la imagen
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            // Padding para el contenido dentro de la Box
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = user.userName,
                style =
                    MaterialTheme.typography.headlineSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    ),
            )

            Text(
                text = "Objetivo: ${user.dietGoal?.displayName}",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            )
            Spacer(modifier = Modifier.size(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Nivel: ${user.level}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    modifier = Modifier.weight(1f),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Puntos",
                        tint = gold,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = "${user.points} Puntos",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun UserInfoHeaderPreview() {
    UserInfoHeader(
        user =
            User(
                id = 1,
                userName = "Lucas",
                password = "1234",
                email = "",
                imageUrl = "",
                dietGoal = DietGoal.GAIN_WEIGHT,
                level = 1,
                points = 100,
            ),
    )
}
