package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipeDetails(recipeId: Int): Flow<Recipe?>

    suspend fun updateRecipe(recipe: Recipe)

    fun getRecipeListItems(): Flow<List<RecipeListItem>>

    suspend fun getRecipeListItemsByIds(ids: List<Int>): List<RecipeHistoryItem>

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
