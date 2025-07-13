package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "used_ingredients",
    primaryKeys = ["recipeId", "ingredientId"], // Clave primaria compuesta
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    // Los índices pueden mejorar el rendimiento de las búsquedas por estas columnas
    indices = [
        Index(value = ["recipeId"]),
        Index(value = ["ingredientId"]),
    ],
)
data class UsedIngredientEntity(
    val recipeId: Int,
    val ingredientId: Int,
    val quantity: String,
)
