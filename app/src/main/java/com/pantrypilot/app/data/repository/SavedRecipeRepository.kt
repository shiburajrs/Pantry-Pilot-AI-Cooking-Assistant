package com.pantrypilot.app.data.repository

import com.pantrypilot.app.data.local.dao.RecipeDao
import com.pantrypilot.app.data.local.entity.toEntity
import com.pantrypilot.app.data.local.entity.toRecipe
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface SavedRecipeRepository {
    fun getSavedRecipes(): Flow<List<Recipe>>
    fun getSavedRecipeIds(): Flow<Set<String>>
    fun isRecipeSaved(id: String): Flow<Boolean>
    suspend fun saveRecipe(recipe: Recipe)
    suspend fun removeRecipe(id: String)
    suspend fun toggleSaveRecipe(recipe: Recipe)
}

@Singleton
class SavedRecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SavedRecipeRepository {

    override fun getSavedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllSavedRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    override fun getSavedRecipeIds(): Flow<Set<String>> {
        return recipeDao.getSavedRecipeIds().map { it.toSet() }
    }

    override fun isRecipeSaved(id: String): Flow<Boolean> {
        return recipeDao.isRecipeSaved(id)
    }

    override suspend fun saveRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        recipeDao.insertRecipe(recipe.toEntity())
    }

    override suspend fun removeRecipe(id: String) = withContext(ioDispatcher) {
        recipeDao.deleteRecipeById(id)
    }

    override suspend fun toggleSaveRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        if (recipeDao.hasRecipe(recipe.id)) {
            recipeDao.deleteRecipeById(recipe.id)
        } else {
            recipeDao.insertRecipe(recipe.toEntity())
        }
    }
}
