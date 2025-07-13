package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableSection(
    title: String,
    isVisible: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit, // Contenido de la sección como un slot Composable
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick) // Hace que toda la fila del encabezado sea clickeable
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            // Padding para el encabezado
            horizontalArrangement = Arrangement.SpaceBetween, // Separa el título del ícono
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                // modifier = Modifier.weight(1f) // Opcional: si quieres que el texto ocupe más espacio
            )
            Icon(
                imageVector = if (isVisible) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (isVisible) "Colapsar $title" else "Expandir $title",
                tint = MaterialTheme.colorScheme.primary, // Opcional: para darle color al ícono
            )
        }

        // AnimatedVisibility para mostrar/ocultar el contenido con animación
        AnimatedVisibility(
            visible = isVisible,
            // Modifica aquí la duración de las animaciones
            enter =
                fadeIn(animationSpec = tween(durationMillis = 100)) +
                        slideInVertically(
                            animationSpec = tween(durationMillis = 150),
                            initialOffsetY = { fullHeight -> -fullHeight / 4 },
                        ),
            exit =
                fadeOut(animationSpec = tween(durationMillis = 100)) +
                        slideOutVertically(
                            animationSpec = tween(durationMillis = 150),
                            targetOffsetY = { fullHeight -> -fullHeight / 4 },
                        ),
        ) {
            Column {
                content()
            }
        }
    }
}