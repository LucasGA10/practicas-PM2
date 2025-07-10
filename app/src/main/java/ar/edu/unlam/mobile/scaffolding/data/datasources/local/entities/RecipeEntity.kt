package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.NutritionalValue

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val category: Category, // (Desayuno, Almuerzo, Cena, Postre, Snack, etc.)
    val difficulty: Difficulty,
    val preparationTime: Double,
    val cookingTime: Double? = null,
    val portions: Int, // Cantidad de porciones
    val instructions: List<String>, // Instrucciones paso a paso
    val note: String, // Consejos o variantes
    val tags: List<Category>, // Etiquetas (sin gluten, vegana, baja en carbohidratos, etc.)
    val rating: Float = 0f, // Puntaje (opcional)
    val isFavorite: Boolean = false,
    @Embedded
    val nutritionalValue: NutritionalValue, // Valor nutricional (opcional) (calorías, proteínas, grasas, etc.)
)
