package com.kaloo.frontend.model

import kotlinx.serialization.Serializable

@Serializable
data class NutritionRequest(val weight: Float, val height: Float)

@Serializable
data class NutritionResponse(val bmi: Float, val recommendation: String)

@Serializable
data class ExerciseRequest(val goal: String)

@Serializable
data class ExerciseResponse(val goal: String, val routine: List<String>)

@Serializable
data class CoachMessageRequest(val username: String, val coachName: String, val message: String)

@Serializable
data class ApiResponse(val success: Boolean, val message: String)
