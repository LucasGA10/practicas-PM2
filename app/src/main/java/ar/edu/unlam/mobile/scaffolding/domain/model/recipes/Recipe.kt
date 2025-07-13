package ar.edu.unlam.mobile.scaffolding.domain.model.recipes

import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.UsedIngredient

data class Recipe(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val category: Category, // (Desayuno, Almuerzo, Cena, Postre, Snack, etc.)
    val difficulty: Difficulty,
    val preparationTime: Double,
    val cookingTime: Double? = null,
    val portions: Int,
    val usedIngredients: List<UsedIngredient>, // Lista de los Ingredientes usados mas sus cantidades
    val instructions: List<String>,
    val note: String,
    val tags: List<Category>,
    val rating: Float = 0f,
    val isFavorite: Boolean = false,
    val nutritionalValue: NutritionalValue,
)
