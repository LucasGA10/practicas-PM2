package ar.edu.unlam.mobile.scaffolding.domain.usecases

import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import javax.inject.Inject

class ToggleFavoriteRecipeUseCase
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository,
    ) {
        suspend operator fun invoke(
            recipeId: Int,
            isFavorite: Boolean,
        ) {
            // La lógica para obtener la receta actual y actualizarla podría estar aquí
            // o directamente en el repositorio si el repositorio ya maneja esta lógica.
            // Por simplicidad, asumamos que el repositorio tiene un método para esto.
            recipesRepository.setRecipeFavoriteStatus(recipeId, isFavorite)
        }
    }
