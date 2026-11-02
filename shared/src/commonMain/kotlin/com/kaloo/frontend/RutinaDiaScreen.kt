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

private fun dos(n: Int) = if (n < 10) "0$n" else "$n"

private fun formatoTiempo(seg: Int): String {
    val h = seg / 3600
    val m = (seg % 3600) / 60
    val s = seg % 60
    return if (h > 0) "$h:${dos(m)}:${dos(s)}" else "${dos(m)}:${dos(s)}"
}

@Composable
fun RutinaDiaScreen(
    dia: DiaRutina,
    sensores: Sensores?,
    onVolver: () -> Unit
) {
    var fase by remember { mutableStateOf(0) }      // 0 = inicio, 1 = en curso, 2 = terminada
    var indice by remember { mutableStateOf(0) }
    var segundos by remember { mutableStateOf(0) }
    var pausado by remember { mutableStateOf(false) }
    val total = dia.ejercicios.size

    // Cronómetro: cuenta mientras la rutina está en curso y no está en pausa
    LaunchedEffect(fase, pausado) {
        while (fase == 1 && !pausado) {
            delay(1000)
            segundos += 1
        }
    }

    when (fase) {
        0 -> Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dia.dia, style = MaterialTheme.typography.headlineMedium)
            if (dia.enfoque.isNotEmpty()) {
                Text(dia.enfoque, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(16.dp))

            if (total == 0) {
                Text("Este día no tiene ejercicios.")
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        dia.ejercicios.forEachIndexed { i, e ->
                            Text("${i + 1}. ${e.nombre}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${e.series} x ${e.repeticiones} · descanso ${e.descansoSeg} s",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        segundos = 0
                        indice = 0
                        pausado = false
                        fase = 1
                    }
                ) { Text("Empezar rutina") }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onVolver) { Text("Volver") }
        }

        1 -> Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tiempo total", style = MaterialTheme.typography.labelMedium)
                Text(formatoTiempo(segundos), fontSize = 40.sp, style = MaterialTheme.typography.headlineLarge)
                Text(
                    "${dia.dia} · ejercicio ${indice + 1} de $total",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { pausado = !pausado }) {
                    Text(if (pausado) "Reanudar tiempo" else "Pausar tiempo")
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                key(indice) {
                    ConteoRepeticionesScreen(
                        ejercicio = dia.ejercicios[indice],
                        sensores = sensores,
                        onVolver = onVolver,
                        onTerminado = {
                            if (indice + 1 < total) indice += 1 else fase = 2
                        }
                    )
                }
            }
        }

        else -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Rutina terminada!", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Text("Tiempo total", style = MaterialTheme.typography.labelMedium)
            Text(formatoTiempo(segundos), fontSize = 48.sp, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text("${dia.dia} · $total ejercicio(s)", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onVolver) { Text("Volver a mi rutina") }
        }
    }
}