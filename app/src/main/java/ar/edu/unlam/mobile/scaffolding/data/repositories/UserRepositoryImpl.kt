package ar.edu.unlam.mobile.scaffolding.data.repositories

import android.util.Log
import ar.edu.unlam.mobile.scaffolding.domain.model.user.CompletedRecipeInfo
import ar.edu.unlam.mobile.scaffolding.domain.model.user.DietGoal
import ar.edu.unlam.mobile.scaffolding.domain.model.user.Gender
import ar.edu.unlam.mobile.scaffolding.domain.model.user.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
    @Inject
    constructor(
        private val ioDispatcher: CoroutineDispatcher, // Es bueno usar un dispatcher para operaciones de IO simuladas
    ) : UserRepository {
        private val users = mutableListOf<User>()
        private val _userFlow =
            MutableStateFlow(
                User(
                    id = 1,
                    userName = "Lucas",
                    email = "myEmail@mail.com",
                    password = "12345",
                    imageUrl = "https://videogamejunk.com/img/catalog/b/balatro/jimbo-plush/75a5ef82ee54340fd237570bbdc386ddb7f85d16.jpeg",
                ),
            )

        init {
            users.add(_userFlow.value)
        }

        override suspend fun newUser(newUser: User) {
            users.add(
                newUser.copy(id = users.size + 1),
            )
        }

        override fun getCurrentUser(): Flow<User?> = _userFlow.asStateFlow()

        override suspend fun getUserById(id: Int): User? {
            return users.find { it.id == id }
        }

        override suspend fun saveUser(user: User) {
            withContext(ioDispatcher) {
                // En una DB real, esto sería una inserción o actualización.
                // Por ahora, si el ID es el mismo que el hardcodeado, lo actualizamos.
                if (_userFlow.value.id == user.id || _userFlow.value == null) {
                    _userFlow.value = user
                } else {
                    // Manejar el caso de intentar guardar un usuario diferente si solo esperas uno.
                    // O expandir para soportar múltiples usuarios si es necesario.
                    println("UserRepositoryImpl: No se puede guardar un usuario diferente en esta implementación simple.")
                }
            }
        }

        override suspend fun updateUserDietProfile(
            userId: Int,
            weightKg: Float,
            heightCm: Float,
            age: Int,
            gender: Gender,
            dietGoal: DietGoal,
            selectedRestrictions: List<String>, // Nuevo parámetro
        ): Result<Unit> {
            return withContext(ioDispatcher) {
                // delay(1000) // Simular latencia

                val currentUser = _userFlow.value
                if (currentUser != null && currentUser.id == userId) {
                    val updatedUser =
                        currentUser.copy(
                            weightKg = weightKg,
                            heightCm = heightCm,
                            age = age,
                            gender = gender,
                            dietGoal = dietGoal,
                            selectedDietaryRestrictions = selectedRestrictions, // Guardar
                        )
                    _userFlow.value = updatedUser
                    Log.d("UserRepositoryImpl", "User profile updated with restrictions: $selectedRestrictions")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Usuario no encontrado para actualizar perfil."))
                }
            }
        }

        override fun updateUser(user: User): String {
            var message = ""
            try {
                if (users.find { it.id == user.id } != null) {
                    users[user.id] = user
                    message = "Usuario actualizado"
                }
            } catch (e: Exception) {
                message = "Error al actualizar el usuario"
            }
            return message
        }

        override suspend fun addRecipeToHistory(
            userId: Int,
            recipeId: Int,
        ): Result<Unit> {
            return withContext(ioDispatcher) {
                val currentUser = _userFlow.value

                if (currentUser.id != userId) {
                    Log.e("UserRepositoryImpl", "Intento de añadir al historial para un ID de usuario incorrecto: $userId")
                    return@withContext Result.failure(Exception("ID de usuario incorrecto."))
                }

                val existingEntryIndex = currentUser.recipeHistory.indexOfFirst { it.recipeId == recipeId }
                val updatedHistory = currentUser.recipeHistory.toMutableList()

                // Formatear la fecha actual como String
                val currentDate = Date() // Obtiene la fecha y hora actual
                val dateFormat =
                    SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()) // Define el formato
                val formattedDateString = dateFormat.format(currentDate) // Formatea a String

                val newEntry = CompletedRecipeInfo(recipeId = recipeId, completionDate = formattedDateString)

                if (existingEntryIndex != -1) {
                    updatedHistory[existingEntryIndex] = newEntry
                } else {
                    updatedHistory.add(newEntry)
                }

                _userFlow.value = currentUser.copy(recipeHistory = updatedHistory)

                Log.d(
                    "UserRepositoryImpl",
                    "Receta $recipeId añadida/actualizada en historial del usuario ${currentUser.id} con fecha '${newEntry.completionDate}'. Nuevo historial: $updatedHistory",
                )
                Result.success(Unit)
            }
        }
    }
