package com.kaloo.frontend.config

import com.kaloo.frontend.platformApiBaseUrl

/**
 * ConfiguraciOn central de red.
 * La URL base se resuelve por plataforma/dispositivo en Platform.*.kt:
 *  - Android emulador                → http://10.0.2.2:8080
 *  - Android fIsico (misma Wi-Fi PC) → http://192.168.1.122:8080
 *  - Desktop / iOS en la misma PC    → http://localhost:8080
 */
object ApiConfig {
    val BASE_URL: String = platformApiBaseUrl()
}
