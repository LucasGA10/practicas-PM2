package ar.edu.unlam.mobile.scaffolding.domain.model.recipes

data class NutritionalValue(
    val generalValue: Float = 0.0f,
    val calories: Float = 0.0f,
    val protein: Float = 0.0f,
    val carbs: Float = 0.0f,
    val fats: Float = 0.0f,
) {
    // Función para sumar dos objetos NutritionalValue
    operator fun plus(other: NutritionalValue?): NutritionalValue {
        if (other == null) return this
        return NutritionalValue(
            generalValue = this.generalValue + other.generalValue,
            calories = this.calories + other.calories,
            protein = this.protein + other.protein,
            carbs = this.carbs + other.carbs,
            fats = this.fats + other.fats,
        )
    }
}
