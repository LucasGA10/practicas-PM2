package ar.edu.unlam.mobile.scaffolding.domain.model.user

data class User(
    // Datos del usuario
    val id: Int,
    val userName: String,
    val email: String,
    val password: String,
    val imageUrl: String?,
    // Datos para la dieta
    val age: Int? = null,
    val weightKg: Float? = null,
    val heightCm: Float? = null,
    val gender: Gender? = null,
    val dietGoal: DietGoal? = null,
    val selectedDietaryRestrictions: List<String>? = null,
    val recipeHistory: List<CompletedRecipeInfo> = emptyList(),
    val points: Int = 0,
    val level: Int = 1,
)
