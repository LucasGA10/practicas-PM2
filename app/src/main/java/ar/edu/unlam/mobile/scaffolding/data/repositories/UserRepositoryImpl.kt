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
        private val ioDispatcher: CoroutineDispatcher,
    ) : UserRepository {
        private val users = mutableListOf<User>()
        private val _currentUserFlow = MutableStateFlow<User?>(null)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val now = System.currentTimeMillis()
        val dayInMillis = 86400000L // Milisegundos en un día
        val hourInMillis = 3600000L // Milisegundos en una hora

        init {
            users.add(
                User(
                    id = 1,
                    userName = "Lucas",
                    email = "miMail@outlook.com",
                    password = "12345",
                    imageUrl = "https://img.freepik.com/premium-vector/funny-mango-character_844724-2012.jpg",
                    age = 23,
                    weightKg = 70f,
                    heightCm = 170f,
                    gender = Gender.MALE,
                    dietGoal = DietGoal.LOSE_WEIGHT,
                    desiredCalories = 950.0,
                    recipeHistory =
                        listOf(
                            CompletedRecipeInfo(
                                recipeId = 1,
                                completionDate = dateFormat.format(Date(now - hourInMillis * 2)),
                            ), // Hace 2 horas
                            CompletedRecipeInfo(
                                recipeId = 3,
                                completionDate = dateFormat.format(Date(now - hourInMillis * 5)),
                            ), // Hace 5 horas
                            CompletedRecipeInfo(
                                recipeId = 1,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 1)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 4,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 1 - hourInMillis * 3)),
                            ), // Ayer, hace unas horas más
                            CompletedRecipeInfo(
                                recipeId = 2,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 2)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 5,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 2 - hourInMillis * 6)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 10,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 3)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 6,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 4)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 2,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 4 - hourInMillis * 4)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 13,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 5)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 7,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 6)),
                            ),
                            CompletedRecipeInfo(
                                recipeId = 1,
                                completionDate = dateFormat.format(Date(now - dayInMillis * 6 - hourInMillis * 5)),
                            ),
                        ),
                    points = 2900,
                    level = 6,
                ),
            )
        }

        override suspend fun loginUser(
            email: String,
            password: String,
        ): Result<User> {
            return withContext(ioDispatcher) {
                Log.d("UserRepositoryImpl", "Intentando login para email: $email")
                val foundUser = users.find { it.email.equals(email, ignoreCase = true) }

                if (foundUser != null) {
                    if (foundUser.password == password) {
                        _currentUserFlow.value = foundUser
                        Log.d("UserRepositoryImpl", "Login exitoso para: ${foundUser.userName}")
                        Result.success(foundUser)
                    } else {
                        Log.w("UserRepositoryImpl", "Contraseña incorrecta para email: $email")
                        Result.failure(Exception("Contraseña incorrecta."))
                    }
                } else {
                    Log.w("UserRepositoryImpl", "Usuario no encontrado con email: $email")
                    Result.failure(Exception("Usuario no encontrado."))
                }
            }
        }

        override suspend fun createUser(newUser: User): Result<Unit> {
            users.add(
                newUser.copy(id = users.size + 1),
            )
            return Result.success(Unit)
        }

        override fun getCurrentUser(): Flow<User?> = _currentUserFlow.asStateFlow()

        override suspend fun getUserById(id: Int): User? {
            return users.find { it.id == id }
        }

        override suspend fun updateUserDietProfile(
            userId: Int,
            weightKg: Float,
            heightCm: Float,
            age: Int,
            gender: Gender,
            dietGoal: DietGoal,
            selectedRestrictions: List<String>,
            desiredCalories: Double,
        ): Result<Unit> {
            return withContext(ioDispatcher) {
                // delay(1000) // Simular latencia

                val currentUser = _currentUserFlow.value
                if (currentUser != null && currentUser.id == userId) {
                    val updatedUser =
                        currentUser.copy(
                            weightKg = weightKg,
                            heightCm = heightCm,
                            age = age,
                            gender = gender,
                            dietGoal = dietGoal,
                            selectedDietaryRestrictions = selectedRestrictions,
                            desiredCalories = desiredCalories,
                        )
                    _currentUserFlow.value = updatedUser
                    Log.d("UserRepositoryImpl", "User profile updated with restrictions: $selectedRestrictions")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Usuario no encontrado para actualizar perfil."))
                }
            }
        }

        override fun editUser(user: User): Result<Unit> {
            val index = users.indexOfFirst { it.id == user.id }
            if (index != -1) {
                users[index] = user
                _currentUserFlow.value = user
                Log.d("UserRepositoryImpl", "Usuario actualizado: $user")
                return Result.success(Unit)
            }
            Log.w("UserRepositoryImpl", "Error al actualizar, usuario no encontrado con ID: ${user.id}")
            return Result.failure(Exception("Error al actualizar el usuario, no encontrado."))
        }

        override suspend fun clearCurrentUserSession() {
            _currentUserFlow.value = null
            Log.d("UserRepository", "Sesión del usuario limpiada.")
        }

        override suspend fun addRecipeToHistory(
            userId: Int,
            recipeId: Int,
        ): Result<Unit> {
            return withContext(ioDispatcher) {
                val currentUser = _currentUserFlow.value

                if (currentUser == null || currentUser.id != userId) {
                    Log.e(
                        "UserRepositoryImpl",
                        "Intento de añadir al historial para un usuario incorrecto o no logueado. UserID: $userId, CurrentUser: ${currentUser?.id}",
                    )
                    return@withContext Result.failure(Exception("Usuario incorrecto o no logueado."))
                }

                val updatedHistory = currentUser.recipeHistory.toMutableList()

                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val formattedDateString = dateFormat.format(currentDate)

                val newEntry = CompletedRecipeInfo(recipeId = recipeId, completionDate = formattedDateString)

                updatedHistory.add(newEntry)

                _currentUserFlow.value = currentUser.copy(recipeHistory = updatedHistory)

                Log.d(
                    "UserRepositoryImpl",
                    "Receta $recipeId AÑADIDA (permitiendo duplicados) al historial del usuario ${currentUser.id} con fecha '${newEntry.completionDate}'. Nuevo historial: $updatedHistory",
                )
                Result.success(Unit)
            }
        }

        private fun calculateLevelBasedOnPoints(totalPoints: Int): Int {
            if (totalPoints < 0) return 1 // Evitar puntos negativos, nivel mínimo 1
            return (totalPoints / 500) + 1 // Si 0-499 es nivel 1, 500-999 es nivel 2, etc.
            // floor(totalPoints / 500).toInt() + 1
        }

        override suspend fun addPointsToUser(pointsToAdd: Int): Result<Unit> {
            return withContext(ioDispatcher) {
                val currentUser = _currentUserFlow.value
                if (currentUser != null) {
                    val oldPoints = currentUser.points
                    val newTotalPoints = oldPoints + pointsToAdd
                    val newPotentialLevel = calculateLevelBasedOnPoints(newTotalPoints)

                    val updatedUser =
                        currentUser.copy(
                            points = newTotalPoints,
                            level = newPotentialLevel, // Actualizar el nivel
                        )
                    // --- FIN LÓGICA DE NIVEL ---

                    val index = users.indexOfFirst { it.id == currentUser.id }
                    if (index != -1) {
                        users[index] = updatedUser
                        _currentUserFlow.value = updatedUser // Actualizar el StateFlow
                        Log.d(
                            "UserRepositoryImpl",
                            "Puntos actualizados para usuario ${currentUser.id}. Total: ${updatedUser.points}. Nivel: ${updatedUser.level}",
                        )
                        if (newPotentialLevel > currentUser.level) {
                            Log.i("UserRepositoryImpl", "¡Usuario ${currentUser.id} subió al nivel $newPotentialLevel!")
                            // Aquí podrías emitir un evento si quieres notificar de forma especial el "Level Up"
                        }
                        Result.success(Unit)
                    } else {
                        Log.e("UserRepositoryImpl", "Usuario ${currentUser.id} no encontrado en la lista para actualizar puntos.")
                        Result.failure(Exception("Usuario no encontrado en la lista para actualizar puntos."))
                    }
                } else {
                    Log.e("UserRepositoryImpl", "No hay usuario actual para añadir puntos.")
                    Result.failure(Exception("No hay usuario logueado para añadir puntos."))
                }
            }
        }
    }
