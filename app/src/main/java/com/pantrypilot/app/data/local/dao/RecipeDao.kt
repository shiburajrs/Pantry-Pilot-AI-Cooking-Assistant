package com.pantrypilot.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pantrypilot.app.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("DELETE FROM saved_recipes WHERE id = :id")
    suspend fun deleteRecipeById(id: String)

    @Query("SELECT * FROM saved_recipes ORDER BY savedAt DESC")
    fun getAllSavedRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM saved_recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM saved_recipes WHERE id = :id)")
    fun isRecipeSaved(id: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_recipes WHERE id = :id)")
    suspend fun hasRecipe(id: String): Boolean

    @Query("SELECT id FROM saved_recipes")
    fun getSavedRecipeIds(): Flow<List<String>>
}
