package ar.edu.unlam.mobile.scaffolding.domain.usecases

import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.RecipeListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeListItemsUseCase
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
    ) {
        operator fun invoke(): Flow<List<RecipeListItem>> {
            return recipesRepository.getRecipeListItems()
        }
    }
