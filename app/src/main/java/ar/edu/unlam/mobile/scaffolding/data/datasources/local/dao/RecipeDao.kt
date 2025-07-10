package ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.RecipeListItem
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

    // --- Ingredientes ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIngredients(ingredients: List<IngredientEntity>)

    @Query("SELECT * FROM ingredients WHERE id = :ingredientId")
    fun getIngredientById(ingredientId: Int): Flow<IngredientEntity?>

    @Query("SELECT * FROM ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    // --- Ingredientes Usados (Relaciones) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsedIngredient(usedIngredient: UsedIngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsedIngredients(usedIngredients: List<UsedIngredientEntity>)

    // Obtener los detalles de los ingredientes usados para UNA receta específica (ID + Cantidad)
    data class UsedIngredientDetail(
        @Embedded val ingredient: IngredientEntity, // El ingrediente completo
        @ColumnInfo(name = "quantity") val quantity: String, // La cantidad de la tabla de unión
    )

    @Transaction // Asegura que la operación sea atómica
    @Query(
        """
        SELECT i.*, ui.quantity 
        FROM ingredients i 
        INNER JOIN used_ingredients ui ON i.id = ui.ingredient_id 
        WHERE ui.recipe_id = :recipeId
    """,
    )
    fun getUsedIngredientDetailsForRecipe(recipeId: Int): Flow<List<UsedIngredientDetail>>

    // Para insertar una receta con sus ingredientes:
    // Esto requiere múltiples pasos (insertar receta, insertar ingredientes si no existen, insertar relaciones)
    // Se maneja mejor en el Repositorio o un UseCase.

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(
        recipeId: Int,
        isFavorite: Boolean,
    )
}
