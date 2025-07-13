package ar.edu.unlam.mobile.scaffolding.data.model

import com.google.gson.annotations.SerializedName

data class IngredientJsonModel(
    val id: Int,
    val name: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    val type: String?,
)
