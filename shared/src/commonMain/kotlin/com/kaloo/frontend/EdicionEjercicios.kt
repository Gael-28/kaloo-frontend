package com.kaloo.frontend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

// ---------- Cambios sobre la rutina (devuelven una copia nueva) ----------

fun RutinaRecomendada.quitarEjercicio(dia: Int, pos: Int): RutinaRecomendada =
    copy(dias = dias.mapIndexed { i, d ->
        if (i == dia) d.copy(ejercicios = d.ejercicios.filterIndexed { j, _ -> j != pos }) else d
    })

fun RutinaRecomendada.cambiarEjercicio(dia: Int, pos: Int, nuevo: EjercicioRutina): RutinaRecomendada =
    copy(dias = dias.mapIndexed { i, d ->
        if (i == dia) d.copy(ejercicios = d.ejercicios.mapIndexed { j, e -> if (j == pos) nuevo else e }) else d
    })

fun RutinaRecomendada.agregarEjercicio(dia: Int, nuevo: EjercicioRutina): RutinaRecomendada =
    copy(dias = dias.mapIndexed { i, d ->
        if (i == dia) d.copy(ejercicios = d.ejercicios + nuevo) else d
    })

// ---------- Fila de un ejercicio ----------

@Composable
fun FilaEjercicio(
    ejercicio: EjercicioRutina,
    onEntrenar: () -> Unit,
    onCambiar: (EjercicioRutina) -> Unit,
    onQuitar: () -> Unit
) {
    var verGuia by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    var confirmando by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(ejercicio.nombre, style = MaterialTheme.typography.titleSmall)
        Text(
            "${ejercicio.series} x ${ejercicio.repeticiones} · descanso ${ejercicio.descansoSeg} s",
            style = MaterialTheme.typography.bodySmall
        )
        Row {
            TextButton(onClick = { verGuia = true }) { Text("Cómo se hace") }
            TextButton(onClick = onEntrenar) { Text("Contar con sensor") }
        }
        Row {
            TextButton(onClick = { editando = true }) { Text("Editar") }
            TextButton(
                onClick = { confirmando = true },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) { Text("Quitar") }
        }
    }

    if (verGuia) GuiaDialog(ejercicio.nombre) { verGuia = false }

    if (editando) {
        EditarDialog(
            ejercicio = ejercicio,
            onGuardar = { onCambiar(it); editando = false },
            onCancelar = { editando = false }
        )
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("Quitar ejercicio") },
            text = { Text("¿Quitar \"${ejercicio.nombre}\" de este día?") },
            confirmButton = {
                TextButton(onClick = { confirmando = false; onQuitar() }) { Text("Quitar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) { Text("Cancelar") }
            }
        )
    }
}

// ---------- Cómo se hace ----------

@Composable
private fun GuiaDialog(nombre: String, onCerrar: () -> Unit) {
    val guia = guiaDe(nombre)
    val uri = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(nombre) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                if (guia != null) {
                    Text("Músculos: ${guia.musculo}", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Cómo hacerlo", style = MaterialTheme.typography.titleSmall)
                    guia.pasos.forEachIndexed { i, p ->
                        Text("${i + 1}. $p", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Consejos", style = MaterialTheme.typography.titleSmall)
                    guia.consejos.forEach {
                        Text("• $it", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Text(
                        "Todavía no tengo una guía escrita para este ejercicio. " +
                                "Toca \"Ver video\" para buscar cómo se hace.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Empieza con poco peso y para si sientes dolor. Esto es una guía general.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { uri.openUri(urlVideo(nombre)) }) { Text("Ver video") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}

// ---------- Editar series, repeticiones y descanso ----------

@Composable
private fun EditarDialog(
    ejercicio: EjercicioRutina,
    onGuardar: (EjercicioRutina) -> Unit,
    onCancelar: () -> Unit
) {
    var series by remember { mutableStateOf(ejercicio.series) }
    var reps by remember { mutableStateOf(ejercicio.repeticiones) }
    var descanso by remember { mutableStateOf(ejercicio.descansoSeg) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(ejercicio.nombre) },
        text = {
            Column {
                Stepper("Series", series, 1, 10, 1) { series = it }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { c -> c.isDigit() || c == '-' }.take(7) },
                    label = { Text("Repeticiones (ej. 10 o 8-12)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Stepper("Descanso (s)", descanso, 0, 300, 15) { descanso = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onGuardar(
                    ejercicio.copy(
                        series = series,
                        repeticiones = reps.ifBlank { "10" },
                        descansoSeg = descanso
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun Stepper(titulo: String, valor: Int, min: Int, max: Int, paso: Int, onCambio: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$titulo: $valor")
        Row {
            OutlinedButton(onClick = { onCambio((valor - paso).coerceAtLeast(min)) }) { Text("-") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { onCambio((valor + paso).coerceAtMost(max)) }) { Text("+") }
        }
    }
}

// ---------- Agregar ejercicio a un día ----------

@Composable
fun AgregarEjercicio(onAgregar: (EjercicioRutina) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    var propio by remember { mutableStateOf("") }

    OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
        Text("Agregar ejercicio")
    }

    if (abierto) {
        AlertDialog(
            onDismissRequest = { abierto = false },
            title = { Text("Agregar ejercicio") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = propio,
                        onValueChange = { if (it.length <= 40) propio = it },
                        label = { Text("Escribe uno propio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("O elige de la lista:", style = MaterialTheme.typography.titleSmall)
                    CATALOGO_EJERCICIOS.forEach { g ->
                        Text(
                            g.nombre,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAgregar(EjercicioRutina(nombre = g.nombre))
                                    propio = ""
                                    abierto = false
                                }
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = propio.isNotBlank(),
                    onClick = {
                        onAgregar(EjercicioRutina(nombre = propio.trim()))
                        propio = ""
                        abierto = false
                    }
                ) { Text("Agregar el mío") }
            },
            dismissButton = { TextButton(onClick = { abierto = false }) { Text("Cancelar") } }
        )
    }
}