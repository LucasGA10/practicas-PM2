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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import coil.compose.AsyncImage
import com.ar.unlam.ddi.ui.theme.PrimaryGreen
import com.ar.unlam.ddi.ui.theme.PrimaryGreenDark

@Composable
fun HistoryRecipeCard(
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

                    IconButton(
                        onClick = { onFavoriteClick() },
                        modifier =
                            Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp)
                                .size(24.dp)
                                .align(Alignment.CenterVertically),
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (recipe.isFavorite) PrimaryGreenDark else PrimaryGreen,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                /*Column(modifier = Modifier.padding(top = 5.dp)) {
                    Text(
                        text = "Kcal: ${recipe.nutritionalValue} kcal",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Proteína: ${recipe.protein} g",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Carbohidratos: ${recipe.carbs} g",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "Grasas: ${recipe.fats} g",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    Text(
                        text = "${recipe.date}",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                }*/
            }
        }
    }
}

@Preview
@Composable
fun HistoryRecipeCardPreview() {
    HistoryRecipeCard(
        recipe =
            RecipeListItem(
                id = 1,
                name = "Avena Proteica con Frutas",
                imageUrl = "https://example.com/avena.jpg",
                category = Category.Desayuno,
                difficulty = Difficulty.Fácil,
                portions = 2,
                tags = listOf(Category.Vegano, Category.Proteico, Category.Desayuno),
                isFavorite = true,
            ),
    )
}
