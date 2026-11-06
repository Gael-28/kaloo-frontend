package com.kaloo.frontend.config

/**
 * Configuración central de red (antes duplicada en AuthApi y ClientApi).
 * Cambia aquí la URL según dónde pruebes:
 *  - Emulador Android                     → http://10.0.2.2:8080
 *  - Celular físico (misma Wi-Fi que PC) → http://192.168.x.x:8080  (IP local de tu PC)
 *  - Desktop / iOS en la misma PC        → http://localhost:8080
 *  - Producción                           → https://tu-dominio/api
 */
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:8080"
}
