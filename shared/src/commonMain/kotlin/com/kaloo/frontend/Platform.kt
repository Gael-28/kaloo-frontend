package com.kaloo.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/** URL base del backend según plataforma/dispositivo. */
expect fun platformApiBaseUrl(): String