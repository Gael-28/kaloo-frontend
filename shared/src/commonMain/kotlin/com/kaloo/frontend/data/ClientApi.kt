package com.kaloo.frontend.data

import com.kaloo.frontend.config.ApiConfig
import com.kaloo.frontend.model.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ClientApi {
    private val client = HttpClientFactory.create()
    private val baseUrl = ApiConfig.BASE_URL

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

    suspend fun getLostAndFound(): LostAndFoundInfo =
        client.get("$baseUrl/api/lost-and-found").body()

    suspend fun getStreak(username: String): UserStreak =
        client.get("$baseUrl/api/streak/$username").body()

    suspend fun checkIn(username: String): UserStreak =
        client.post("$baseUrl/api/streak/check-in") {
            contentType(ContentType.Application.Json)
            setBody(CheckInRequest(username))
        }.body()

    suspend fun sendCoachMessage(username: String, coachName: String, message: String): ApiResponse {
        return client.post("$baseUrl/api/coaches/message") {
            contentType(ContentType.Application.Json)
            setBody(CoachMessageRequest(username, coachName, message))
        }.body()
    }
}
