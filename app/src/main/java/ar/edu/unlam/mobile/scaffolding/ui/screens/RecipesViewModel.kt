package ar.edu.unlam.mobile.scaffolding.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Category
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.recipes.Recipe
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel
    @Inject
    constructor(
        private val recipesRepository: RecipesRepository
    ) : ViewModel() {

    // Simulación de una fuente de todas las recetas. En una app real, vendría de un repositorio.
    private val _allRecipes = MutableStateFlow<List<Recipe>>(emptyList())

    private val _selectedFilterTags = MutableStateFlow<List<Category>>(emptyList())
    val selectedFilterTags: StateFlow<List<Category>> = _selectedFilterTags.asStateFlow()

    // Este es el StateFlow que tu UI observará para la lista filtrada de recetas
    @OptIn(ExperimentalCoroutinesApi::class)
    val recipes: StateFlow<List<Recipe>> =
        combine(_allRecipes, _selectedFilterTags) { all, tagsToFilter ->
            if (tagsToFilter.isEmpty()) {
                all // Sin filtros activos, muestra todas las recetas
            } else {
                // Lógica de filtro "AND": La receta debe contener TODAS las tags seleccionadas
                all.filter { recipe ->
                    val matches = recipe.tags.containsAll(tagsToFilter)
                    Log.d("RecipesViewModel", "Recipe '${recipe.name}' tags: ${recipe.tags}, Matches ALL selected: $matches")
                    matches
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun loadAllRecipes() {
        viewModelScope.launch {
            try {
                // Simula una carga o llama a tu repositorio
                _allRecipes.value = recipesRepository.getRecipes()
                Log.d("RecipesViewModel", "Loaded _allRecipes: Count = ${_allRecipes.value.size}")
                if (_allRecipes.value.isNotEmpty()) {
                    Log.d("RecipesViewModel", "First recipe example: ${_allRecipes.value.first().name} with tags: ${_allRecipes.value.first().tags}")
                }
            } catch (e: Exception) {
                Log.e("RecipesViewModel", "Error loading recipes", e)
                _allRecipes.value = emptyList() // Asegura que sea una lista vacía en caso de error
            }
        }
    }

    init {
        // Carga las recetas iniciales cuando el ViewModel se crea
        loadAllRecipes()
    }

    fun updateSelectedFilterTags(newSelectedTags: List<Category>) {
        _selectedFilterTags.value = newSelectedTags.distinct() // distinct() para evitar duplicados
    }

    fun toggleFilterTag(tag: Category) {
        val currentTags = _selectedFilterTags.value.toMutableList()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        _selectedFilterTags.value = currentTags.toList() // Esta línea es la que emite el nuevo estado
        Log.d("RecipesViewModel", "Toggled filter. New selected tags: ${_selectedFilterTags.value}")
    }

    }
