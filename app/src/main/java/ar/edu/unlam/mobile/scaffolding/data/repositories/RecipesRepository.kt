package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipeDetails(recipeId: Int): Flow<Recipe?>

    suspend fun updateRecipe(recipe: Recipe)

    fun getRecipeListItems(): Flow<List<RecipeListItem>>

    fun getRecipeListItemById(recipeId: Int): Flow<RecipeListItem?>

    suspend fun getRecipeHistoryItemsByIds(ids: List<Int>): List<RecipeHistoryItem>

    suspend fun getNutritionalValueByRecipeId(recipeId: Int): NutritionalValue

    // suspend fun addRecipe(recipe: Recipe, ingredients: List<Ingredient>, )

    suspend fun setRecipeFavoriteStatus(
        recipeId: Int,
        isFavorite: Boolean,
    )

    suspend fun updateRecipeRating(
        recipeId: Int,
        newRating: Float,
    )
}
