package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.IngredientDao // Asegúrate que la ruta sea correcta
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao // Asegúrate que la ruta sea correcta
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.UsedIngredientDao // Si también lo pasas aquí
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.IngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.RecipeEntity
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.entities.UsedIngredientEntity
import ar.edu.unlam.mobile.scaffolding.data.model.IngredientJsonModel
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.IngredientType
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import javax.inject.Provider
import kotlin.text.replace
import kotlin.text.uppercase

// El Callback ahora toma las dependencias en su constructor
// Callback para poblar la base de datos al crearla
class AppDatabaseCallback(
    private val context: Context, // Solo necesita el contexto para assets
    private val recipeDaoProvider: Provider<RecipeDao>,
    private val ingredientDaoProvider: Provider<IngredientDao>,
    private val usedIngredientDaoProvider: Provider<UsedIngredientDao>,
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Log.d("AppDatabaseCallback", "onCreate DB - Iniciando carga de datos")

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("AppDatabaseCallback", "Dentro de launch - Poblando datos...")
            // Obtener los DAOs usando .get() de los Providers AHORA,
            // cuando realmente se necesitan y la BD está siendo creada.
            val recipeDao = recipeDaoProvider.get()
            val ingredientDao = ingredientDaoProvider.get()
            val usedIngredientDao = usedIngredientDaoProvider.get()

            populateDatabase(context, ingredientDao, recipeDao, usedIngredientDao)
            Log.d("AppDatabaseCallback", "Proceso de poblado de datos finalizado.")
        }
    }

    private fun loadJsonFromAssets(
        context: Context,
        fileName: String,
    ): String? {
        // ... (implementación de loadJsonFromAssets)
        // (La que te proporcioné o una similar que devuelva String? y maneje IOException)
        var jsonString: String? = null
        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(fileName)
            jsonString = inputStream.bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("AppDatabaseCallback", "Error leyendo $fileName desde assets", ioException)
            return null
        } finally {
            try {
                inputStream?.close()
            } catch (ioException: IOException) {
                // Log o ignorar
            }
        }
        return jsonString
    }

    suspend fun populateDatabase(
        context: Context,
        ingredientDao: IngredientDao,
        recipeDao: RecipeDao,
        usedIngredientDao: UsedIngredientDao,
    ) {
        val gson = Gson()
        Log.d("AppDbCallbackInternal", "populateDatabase iniciado.")

        // Listas para recolectar entidades ANTES de cualquier inserción de UsedIngredient
        val ingredientEntitiesToInsert = mutableListOf<IngredientEntity>()
        val recipeEntitiesToInsert = mutableListOf<RecipeEntity>()
        val usedIngredientEntitiesToInsert = mutableListOf<UsedIngredientEntity>() // Ya la tenías

        // --------------------------------------------------------------------
        // PROCESAMIENTO DE INGREDIENTES
        // --------------------------------------------------------------------
        try {
            val initialIngredientsJson = loadJsonFromAssets(context, "initial_ingredients.json")
            if (initialIngredientsJson != null) {
                val typeToken = object : TypeToken<List<IngredientJsonModel>>() {}.type // USA TU JSON MODEL AQUÍ
                val initialIngredientsModels: List<IngredientJsonModel> = // USA TU JSON MODEL AQUÍ
                    gson.fromJson(initialIngredientsJson, typeToken) ?: emptyList()

                Log.d("AppDbCallbackInternal", "Ingredientes JSON leídos: ${initialIngredientsModels.size} modelos.")

                // Mapea y AÑADE a la lista, NO insertes aún.
                initialIngredientsModels.forEach { jsonModel ->
                    val rawTypeStringFromJson = jsonModel.type // Asumo que jsonModel.type es String?

                    val typeAsEnum: IngredientType? =
                        if (rawTypeStringFromJson == null) {
                            Log.w("AppDbCallback", "Tipo nulo desde JSON para '${jsonModel.name}', usando null o OTROS.")
                            // DECIDE: null o IngredientType.OTROS (depende de IngredientEntity.type)
                            // Si IngredientEntity.type es IngredientType (no-nullable), usa OTROS
                            // Si IngredientEntity.type es IngredientType? (nullable), usa null
                            IngredientType.OTROS // Ejemplo si IngredientEntity.type es NO-NULLABLE
                            // Si es nullable, podría ser null aquí.
                        } else {
                            var normalizedString = rawTypeStringFromJson.uppercase()
                            normalizedString =
                                normalizedString
                                    .replace("Á", "A").replace("É", "E")
                                    .replace("Í", "I").replace("Ó", "O")
                                    .replace("Ú", "U") // Normalización completa
                            try {
                                IngredientType.valueOf(normalizedString)
                            } catch (e: IllegalArgumentException) {
                                Log.e(
                                    "AppDbCallback",
                                    "Valor de tipo desconocido: '$rawTypeStringFromJson' -> '$normalizedString'. Asignando default.",
                                    e,
                                )
                                // DECIDE: null o IngredientType.OTROS
                                IngredientType.OTROS // Ejemplo si IngredientEntity.type es NO-NULLABLE
                            }
                        }
                    // ASUMO que IngredientEntity.type es IngredientType (NO-NULLABLE)
                    // Si fuera IngredientType?, entonces typeAsEnum podría ser null aquí.
                    ingredientEntitiesToInsert.add(
                        IngredientEntity(
                            id = jsonModel.id, // ASEGÚRATE que IngredientEntity.id NO es autogenerada
                            name = jsonModel.name ?: "Nombre Desconocido",
                            imageUrl = jsonModel.imageUrl, // Asume que es String?
                            type = typeAsEnum ?: IngredientType.OTROS, // Manejo final si typeAsEnum fuera null y el campo no lo permite
                            // Si IngredientEntity.type es IngredientType (no nullable)
                            // Y typeAsEnum puede ser null, necesitas este ?: OTROS.
                            // Si IngredientEntity.type es IngredientType? y typeAsEnum es IngredientType?
                            // entonces solo "type = typeAsEnum" está bien.
                        ),
                    )
                }
            } else {
                Log.e("AppDbCallback", "No se pudo cargar initial_ingredients.json desde assets.")
                // Considera no continuar si los ingredientes son esenciales para las recetas
            }
        } catch (e: Exception) {
            // Captura más general para errores de parsing Gson, etc.
            Log.e("AppDbCallbackInternal", "Error procesando initial_ingredients.json", e)
        }

        // --------------------------------------------------------------------
        // PROCESAMIENTO DE RECETAS Y SUS INGREDIENTES USADOS
        // --------------------------------------------------------------------
        Log.d("AppDbCallbackInternal", "--- INICIO PROCESAMIENTO DE RECETAS ---") // Log de inicio de sección
        try {
            val recipesJsonString = loadJsonFromAssets(context, "initial_recipes.json")
            if (recipesJsonString != null) {
                Log.d("AppDbCallbackInternal", "initial_recipes.json cargado correctamente. Longitud: ${recipesJsonString.length}")
                val listRecipeType = object : TypeToken<List<Recipe>>() {}.type
                val initialDomainRecipes: List<Recipe> = gson.fromJson(recipesJsonString, listRecipeType) ?: emptyList()
                // ***** LOG CLAVE #1 *****
                Log.d("AppDbCallbackInternal", "RECETAS DESERIALIZADAS (initialDomainRecipes.size): ${initialDomainRecipes.size}")

                if (initialDomainRecipes.isEmpty() && recipesJsonString.length > 10) { // Un check extra
                    Log.w(
                        "AppDbCallbackInternal",
                        "ALERTA: initialDomainRecipes está VACÍA pero el JSON no lo estaba. Problema de deserialización de GSON con el modelo Recipe?",
                    )
                }

                initialDomainRecipes.forEachIndexed { index, recipeModel -> // recipeModel es de tipo Recipe (dominio)
                    val recipeEntity =
                        RecipeEntity(
                            // ... (mapeo de campos) ...
                            id = recipeModel.id,
                            name = recipeModel.name,
                            imageUrl = recipeModel.imageUrl,
                            category = recipeModel.category,
                            difficulty = recipeModel.difficulty,
                            preparationTime = recipeModel.preparationTime,
                            cookingTime = recipeModel.cookingTime,
                            portions = recipeModel.portions,
                            instructions = recipeModel.instructions,
                            note = recipeModel.note,
                            tags = recipeModel.tags,
                            nutritionalValue = recipeModel.nutritionalValue,
                            rating = recipeModel.rating,
                            isFavorite = recipeModel.isFavorite,
                        )
                    recipeEntitiesToInsert.add(recipeEntity)
                    Log.d("AppDbCallbackInternal", "RecipeEntity añadida a la lista: ID=${recipeEntity.id}")

                    recipeModel.usedIngredients.forEach { domainUsedIngredient -> // domainUsedIngredient es UsedIngredient (dominio)
                        val usedIngredientEntity =
                            UsedIngredientEntity(
                                recipeId = recipeModel.id,
                                ingredientId = domainUsedIngredient.id,
                                quantity = domainUsedIngredient.quantity,
                            )
                        usedIngredientEntitiesToInsert.add(usedIngredientEntity)
                        Log.d(
                            "AppDbCallbackInternal",
                            "  UsedIngredientEntity AÑADIDA a la lista: recipeId=${usedIngredientEntity.recipeId}, ingredientId=${usedIngredientEntity.ingredientId}, quantity='${usedIngredientEntity.quantity}'",
                        )
                    }
                }
            } else {
                Log.w("AppDbCallbackInternal", "El archivo JSON de recetas (initial_recipes.json) está vacío o es nulo.")
            }
        } catch (e: Exception) {
            // ***** LOG CLAVE #3 (si hay error en el bloque try) *****
            Log.e("AppDbCallbackInternal", "EXCEPCIÓN al procesar initial_recipes.json: ${e.message}", e)
        }
        Log.d("AppDbCallbackInternal", "--- FIN PROCESAMIENTO DE RECETAS ---")

        // --------------------------------------------------------------------
        // INSERCIONES EN LA BASE DE DATOS (EN ORDEN CORRECTO)
        // --------------------------------------------------------------------
        if (ingredientEntitiesToInsert.isNotEmpty()) {
            Log.d(
                "AppDbCallbackInternal",
                "Intentando insertar ${ingredientEntitiesToInsert.size} entidades de ingredientes. IDs: ${ingredientEntitiesToInsert.map { it.id }}",
            )
            ingredientDao.insertAll(ingredientEntitiesToInsert)
            Log.d("AppDbCallbackInternal", "Entidades de ingredientes insertadas.")
        } else {
            Log.d("AppDbCallbackInternal", "No hay entidades de ingredientes para insertar del JSON.")
        }

        Log.d("AppDbCallbackInternal", "RECETAS LISTAS PARA INSERTAR (recipeEntitiesToInsert.size): ${recipeEntitiesToInsert.size}")
        if (recipeEntitiesToInsert.isNotEmpty()) {
            Log.d("AppDbCallbackInternal", "Intentando insertar ${recipeEntitiesToInsert.size} entidades de recetas.")
            try {
                recipeDao.insertAllRecipes(recipeEntitiesToInsert)
                Log.d("AppDbCallbackInternal", "Entidades de recetas insertadas CORRECTAMENTE.")
            } catch (e: Exception) {
                // ***** LOG CLAVE #5 (si hay error en la inserción) *****
                Log.e("AppDbCallbackInternal", "¡¡¡ERROR al insertar RecipeEntities!!!: ${e.message}", e)
            }
        } else {
            Log.d("AppDbCallbackInternal", "No hay entidades de recetas para insertar del JSON.")
        }

        if (usedIngredientEntitiesToInsert.isNotEmpty()) {
            Log.d("AppDbCallbackInternal", "Intentando insertar ${usedIngredientEntitiesToInsert.size} UsedIngredientEntities.")
            usedIngredientEntitiesToInsert.forEachIndexed { index, usedIng ->
                Log.d(
                    "AppDbCallbackInternal",
                    "Pre-insert UsedIng #$index: recipeId=${usedIng.recipeId}, ingredientId=${usedIng.ingredientId}, quantity='${usedIng.quantity}'",
                )
            }
            try {
                usedIngredientDao.insertAll(usedIngredientEntitiesToInsert)
                Log.d("AppDbCallbackInternal", "Entidades de ingredientes usados insertadas.")
            } catch (e: Exception) {
                Log.e("AppDbCallbackInternal", "¡¡¡ERROR al insertar UsedIngredientEntities!!!: ${e.message}", e)
                // Opcional: Loguear de nuevo las entidades que intentaste insertar para un análisis post-mortem
                Log.e("AppDbCallbackInternal", "Problematic UsedIngredientEntities: $usedIngredientEntitiesToInsert")
            }
        } else {
            Log.d("AppDbCallbackInternal", "No hay entidades de ingredientes usados para insertar.")
        }

        Log.d("AppDbCallbackInternal", "populateDatabase finalizado.")
    }
}
