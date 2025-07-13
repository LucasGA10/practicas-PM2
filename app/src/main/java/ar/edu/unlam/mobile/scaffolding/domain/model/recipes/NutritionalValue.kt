package ar.edu.unlam.mobile.scaffolding.domain.model.recipes

data class NutritionalValue(
    val generalValue: Float,
    val calories: Float = 20f,
    val protein: Float = 20f,
    val carbs: Float = 20f,
    val fats: Float = 20f,
)
