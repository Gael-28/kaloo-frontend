package com.kaloo.frontend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val OBJETIVOS = listOf("Bajar de peso", "Ganar músculo", "Tonificar", "Resistencia")
private val NIVELES = listOf("Principiante", "Intermedio", "Avanzado")
private val DURACIONES = listOf(30, 45, 60)

@Composable
fun RecomendacionScreen(
    username: String,
    rutinaInicial: RutinaRecomendada? = null,
    onRutina: (RutinaRecomendada) -> Unit = {},
    onEntrenar: (EjercicioRutina) -> Unit = {},
    onIniciarDia: (DiaRutina) -> Unit = {},
    onVolver: () -> Unit
) {
    val api = remember { AuthApi() }
    val scope = rememberCoroutineScope()

    var objetivo by remember { mutableStateOf(OBJETIVOS[0]) }
    var nivel by remember { mutableStateOf(NIVELES[0]) }
    var dias by remember { mutableStateOf(3f) }
    var minutos by remember { mutableStateOf(DURACIONES[1]) }
    var limitaciones by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var rutina by remember { mutableStateOf(rutinaInicial) }

    fun actualizar(nueva: RutinaRecomendada) {
        rutina = nueva
        onRutina(nueva)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Recomendación de ejercicios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Usuario: $username", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Opciones(
                    titulo = "Objetivo",
                    opciones = OBJETIVOS,
                    seleccion = objetivo,
                    texto = { it },
                    onSelect = { objetivo = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Opciones(
                    titulo = "Nivel",
                    opciones = NIVELES,
                    seleccion = nivel,
                    texto = { it },
                    onSelect = { nivel = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Días por semana: ${dias.toInt()}",
                    style = MaterialTheme.typography.titleSmall
                )
                Slider(
                    value = dias,
                    onValueChange = { dias = it },
                    valueRange = 1f..7f,
                    steps = 5
                )
                Spacer(modifier = Modifier.height(12.dp))

                Opciones(
                    titulo = "Duración por sesión",
                    opciones = DURACIONES,
                    seleccion = minutos,
                    texto = { "$it min" },
                    onSelect = { minutos = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = limitaciones,
                    onValueChange = { if (it.length <= 200) limitaciones = it },
                    label = { Text("Lesiones o limitaciones (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            cargando = true
                            error = ""
                            val resultado = api.recomendarRutina(
                                PerfilEntrenamiento(
                                    objetivo = objetivo,
                                    nivel = nivel,
                                    diasPorSemana = dias.toInt(),
                                    minutosPorSesion = minutos,
                                    limitaciones = limitaciones.trim()
                                )
                            )
                            cargando = false
                            if (resultado == null) {
                                error = "No se pudo generar la rutina. Revisa la conexión con el servidor."
                            } else {
                                actualizar(resultado)
                            }
                        }
                    }
                ) {
                    Text(if (cargando) "Generando rutina..." else "Generar rutina")
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        val actual = rutina
        if (actual != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tu rutina", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(actual.resumen, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Generada con: ${if (actual.fuente == "IA") "inteligencia artificial" else "reglas del sistema"}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            actual.dias.forEachIndexed { di, dia ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(dia.dia, style = MaterialTheme.typography.titleMedium)
                        if (dia.enfoque.isNotEmpty()) {
                            Text(dia.enfoque, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        dia.ejercicios.forEachIndexed { ei, e ->
                            FilaEjercicio(
                                ejercicio = e,
                                onEntrenar = { onEntrenar(e) },
                                onCambiar = { actualizar(actual.cambiarEjercicio(di, ei, it)) },
                                onQuitar = { actualizar(actual.quitarEjercicio(di, ei)) }
                            )
                        }

                        if (dia.ejercicios.isEmpty()) {
                            Text(
                                "Sin ejercicios en este día",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        AgregarEjercicio { nuevo ->
                            actualizar(actual.agregarEjercicio(di, nuevo))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    enabled = dia.ejercicios.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onIniciarDia(dia) }
                ) {
                    Text("Iniciar rutina: ${dia.dia}")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(actual.aviso, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVolver) { Text("Volver") }
    }
}

@Composable
private fun <T> Opciones(
    titulo: String,
    opciones: List<T>,
    seleccion: T,
    texto: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(titulo, style = MaterialTheme.typography.titleSmall)
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(opcion) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = opcion == seleccion, onClick = { onSelect(opcion) })
                Text(texto(opcion))
            }
        }
    }
}