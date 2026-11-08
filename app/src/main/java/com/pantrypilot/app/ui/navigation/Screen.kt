package com.pantrypilot.app.ui.navigation

import android.net.Uri
import com.google.gson.Gson
import com.pantrypilot.app.data.model.Recipe

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Home : Screen("home")

    data object Camera : Screen("camera")

    data object SavedRecipes : Screen("saved_recipes")

    data object ReviewIngredients : Screen("review_ingredients?imagePath={imagePath}") {
        const val ARG_IMAGE_PATH = "imagePath"

        fun createRoute(imagePath: String? = null): String {
            return if (!imagePath.isNullOrBlank()) {
                val encodedPath = Uri.encode(imagePath)
                "review_ingredients?imagePath=$encodedPath"
            } else {
                "review_ingredients"
            }
        }
    }

    data object RecipeSuggestions :
        Screen("recipe_suggestions?ingredients={ingredients}") {

        const val ARG_INGREDIENTS = "ingredients"

        fun createRoute(ingredients: String): String {
            val encodedIngredients = Uri.encode(ingredients)
            return "recipe_suggestions?ingredients=$encodedIngredients"
        }
    }

    data object RecipeDetail :
        Screen("recipe_detail?recipe={recipe}") {

        const val ARG_RECIPE = "recipe"

        fun createRoute(recipe: Recipe): String {
            val encodedRecipe = Uri.encode(Gson().toJson(recipe))
            return "recipe_detail?recipe=$encodedRecipe"
        }
    }

    data object CookingSteps :
        Screen("cooking_steps?recipe={recipe}") {

        const val ARG_RECIPE = "recipe"

        fun createRoute(recipe: Recipe): String {
            val encodedRecipe = Uri.encode(Gson().toJson(recipe))
            return "cooking_steps?recipe=$encodedRecipe"
        }
    }
}
