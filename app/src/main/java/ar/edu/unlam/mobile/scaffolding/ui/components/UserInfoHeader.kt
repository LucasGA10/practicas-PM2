package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.core.copy
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.R
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import ar.edu.unlam.mobile.scaffolding.ui.theme.gold

data class LevelTierInfo(
    val title: String,
    val backgroundColor: Color,
    val textColor: Color,
    val borderColor: Color? = null, // Borde opcional, podría ser el mismo que textColor o diferente
)

fun Color.simpleDarker(factor: Float = 0.8f): Color { // Factor < 1 para oscurecer
    return this.copy(
        red = (this.red * factor).coerceIn(0f, 1f),
        green = (this.green * factor).coerceIn(0f, 1f),
        blue = (this.blue * factor).coerceIn(0f, 1f),
    )
}

@Composable
fun UserInfoHeader(user: User) {
    val textMeasurer = rememberTextMeasurer()

    val levelTier =
        when {
            user.level >= 7 -> LevelTierInfo("Cocinero Maestro", gold, Color.Black) // Texto negro para mejor contraste con dorado
            user.level >= 5 -> LevelTierInfo("Cocinero Experimentado", MaterialTheme.colorScheme.primary, Color.White)
            user.level >= 1 -> LevelTierInfo("Cocinero Iniciado", LightGray, Color.DarkGray) // Texto más oscuro para gris claro
            else -> LevelTierInfo("Novato", Color.Transparent, Color.White) // Caso por defecto
        }

    val borderColor =
        if (levelTier.backgroundColor != Color.Transparent) {
            levelTier.backgroundColor.simpleDarker() // <--- USANDO LA FUNCIÓN PARA OSCURECER
        } else {
            levelTier.textColor // Borde predeterminado si el fondo es transparente
        }

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
                text = "Objetivo: ${user.dietGoal?.displayName} ~ ${user.desiredCalories} cals.",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            )

            Spacer(modifier = Modifier.size(4.dp))

            Box(
                modifier =
                    Modifier
                        .widthIn(min = 180.dp) // Ancho mínimo para el recuadro
                        .background(
                            levelTier.backgroundColor.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp),
                        )
                        .border(
                            width = 2.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val titleBaseStyle =
                        MaterialTheme.typography.titleMedium.copy( // Más grande
                            fontWeight = FontWeight.Bold,
                        )
                    val currentOutlineColor = borderColor // Usamos el borderColor calculado previamente
                    Text(
                        text = levelTier.title,
                        style =
                            titleBaseStyle.copy(
                                color = levelTier.textColor, // Color principal del texto
                                shadow =
                                    Shadow(
                                        color = currentOutlineColor, // Color del "contorno"
                                        offset = Offset(1.0f, 1.0f), // Pequeño desplazamiento
                                        blurRadius = 0.1f, // Blur mínimo para nitidez
                                    ),
                            ),
                        modifier =
                            Modifier
                                .align(Alignment.Start),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween, // <--- CLAVE PARA ESQUINAS OPUESTAS
                    ) {
                        // Nivel (a la izquierda)
                        Text(
                            text = "Nivel: ${user.level}",
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    color = levelTier.textColor,
                                    fontSize = 14.sp, // Un poco más grande si se desea
                                ),
                        )

                        // Puntos (a la derecha)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Puntos",
                                tint = if (levelTier.backgroundColor == gold) Color.White else gold,
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user.points} Puntos",
                                style =
                                    MaterialTheme.typography.bodyMedium.copy(
                                        color = levelTier.textColor,
                                        fontSize = 14.sp, // Un poco más grande si se desea
                                    ),
                            )
                        }
                    }
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
                level = 7,
                points = 2000,
                desiredCalories = 1000.0,
            ),
    )
}
