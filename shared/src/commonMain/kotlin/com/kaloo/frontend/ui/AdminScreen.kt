package com.kaloo.frontend.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaloo.frontend.data.AdminApi
import com.kaloo.frontend.model.Establishment
import kotlinx.coroutines.launch

/** Panel de administrador conectado a /api/admin (backend). */
@Composable
fun AdminScreen(username: String, onLogout: () -> Unit) {
    val api = remember { AdminApi() }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("Panel de administrador") }
    var establishment by remember { mutableStateOf<Establishment?>(null) }
    var error by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var openingHours by remember { mutableStateOf("") }
    var managerName by remember { mutableStateOf("") }
    var managerPhone by remember { mutableStateOf("") }
    var lostAndFoundNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val panel = api.getPanel()
            title = panel.title
            establishment = panel.establishment
            name = panel.establishment.name
            address = panel.establishment.address
            phone = panel.establishment.phone
            email = panel.establishment.email
            openingHours = panel.establishment.openingHours
            managerName = panel.establishment.managerName
            managerPhone = panel.establishment.managerPhone
            lostAndFoundNote = panel.establishment.lostAndFoundNote ?: ""
        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text("¡Bienvenido, $username!", style = MaterialTheme.typography.bodyLarge)

        when {
            isLoading -> Text("Cargando establecimiento...")
            error.isNotEmpty() -> Text(error, color = MaterialTheme.colorScheme.error)
            establishment != null -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(establishment!!.name, style = MaterialTheme.typography.titleMedium)
                        Text(establishment!!.address)
                        Text(establishment!!.phone)
                        Text(establishment!!.email)
                        Text(establishment!!.openingHours)
                        establishment!!.description?.let { Text(it) }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Editar establecimiento", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(address, { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(phone, { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(openingHours, { openingHours = it }, label = { Text("Horario") }, modifier = Modifier.fillMaxWidth())

                // ===== Rama 2: Objetos perdidos (teléfono del gerente) =====
                Spacer(modifier = Modifier.height(12.dp))
                Text("Objetos perdidos", style = MaterialTheme.typography.titleSmall)
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Gerente: ${managerName.ifBlank { "—" }}", style = MaterialTheme.typography.titleMedium)
                        Text("Teléfono objetos perdidos: ${managerPhone.ifBlank { "—" }}")
                        if (lostAndFoundNote.isNotBlank()) Text(lostAndFoundNote)
                    }
                }
                OutlinedTextField(managerName, { managerName = it }, label = { Text("Nombre del gerente") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(managerPhone, { managerPhone = it }, label = { Text("Teléfono del gerente (objetos perdidos)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(lostAndFoundNote, { lostAndFoundNote = it }, label = { Text("Nota de objetos perdidos") }, modifier = Modifier.fillMaxWidth())

                if (status.isNotEmpty()) Text(status, color = MaterialTheme.colorScheme.primary)
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            status = ""
                            try {
                                val updated = api.updateEstablishment(
                                    establishment!!.copy(
                                        name = name, address = address, phone = phone, email = email,
                                        openingHours = openingHours, managerName = managerName,
                                        managerPhone = managerPhone, lostAndFoundNote = lostAndFoundNote
                                    )
                                )
                                establishment = updated
                                status = "Guardado correctamente."
                            } catch (e: Exception) {
                                status = "Error al guardar: ${e.message}"
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSaving) "Guardando..." else "Guardar cambios")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar Sesión")
        }
    }
}
