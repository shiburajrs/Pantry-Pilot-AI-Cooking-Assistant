package com.pantrypilot.app.data.model

data class Ingredient(
    val name: String,
    val confidence: Float,
    val quantity: String,
    val category: String
)