package com.pantrypilot.app.ui.screens.recipe_discovery

import com.pantrypilot.app.data.model.Recipe

sealed interface RecipeUiState {
    data object Idle : RecipeUiState
    data object Loading : RecipeUiState
    data class Success(val recipes: List<Recipe>) : RecipeUiState
    data class Error(val message: String) : RecipeUiState
}
