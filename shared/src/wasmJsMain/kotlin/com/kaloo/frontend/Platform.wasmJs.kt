package com.kaloo.frontend

import com.kaloo.frontend.config.BuildKonfig

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

actual fun platformApiBaseUrl(): String = BuildKonfig.API_BASE_URL
