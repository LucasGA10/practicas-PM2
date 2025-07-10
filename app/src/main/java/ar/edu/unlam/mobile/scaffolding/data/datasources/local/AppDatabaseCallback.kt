package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

// El Callback ahora toma las dependencias en su constructor
class AppDatabaseCallback(
    private val recipeDaoProvider: Provider<RecipeDao>,
    private val applicationScope: CoroutineScope,
    // Recibe los datos
    private val initialDataSource: InitialDataSource,
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch(Dispatchers.IO) {
            populateDatabase(recipeDaoProvider.get())
        }
    }

    private suspend fun populateDatabase(recipeDao: RecipeDao) {
        // Mapear ingredientes del InitialDataSource a IngredientEntity e insertar
        val ingredientEntities =
            initialDataSource.ingredients.map { domainIngredient ->
                IngredientEntity(
                    // Asegúrate que el ID en IngredientEntity no sea autogenerado
                    id = domainIngredient.id,
                    // si estás asignando IDs aquí. O ajusta según tu diseño de ID.
                    name = domainIngredient.name,
                    imageUrl = domainIngredient.imageUrl,
                    type = domainIngredient.type,
                )
            }
        recipeDao.insertAllIngredients(ingredientEntities)

        // Mapear recetas del InitialDataSource y sus UsedIngredients
        initialDataSource.recipes.forEach { domainRecipe ->
            val recipeEntity =
                RecipeEntity(
                    id = domainRecipe.id,
                    name = domainRecipe.name,
                    imageUrl = domainRecipe.imageUrl,
                    category = domainRecipe.category,
                    difficulty = domainRecipe.difficulty,
                    preparationTime = domainRecipe.preparationTime,
                    cookingTime = domainRecipe.cookingTime,
                    portions = domainRecipe.portions,
                    instructions = domainRecipe.instructions,
                    note = domainRecipe.note,
                    tags = domainRecipe.tags,
                    nutritionalValue = domainRecipe.nutritionalValue,
                    isFavorite = domainRecipe.isFavorite,
                    rating = domainRecipe.rating,
                )
            recipeDao.insertRecipe(recipeEntity)

            val usedIngredientEntities =
                domainRecipe.usedIngredients.map { domainUsedIngredient ->
                    UsedIngredientEntity(
                        recipeId = domainRecipe.id,
                        ingredientId = domainUsedIngredient.ingredient.id, // ID del ingrediente de tu modelo
                        quantity = domainUsedIngredient.quantity,
                    )
                }
            recipeDao.insertAllUsedIngredients(usedIngredientEntities)
        }
        // Log o confirmación (opcional)
        // Log.d("DatabaseInit", "Database populated with initial data.")
    }
}
