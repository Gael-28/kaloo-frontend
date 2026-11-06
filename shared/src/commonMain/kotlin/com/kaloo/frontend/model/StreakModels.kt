package com.kaloo.frontend.model

import kotlinx.serialization.Serializable

/** Rama "Racha" — debe coincidir con el backend (com.kaloo.models.Streak). */
@Serializable
data class UserStreak(
    val username: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCheckIns: Int = 0,
    val lastCheckIn: String? = null,
    val message: String? = null
)

@Serializable
data class CheckInRequest(
    val username: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
