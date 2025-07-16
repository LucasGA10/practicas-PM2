package ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeWithItsUsedIngredients
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // --- Recetas ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRecipes(recipes: List<RecipeEntity>)

    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeEntityById(recipeId: Int): Flow<RecipeEntity?> // Solo la entidad receta

    @Query("SELECT id, name, imageUrl, category, difficulty, portions, tags, rating, isFavorite FROM recipes")
    fun getRecipeListItems(): Flow<List<RecipeListItem>>

    @Query("SELECT id, name, imageUrl as imageUrl, category, portions, tags, isFavorite as isFavorite FROM recipes WHERE id IN (:ids)")
    suspend fun getRecipeHistoryItemsByIds(ids: List<Int>): List<RecipeHistoryItem>

    @Query("SELECT * FROM used_ingredients WHERE recipeId = :recipeId")
    fun getUsedIngredientsForRecipe(recipeId: Int): Flow<List<UsedIngredientEntity>>
    // Para insertar una receta con sus ingredientes:
    // Esto requiere múltiples pasos (insertar receta, insertar ingredientes si no existen, insertar relaciones)
    // Se maneja mejor en el Repositorio o un UseCase.

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(
        recipeId: Int,
        isFavorite: Boolean,
    )

    @Query("UPDATE recipes SET rating = :newRating WHERE id = :recipeId")
    suspend fun updateRating(
        recipeId: Int,
        newRating: Float,
    )

    @Query("SELECT id, name, imageUrl, category, difficulty, portions, tags, rating, isFavorite FROM recipes WHERE id = :recipeId")
    fun getRecipeListItemById(recipeId: Int): Flow<RecipeListItem?>

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithItsUsedIngredientsById(recipeId: Int): Flow<RecipeWithItsUsedIngredients>

    @Query("SELECT nutritionalValue FROM recipes WHERE id = :recipeId")
    suspend fun getNutritionalValueForRecipe(recipeId: Int): NutritionalValue
}
