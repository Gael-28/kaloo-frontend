package com.kaloo.frontend.data

import com.kaloo.frontend.config.ApiConfig
import com.kaloo.frontend.model.AuthResponse
import com.kaloo.frontend.model.LoginRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi {
    private val client = HttpClientFactory.create()
    private val baseUrl = ApiConfig.BASE_URL

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
