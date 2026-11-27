package com.kaloo.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/** URL base del backend segUn plataforma/dispositivo. */
expect fun platformApiBaseUrl(): String