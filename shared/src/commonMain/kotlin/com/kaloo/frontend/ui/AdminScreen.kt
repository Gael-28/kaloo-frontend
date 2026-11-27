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
import com.kaloo.frontend.model.LostAndFoundInfo
import com.kaloo.frontend.model.UserStreak
import com.kaloo.frontend.model.UserSummary
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(username: String, onLogout: () -> Unit) {
    val api = remember { AdminApi() }
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Establecimiento", "Usuarios", "Objetos perdidos", "Rachas")

    var title by remember { mutableStateOf("Panel de administrador") }
    var establishment by remember { mutableStateOf<Establishment?>(null) }
    var lostInfo by remember { mutableStateOf<LostAndFoundInfo?>(null) }
    var streaks by remember { mutableStateOf<List<UserStreak>>(emptyList()) }
    var users by remember { mutableStateOf<List<UserSummary>>(emptyList()) }
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

    suspend fun loadAll() {
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
            lostInfo = api.getLostAndFound()
            streaks = api.getStreaks()
            users = api.getUsers()
            error = ""
        } catch (e: Exception) {
            error = "Error de conexiOn: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadAll() }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.headlineSmall)
            Text("Bienvenido, $username", style = MaterialTheme.typography.bodyMedium)
        }

        ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 16.dp) {
            tabs.forEachIndexed { index, t ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(t, maxLines = 1) })
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading -> Text("Cargando...")
                error.isNotEmpty() -> Text(error, color = MaterialTheme.colorScheme.error)
                else -> when (selectedTab) {
                    0 -> {
                        establishment?.let { est ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(est.name, style = MaterialTheme.typography.titleMedium)
                                    Text(est.address); Text(est.phone); Text(est.email); Text(est.openingHours)
                                    est.description?.let { Text(it) }
                                }
                            }
                        }
                        Text("Editar establecimiento", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(address, { address = it }, label = { Text("DirecciOn") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(phone, { phone = it }, label = { Text("TelEfono") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(openingHours, { openingHours = it }, label = { Text("Horario") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(managerName, { managerName = it }, label = { Text("Nombre del gerente") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(managerPhone, { managerPhone = it }, label = { Text("TelEfono gerente (objetos perdidos)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(lostAndFoundNote, { lostAndFoundNote = it }, label = { Text("Nota objetos perdidos") }, modifier = Modifier.fillMaxWidth())
                        if (status.isNotEmpty()) Text(status, color = MaterialTheme.colorScheme.primary)
                        Button(
                            onClick = {
                                scope.launch {
                                    isSaving = true; status = ""
                                    try {
                                        val updated = api.updateEstablishment(
                                            establishment!!.copy(
                                                name = name, address = address, phone = phone, email = email,
                                                openingHours = openingHours, managerName = managerName,
                                                managerPhone = managerPhone, lostAndFoundNote = lostAndFoundNote
                                            )
                                        )
                                        establishment = updated
                                        lostInfo = api.getLostAndFound()
                                        status = "Guardado correctamente."
                                    } catch (e: Exception) {
                                        status = "Error al guardar: ${e.message}"
                                    } finally { isSaving = false }
                                }
                            },
                            enabled = !isSaving,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (isSaving) "Guardando..." else "Guardar cambios") }
                    }

                    1 -> {
                        Text("Usuarios y roles", style = MaterialTheme.typography.titleSmall)
                        if (users.isEmpty()) Text("No hay usuarios.")
                        users.forEach { u ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(u.username, style = MaterialTheme.typography.titleSmall)
                                    Text("Rol: ${u.role}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    2 -> {
                        lostInfo?.let { info ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Contacto objetos perdidos", style = MaterialTheme.typography.titleSmall)
                                    Text("Gerente: ${info.managerName}")
                                    Text("TelEfono: ${info.managerPhone}")
                                    info.note?.let { Text(it) }
                                }
                            }
                            Text("Objetos reportados", style = MaterialTheme.typography.titleSmall)
                            if (info.items.isEmpty()) Text("No hay objetos reportados.")
                            info.items.forEach { item ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(item.name, style = MaterialTheme.typography.titleSmall)
                                        if (item.description.isNotBlank()) Text(item.description, style = MaterialTheme.typography.bodySmall)
                                        item.color?.let { Text("Color: $it", style = MaterialTheme.typography.bodySmall) }
                                        item.dateFound?.let { Text("Fecha: $it", style = MaterialTheme.typography.bodySmall) }
                                        Text(item.status, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        Text("Rachas de clientes", style = MaterialTheme.typography.titleSmall)
                        if (streaks.isEmpty()) Text("No hay rachas registradas.")
                        streaks.forEach { s ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(s.username, style = MaterialTheme.typography.titleSmall)
                                    Text("Racha actual: ${s.currentStreak} dIas")
                                    Text("Mejor racha: ${s.longestStreak} dIas")
                                    Text("Check-ins: ${s.totalCheckIns}")
                                    s.lastCheckIn?.let { Text("Último: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                    }
                }
            }

            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesiOn")
            }
        }
    }
}
