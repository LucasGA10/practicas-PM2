package ar.edu.unlam.mobile.scaffolding.data.repositories

import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun newUser(newUser: User)

    fun getCurrentUser(): Flow<User?>

    fun updateUser(user: User): String

    suspend fun getUserById(id: Int): User?

    suspend fun saveUser(user: User)

    suspend fun updateUserDietProfile(
        userId: Int,
        weightKg: Float,
        heightCm: Float,
        age: Int,
        gender: Gender,
        dietGoal: DietGoal,
        selectedRestrictions: List<String>,
    ): Result<Unit>

    suspend fun addRecipeToHistory(
        userId: Int,
        recipeId: Int,
    ): Result<Unit>
}
