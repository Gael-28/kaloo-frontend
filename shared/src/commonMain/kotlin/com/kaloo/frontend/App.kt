package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

sealed class Screen {
    object Login : Screen()
    data class Admin(val username: String) : Screen()
    data class Coach(val username: String) : Screen()
    data class Client(val username: String) : Screen()
}

@Composable
// Cambiamos la firma para que pueda enviar la función de éxito
fun App(onBiometricRequest: (onSuccess: () -> Unit) -> Unit = {}) {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

        when (val screen = currentScreen) {
            is Screen.Login -> LoginView(
                onBiometricClick = {
                    // Llamamos a la huella y le decimos qué hacer si tiene éxito
                    onBiometricRequest {

                        currentScreen = Screen.Client("Usuario")
                    }
                },
                onLoginSuccess = { username, role ->
                    currentScreen = when (role?.uppercase()) {
                        "ADMINISTRADOR", "ADMIN" -> Screen.Admin(username)
                        "ENTRENADOR", "COACH" -> Screen.Coach(username)
                        "CLIENTE" -> Screen.Client(username)
                        else -> Screen.Client(username)
                    }
                }
            )
            is Screen.Admin -> AdminScreen(screen.username) { currentScreen = Screen.Login }
            is Screen.Coach -> CoachScreen(screen.username) { currentScreen = Screen.Login }
            is Screen.Client -> ClientScreen(screen.username) { currentScreen = Screen.Login }
        }
    }
}

@Composable
fun LoginView(
    onBiometricClick: () -> Unit,
    onLoginSuccess: (String, String?) -> Unit
) {
    val api = remember { AuthApi() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ProyectoGym - Kaloo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    val response = api.login(LoginRequest(username, password))
                    isLoading = false

                    if (response.success && response.username != null) {
                        onLoginSuccess(response.username, response.role)
                    } else {
                        errorMessage = response.message ?: "Credenciales inválidas"
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Verificando..." else "Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onBiometricClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar con Huella Digital")
        }
    }
}