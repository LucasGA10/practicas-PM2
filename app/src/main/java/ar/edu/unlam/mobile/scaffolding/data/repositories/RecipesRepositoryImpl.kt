package ar.edu.unlam.mobile.scaffolding.data.repositories

import android.util.Log
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.UsedIngredient
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeHistoryItem
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipesRepositoryImpl
    @Inject
    constructor(
        private val recipeDao: RecipeDao,
    ) : RecipesRepository {
        override fun getRecipeListItems(): Flow<List<RecipeListItem>> {
            Log.d("RecipesRepo", "getRecipeListItems llamado")
            return recipeDao.getRecipeListItems().map { listQueryResults ->
                Log.d("RecipesRepo", "Entities desde DAO: ${listQueryResults.size}")
                listQueryResults.map { dbItem ->
                    // Mapea RecipeListItemQueryResult a tu RecipeListItem del dominio
                    RecipeListItem(
                        id = dbItem.id,
                        name = dbItem.name,
                        imageUrl = dbItem.imageUrl,
                        category = dbItem.category,
                        difficulty = dbItem.difficulty,
                        portions = dbItem.portions,
                        tags = dbItem.tags,
                        rating = dbItem.rating,
                        isFavorite = dbItem.isFavorite,
                    )
                }
            }
        }

        override suspend fun getRecipeListItemsByIds(ids: List<Int>): List<RecipeHistoryItem> {
            if (ids.isEmpty()) {
                return emptyList()
            }
            // Asume que tu DAO tiene una función para obtener entidades por una lista de IDs
            val recipeEntities = recipeDao.getRecipeHistoryItemsByIds(ids)
            return recipeEntities.map { result ->
                RecipeHistoryItem(
                    id = result.id,
                    name = result.name,
                    imageUrl = result.imageUrl,
                    category = result.category,
                    portions = result.portions,
                    tags = result.tags,
                    isFavorite = result.isFavorite,
                )
            }
        }

        override fun getRecipeDetails(recipeId: Int): Flow<Recipe?> {
            Log.d("RecipesRepo", "getRecipeDetails llamado para recipeId: $recipeId")
            return recipeDao.getRecipeWithItsUsedIngredientsById(recipeId)
                .map { recipeWithEntities -> // recipeWithEntities es de tipo RecipeWithItsUsedIngredients?
                    Log.d(
                        "RecipesRepo",
                        "RecipeWithItsUsedIngredients obtenido para recipeId: $recipeId. " +
                            "Nombre: ${recipeWithEntities.recipe.name}, " +
                            "UsedIngredientEntities: ${recipeWithEntities.usedIngredientEntities.size}",
                    )

                    recipeWithEntities.let {
                        // it.recipe es RecipeEntity
                        // it.usedIngredientEntities es List<UsedIngredientEntity>

                        val domainUsedIngredients =
                            it.usedIngredientEntities.map { usedIngredientEntity ->
                                // Mapea de la entidad de BD (UsedIngredientEntity)
                                // al modelo de dominio (UsedIngredient)
                                UsedIngredient(
                                    id = usedIngredientEntity.ingredientId,
                                    quantity = usedIngredientEntity.quantity,
                                )
                            }

                        // Mapea RecipeEntity (it.recipe) al modelo de dominio Recipe
                        Recipe(
                            id = it.recipe.id,
                            name = it.recipe.name,
                            imageUrl = it.recipe.imageUrl,
                            category = it.recipe.category,
                            difficulty = it.recipe.difficulty,
                            preparationTime = it.recipe.preparationTime,
                            cookingTime = it.recipe.cookingTime,
                            portions = it.recipe.portions,
                            instructions = it.recipe.instructions,
                            note = it.recipe.note,
                            tags = it.recipe.tags,
                            nutritionalValue = it.recipe.nutritionalValue,
                            rating = it.recipe.rating,
                            isFavorite = it.recipe.isFavorite,
                            // Asigna la lista de ingredientes de dominio mapeados
                            usedIngredients = domainUsedIngredients,
                        )
                    }
                }
        }

        override suspend fun updateRecipe(recipe: Recipe) { // Tu modelo de dominio
            // Esta función se vuelve más compleja si necesitas actualizar ingredientes usados.
            // Por ahora, asumamos que solo actualiza campos de RecipeEntity.
            val recipeEntity =
                RecipeEntity( // ... mapea Recipe a RecipeEntity ...
                    id = recipe.id,
                    name = recipe.name,
                    imageUrl = recipe.imageUrl,
                    category = recipe.category,
                    difficulty = recipe.difficulty,
                    preparationTime = recipe.preparationTime,
                    cookingTime = recipe.cookingTime,
                    portions = recipe.portions,
                    instructions = recipe.instructions,
                    note = recipe.note,
                    tags = recipe.tags,
                    nutritionalValue = recipe.nutritionalValue,
                    rating = recipe.rating,
                    isFavorite = recipe.isFavorite,
                )
            recipeDao.updateRecipe(recipeEntity)
        }

        override suspend fun setRecipeFavoriteStatus(
            recipeId: Int,
            isFavorite: Boolean,
        ) {
            recipeDao.updateFavoriteStatus(recipeId, isFavorite)
        }

        override suspend fun updateRecipeRating(
            recipeId: Int,
            newRating: Float,
        ) {
            recipeDao.updateRating(recipeId, newRating)
        }
    }
