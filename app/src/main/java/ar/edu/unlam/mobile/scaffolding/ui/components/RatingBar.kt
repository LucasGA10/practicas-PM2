package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.ui.theme.gold

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Float,
    maxRating: Int = 5,
    starsColor: Color = gold,
    emptyStarColor: Color = Color.Gray,
    starSize: Dp = 20.dp, // Añadido para consistencia
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            val icon =
                when {
                    rating >= i -> Icons.Filled.Star
                    rating >= i - 0.5f -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (icon == Icons.Filled.StarBorder) emptyStarColor else starsColor,
                modifier = Modifier.size(starSize),
            )
        }
    }
}

@Composable
fun ClickableRatingBar(
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    currentRating: Float,
    onRatingChanged: (Float) -> Unit,
    starSize: Dp = 24.dp, // Tamaño de las estrellas
    starColor: Color = gold,
    emptyStarColor: Color = MaterialTheme.colorScheme.outline,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 1..maxRating) {
            val isSelected = currentRating >= i
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Calificar $i de $maxRating estrellas",
                tint = if (isSelected) starColor else emptyStarColor,
                modifier =
                    Modifier
                        .size(starSize)
                        .clickable { onRatingChanged(i.toFloat()) }
                        .padding(horizontal = 2.dp),
            )
        }
    }
}
