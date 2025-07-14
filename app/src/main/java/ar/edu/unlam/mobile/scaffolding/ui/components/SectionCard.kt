package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.animation.core.copy
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
    title: String,
    isContentVisible: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface( // O puedes usar Card para elevación y forma por defecto
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), // Un borde sutil
                    RoundedCornerShape(8.dp), // Bordes redondeados
                ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface, // Color de fondo de la caja
    ) {
        ExpandableSection( // Usamos tu ExpandableSection para el encabezado y la lógica de expansión
            title = title,
            isVisible = isContentVisible,
            onHeaderClick = onHeaderClick,
            content = content, // El contenido se pasa aquí
        )
    }
}
