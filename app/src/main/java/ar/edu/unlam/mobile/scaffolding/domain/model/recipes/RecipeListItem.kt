package ar.edu.unlam.mobile.scaffolding.domain.model.recipes

data class RecipeListItem(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val category: Category,
    val difficulty: Difficulty,
    val portions: Int,
    val tags: List<Category>,
    val rating: Float = 0f,
    val isFavorite: Boolean = false,
)
