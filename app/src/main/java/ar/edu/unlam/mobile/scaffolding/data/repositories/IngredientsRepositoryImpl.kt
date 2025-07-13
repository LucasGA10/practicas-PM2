package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.IngredientDao
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.Ingredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientsRepositoryImpl
    @Inject
    constructor(
        private val ingredientDao: IngredientDao,
    ) : IngredientsRepository {
        override fun getIngredientsByIds(ids: List<Int>): Flow<List<Ingredient>> {
            if (ids.isEmpty()) {
                return flowOf(emptyList()) // Manejar caso de IDs vacíos
            }
            return ingredientDao.getIngredientsByIds(ids).map { entityList ->
                // Mapea List<IngredientEntity> a List<Ingredient> (modelo de dominio)
                entityList.map { entity ->
                    // Mapeo de Entidad a Dominio
                    Ingredient(
                        id = entity.id,
                        name = entity.name,
                        imageUrl = entity.imageUrl,
                        type = entity.type,
                    )
                }
            }
        }
    }
