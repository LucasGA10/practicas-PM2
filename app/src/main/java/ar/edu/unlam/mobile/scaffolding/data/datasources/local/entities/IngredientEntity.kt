package ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.IngredientTypeConverter
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.IngredientType

@Entity(tableName = "ingredients")
@TypeConverters(IngredientTypeConverter::class)
data class IngredientEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val type: IngredientType,
)
