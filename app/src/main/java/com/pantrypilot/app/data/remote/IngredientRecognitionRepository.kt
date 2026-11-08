package com.pantrypilot.app.data.remote

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.pantrypilot.app.data.model.Ingredient
import com.pantrypilot.app.util.AppConstants
import com.pantrypilot.app.util.ImageCompressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.IOException

class IngredientRecognitionRepository(
    private val model: GenerativeModel = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = AppConstants.GEMINI_FLASH_MODEL
    )
) {

    suspend fun detectIngredients(
        imageFile: File
    ): List<Ingredient> = withContext(Dispatchers.IO) {

        require(imageFile.exists()) {
            "Photo file could not be found. Please capture or select a new photo."
        }

        val bitmap = ImageCompressor.decodeSampledBitmap(
            imageFile = imageFile,
            maxDimension = 1024
        ) ?: throw IllegalArgumentException(
            "Unable to read or decode image. Please try another photo."
        )

        try {
            val prompt = """
                You are PantryPilot, an intelligent cooking assistant.

                Analyze this image and identify all visible
                edible ingredients.

                Rules:
                - Identify only clearly visible ingredients.
                - Do not invent ingredients.
                - Avoid duplicate ingredients.
                - Estimate quantity only when reasonably visible.
                - Use common ingredient names.
                - Return valid JSON only.

                Required response:

                {
                  "ingredients": [
                    {
                      "name": "Tomato",
                      "confidence": 0.98,
                      "quantity": "3 pieces",
                      "category": "Vegetable"
                    }
                  ]
                }
            """.trimIndent()

            val response = try {
                model.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
            } catch (ioe: IOException) {
                throw IOException("Unable to connect to the AI service. Please check your internet connection.", ioe)
            }

            val responseText = response.text
                ?: throw IllegalStateException("Could not detect any ingredients in this photo. Please try a clearer picture.")

            parseResponse(responseText)

        } finally {
            bitmap.recycle()
        }
    }

    private fun parseResponse(
        response: String
    ): List<Ingredient> {

        val start = response.indexOf("{")
        val end = response.lastIndexOf("}")

        require(start >= 0 && end > start) {
            "Unable to recognize ingredients from this photo. Please try again with better lighting."
        }

        val json = JSONObject(
            response.substring(start, end + 1)
        )

        val array = json.optJSONArray("ingredients")
            ?: return emptyList()

        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                add(
                    Ingredient(
                        name = item.getString("name"),
                        confidence = item.optDouble(
                            "confidence", 0.0
                        ).toFloat().coerceIn(0f, 1f),
                        quantity = item.optString(
                            "quantity", "Unknown"
                        ),
                        category = item.optString(
                            "category", "Other"
                        )
                    )
                )
            }
        }
    }
}
