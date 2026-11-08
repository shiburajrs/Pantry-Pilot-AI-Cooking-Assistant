package com.pantrypilot.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.model.RecipeIngredient

@Entity(tableName = "saved_recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val cookingTime: Int,
    val difficulty: String,
    val servings: Int,
    val usedIngredients: List<RecipeIngredient>,
    val missingIngredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val savedAt: Long = System.currentTimeMillis()
)

fun RecipeEntity.toRecipe(): Recipe = Recipe(
    id = id,
    name = name,
    description = description,
    cookingTime = cookingTime,
    difficulty = difficulty,
    servings = servings,
    usedIngredients = usedIngredients,
    missingIngredients = missingIngredients,
    instructions = instructions
)

fun Recipe.toEntity(savedAt: Long = System.currentTimeMillis()): RecipeEntity = RecipeEntity(
    id = id,
    name = name,
    description = description,
    cookingTime = cookingTime,
    difficulty = difficulty,
    servings = servings,
    usedIngredients = usedIngredients,
    missingIngredients = missingIngredients,
    instructions = instructions,
    savedAt = savedAt
)
