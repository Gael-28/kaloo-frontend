package com.kaloo.frontend.model

import kotlinx.serialization.Serializable

/** Debe coincidir con el modelo del backend (com.kaloo.models.Establishment). */
@Serializable
data class Establishment(
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val openingHours: String,
    val description: String? = null,
    val managerName: String = "",
    val managerPhone: String = "",
    val lostAndFoundNote: String? = null
)

@Serializable
data class AdminPanelResponse(
    val title: String = "Panel de administrador",
    val establishment: Establishment
)

@Serializable
data class LostItem(
    val id: Int = 0,
    val name: String,
    val description: String = "",
    val color: String? = null,
    val imageBase64: String? = null,
    val dateFound: String? = null,
    val status: String = "ENCONTRADO"
)

@Serializable
data class LostItemRequest(
    val name: String,
    val description: String = "",
    val color: String? = null,
    val imageBase64: String? = null
)

@Serializable
data class LostAndFoundInfo(
    val establishmentName: String,
    val managerName: String,
    val managerPhone: String,
    val note: String? = null,
    val message: String = "Llama al gerente para checar objetos perdidos.",
    val items: List<LostItem> = emptyList()
)

@Serializable
data class UserSummary(
    val username: String,
    val role: String
)
