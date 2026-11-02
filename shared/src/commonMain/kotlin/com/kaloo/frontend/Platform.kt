package com.kaloo.frontend

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform