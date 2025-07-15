package ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsedIngredientDao {
    // --- Ingredientes Usados (Relaciones) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsedIngredient(usedIngredient: UsedIngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usedIngredients: List<UsedIngredientEntity>)

    // Obtener los detalles de los ingredientes usados para UNA receta específica (ID + Cantidad)
    data class UsedIngredientDetail(
        @Embedded val ingredient: IngredientEntity,
        @ColumnInfo(name = "quantity") val quantity: String,
    )

    @Transaction
    @Query(
        """
        SELECT i.*, ui.quantity 
        FROM ingredients i 
        INNER JOIN used_ingredients ui ON i.id = ui.ingredientId
        WHERE ui.recipeId = :recipeId
    """,
    )
    fun getUsedIngredientDetails(recipeId: Int): Flow<List<UsedIngredientDetail>>

    // Para insertar una receta con sus ingredientes:
    // Esto requiere múltiples pasos (insertar receta, insertar ingredientes si no existen, insertar relaciones)
    // Se maneja mejor en el Repositorio o un UseCase.
}
