package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import coil.compose.AsyncImage

@Composable
fun HistoryRecipeCard(
    recipe: RecipeHistoryItem,
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
        modifier =
            modifier
                .padding(2.dp)
                .fillMaxWidth()
                .padding(vertical = 5.dp),
        onClick = { onClickAction() },
    ) {
        Row(
            modifier =
                Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier =
                    Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                contentScale = ContentScale.Crop,
                onError = {
                    // Opcional: mostrar un placeholder o icono si la imagen falla
                },
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = recipe.name,
                    style =
                        MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Categoría: ${recipe.category}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Mostrar la fecha de completitud
                Text(
                    text = "Completada: ${recipe.completionDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                // Opcional: Mostrar porciones o tags si hay espacio y es relevante
                // Row {
                //     Text("Porciones: ${recipe.portions}", style = MaterialTheme.typography.bodySmall)
                //     Spacer(modifier = Modifier.width(8.dp))
                //     if (recipe.tags.isNotEmpty()) {
                //         Text("Tags: ${recipe.tags.take(2).joinToString()}", style = MaterialTheme.typography.bodySmall)
                //     }
                // }
            }
            Spacer(modifier = Modifier.width(8.dp)) // Espacio antes del botón de favorito

            IconButton(
                onClick = { onFavoriteClick() },
                modifier =
                    Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically),
            ) {
                Icon(
                    imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                    tint = if (recipe.isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun HistoryRecipeCardPreview() {
    HistoryRecipeCard(
        recipe =
            RecipeHistoryItem(
                id = 1,
                name = "Avena Proteica con Frutas",
                imageUrl = "https://example.com/avena.jpg",
                category = Category.Desayuno,
                portions = 2,
                tags = listOf(Category.Vegano, Category.Proteico, Category.Desayuno),
                isFavorite = true,
                completionDate = "23/05/2024 10:30:15",
            ),
    )
}
