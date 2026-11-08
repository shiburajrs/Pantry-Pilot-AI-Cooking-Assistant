package com.pantrypilot.app.data.local.converters

import com.pantrypilot.app.data.model.RecipeIngredient
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeConvertersTest {

    private val converters = RecipeConverters()

    @Test
    fun testRecipeIngredientListSerialization() {
        val ingredients = listOf(
            RecipeIngredient(name = "Tomato", quantity = "2 pieces"),
            RecipeIngredient(name = "Salt", quantity = "1 tsp")
        )
        val json = converters.fromRecipeIngredientList(ingredients)
        val deserialized = converters.toRecipeIngredientList(json)

        assertEquals(2, deserialized.size)
        assertEquals("Tomato", deserialized[0].name)
        assertEquals("2 pieces", deserialized[0].quantity)
        assertEquals("Salt", deserialized[1].name)
        assertEquals("1 tsp", deserialized[1].quantity)
    }

    @Test
    fun testStringListSerialization() {
        val instructions = listOf("Chop vegetables", "Boil water", "Cook pasta")
        val json = converters.fromStringList(instructions)
        val deserialized = converters.toStringList(json)

        assertEquals(3, deserialized.size)
        assertEquals("Chop vegetables", deserialized[0])
        assertEquals("Boil water", deserialized[1])
        assertEquals("Cook pasta", deserialized[2])
    }

    @Test
    fun testEmptyAndNullHandling() {
        assertEquals("[]", converters.fromRecipeIngredientList(null))
        assertEquals(emptyList<RecipeIngredient>(), converters.toRecipeIngredientList(null))
        assertEquals(emptyList<RecipeIngredient>(), converters.toRecipeIngredientList(""))

        assertEquals("[]", converters.fromStringList(null))
        assertEquals(emptyList<String>(), converters.toStringList(null))
        assertEquals(emptyList<String>(), converters.toStringList(""))
    }
}
