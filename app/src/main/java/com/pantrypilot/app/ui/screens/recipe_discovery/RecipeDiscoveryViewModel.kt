package com.pantrypilot.app.ui.screens.recipe_discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.remote.RecipeDiscoveryRepository
import com.pantrypilot.app.data.repository.SavedRecipeRepository
import com.pantrypilot.app.ui.screens.recipe_detail.RecipeCoverUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class RecipeDiscoveryViewModel @Inject constructor(
    private val repository: RecipeDiscoveryRepository,
    private val savedRecipeRepository: SavedRecipeRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<RecipeUiState>(RecipeUiState.Idle)

    private val _coverImageState = MutableStateFlow<RecipeCoverUiState>(RecipeCoverUiState.Idle)

    val state = _state.asStateFlow()

    val coverImageState = _coverImageState.asStateFlow()

    val savedRecipeIds: StateFlow<Set<String>> = savedRecipeRepository.getSavedRecipeIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun toggleSaveRecipe(recipe: Recipe) {
        viewModelScope.launch {
            savedRecipeRepository.toggleSaveRecipe(recipe)
        }
    }

    fun isRecipeSaved(recipeId: String): Flow<Boolean> {
        return savedRecipeRepository.isRecipeSaved(recipeId)
    }

    fun findRecipes(ingredients: List<Ingredient>, forceRefresh: Boolean = false) {
        if (_state.value == RecipeUiState.Loading) return
        if (!forceRefresh && _state.value is RecipeUiState.Success) return

        viewModelScope.launch {
            _state.value = RecipeUiState.Loading

            try {
                val recipes = repository.findRecipes(ingredients)
                _state.value = RecipeUiState.Success(recipes)
            } catch (e: Exception) {
                val userFriendlyMessage = when {
                    e is UnknownHostException || e is IOException ||
                        e.message?.contains("network", ignoreCase = true) == true ||
                        e.message?.contains("connection", ignoreCase = true) == true ||
                        e.message?.contains("host", ignoreCase = true) == true ->
                        "Unable to connect to the recipe service. Please check your internet connection and try again."

                    e is IllegalArgumentException || e is IllegalStateException ->
                        e.localizedMessage ?: "Unable to find recipes with these ingredients."

                    else ->
                        "Unable to generate recipes right now. Please try again in a moment."
                }
                _state.value = RecipeUiState.Error(userFriendlyMessage)
            }
        }
    }

    fun generateRecipeCover(recipe: Recipe) {
        if (_coverImageState.value == RecipeCoverUiState.Loading) return

        viewModelScope.launch {
            _coverImageState.value = RecipeCoverUiState.Loading

            try {
                val recipes = repository.generateRecipeImage(recipe)
                _coverImageState.value = RecipeCoverUiState.Success(recipes)
            } catch (e: Exception) {
                _coverImageState.value = RecipeCoverUiState.Error(
                    "Unable to generate recipe image. Please try again."
                )
            }
        }
    }

    fun generateRecipeStepCover(recipe: Recipe, stepIndex: Int) {
        if (_coverImageState.value == RecipeCoverUiState.Loading) return

        viewModelScope.launch {
            _coverImageState.value = RecipeCoverUiState.Loading

            try {
                val recipes = repository.generateRecipeImage(recipe, false, stepIndex)
                _coverImageState.value = RecipeCoverUiState.Success(recipes)
            } catch (e: Exception) {
                _coverImageState.value = RecipeCoverUiState.Error(
                    "Unable to generate cooking step image. Please try again."
                )
            }
        }
    }

    fun reset() {
        _state.value = RecipeUiState.Idle
    }
}
