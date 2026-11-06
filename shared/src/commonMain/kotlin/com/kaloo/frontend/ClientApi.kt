package com.kaloo.frontend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.HttpTimeout

// --- Modelos de datos compartidos ---

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

// --- Cliente de API ---

class ClientApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // IP para emulador Android (cambiar a localhost o tu IP local en Desktop/iOS)
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun getNutritionPlan(weight: Float, height: Float): NutritionResponse {
        return client.post("$baseUrl/api/nutrition") {
            contentType(ContentType.Application.Json)
            setBody(NutritionRequest(weight, height))
        }.body()
    }

    suspend fun getExerciseRecommendation(goal: String): ExerciseResponse {
        return client.post("$baseUrl/api/exercises") {
            contentType(ContentType.Application.Json)
            setBody(ExerciseRequest(goal))
        }.body()
    }

    suspend fun sendCoachMessage(username: String, coachName: String, message: String): ApiResponse {
        return client.post("$baseUrl/api/coaches/message") {
            contentType(ContentType.Application.Json)
            setBody(CoachMessageRequest(username, coachName, message))
        }.body()
    }
}
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 60_000
    }
}