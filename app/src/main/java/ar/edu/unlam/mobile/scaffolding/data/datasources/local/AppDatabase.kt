package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.IngredientDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.UsedIngredientDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity

@Database(
    entities = [
        RecipeEntity::class,
        IngredientEntity::class,
        UsedIngredientEntity::class,
    ],
    version = 1, // O la versión que estés usando
    exportSchema = false,
)
@TypeConverters(
    StringListConverter::class,
    NutritionalValueConverter::class,
    IngredientTypeConverter::class,
    CategoryListConverter::class,
    CategoryConverter::class,
    DifficultyConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    abstract fun ingredientDao(): IngredientDao

    abstract fun usedIngredientDao(): UsedIngredientDao

    // El companion object para el singleton manual NO es necesario con Hilt.
}
