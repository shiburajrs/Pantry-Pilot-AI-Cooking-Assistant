package com.pantrypilot.app.data.remote

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.data.model.Recipe
import com.pantrypilot.app.data.model.RecipeIngredient
import com.pantrypilot.app.util.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class RecipeDiscoveryRepository(
    private val model: GenerativeModel = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = AppConstants.GEMINI_FLASH_MODEL
    ),
    private val imageModel: GenerativeModel = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = AppConstants.GEMINI_IMAGE_MODEL,
        generationConfig = generationConfig {
            responseModalities = listOf(
                ResponseModality.IMAGE
            )
        }
    )
) {

    fun buildRecipeCoverPrompt(recipe: Recipe): String {

        val ingredients = recipe.usedIngredients.joinToString(", ") {
            it.name
        }

        return """
        Generate a premium, photorealistic food photography
        image for a cooking and recipe application.

        DISH NAME:
        ${recipe.name}

        DESCRIPTION:
        ${recipe.description}

        INGREDIENTS:
        $ingredients

        IMAGE REQUIREMENTS:

        1. Show the fully prepared and beautifully presented dish.
        2. Accurately represent the dish based on its name,
           description and ingredients.
        3. Use realistic food textures, natural colours
           and appetizing presentation.
        4. Present the food in an elegant ceramic plate
           or bowl suitable for the dish.
        5. Use soft natural lighting and professional
           food photography techniques.
        6. Use a clean, minimal background.
        7. Make the dish the clear focal point.
        8. Use a slightly elevated three-quarter camera angle.
        9. Avoid unnecessary decorations and props.
        10. No people, hands, text, labels, logos or watermarks.

        VISUAL STYLE:
        Premium restaurant-quality food photography.
        Warm, natural lighting.
        Realistic details.
        Clean editorial composition.

        OUTPUT:
        Landscape 4:3 composition.
        High-quality, realistic food photograph.
    """.trimIndent()
    }

    fun buildCookingStepImagePrompt(
        recipe: Recipe,
        stepIndex: Int
    ): String {

        val instruction = recipe.instructions.getOrNull(stepIndex)
            ?: return ""

        val ingredients = recipe.usedIngredients.joinToString(", ") {
            "${it.name} (${it.quantity})"
        }

        return """
        You are a professional food photography and
        cooking instruction visualisation assistant.

        Generate a premium, photorealistic cooking tutorial
        image for PantryPilot.

        RECIPE INFORMATION:

        Dish Name:
        ${recipe.name}

        Description:
        ${recipe.description}

        Ingredients:
        $ingredients

        COOKING STEP:

        Step Number:
        ${stepIndex + 1} of ${recipe.instructions.size}

        Instruction:
        $instruction

        IMAGE REQUIREMENTS:

        1. Visually represent the exact cooking action
           described in the instruction.
        2. Show the cooking process at the correct stage.
        3. Use the actual ingredients mentioned in the recipe.
        4. Show appropriate kitchen utensils and cookware
           required for this particular step.
        5. Make the cooking action easy to understand
           at first glance.
        6. Use realistic ingredient colours and textures.
        7. Maintain authentic Indian home-cooking context
           when appropriate.
        8. Use realistic food preparation techniques.
        9. Keep the composition clean and uncluttered.
        10. Do not show unrelated ingredients or actions.
        11. Do not skip ahead to a later cooking step.
        12. If the instruction describes serving, show
            the completed dish attractively plated.

        VISUAL STYLE:

        Premium cooking tutorial photography.
        Photorealistic food and ingredients.
        Warm natural kitchen lighting.
        Professional food photography.
        Clean and minimal composition.
        Slightly elevated three-quarter camera angle.

        IMPORTANT:

        - Show only the action described in this step.
        - Do not invent additional cooking actions.
        - Hands may be visible when needed to demonstrate
          a cooking action, but never show faces.
        - Do not include text, captions, labels,
          numbers, logos or watermarks.
        - Avoid cartoon, illustration or 3D-render styles.

        OUTPUT:

        Landscape 4:3 composition.
        High-quality realistic cooking tutorial image.
    """.trimIndent()
    }

    suspend fun findRecipes(
        ingredients: List<Ingredient>
    ): List<Recipe> = withContext(Dispatchers.IO) {

        require(ingredients.isNotEmpty()) {
            "Please select or add at least one ingredient to discover recipes."
        }

        val ingredientText = ingredients.joinToString("\n") {
            "- ${it.name}: ${it.quantity} (${it.category})"
        }

        val prompt = """
    You are PantryPilot, an expert cooking assistant.

    Find 5 practical recipes using the following
    available ingredients:

    $ingredientText

    Requirements:

    1. Prioritize recipes using the available ingredients.
    2. Prefer recipes that use the maximum available ingredients.
    3. Never claim a missing ingredient is available.
    4. List missing ingredients separately.
    5. Common pantry staples such as salt, oil and
       water may be assumed, but list other missing items.
    6. Suggest realistic cooking times.
    7. Include simple, practical cooking instructions.
    8. Prefer familiar Indian recipes when appropriate.
    9. Do not invent ingredients or unrealistic recipes.

    INGREDIENT QUANTITY REQUIREMENTS:

    10. Every ingredient must have a name and quantity.
    11. Include quantities in BOTH usedIngredients
        and missingIngredients.
    12. Quantities must be realistic for the recipe's
        specified serving count.
    13. Use appropriate measurements such as:
        grams (g), millilitres (ml), cups, tablespoons,
        teaspoons, pieces, cloves and pinches.
    14. Estimate reasonable quantities when exact
        measurements are not available.
    15. Never return an ingredient with an empty quantity.
    16. Keep ingredient quantities separate from
        ingredient names.

    Return ONLY valid JSON. Do not include Markdown
    formatting or explanations.

    Follow this exact JSON structure:

    {
      "recipes": [
        {
          "id": "1",
          "name": "Potato Tomato Curry",
          "description": "A simple homemade curry",
          "cookingTime": 25,
          "difficulty": "Easy",
          "servings": 2,
          "usedIngredients": [
            {
              "name": "Potato",
              "quantity": "2 medium (200 g)"
            },
            {
              "name": "Tomato",
              "quantity": "2 medium"
            },
            {
              "name": "Onion",
              "quantity": "1 medium"
            }
          ],
          "missingIngredients": [
            {
              "name": "Cumin seeds",
              "quantity": "1/2 tsp"
            },
            {
              "name": "Turmeric powder",
              "quantity": "1/4 tsp"
            }
          ],
          "instructions": [
            "Wash and chop the vegetables.",
            "Heat oil and saute the onion.",
            "Add spices and vegetables.",
            "Cook until tender and serve."
          ]
        }
      ]
    }
""".trimIndent()

        val response = try {
            model.generateContent(
                content {
                    text(prompt)
                }
            )
        } catch (ioe: java.io.IOException) {
            throw java.io.IOException("Unable to connect to recipe generator. Please check your internet connection.", ioe)
        }

        val responseText = response.text
            ?: throw IllegalStateException("We couldn't generate recipes for these ingredients. Please try adjusting your ingredients or try again.")

        parseRecipes(responseText)
    }

    suspend fun generateRecipeImage(
        recipe: Recipe,
        isRecipeImage: Boolean = true,
        stepIndex: Int = 0
    ): Bitmap {
        val response = imageModel.generateContent(if(isRecipeImage)
            buildRecipeCoverPrompt(recipe) else buildCookingStepImagePrompt(recipe,stepIndex))

        return response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.filterIsInstance<ImagePart>()
            ?.firstOrNull()
            ?.image
            ?: throw IllegalStateException("Image generation failed")
    }

    private fun parseRecipes(
        response: String
    ): List<Recipe> {

        val start = response.indexOf("{")
        val end = response.lastIndexOf("}")

        require(start >= 0 && end > start) {
            "Could not parse recipe suggestions. Please try again."
        }

        val json = JSONObject(
            response.substring(start, end + 1)
        )

        val array = json.getJSONArray("recipes")

        return buildList {

            for (i in 0 until array.length()) {

                val item = array.getJSONObject(i)

                fun getStringList(key: String): List<String> {
                    val values = item.getJSONArray(key)

                    return List(values.length()) {
                        values.getString(it)
                    }
                }

                val recipeName = item.getString("name")
                val rawId = item.optString("id")
                val recipeId = if (rawId.isNotBlank() && rawId.length > 5 && !rawId.all { it.isDigit() }) {
                    rawId
                } else {
                    "recipe_" + recipeName.trim().lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
                }

                add(
                    Recipe(
                        id = recipeId,
                        name = recipeName,
                        description = item.getString("description"),
                        cookingTime = item.getInt("cookingTime"),
                        difficulty = item.getString("difficulty"),
                        servings = item.getInt("servings"),

                        usedIngredients = parseIngredientList(
                            item.getJSONArray("usedIngredients")
                        ),

                        missingIngredients = parseIngredientList(
                            item.getJSONArray("missingIngredients")
                        ),

                        instructions = getStringList("instructions")
                    )
                )
            }
        }
    }
    private fun parseIngredientList(
        jsonArray: JSONArray
    ): List<RecipeIngredient> {

        return buildList {
            for (i in 0 until jsonArray.length()) {

                val ingredient = jsonArray.optJSONObject(i)
                    ?: continue

                add(
                    RecipeIngredient(
                        name = ingredient.optString("name"),
                        quantity = ingredient.optString("quantity")
                    )
                )
            }
        }
    }
}
