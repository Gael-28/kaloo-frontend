package com.kaloo.frontend.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val GymColorScheme = lightColorScheme(
    primary = Color(0xFF1F6F43),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEFE3),
    onPrimaryContainer = Color(0xFF103B24),
    secondary = Color(0xFFF57C00),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFEAEAEA),
    onSurfaceVariant = Color(0xFF222222),
    outline = Color(0xFF7A7A7A),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF)
)

sealed class Screen {
    object Login : Screen()
    data class Admin(val username: String) : Screen()
    data class Coach(val username: String) : Screen()
    data class Client(val username: String) : Screen()
}

@Composable
fun App() {
    MaterialTheme(colorScheme = GymColorScheme) {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        when (val screen = currentScreen) {
            is Screen.Login -> LoginScreen(onLoginSuccess = { username, role ->
                currentScreen = when (role?.uppercase()) {
                    "ADMINISTRADOR", "ADMIN" -> Screen.Admin(username)
                    "ENTRENADOR", "COACH" -> Screen.Coach(username)
                    "CLIENTE" -> Screen.Client(username)
                    else -> Screen.Client(username)
                }
            })
            is Screen.Admin -> AdminScreen(screen.username) { currentScreen = Screen.Login }
            is Screen.Coach -> CoachScreen(screen.username) { currentScreen = Screen.Login }
            is Screen.Client -> ClientScreen(screen.username) { currentScreen = Screen.Login }
        }
    }
}
