package com.pantrypilot.app.ui.screens.review_ingredients

import com.pantrypilot.app.data.model.Ingredient
import java.io.File

data class ReviewIngredientUiState(
    val imageFile: File = File(""),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val ingredients: List<Ingredient> = emptyList()
)