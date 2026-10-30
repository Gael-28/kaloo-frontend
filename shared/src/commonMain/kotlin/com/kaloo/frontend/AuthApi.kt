package com.kaloo.frontend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val username: String? = null,
    val role: String? = null,
    val message: String? = null
)

class AuthApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    // 10.0.2.2 es la IP para acceder al localhost de tu PC desde el emulador de Android
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun login(request: LoginRequest): AuthResponse {
        return try {
            client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        } catch (e: Exception) {
            AuthResponse(success = false, message = "Error de conexión: ${e.message}")
        }
    }
}