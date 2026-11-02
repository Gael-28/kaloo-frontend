package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalibracionScreen(
    username: String,
    sensores: Sensores?,
    onContinuar: () -> Unit
) {
    val api = remember { AuthApi() }
    var estadoBio by remember {
        mutableStateOf(sensores?.estadoBiometrico() ?: EstadoSensor.NO_DISPONIBLE)
    }
    var mensaje by remember { mutableStateOf("") }
    var calibracion by remember { mutableStateOf<Calibracion?>(null) }

    LaunchedEffect(username) {
        calibracion = api.obtenerCalibracion(username)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Calibración de sensores", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Usuario: $username", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TarjetaSensor(
            titulo = "Biométrico (huella / rostro)",
            estado = when (estadoBio) {
                EstadoSensor.LISTO -> "Listo"
                EstadoSensor.FALTA_CONFIGURAR -> "Falta registrar una huella o rostro"
                EstadoSensor.NO_DISPONIBLE -> "No disponible en este dispositivo"
            }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    enabled = sensores != null && estadoBio == EstadoSensor.LISTO,
                    onClick = {
                        mensaje = ""
                        sensores?.pedirBiometrico(
                            onOk = { mensaje = "El sensor funciona correctamente" },
                            onError = { mensaje = it }
                        )
                    }
                ) { Text("Probar sensor") }

                if (sensores != null && estadoBio == EstadoSensor.FALTA_CONFIGURAR) {
                    OutlinedButton(onClick = { sensores.abrirRegistroBiometrico() }) {
                        Text("Registrar")
                    }
                }

                if (sensores != null) {
                    OutlinedButton(onClick = { estadoBio = sensores.estadoBiometrico() }) {
                        Text("Actualizar")
                    }
                }
            }
            if (mensaje.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(mensaje, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaSensor(
            titulo = "Acelerómetro",
            estado = if (calibracion != null) {
                "Calibración guardada (umbral: ${calibracion?.umbralRepeticion})"
            } else {
                "Sin calibración guardada"
            }
        ) {
            Text(
                "Pendiente: se calibrará con el módulo de conteo de repeticiones.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(enabled = false, onClick = {}) { Text("Calibrar") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TarjetaSensor(titulo = "GPS", estado = "Pendiente") {
            Text(
                "Pendiente: se activará con los módulos de racha y objetos perdidos.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(enabled = false, onClick = {}) { Text("Calibrar") }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onContinuar) { Text("Continuar") }
    }
}

@Composable
private fun TarjetaSensor(
    titulo: String,
    estado: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Estado: $estado", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            contenido()
        }
    }
}