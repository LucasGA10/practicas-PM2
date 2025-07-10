package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.Resource
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.Ingredient
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.UsedIngredient
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipesRepositoryImpl
    @Inject
    constructor(
        private val recipeDao: RecipeDao,
        // Podrías tener un IngredientDao separado si la lógica de ingredientes es muy compleja
    ) : RecipesRepository {
        override fun getRecipeListItems(): Flow<List<RecipeListItem>> {
            return recipeDao.getRecipeListItems().map { listQueryResults ->
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


    override fun getRecipeById(recipeId: Int): Flow<Recipe?> {
        return recipeDao.getRecipeEntityById(recipeId).combine(
            recipeDao.getUsedIngredientDetailsForRecipe(recipeId),
        ) { recipeEntity, usedIngredientDetails ->
            recipeEntity?.let { entity ->
                Recipe(
                    id = entity.id,
                    name = entity.name,
                    imageUrl = entity.imageUrl,
                    category = entity.category,
                    difficulty = entity.difficulty,
                    preparationTime = entity.preparationTime,
                    cookingTime = entity.cookingTime,
                    portions = entity.portions,
                    usedIngredients =
                        usedIngredientDetails.map { detail ->
                            // Mapea UsedIngredientDetail a tu UsedIngredient del dominio
                            UsedIngredient(
                                ingredient =
                                    Ingredient( // Mapea IngredientEntity a tu Ingredient del dominio
                                        id = detail.ingredient.id,
                                        name = detail.ingredient.name,
                                        imageUrl = detail.ingredient.imageUrl,
                                        type = detail.ingredient.type,
                                    ),
                                quantity = detail.quantity,
                            )
                        },
                    instructions = entity.instructions,
                    note = entity.note,
                    tags = entity.tags,
                    isFavorite = entity.isFavorite,
                    rating = entity.rating,
                    nutritionalValue = entity.nutritionalValue,
                )
            }
        }
    }

        override suspend fun updateRecipe(recipe: Recipe) { // Tu modelo de dominio
            // Esta función se vuelve más compleja si necesitas actualizar ingredientes usados.
            // Por ahora, asumamos que solo actualiza campos de RecipeEntity.
            val recipeEntity =
                RecipeEntity(
                    // ... mapea Recipe a RecipeEntity ...
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
                    isFavorite = recipe.isFavorite,
                    rating = recipe.rating,
                    nutritionalValue = recipe.nutritionalValue,
                )
            recipeDao.updateRecipe(recipeEntity)
        }

        // Insertar una receta completa (incluyendo ingredientes y sus relaciones)
        override suspend fun addCompleteRecipe(
            recipe: Recipe,
            ingredients: List<Ingredient>,
        ) {
            // 1. Insertar todos los ingredientes (si no existen)
            val ingredientEntities =
                ingredients.map { // Mapea Ingredient a IngredientEntity
                    IngredientEntity(id = it.id, name = it.name, imageUrl = it.imageUrl, type = it.type)
                }
            recipeDao.insertAllIngredients(ingredientEntities) // Asume que el ID es el mismo o que el conflicto se maneja

            // 2. Insertar la receta
            val recipeEntity =
                RecipeEntity(
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
                    isFavorite = recipe.isFavorite,
                    rating = recipe.rating,
                    nutritionalValue = recipe.nutritionalValue,
                )
            recipeDao.insertRecipe(recipeEntity)

            // 3. Insertar las relaciones en UsedIngredientEntity
            val usedIngredientEntities =
                recipe.usedIngredients.map { usedIng ->
                    UsedIngredientEntity(
                        recipeId = recipe.id,
                        ingredientId = usedIng.ingredient.id,
                        quantity = usedIng.quantity,
                    )
                }
            recipeDao.insertAllUsedIngredients(usedIngredientEntities)
        }

        override suspend fun setRecipeFavoriteStatus(
            recipeId: Int,
            isFavorite: Boolean,
        ) {
            recipeDao.updateFavoriteStatus(recipeId, isFavorite)
        }
    }
