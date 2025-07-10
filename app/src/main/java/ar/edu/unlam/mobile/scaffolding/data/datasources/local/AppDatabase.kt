package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity

@Database(
    entities = [RecipeEntity::class, IngredientEntity::class, UsedIngredientEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(
    StringListConverter::class,
    UsedIngredientListConverter::class,
    CategoryConverter::class,
    DifficultyConverter::class,
    IngredientTypeConverter::class,
    RecipeTypeConverters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "recipe_database",
                    )
                        // .addCallback(AppDatabaseCallback(context)) // Para pre-poblar si es necesario
                        .fallbackToDestructiveMigration(true)
                        .build()
                Companion.instance = instance
                instance
            }
        }
    }
}
