package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ConfiguracionScreen(
    username: String,
    sensores: Sensores?,
    onVolver: () -> Unit
) {
    val api = remember { AuthApi() }
    val scope = rememberCoroutineScope()
    var activada by remember { mutableStateOf(sensores?.huellaActivada() ?: false) }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Configuración", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Usuario: $username", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Entrar con huella", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                if (sensores == null) {
                    Text("No disponible en esta plataforma.")
                } else if (sensores.estadoBiometrico() != EstadoSensor.LISTO) {
                    Text("Primero registra una huella en el celular (pantalla de calibración).")
                } else if (activada) {
                    Text("Activada para: ${sensores.usuarioConHuella() ?: "?"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = {
                        sensores.desactivarHuella()
                        activada = false
                        mensaje = "Huella desactivada"
                    }) { Text("Desactivar") }
                } else {
                    Text("Escribe tu contraseña una vez para activarla.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        enabled = !cargando && password.isNotEmpty(),
                        onClick = {
                            scope.launch {
                                cargando = true
                                mensaje = ""
                                val r = api.login(LoginRequest(username, password))
                                cargando = false
                                if (r.success) {
                                    sensores.pedirBiometrico(
                                        onOk = {
                                            sensores.activarHuella(username, password)
                                            activada = true
                                            password = ""
                                            mensaje = "Huella activada"
                                        },
                                        onError = { mensaje = it }
                                    )
                                } else {
                                    mensaje = "Contraseña incorrecta"
                                }
                            }
                        }
                    ) { Text("Activar huella") }
                }

                if (mensaje.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(mensaje, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVolver) { Text("Volver") }
    }
}