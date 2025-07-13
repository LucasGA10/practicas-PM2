package ar.edu.unlam.mobile.scaffolding.domain.model.user

data class DietaryRestriction(
    val id: String,
    val displayName: String, // ej: "Vegetariano", "Vegano"
    val isSelected: Boolean = false
)

val allDietaryRestrictions = listOf(
    DietaryRestriction(id = "vegetarian", displayName = "Vegetariano/a"),
    DietaryRestriction(id = "vegan", displayName = "Vegano/a"),
    DietaryRestriction(id = "lactose_intolerant", displayName = "Intolerante a la lactosa"),
    DietaryRestriction(id = "gluten_free", displayName = "Sin gluten (celíaco/a o sensible)"),
    DietaryRestriction(id = "nut_allergy", displayName = "Alergia a los frutos secos"),
    DietaryRestriction(id = "fish_allergy", displayName = "Alergia al pescado"),
    DietaryRestriction(id = "shellfish_allergy", displayName = "Alergia a los mariscos"),
)