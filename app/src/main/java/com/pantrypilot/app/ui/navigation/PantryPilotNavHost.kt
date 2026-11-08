package com.pantrypilot.app.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.ui.screens.PantryCameraScreen
import com.pantrypilot.app.ui.screens.cooking_steps.CookingStepsScreen
import com.pantrypilot.app.ui.screens.home.HomeScreen
import com.pantrypilot.app.ui.screens.recipe_detail.RecipeDetailScreen
import com.pantrypilot.app.ui.screens.recipe_discovery.RecipeDiscoveryScreen
import com.pantrypilot.app.ui.screens.review_ingredients.ReviewIngredientsScreen
import com.pantrypilot.app.ui.screens.saved_recipes.SavedRecipesScreen
import com.pantrypilot.app.ui.screens.splash.SplashScreen

@Composable
fun PantryPilotNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route,
    onExitApp: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = NavigationTransitions.enterTransition,
        exitTransition = NavigationTransitions.exitTransition,
        popEnterTransition = NavigationTransitions.popEnterTransition,
        popExitTransition = NavigationTransitions.popExitTransition
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onScanIngredients = {
                    navController.navigate(Screen.Camera.route)
                },
                onAddManually = {
                    navController.navigate(Screen.ReviewIngredients.createRoute(null))
                },
                onSavedRecipes = {
                    navController.navigate(Screen.SavedRecipes.route)
                }
            )
        }

        composable(
            route = Screen.Camera.route
        ) {
            PantryCameraScreen(
                onPhotoCaptured = { photoFile ->
                    navController.navigate(
                        Screen.ReviewIngredients.createRoute(photoFile.absolutePath)
                    )
                },
                onClose = {
                    val popped = navController.popBackStack()
                    if (!popped) {
                        onExitApp()
                    }
                }
            )
        }

        composable(Screen.SavedRecipes.route) {
            SavedRecipesScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRecipeClick = { recipe ->
                    navController.navigate(
                        Screen.RecipeDetail.createRoute(recipe)
                    )
                },
                onScanClick = {
                    navController.navigate(Screen.Camera.route)
                }
            )
        }

        composable(
            route = Screen.RecipeSuggestions.route,
            arguments = listOf(
                navArgument(Screen.RecipeSuggestions.ARG_INGREDIENTS) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val ingredientsJson = backStackEntry.arguments
                ?.getString(Screen.RecipeSuggestions.ARG_INGREDIENTS)

            val ingredients = remember(ingredientsJson) {
                if (ingredientsJson.isNullOrBlank()) {
                    emptyList()
                } else {
                    Gson().fromJson<List<Ingredient>>(
                        ingredientsJson,
                        object : TypeToken<List<Ingredient>>() {}.type
                    )
                }
            }

            RecipeDiscoveryScreen(
                ingredients = ingredients,
                onBackClick = {
                    navController.popBackStack()
                },
                onRecipeDetail = { recipe ->
                    navController.navigate(
                        Screen.RecipeDetail.createRoute(recipe)
                    )
                }
            )
        }

        composable(
            route = Screen.ReviewIngredients.route,
            arguments = listOf(
                navArgument(Screen.ReviewIngredients.ARG_IMAGE_PATH) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString(Screen.ReviewIngredients.ARG_IMAGE_PATH)
            val decodedPath = encodedPath?.let { Uri.decode(it) }

            ReviewIngredientsScreen(
                imagePath = decodedPath,
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = { ingredients ->
                    val ingredientsJson = Gson().toJson(ingredients)

                    navController.navigate(
                        Screen.RecipeSuggestions.createRoute(ingredientsJson)
                    )
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument(Screen.RecipeDetail.ARG_RECIPE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recipeJson = backStackEntry.arguments
                ?.getString(Screen.RecipeDetail.ARG_RECIPE)

            val recipe = remember(recipeJson) {
                recipeJson?.let {
                    Gson().fromJson(it, Recipe::class.java)
                }
            }

            recipe?.let {
                RecipeDetailScreen(
                    recipe = it,
                    onStartCookingClick = { targetRecipe ->
                        navController.navigate(
                            Screen.CookingSteps.createRoute(targetRecipe)
                        )
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Screen.CookingSteps.route,
            arguments = listOf(
                navArgument(Screen.CookingSteps.ARG_RECIPE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val recipeJson = backStackEntry.arguments
                ?.getString(Screen.CookingSteps.ARG_RECIPE)

            val recipe = remember(recipeJson) {
                recipeJson?.let {
                    Gson().fromJson(it, Recipe::class.java)
                }
            }

            recipe?.let {
                CookingStepsScreen(
                    recipe = it,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onFinishCooking = {
                        navController.popBackStack(Screen.Home.route, false)
                    }
                )
            }
        }
    }
}
