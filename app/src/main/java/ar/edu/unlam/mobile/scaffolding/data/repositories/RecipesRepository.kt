package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.Resource
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getRecipeById(recipeId: Int): Flow<Recipe?>

    suspend fun updateRecipe(recipe: Recipe)

    fun getRecipeListItems(): Flow<List<RecipeListItem>>

    suspend fun addCompleteRecipe(
        recipe: Recipe,
        ingredients: List<Ingredient>,
    )

    suspend fun setRecipeFavoriteStatus(
        recipeId: Int,
        isFavorite: Boolean,
    )
}
