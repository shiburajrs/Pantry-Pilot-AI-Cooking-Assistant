package com.pantrypilot.app.data.model


data class Recipe(
    val id: String,
    val name: String,
    val description: String,
    val cookingTime: Int,
    val difficulty: String,
    val servings: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missingIngredients: List<RecipeIngredient>,
    val instructions: List<String>
)
data class RecipeIngredient(
    val name: String,
    val quantity: String
)