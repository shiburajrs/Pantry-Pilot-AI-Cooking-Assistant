package com.pantrypilot.app.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pantrypilot.app.data.model.RecipeIngredient

class RecipeConverters {

    private val gson = Gson()

    @TypeConverter
    fun fromRecipeIngredientList(value: List<RecipeIngredient>?): String {
        return gson.toJson(value ?: emptyList<RecipeIngredient>())
    }

    @TypeConverter
    fun toRecipeIngredientList(value: String?): List<RecipeIngredient> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<RecipeIngredient>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
