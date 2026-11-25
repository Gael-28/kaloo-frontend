package com.kaloo.frontend

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun platformApiBaseUrl(): String {
    val isEmulator = Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
        Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
        Build.MODEL.contains("google_sdk", ignoreCase = true) ||
        Build.MODEL.contains("Emulator", ignoreCase = true) ||
        Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
        Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
        Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
        Build.PRODUCT.contains("sdk", ignoreCase = true)
    return if (isEmulator) "http://10.0.2.2:8080" else "http://192.168.1.122:8080"
}