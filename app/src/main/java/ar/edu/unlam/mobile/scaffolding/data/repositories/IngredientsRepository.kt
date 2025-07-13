package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientsRepository {
    fun getIngredientsByIds(ids: List<Int>): Flow<List<Ingredient>>
}
