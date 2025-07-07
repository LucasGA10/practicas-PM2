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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.IngredientType
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.UsedIngredient
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import coil.compose.AsyncImage

@Composable
fun RecipeCard(
    recipe: Recipe,
    modifier: Modifier = Modifier,
    onClickAction: () -> Unit = {},
    onFavoriteClick: (Recipe) -> Unit = {}
){

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
                .padding(vertical = 3.dp),
        onClick = { onClickAction() })
    {
        Row(modifier = Modifier.padding(3.dp)) {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.secondary),
            )
            Spacer(modifier = Modifier.size(5.dp))

            Column(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .weight(1f)
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    ){
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).size(26.dp)
                    )

                    IconButton(
                        onClick = { onFavoriteClick(recipe) },
                        modifier = Modifier
                            .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp) // Elimina las restricciones de tamaño mínimo
                            .size(24.dp) // Ajusta el tamaño según necesites
                            .align(Alignment.CenterVertically),
                    ) { // IconButton para mejor accesibilidad y ripple
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (recipe.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                            tint = if (recipe.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp) // Ajusta el tamaño según necesites
                        )
                    }
                }

                Row{
                    Text(
                        text = recipe.category.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                        )
                    Spacer(modifier = Modifier.size(10.dp))
                    RatingBar(
                        rating = recipe.rating,
                        starSize = 16.dp,
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 2.dp)
                            .align(Alignment.CenterVertically),
                    )
                }

                Row {
                    Text(
                        text = "${recipe.difficulty} de hacer",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = "Porciones: ${recipe.portions} ",
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}

@Preview
@Composable
fun RecipeCardPreview(){
    RecipeCard(
        recipe = Recipe(
            id = 1,
            name = "Avena Proteica con Frutas",
            imageUrl = "https://example.com/avena.jpg",
            category = Category.Desayuno,
            difficulty = Difficulty.Fácil,
            preparationTime = 5.0,
            cookingTime = 10.0,
            portions = 2,
            usedIngredients = listOf(
                UsedIngredient(Ingredient("Aceite de oliva", "https://example.com/aceite-oliva.jpg", IngredientType.Aceite), "1 taza"),
                UsedIngredient(Ingredient("Aceite vegetal", "https://example.com/aceite-vegetal.jpg", IngredientType.Aceite), "2 tazas"),
            ),
            instructions = listOf(
                "Calienta la leche vegetal en una olla a fuego medio.",
                "Agrega la avena y cocina por 5-7 minutos, revolviendo constantemente.",
                "Incorpora la proteína en polvo y mezcla bien.",
                "Sirve en un bowl y decora con banana, frutillas y chía.",
                "Añade miel si deseas un toque dulce."
            ),
            note = "Puedes usar otras frutas de temporada. Agrega canela para más sabor sin calorías.",
            tags = listOf(Category.Vegano, Category.Proteico, Category.Desayuno),
            nutritionalValue = 350)
    )
}