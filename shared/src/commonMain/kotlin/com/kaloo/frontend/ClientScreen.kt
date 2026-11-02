package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ClientScreen(
    username: String,
    onLogout: () -> Unit,
    onCalibracion: (() -> Unit)? = null,
    onConfiguracion: (() -> Unit)? = null,
    onRecomendacion: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Panel de Cliente", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("¡Bienvenido, $username!", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Módulo de consulta de rutinas diarias y avance personal.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (onRecomendacion != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRecomendacion) { Text("Recomendación de ejercicios") }
        }
        if (onCalibracion != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onCalibracion) { Text("Calibración de sensores") }
        }
        if (onConfiguracion != null) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onConfiguracion) { Text("Configuración") }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLogout) { Text("Cerrar Sesión") }
    }
}