package ar.edu.unlam.mobile.scaffolding.domain.usecases

import ar.edu.unlam.mobile.scaffolding.data.repositories.IngredientsRepository
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetIngredientsByIdsUseCase
    @Inject
    constructor(
        private val ingredientsRepository: IngredientsRepository,
    ) {
        operator fun invoke(ids: List<Int>): Flow<Map<Int, Ingredient>> {
            return ingredientsRepository.getIngredientsByIds(ids).map { list ->
                list.associateBy { it.id } // Convierte List<Ingredient> a Map<Int, Ingredient>
            }
        }
    }
