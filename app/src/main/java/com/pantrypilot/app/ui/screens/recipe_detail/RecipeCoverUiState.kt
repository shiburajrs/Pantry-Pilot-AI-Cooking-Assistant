package com.pantrypilot.app.ui.screens.recipe_detail

import android.graphics.Bitmap

sealed interface RecipeCoverUiState {
    data object Idle : RecipeCoverUiState
    data object Loading : RecipeCoverUiState
    data class Success(val coverImage: Bitmap) : RecipeCoverUiState
    data class Error(val message: String) : RecipeCoverUiState
}
