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
fun ClientScreen(username: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Nutrición IA", "Ejercicios IA", "Entrenadores")

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabecera superior
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Hola, $username", style = MaterialTheme.typography.titleLarge)
                Text("Panel del Cliente", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedButton(onClick = onLogout) {
                Text("Salir")
            }
        }

        // Pestañas para tus 3 módulos asignados
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        // Contenido según la pestaña activa
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                0 -> NutritionTab()
                1 -> ExercisesTab()
                2 -> CoachesTab()
            }
        }
    }
}

@Composable
fun NutritionTab() {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Plan Nutricional con IA", style = MaterialTheme.typography.titleMedium)
        Text("Ingresa tus datos para calcular tu IMC y recibir una sugerencia nutricional.")

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Peso (kg)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Estatura (cm)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val w = weight.toFloatOrNull()
                val h = height.toFloatOrNull()?.div(100)
                if (w != null && h != null && h > 0) {
                    val bmi = w / (h * h)
                    val bmiRounded = (bmi * 100).toInt() / 100.0
                    bmiResult = "Tu IMC calculado es: $bmiRounded"
                } else {
                    bmiResult = "Ingresa peso y estatura válidos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular IMC y Recomendar Dieta")
        }

        if (bmiResult.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(bmiResult, style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Sugerencia de IA: Mantén una ingesta de 2g de proteína por kg " +
                                "y prioriza carbohidratos complejos en días de entrenamiento pesado.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ExercisesTab() {
    var goal by remember { mutableStateOf("") }
    var recommendation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recomendación de Ejercicios IA", style = MaterialTheme.typography.titleMedium)
        Text("Describe tu objetivo principal (ej. Ganar masa muscular, Perder grasa, Fuerza).")

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it },
            label = { Text("Objetivo personal") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (goal.isNotBlank()) {
                    recommendation = "Rutina sugerida por IA para '$goal':\n" +
                            "• Sentadilla libre: 4 series x 8-10 reps\n" +
                            "• Press de banca plano: 4 series x 8 reps\n" +
                            "• Remo inclinado con barra: 4 series x 10 reps\n" +
                            "• Press militar con mancuernas: 3 series x 12 reps"
                } else {
                    recommendation = "Por favor ingresa un objetivo."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar Rutina con IA")
        }

        if (recommendation.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(recommendation, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun CoachesTab() {
    var question by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Entrenadores Disponibles", style = MaterialTheme.typography.titleMedium)
        Text("Consulta dudas o solicita consejos a los entrenadores en turno.")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Entrenador en turno: Entrenador Kaloo", style = MaterialTheme.typography.titleSmall)
                Text("Estado: Disponible para consultas", style = MaterialTheme.typography.bodySmall)
            }
        }

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Escribe tu pregunta o duda...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Button(
            onClick = {
                if (question.isNotBlank()) {
                    statusMessage = "Consulta enviada exitosamente al entrenador."
                    question = ""
                } else {
                    statusMessage = "Escribe un mensaje antes de enviar."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Mensaje al Entrenador")
        }

        if (statusMessage.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(statusMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}