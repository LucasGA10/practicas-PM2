package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    ) {
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    // Considera usar colores del tema para consistencia:
                    // color = MaterialTheme.colorScheme.onSurface
                    color = Color.Black,
                    textAlign = TextAlign.Center // El componente ya centra, pero esto asegura el centrado del texto si tiene múltiples líneas
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors( // <<< Usa los colores para CenterAligned
                containerColor = Color.White, // Ej: MaterialTheme.colorScheme.surface
                titleContentColor = Color.Black, // Ej: MaterialTheme.colorScheme.onSurface
                navigationIconContentColor = Color.Black // Ej: MaterialTheme.colorScheme.onSurfaceVariant
            ),
            navigationIcon = {
                if (onNavigateBack != null) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Retroceder",
                        )
                    }
                }
            }
        )
        HorizontalDivider(
            color = Color.LightGray.copy(alpha = 0.5f),
            thickness = 1.dp,
        )
    }
}

@Preview
@Composable
fun TopBarPreview(){
    TopBar("TopBar")
}