package com.kaloo.frontend.data

import com.kaloo.frontend.config.ApiConfig
import com.kaloo.frontend.model.AdminPanelResponse
import com.kaloo.frontend.model.Establishment
import com.kaloo.frontend.model.LostAndFoundInfo
import com.kaloo.frontend.model.UserStreak
import com.kaloo.frontend.model.UserSummary
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Cliente del Panel de administrador.
 * Backend: GET /api/admin/panel, GET /api/admin/establishment, PUT /api/admin/establishment
 */
class AdminApi {
    private val client = HttpClientFactory.create()
    private val baseUrl = ApiConfig.BASE_URL

    suspend fun getPanel(): AdminPanelResponse =
        client.get("$baseUrl/api/admin/panel").body()

    suspend fun getEstablishment(): Establishment =
        client.get("$baseUrl/api/admin/establishment").body()

    suspend fun getLostAndFound(): LostAndFoundInfo =
        client.get("$baseUrl/api/admin/lost-and-found").body()

    suspend fun getStreaks(): List<UserStreak> =
        client.get("$baseUrl/api/admin/streaks").body()

    suspend fun getUsers(): List<UserSummary> =
        client.get("$baseUrl/api/admin/users").body()

    suspend fun updateEstablishment(establishment: Establishment): Establishment =
        client.put("$baseUrl/api/admin/establishment") {
            contentType(ContentType.Application.Json)
            setBody(establishment)
        }.body()
}
