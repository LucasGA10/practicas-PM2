package ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    // --- Ingredientes --- // ESTAS OPERACIONES DEBERÍAN IR A IngredientDao
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity) // <-- Mover

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientEntity>) // <-- Mover

    @Query("SELECT * FROM ingredients WHERE id = :ingredientId")
    fun getIngredientById(ingredientId: Int): Flow<IngredientEntity?> // <-- Mover

    @Query("SELECT * FROM ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE id IN (:ids)")
    fun getIngredientsByIds(ids: List<Int>): Flow<List<IngredientEntity>>
}
