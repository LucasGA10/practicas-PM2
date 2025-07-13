package ar.edu.unlam.mobile.scaffolding.domain.usecases

import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeByIdUseCase
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
    ) {
        operator fun invoke(recipeId: Int): Flow<Recipe?> {
            return recipesRepository.getRecipeDetails(recipeId)
        }
    }
