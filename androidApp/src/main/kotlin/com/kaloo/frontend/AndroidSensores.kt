package com.kaloo.frontend
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AndroidSensores(private val activity: FragmentActivity) : Sensores {

    private val permitido = BiometricManager.Authenticators.BIOMETRIC_WEAK

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(activity)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            activity,
            "kaloo_huella",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun estadoBiometrico(): EstadoSensor =
        when (BiometricManager.from(activity).canAuthenticate(permitido)) {
            BiometricManager.BIOMETRIC_SUCCESS -> EstadoSensor.LISTO
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> EstadoSensor.FALTA_CONFIGURAR
            else -> EstadoSensor.NO_DISPONIBLE
        }

    override fun pedirBiometrico(onOk: () -> Unit, onError: (String) -> Unit) {
        if (estadoBiometrico() != EstadoSensor.LISTO) {
            onError("Este dispositivo no tiene biometría configurada")
            return
        }

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onOk()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                val cancelado = errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED
                onError(if (cancelado) "Verificación cancelada" else errString.toString())
            }
        }

        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback)
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Kaloo")
            .setSubtitle("Confirma tu identidad")
            .setNegativeButtonText("Cancelar")
            .setAllowedAuthenticators(permitido)
            .build()
        prompt.authenticate(info)
    }

    override fun abrirRegistroBiometrico() {
        val intent = if (Build.VERSION.SDK_INT >= 30) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                permitido
            )
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        activity.startActivity(intent)
    }

    override fun huellaActivada(): Boolean = try {
        prefs.contains("usuario") && prefs.contains("clave")
    } catch (e: Exception) {
        false
    }

    override fun usuarioConHuella(): String? = try {
        prefs.getString("usuario", null)
    } catch (e: Exception) {
        null
    }

    override fun credencialesGuardadas(): Pair<String, String>? = try {
        val u = prefs.getString("usuario", null)
        val c = prefs.getString("clave", null)
        if (u != null && c != null) u to c else null
    } catch (e: Exception) {
        null
    }
    private val sensorManager by lazy {
        activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    private var escucha: SensorEventListener? = null

    private fun sensorAcelerometro(): Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override fun acelerometroDisponible(): Boolean = sensorAcelerometro() != null

    override fun iniciarAcelerometro(onMuestra: (Long, Float, Float, Float, Boolean) -> Unit) {
        detenerAcelerometro()
        val sensor = sensorAcelerometro() ?: return
        val lineal = sensor.type == Sensor.TYPE_LINEAR_ACCELERATION
        val l = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                onMuestra(e.timestamp / 1_000_000L, e.values[0], e.values[1], e.values[2], lineal)
            }
            override fun onAccuracyChanged(s: Sensor?, accuracy: Int) {}
        }
        escucha = l
        sensorManager.registerListener(l, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun detenerAcelerometro() {
        escucha?.let { sensorManager.unregisterListener(it) }
        escucha = null
    }
    override fun activarHuella(username: String, password: String) {
        prefs.edit().putString("usuario", username).putString("clave", password).apply()
    }

    override fun desactivarHuella() {
        prefs.edit().clear().apply()
    }
}