package ar.edu.unlam.mobile.scaffolding.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import coil.compose.AsyncImage

@Composable
fun RecipeCard(
    recipe: RecipeListItem,
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
        Row(modifier = Modifier.padding(3.dp)) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.secondary),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.size(5.dp))

            Column(
                modifier =
                    Modifier
                        .padding(start = 5.dp)
                        .weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        modifier =
                            Modifier
                                .weight(1f)
                                .size(24.dp),
                    )
                    // IconButton para mejor accesibilidad y ripple
                    IconButton(
                        onClick = { onFavoriteClick() },
                        modifier =
                            Modifier
                                // Elimina las restricciones de tamaño mínimo
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
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

                Row {
                    Text(
                        text = recipe.category.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    RatingBar(
                        rating = recipe.rating,
                        starSize = 18.dp,
                        modifier =
                            Modifier
                                .padding(top = 2.dp, bottom = 2.dp)
                                .align(Alignment.CenterVertically),
                    )
                }

                Row {
                    Text(
                        text = "Dificultad: ${recipe.difficulty}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = "Porciones: ${recipe.portions} ",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RecipeCardPreview() {
    RecipeCard(
        recipe =
            RecipeListItem(
                id = 1,
                name = "Avena Proteica con Frutas",
                imageUrl = "https://example.com/avena.jpg",
                category = Category.Desayuno,
                difficulty = Difficulty.Fácil,
                portions = 2,
                tags = listOf(Category.Vegano, Category.Proteico, Category.Desayuno),
            ),
    )
}
