package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.Embedded
import androidx.room.Relation

// Clase de relación para obtener una receta CON sus ingredientes usados
data class RecipeWithItsUsedIngredients(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id", // PK de RecipeEntity
        entityColumn = "recipeId", // FK en UsedIngredientEntity
        entity = UsedIngredientEntity::class,
    )
    val usedIngredientEntities: List<UsedIngredientEntity>,
)
