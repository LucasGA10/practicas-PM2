package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import androidx.room.TypeConverter
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.IngredientType
import ar.edu.unlam.mobile.scaffolding.data.model.ingredients.UsedIngredient
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.model.recipes.Difficulty
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class StringListConverter {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        // Considera una implementación más robusta si las comas pueden estar en los strings
        return value?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}

class UsedIngredientListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<UsedIngredient>? { // Debe ser tu UsedIngredient
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<UsedIngredient>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<UsedIngredient>?): String? {
        return gson.toJson(list)
    }
}

class CategoryConverter {
    @TypeConverter
    fun toCategory(value: String?): Category? {
        return value?.let { enumValueOf<Category>(it) }
    }

    @TypeConverter
    fun fromCategory(value: Category?): String? {
        return value?.name
    }
}

class IngredientTypeConverter {
    @TypeConverter
    fun toIngredientType(value: String?) = value?.let { enumValueOf<IngredientType>(it) }

    @TypeConverter
    fun fromIngredientType(value: IngredientType?) = value?.name
}

class DifficultyConverter {
    @TypeConverter
    fun toDifficulty(value: String?): Difficulty? {
        // Usa 'enumValueOf<Difficulty>(it.uppercase())' si los nombres en la DB pueden estar en minúscula
        // o si los nombres del enum son siempre en mayúsculas.
        // Si los nombres en el enum coinciden exactamente (sensible a mayúsculas/minúsculas)
        // con lo que se guardará/leerá, 'enumValueOf<Difficulty>(it)' es suficiente.
        return value?.let {
            try {
                enumValueOf<Difficulty>(it)
            } catch (e: IllegalArgumentException) {
                // Opcional: Manejar el caso donde el string de la DB no coincida con ningún enum
                // Podrías retornar null, un valor por defecto, o lanzar una excepción diferente.
                null
            }
        }
    }

    @TypeConverter
    fun fromDifficulty(value: Difficulty?): String? {
        return value?.name // Esto guarda el nombre exacto del enum (ej. "Fácil", "Media", "Difícil")
        // O "FACIL", "MEDIA", "DIFICIL" si así están definidos en el enum.
    }
}

object RecipeTypeConverters {
    private const val CATEGORY_LIST_SEPARATOR = ","

    @TypeConverter
    @JvmStatic // Necesario si el TypeConverter está en un object para que Room lo encuentre
    fun fromCategoryList(categories: List<Category>?): String? {
        return categories?.joinToString(CATEGORY_LIST_SEPARATOR) { it.name }
    }

    @TypeConverter
    @JvmStatic // Necesario si el TypeConverter está en un object
    fun toCategoryList(data: String?): List<Category>? {
        return data?.split(CATEGORY_LIST_SEPARATOR)
            ?.mapNotNull { categoryName ->
                try {
                    Category.valueOf(categoryName)
                } catch (e: IllegalArgumentException) {
                    // Manejar el caso donde un nombre de categoría guardado ya no es válido
                    // Podrías loguear esto o simplemente ignorar la categoría inválida
                    null
                }
            }
    }
}
