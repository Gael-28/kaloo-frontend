package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private fun metaDe(texto: String): Int =
    Regex("\\d+").findAll(texto).lastOrNull()?.value?.toIntOrNull()?.coerceIn(1, 100) ?: 10

@Composable
fun ConteoRepeticionesScreen(
    ejercicio: EjercicioRutina,
    sensores: Sensores?,
    onVolver: () -> Unit,
    onTerminado: (() -> Unit)? = null
) {
    val contador = remember { ContadorRepeticiones() }
    val meta = remember(ejercicio) { metaDe(ejercicio.repeticiones) }
    val hayAcelerometro = sensores?.acelerometroDisponible() == true

    var reps by remember { mutableStateOf(0) }
    var serie by remember { mutableStateOf(1) }
    var activo by remember { mutableStateOf(false) }
    var descanso by remember { mutableStateOf(0) }
    var calibrando by remember { mutableStateOf(false) }
    var segCalib by remember { mutableStateOf(0) }
    var mensaje by remember { mutableStateOf("") }
    var terminado by remember { mutableStateOf(false) }

    DisposableEffect(activo, calibrando) {
        val s = sensores
        if (s != null && hayAcelerometro && (activo || calibrando)) {
            s.iniciarAcelerometro { t, x, y, z, lineal ->
                if (contador.procesar(t, x, y, z, lineal) && activo) reps += 1
            }
        }
        onDispose { s?.detenerAcelerometro() }
    }

    LaunchedEffect(reps) {
        if (activo && reps >= meta) {
            activo = false
            if (serie >= ejercicio.series) {
                terminado = true
                mensaje = "¡Ejercicio terminado!"
            } else {
                mensaje = "Serie $serie completada"
                descanso = ejercicio.descansoSeg
            }
        }
    }
    LaunchedEffect(descanso) {
        if (descanso > 0) {
            delay(1000)
            descanso -= 1
            if (descanso == 0) {
                serie += 1
                reps = 0
                contador.reiniciarConteo()
                mensaje = "Serie $serie: listo para empezar"
            }
        }
    }
    LaunchedEffect(calibrando) {
        if (calibrando) {
            contador.iniciarCalibracion()
            segCalib = 6
            while (segCalib > 0) { delay(1000); segCalib -= 1 }
            val u = contador.terminarCalibracion()
            calibrando = false
            mensaje = "Calibrado (sensibilidad ${kotlin.math.round(u * 10) / 10})"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(ejercicio.nombre, style = MaterialTheme.typography.headlineSmall)
        Text(
            "Serie $serie de ${ejercicio.series} · meta $meta repeticiones",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))

        Text("$reps", fontSize = 96.sp, style = MaterialTheme.typography.displayLarge)
        Text("de $meta", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (descanso > 0) {
            Text("Descanso: $descanso s", style = MaterialTheme.typography.titleLarge)
        } else if (calibrando) {
            Text("Calibrando... haz repeticiones normales ($segCalib s)")
        } else if (mensaje.isNotEmpty()) {
            Text(mensaje)
        }
        Spacer(Modifier.height(16.dp))

        if (!hayAcelerometro) {
            Text(
                "Este dispositivo no tiene acelerómetro. Usa los botones +1 / -1.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            Text(
                "Sujeta el celular con la mano, o fíjalo al brazo o a la barra, y mantén la pantalla encendida.",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(8.dp))

        if (!terminado) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = descanso == 0 && !calibrando,
                onClick = {
                    if (activo) {
                        activo = false
                        mensaje = "En pausa"
                    } else {
                        contador.reiniciarConteo()
                        reps = 0
                        mensaje = "Contando..."
                        activo = true
                    }
                }
            ) { Text(if (activo) "Pausar" else "Empezar serie") }

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { if (reps > 0) reps -= 1 }) { Text("-1") }
                OutlinedButton(modifier = Modifier.weight(1f), onClick = { reps += 1 }) { Text("+1") }
            }
            if (hayAcelerometro) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !activo && descanso == 0 && !calibrando,
                    onClick = { calibrando = true }
                ) { Text("Calibrar sensibilidad (6 s)") }
            }
            if (descanso > 0) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { descanso = 1 }) { Text("Saltar descanso") }
            }
            if (onTerminado != null) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { onTerminado() }) { Text("Saltar ejercicio") }
            }
        } else if (onTerminado != null) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onTerminado() }) {
                Text("Siguiente ejercicio")
            }
        }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onVolver) {
            Text(if (onTerminado != null) "Salir de la rutina" else "Volver")
        }
    }
}