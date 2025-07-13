package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    // Nuevos parámetros para personalizar colores
    // Por defecto, usa los colores primarios del tema (tu verde)
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary, // Verde por defecto
        titleContentColor = MaterialTheme.colorScheme.onPrimary, // Blanco por defecto (sobre verde)
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary, // Blanco por defecto (sobre verde)
    ),
    modifier: Modifier = Modifier // Añadir modifier para flexibilidad
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                // El color del título ahora se toma de 'colors.titleContentColor'
                // No es necesario asignarlo aquí explícitamente si 'colors' está bien configurado.
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Usar filled para mejor visibilidad
                        contentDescription = "Retroceder",
                        // El color del ícono se toma de 'colors.navigationIconContentColor'
                    )
                }
            }
        },
        colors = colors, // Usa los colores pasados o los por defecto
        modifier = modifier // Pasa el modifier aquí
            .shadow(elevation = 0.dp) // Quita la sombra explícitamente (opcional si lo de abajo funciona)
            .background(Color.White), // Asegura el fondo si el tinte es un problema
    )
}

// Preview con colores por defecto (verde)
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopBarPreviewDefault() {
    MaterialTheme { // Asegúrate de que el Preview use un tema
        TopBar(title = "TopBar", onNavigateBack = {})
    }
}
