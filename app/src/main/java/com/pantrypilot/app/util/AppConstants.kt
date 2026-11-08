package com.pantrypilot.app.util

object AppConstants {
    const val GEMINI_FLASH_MODEL = "gemini-3.5-flash-lite"
    const val GEMINI_IMAGE_MODEL = "gemini-nano-banana-2.1"
    const val THE_MEAL_DB_INGREDIENT_BASE_URL = "https://www.themealdb.com/images/ingredients/"

    fun getIngredientImageUrl(ingredientName: String): String {
        val slug = ingredientName.lowercase().trim().replace(" ", "%20")
        return "$THE_MEAL_DB_INGREDIENT_BASE_URL$slug.png"
    }
}
