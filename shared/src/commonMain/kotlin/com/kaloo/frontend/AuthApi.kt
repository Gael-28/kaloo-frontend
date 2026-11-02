package com.kaloo.frontend

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpTimeout
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

@Serializable
data class Calibracion(
    val username: String,
    val gravedadBase: Double,
    val umbralRepeticion: Double
)

// ---- Recomendacion de ejercicios (copia de las clases del backend) ----

@Serializable
data class PerfilEntrenamiento(
    val objetivo: String,
    val nivel: String,
    val diasPorSemana: Int,
    val minutosPorSesion: Int = 45,
    val limitaciones: String = ""
)

@Serializable
data class EjercicioRutina(
    val nombre: String,
    val series: Int = 3,
    val repeticiones: String = "10-12",
    val descansoSeg: Int = 60
)

@Serializable
data class DiaRutina(
    val dia: String,
    val enfoque: String = "",
    val ejercicios: List<EjercicioRutina> = emptyList()
)

@Serializable
data class RutinaRecomendada(
    val resumen: String,
    val dias: List<DiaRutina>,
    val fuente: String,
    val aviso: String
)

class AuthApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        // La IA puede tardar varios segundos en contestar
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 10_000
        }
    }


    private val baseUrl = "http://192.168.0.108:8080"

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

    /** Devuelve la calibración guardada del usuario, o null si no hay (o no hay conexión). */
    suspend fun obtenerCalibracion(username: String): Calibracion? {
        return try {
            val respuesta = client.get("$baseUrl/calibracion/$username")
            if (respuesta.status == HttpStatusCode.OK) respuesta.body() else null
        } catch (e: Exception) {
            null
        }
    }

    /** Guarda la calibración en el backend. Devuelve true si se guardó. */
    suspend fun guardarCalibracion(calibracion: Calibracion): Boolean {
        return try {
            val respuesta = client.put("$baseUrl/calibracion/${calibracion.username}") {
                contentType(ContentType.Application.Json)
                setBody(calibracion)
            }
            respuesta.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    /** Pide al backend una rutina de ejercicios. Devuelve null si falla. */
    suspend fun recomendarRutina(perfil: PerfilEntrenamiento): RutinaRecomendada? {
        return try {
            val respuesta = client.post("$baseUrl/recomendacion") {
                contentType(ContentType.Application.Json)
                setBody(perfil)
            }
            if (respuesta.status == HttpStatusCode.OK) respuesta.body<RutinaRecomendada>() else null
        } catch (e: Exception) {
            null
        }
    }
}