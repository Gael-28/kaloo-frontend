package com.kaloo.frontend

enum class EstadoSensor { LISTO, FALTA_CONFIGURAR, NO_DISPONIBLE }

interface Sensores {
    fun estadoBiometrico(): EstadoSensor
    fun pedirBiometrico(onOk: () -> Unit, onError: (String) -> Unit)
    fun abrirRegistroBiometrico()

    // Login con huella
    fun huellaActivada(): Boolean
    fun usuarioConHuella(): String?
    fun credencialesGuardadas(): Pair<String, String>?
    fun activarHuella(username: String, password: String)
    fun desactivarHuella()
    // Acelerómetro (conteo de repeticiones)
    fun acelerometroDisponible(): Boolean = false
    fun iniciarAcelerometro(onMuestra: (tMs: Long, x: Float, y: Float, z: Float, lineal: Boolean) -> Unit) {}
    fun detenerAcelerometro() {}
}