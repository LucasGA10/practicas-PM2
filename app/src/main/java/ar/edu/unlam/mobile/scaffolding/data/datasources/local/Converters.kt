package ar.edu.unlam.mobile.scaffolding.data.datasources.local

import android.util.Log
import androidx.room.TypeConverter
import ar.edu.unlam.mobile.scaffolding.domain.model.ingredients.IngredientType
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Category
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.Difficulty
import ar.edu.unlam.mobile.scaffolding.domain.model.recipes.NutritionalValue
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

// --- Para List<String> (ej: instructions) ---
class StringListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, listType)
        }
    }
}

// --- Para Category (Enum) ---
class CategoryConverter {
    @TypeConverter
    fun fromCategory(category: Category?): String? {
        return category?.name // Almacena el nombre del enum como String
    }

    @TypeConverter
    fun toCategory(name: String?): Category? {
        return name?.let { Category.valueOf(it) }
    }
}

// --- Para List<Category> (Enum) (ej: tags) ---
class CategoryListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromCategoryList(categories: List<Category>?): String? {
        return categories?.let { list ->
            // Convertimos cada enum a su nombre y luego unimos a un string JSON
            // o simplemente un string separado por comas si prefieres, pero JSON es más robusto.
            gson.toJson(list.map { it.name })
        }
    }

    @TypeConverter
    fun toCategoryList(categoryNamesString: String?): List<Category>? {
        return categoryNamesString?.let {
            val listType = object : TypeToken<List<String>>() {}.type
            val names: List<String> = gson.fromJson(it, listType)
            names.map { name -> Category.valueOf(name) }
        }
    }
}

class IngredientTypeConverter {
    @TypeConverter
    fun fromIngredientType(value: IngredientType): String {
        return value.name // Guarda el nombre del enum como String (ej. "VERDURA")
    }

    @TypeConverter
    fun toIngredientType(value: String): IngredientType {
        return value.let { enumName ->
            try {
                IngredientType.valueOf(enumName.uppercase()) // Convierte el String de la BD de nuevo al enum
            } catch (e: IllegalArgumentException) {
                Log.e("TypeConverter", "No se pudo convertir String '$enumName' a IngredientType", e)
                IngredientType.OTROS // O un valor por defecto
            }
        }
    }
}

// --- Para Difficulty (Enum) ---
class DifficultyConverter {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty?): String? {
        return difficulty?.name
    }

    @TypeConverter
    fun toDifficulty(name: String?): Difficulty? {
        return name?.let { Difficulty.valueOf(it) }
    }
}

// --- Para NutritionalValue (Data Class) ---
class NutritionalValueConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionalValue(nutritionalValue: NutritionalValue?): String? {
        return nutritionalValue?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toNutritionalValue(json: String?): NutritionalValue? {
        return json?.let { gson.fromJson(it, NutritionalValue::class.java) }
    }
}
