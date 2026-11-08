package com.pantrypilot.app.ui.screens.saved_recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.repository.SavedRecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedRecipesViewModel @Inject constructor(
    private val repository: SavedRecipeRepository
) : ViewModel() {

    val savedRecipes: StateFlow<List<Recipe>> = repository.getSavedRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeRecipe(recipeId: String) {
        viewModelScope.launch {
            repository.removeRecipe(recipeId)
        }
    }
}
