package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.IngredientType

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Autogenerado o puedes definirlo tú
    val name: String,
    val imageUrl: String,
    val type: IngredientType, // Necesitará un TypeConverter si es enum/clase
)
