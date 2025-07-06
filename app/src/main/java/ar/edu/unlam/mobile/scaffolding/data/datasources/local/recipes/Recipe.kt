package ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.ingredients.UsedIngredient

data class Recipe(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val category: Category, //(Desayuno, Almuerzo, Cena, Postre, Snack, etc.)
    val difficulty: Difficulty,
    val preparationTime: Double,
    val cookingTime: Double? = null,
    val portions: Int, //Cantidad de porciones
    val usedIngredients: List<UsedIngredient>, //Lista de Ingredientes (con cantidades)
    val instructions: List<String>, //Instrucciones paso a paso
    val note: String, //Consejos o variantes
    val tags: List<Category>, //Etiquetas (sin gluten, vegana, baja en carbohidratos, etc.)
    val nutritionalValue: Int, //Valor nutricional (opcional) (calorías, proteínas, grasas, etc.)
    val rating: Float = 0f, //Puntaje (opcional)
    val isFavorite: Boolean = false
)