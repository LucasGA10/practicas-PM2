package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Entidad para la relación Receta-Ingrediente (Tabla de Unión)
@Entity(
    tableName = "used_ingredients",
    primaryKeys = ["recipe_id", "ingredient_id"], // Clave primaria compuesta
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE, // Si se borra una receta, se borran sus ingredientes usados
        ),
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredient_id"],
            onDelete = ForeignKey.CASCADE, // Si se borra un ingrediente, se borran sus usos (o RESTRICT si prefieres)
        ),
    ],
    indices = [Index(value = ["recipe_id"]), Index(value = ["ingredient_id"])],
)
data class UsedIngredientEntity(
    @ColumnInfo(name = "recipe_id") val recipeId: Int,
    @ColumnInfo(name = "ingredient_id") val ingredientId: Int,
    val quantity: String,
)
