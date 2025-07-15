package ar.edu.unlam.mobile.scaffolding.domain.model.ingredients

data class Ingredient(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val type: IngredientType,
)
