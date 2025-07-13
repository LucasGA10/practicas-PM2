package ar.edu.unlam.mobile.scaffolding.domain.usecases

import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import javax.inject.Inject

class UpdateRecipeRatingUseCase
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
    ) {
        suspend operator fun invoke(
            recipeId: Int,
            newRating: Float,
        ) {
            // La validación del rating (ej. entre 0 y 5) puede estar aquí o en el ViewModel
            // antes de llamar al caso de uso, o incluso en el repositorio.
            recipesRepository.updateRecipeRating(recipeId, newRating)
        }
    }
