package com.kaloo.frontend.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import com.kaloo.frontend.data.ClientApi
import com.kaloo.frontend.model.LostAndFoundInfo
import com.kaloo.frontend.model.UserStreak
import kotlinx.coroutines.launch

@Composable
fun ClientScreen(username: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Nutrición IA", "Ejercicios IA", "Entrenadores", "Objetos perdidos", "Racha")
    val api = remember { ClientApi() }

    Column(modifier = Modifier.fillMaxSize()) {
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

        PrimaryTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (selectedTab) {
                0 -> NutritionTab(api)
                1 -> ExercisesTab(api)
                2 -> CoachesTab(api, username)
                3 -> LostItemsTab(api)
                4 -> StreakTab(api, username)
            }
        }
    }
}

@Composable
fun NutritionTab(api: ClientApi) {
    val scope = rememberCoroutineScope()
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf("") }
    var recommendation by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Plan Nutricional con IA", style = MaterialTheme.typography.titleMedium)
        Text("Ingresa tus datos para calcular tu IMC y recibir tu plan del servidor.")

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
                val h = height.toFloatOrNull()
                if (w != null && h != null && h > 0) {
                    scope.launch {
                        isLoading = true
                        try {
                            val res = api.getNutritionPlan(w, h)
                            val roundedBmi = (res.bmi * 100).toInt() / 100.0
                            bmiResult = "Tu IMC: $roundedBmi"
                            recommendation = res.recommendation
                        } catch (e: Exception) {
                            recommendation = "Error de conexión: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    bmiResult = "Ingresa peso y estatura válidos."
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Consultando Backend..." else "Calcular IMC y Recomendar Dieta")
        }

        if (bmiResult.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(bmiResult, style = MaterialTheme.typography.titleSmall)
                    if (recommendation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(recommendation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ExercisesTab(api: ClientApi) {
    val scope = rememberCoroutineScope()
    var goal by remember { mutableStateOf("") }
    var routineList by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recomendación de Ejercicios IA", style = MaterialTheme.typography.titleMedium)
        Text("Escribe tu objetivo (ej. Fuerza, Masa muscular, Perder grasa).")

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
                    scope.launch {
                        isLoading = true
                        try {
                            val res = api.getExerciseRecommendation(goal)
                            routineList = res.routine
                        } catch (e: Exception) {
                            routineList = listOf("Error al consultar la rutina: ${e.message}")
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Generando..." else "Generar Rutina con IA")
        }

        if (routineList.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Rutina recomendada del servidor:", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    routineList.forEach { exercise ->
                        Text("• $exercise", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CoachesTab(api: ClientApi, username: String) {
    val scope = rememberCoroutineScope()
    var question by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Entrenadores Disponibles", style = MaterialTheme.typography.titleMedium)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Entrenador en turno: Entrenador Kaloo", style = MaterialTheme.typography.titleSmall)
                Text("Estado: En línea", style = MaterialTheme.typography.bodySmall)
            }
        }

        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Escribe tu duda para el entrenador...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4
        )

        Button(
            onClick = {
                if (question.isNotBlank()) {
                    scope.launch {
                        isLoading = true
                        try {
                            val res = api.sendCoachMessage(username, "Entrenador Kaloo", question)
                            statusMessage = res.message
                            question = ""
                        } catch (e: Exception) {
                            statusMessage = "Error al enviar mensaje: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Enviando..." else "Enviar Mensaje")
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

@Composable
fun LostItemsTab(api: ClientApi) {
    var info by remember { mutableStateOf<LostAndFoundInfo?>(null) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        try {
            info = api.getLostAndFound()
        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Objetos perdidos", style = MaterialTheme.typography.titleMedium)
        when {
            isLoading -> Text("Cargando...")
            error.isNotEmpty() -> Text(error, color = MaterialTheme.colorScheme.error)
            info != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(info!!.establishmentName, style = MaterialTheme.typography.titleSmall)
                        Text("Gerente: ${info!!.managerName}", style = MaterialTheme.typography.bodyLarge)
                        Text("Teléfono: ${info!!.managerPhone}", style = MaterialTheme.typography.bodyLarge)
                        info!!.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                        Text(info!!.message, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Button(
                    onClick = { uriHandler.openUri("tel:${info!!.managerPhone}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Llamar al gerente")
                }
            }
        }
    }
}

@Composable
fun StreakTab(api: ClientApi, username: String) {
    val scope = rememberCoroutineScope()
    var streak by remember { mutableStateOf<UserStreak?>(null) }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    suspend fun loadStreak() {
        try {
            streak = api.getStreak(username)
            error = ""
        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadStreak() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Racha 🔥", style = MaterialTheme.typography.titleMedium)
        Text("Registra tu asistencia cada día para mantener tu racha.")
        when {
            isLoading -> Text("Cargando...")
            error.isNotEmpty() -> Text(error, color = MaterialTheme.colorScheme.error)
            streak != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Racha actual: ${streak!!.currentStreak} días 🔥", style = MaterialTheme.typography.titleLarge)
                        Text("Racha más larga: ${streak!!.longestStreak} días")
                        Text("Asistencias totales: ${streak!!.totalCheckIns}")
                        streak!!.lastCheckIn?.let { Text("Última asistencia: $it") }
                        streak!!.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    }
                }
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            try {
                                streak = api.checkIn(username)
                            } catch (e: Exception) {
                                error = "Error al registrar: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSaving) "Registrando..." else "Registrar asistencia de hoy")
                }
            }
        }
    }
}
