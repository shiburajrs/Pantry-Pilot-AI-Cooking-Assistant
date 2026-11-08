package com.pantrypilot.app.ui.screens.review_ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.data.remote.IngredientRecognitionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ReviewIngredientViewModel @Inject constructor(
    private val repository: IngredientRecognitionRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ReviewIngredientUiState())

    val uiState = _uiState.asStateFlow()

    private var lastImageFile: File? = null

    fun detectIngredients(imageFile: File, forceRefresh: Boolean = false) {
        if (_uiState.value.isLoading) return

        if (!forceRefresh && _uiState.value.isSuccess && lastImageFile?.absolutePath == imageFile.absolutePath) {
            return
        }

        lastImageFile = imageFile

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false,
                    ingredients = emptyList()
                )
            }

            try {
                val ingredients = repository.detectIngredients(imageFile)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ingredients = ingredients,
                        isSuccess = true,
                        errorMessage = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val userFriendlyMessage = when {
                    e is UnknownHostException || e is IOException ||
                        e.message?.contains("network", ignoreCase = true) == true ||
                        e.message?.contains("connection", ignoreCase = true) == true ||
                        e.message?.contains("host", ignoreCase = true) == true ->
                        "Unable to connect to the AI service. Please check your internet connection and try again."

                    !e.message.isNullOrBlank() -> e.message!!

                    else -> "Could not detect ingredients from this photo. Please try again or add ingredients manually."
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = false,
                        errorMessage = userFriendlyMessage
                    )
                }
            }
        }
    }

    fun updateIngredients(updatedIngredients: List<Ingredient>) {
        _uiState.update {
            it.copy(ingredients = updatedIngredients)
        }
    }

    fun retryDetection() {
        lastImageFile?.let { imageFile ->
            detectIngredients(imageFile, forceRefresh = true)
        }
    }

    fun clearState() {
        lastImageFile = null
        _uiState.value = ReviewIngredientUiState()
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}
