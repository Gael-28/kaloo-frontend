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
    data class Calibrar(val username: String, val destino: Screen) : Screen()
    data class Configuracion(val username: String, val destino: Screen) : Screen()
    data class Recomendacion(val username: String, val destino: Screen) : Screen()
    data class Conteo(val username: String, val ejercicio: EjercicioRutina, val destino: Screen) : Screen()
    data class RutinaDia(val username: String, val dia: DiaRutina, val destino: Screen) : Screen()
}

private fun pantallaPorRol(username: String, role: String?): Screen =
    when (role?.uppercase()) {
        "ADMINISTRADOR", "ADMIN" -> Screen.Admin(username)
        "ENTRENADOR", "COACH" -> Screen.Coach(username)
        else -> Screen.Client(username)
    }

@Composable
fun App(sensores: Sensores? = null) {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
        var rutinaGuardada by remember { mutableStateOf<RutinaRecomendada?>(null) }

        when (val screen = currentScreen) {
            is Screen.Login -> LoginView(
                sensores = sensores,
                onLoginSuccess = { username, role ->
                    rutinaGuardada = null
                    currentScreen = pantallaPorRol(username, role)
                },
                onNecesitaCalibrar = { username, role ->
                    rutinaGuardada = null
                    currentScreen = Screen.Calibrar(username, pantallaPorRol(username, role))
                }
            )

            is Screen.Admin -> AdminScreen(screen.username) { currentScreen = Screen.Login }

            is Screen.Coach -> Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    CoachScreen(screen.username) { currentScreen = Screen.Login }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { currentScreen = Screen.Recomendacion(screen.username, screen) }
                    ) { Text("Recomendación de rutinas") }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { currentScreen = Screen.Configuracion(screen.username, screen) }
                    ) { Text("Configuración") }
                }
            }

            is Screen.Client -> ClientScreen(
                username = screen.username,
                onLogout = { currentScreen = Screen.Login },
                onCalibracion = { currentScreen = Screen.Calibrar(screen.username, screen) },
                onConfiguracion = { currentScreen = Screen.Configuracion(screen.username, screen) },
                onRecomendacion = { currentScreen = Screen.Recomendacion(screen.username, screen) }
            )

            is Screen.Calibrar -> CalibracionScreen(
                username = screen.username,
                sensores = sensores,
                onContinuar = { currentScreen = screen.destino }
            )

            is Screen.Configuracion -> ConfiguracionScreen(
                username = screen.username,
                sensores = sensores,
                onVolver = { currentScreen = screen.destino }
            )

            is Screen.Recomendacion -> RecomendacionScreen(
                username = screen.username,
                rutinaInicial = rutinaGuardada,
                onRutina = { rutinaGuardada = it },
                onEntrenar = { ejercicio ->
                    currentScreen = Screen.Conteo(screen.username, ejercicio, screen)
                },
                onIniciarDia = { dia ->
                    currentScreen = Screen.RutinaDia(screen.username, dia, screen)
                },
                onVolver = { currentScreen = screen.destino }
            )

            is Screen.Conteo -> ConteoRepeticionesScreen(
                ejercicio = screen.ejercicio,
                sensores = sensores,
                onVolver = { currentScreen = screen.destino }
            )

            is Screen.RutinaDia -> RutinaDiaScreen(
                dia = screen.dia,
                sensores = sensores,
                onVolver = { currentScreen = screen.destino }
            )
        }
    }
}

@Composable
fun LoginView(
    sensores: Sensores? = null,
    onLoginSuccess: (String, String?) -> Unit,
    onNecesitaCalibrar: (String, String?) -> Unit = onLoginSuccess
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

                    val usuario = response.username
                    val rol = response.role
                    if (response.success && usuario != null) {
                        if (sensores == null) {
                            onLoginSuccess(usuario, rol)
                        } else if (sensores.estadoBiometrico() == EstadoSensor.LISTO) {
                            sensores.pedirBiometrico(
                                onOk = { onLoginSuccess(usuario, rol) },
                                onError = { errorMessage = it }
                            )
                        } else {
                            onNecesitaCalibrar(usuario, rol)
                        }
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

        if (sensores != null &&
            sensores.huellaActivada() &&
            sensores.estadoBiometrico() == EstadoSensor.LISTO
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    errorMessage = ""
                    sensores.pedirBiometrico(
                        onOk = {
                            val cred = sensores.credencialesGuardadas()
                            if (cred == null) {
                                errorMessage = "No hay huella activada"
                            } else {
                                scope.launch {
                                    isLoading = true
                                    val r = api.login(LoginRequest(cred.first, cred.second))
                                    isLoading = false
                                    val u = r.username
                                    if (r.success && u != null) {
                                        onLoginSuccess(u, r.role)
                                    } else {
                                        errorMessage = "Tus datos cambiaron, vuelve a iniciar sesión"
                                    }
                                }
                            }
                        },
                        onError = { errorMessage = it }
                    )
                }
            ) { Text("Entrar con huella") }
        }
    }
}