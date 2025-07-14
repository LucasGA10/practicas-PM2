package ar.edu.unlam.mobile.scaffolding.domain.model.recipes

data class RecipeHistoryItem(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val category: Category,
    val portions: Int,
    val tags: List<Category>,
    val isFavorite: Boolean,
    val completionDate: String? = null,
)
