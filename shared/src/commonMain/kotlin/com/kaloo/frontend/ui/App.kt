package com.kaloo.frontend.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*

sealed class Screen {
    object Login : Screen()
    data class Admin(val username: String) : Screen()
    data class Coach(val username: String) : Screen()
    data class Client(val username: String) : Screen()
}

@Composable
fun App() {
    MaterialTheme {
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
