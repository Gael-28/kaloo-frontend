package com.kaloo.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Modelos para la UI ---

data class UserAdminModel(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val isMembershipActive: Boolean
)

data class PaymentModel(
    val id: String,
    val userName: String,
    val plan: String,
    val amount: String,
    val date: String,
    val status: String // "Completado", "Pendiente", "Rechazado"
)

// --- Pantalla Principal ---

@Composable
fun AdminScreen(
    username: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Datos simulados de usuarios
    var usersList by remember {
        mutableStateOf(
            listOf(
                UserAdminModel("1", "Carlos Mendoza", "carlos@gmail.com", "CLIENTE", true),
                UserAdminModel("2", "Ana Gómez", "ana@gmail.com", "CLIENTE", true),
                UserAdminModel("3", "Juan Pérez", "juan@kaloo.com", "ENTRENADOR", true),
                UserAdminModel("4", "Luis Paredes", "luis@gmail.com", "CLIENTE", false)
            )
        )
    }

    // Datos simulados de pagos
    val paymentsList = remember {
        listOf(
            PaymentModel("P-101", "Carlos Mendoza", "Plan Mensual", "$450 MXN", "Hoy, 10:15 AM", "Completado"),
            PaymentModel("P-102", "Ana Gómez", "Plan Anual", "$4,200 MXN", "Ayer, 03:40 PM", "Completado"),
            PaymentModel("P-103", "Luis Paredes", "Plan Mensual", "$450 MXN", "01 Nov", "Rechazado"),
            PaymentModel("P-104", "Sofía Torres", "Plan Trimestral", "$1,200 MXN", "28 Oct", "Completado"),
            PaymentModel("P-105", "Miguel Ángel", "Plan Mensual", "$450 MXN", "25 Oct", "Pendiente")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel de Administrador",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "¡Bienvenido, $username!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(onClick = onLogout) {
                Text("Salir")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Métricas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminStatCard("Total Usuarios", usersList.size.toString(), Modifier.weight(1f))
            AdminStatCard("Membresías Activas", usersList.count { it.isMembershipActive }.toString(), Modifier.weight(1f))
            AdminStatCard("Ingresos Mes", "$6,300 MXN", Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pestañas
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Gestión de Usuarios", modifier = Modifier.padding(12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Pagos y Membresías", modifier = Modifier.padding(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vistas por Pestaña
        if (selectedTab == 0) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(usersList) { user ->
                    AdminUserCard(
                        user = user,
                        onToggleStatus = {
                            usersList = usersList.map {
                                if (it.id == user.id) it.copy(isMembershipActive = !it.isMembershipActive)
                                else it
                            }
                        }
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(paymentsList) { payment ->
                    PaymentCard(payment = payment)
                }
            }
        }
    }
}

// --- Componentes Reutilizables ---

@Composable
fun AdminStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminUserCard(
    user: UserAdminModel,
    onToggleStatus: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "${user.email} • ${user.role}", fontSize = 12.sp, color = Color.Gray)
            }

            Button(
                onClick = onToggleStatus,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (user.isMembershipActive) Color(0xFFE53935) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (user.isMembershipActive) "Desactivar" else "Activar", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PaymentCard(payment: PaymentModel) {
    val statusColor = when (payment.status) {
        "Completado" -> Color(0xFF2E7D32)
        "Pendiente" -> Color(0xFFE65100)
        else -> Color(0xFFC62828)
    }

    val statusBg = when (payment.status) {
        "Completado" -> Color(0xFFE8F5E9)
        "Pendiente" -> Color(0xFFFFF3E0)
        else -> Color(0xFFFFEBEE)
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = payment.userName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "${payment.plan} • ${payment.date}", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = payment.amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusBg
                ) {
                    Text(
                        text = payment.status,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}