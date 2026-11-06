package com.kaloo.frontend

import androidx.compose.foundation.background
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

@Composable
fun CoachScreen(
    username: String,
    onLogout: () -> Unit
) {

    var messages by remember {
        mutableStateOf(
            listOf(
                StudentMessage("1", "Carlos Mendoza", "¿Puedo cambiar el press de banca por mancuernas?", "Hoy, 10:30 AM"),
                StudentMessage("2", "Ana Gómez", "¿Cuánta agua debo tomar durante el entrenamiento?", "Ayer, 4:15 PM"),
                StudentMessage("3", "Luis Paredes", "Tengo molestia leve en la rodilla al hacer sentadilla.", "Ayer, 8:00 AM", reply = "Baja el peso a un 50% y realiza calentamiento previo.", isAnswered = true)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabecera con bienvenida y botón de Cerrar Sesión
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel de Entrenador",
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

        Text(
            text = "Dudas y Consultas de Alumnos",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Lista de tarjetas de mensajes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { msg ->
                StudentMessageCard(
                    message = msg,
                    onSendReply = { responseText ->
                        messages = messages.map { item ->
                            if (item.id == msg.id) {
                                item.copy(reply = responseText, isAnswered = true)
                            } else item
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StudentMessageCard(
    message: StudentMessage,
    onSendReply: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.studentName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (message.isAnswered) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (message.isAnswered) "Respondido" else "Pendiente",
                        color = if (message.isAnswered) Color(0xFF2E7D32) else Color(0xFFE65100),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = message.date,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Pregunta: \"${message.question}\"",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (message.isAnswered) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Tu respuesta:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message.reply ?: "",
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    label = { Text("Escribe tu respuesta...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSendReply(replyText)
                            replyText = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = replyText.isNotBlank()
                ) {
                    Text("Enviar Respuesta")
                }
            }
        }
    }
}

data class StudentMessage(
    val id: String,
    val studentName: String,
    val question: String,
    val date: String,
    var reply: String? = null,
    var isAnswered: Boolean = false
)