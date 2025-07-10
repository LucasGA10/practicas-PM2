package ar.edu.unlam.mobile.scaffolding.data.model.ingredients

data class Ingredient(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val type: IngredientType,
)
