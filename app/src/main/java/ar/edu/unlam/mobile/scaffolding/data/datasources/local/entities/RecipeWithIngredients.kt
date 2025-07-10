package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

// Clase de relación para obtener una receta CON sus ingredientes usados
data class RecipeWithIngredients(
    @androidx.room.Embedded val recipe: RecipeEntity,
    @androidx.room.Relation(
        parentColumn = "id", // De RecipeEntity
        entityColumn = "ingredient_id", // De IngredientEntity
        associateBy =
            androidx.room.Junction(
                value = UsedIngredientEntity::class,
                parentColumn = "recipe_id", // De UsedIngredientEntity, enlaza con RecipeEntity.id
                entityColumn = "ingredient_id", // De UsedIngredientEntity, enlaza con IngredientEntity.id
            ),
    )
    val ingredients: List<IngredientEntity>, // Los ingredientes completos
    // Para obtener la cantidad, necesitamos una consulta más compleja o un segundo paso.
    // Room no puede meter directamente la 'quantity' de UsedIngredientEntity en esta relación simple.
    // Veremos cómo manejar esto en el DAO.
)
