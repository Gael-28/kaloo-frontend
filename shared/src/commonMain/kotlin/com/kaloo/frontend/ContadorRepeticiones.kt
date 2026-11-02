package com.kaloo.frontend

class ContadorRepeticiones(
    umbralInicial: Float = 0.8f,
    private val intervaloMinMs: Long = 600,
    private val reboteMs: Long = 250
) {
    var umbral: Float = umbralInicial
        private set
    var repeticiones: Int = 0
        private set

    private val base = FloatArray(3)
    private val suave = FloatArray(3)
    private val varianza = FloatArray(3)
    private var ultimoMs = -1L
    private var ultimaRepMs = Long.MIN_VALUE / 2
    private var ultimaTransMs = Long.MIN_VALUE / 2
    private var ultimoExtremoMs = 0L
    private var eje = 0
    private var estado = 0          // -1 abajo, 0 neutro, 1 arriba
    private var transiciones = 0

    private var calibrando = false
    private val muestrasCalibracion = ArrayList<Float>()

    fun reiniciarConteo() {
        repeticiones = 0
        transiciones = 0
        estado = 0
    }

    fun iniciarCalibracion() {
        muestrasCalibracion.clear()
        calibrando = true
    }

    fun terminarCalibracion(): Float {
        calibrando = false
        if (muestrasCalibracion.size >= 50) {
            val orden = muestrasCalibracion.sorted()
            val amplitud = orden[(orden.size * 0.8f).toInt().coerceAtMost(orden.size - 1)]
            umbral = (amplitud * 0.45f).coerceIn(0.3f, 4f)
        }
        muestrasCalibracion.clear()
        reiniciarConteo()
        return umbral
    }

    fun procesar(tMs: Long, x: Float, y: Float, z: Float, lineal: Boolean): Boolean {
        val v = floatArrayOf(x, y, z)
        if (ultimoMs < 0) {
            ultimoMs = tMs
            for (i in 0..2) base[i] = v[i]
            return false
        }
        val dt = (tMs - ultimoMs).coerceIn(1L, 200L) / 1000f
        ultimoMs = tMs
        val aBase = dt / (2f + dt)
        val aSuave = dt / (0.053f + dt)
        val aVar = dt / (3f + dt)

        for (i in 0..2) {
            val dinamica = if (lineal) v[i] else {
                base[i] += aBase * (v[i] - base[i])
                v[i] - base[i]
            }
            suave[i] += aSuave * (dinamica - suave[i])
            varianza[i] += aVar * (suave[i] * suave[i] - varianza[i])
        }

        if (estado == 0) {
            eje = 0
            for (i in 1..2) if (varianza[i] > varianza[eje]) eje = i
        }
        val a = suave[eje]
        if (calibrando) muestrasCalibracion.add(kotlin.math.abs(a))

        val nuevo = when {
            a < -umbral -> -1
            a > umbral -> 1
            else -> 0
        }
        var conto = false
        if (nuevo != 0) {
            ultimoExtremoMs = tMs
            if (estado == 0) {
                estado = nuevo
            } else if (nuevo != estado && tMs - ultimaTransMs >= reboteMs) {
                estado = nuevo
                transiciones++
                ultimaTransMs = tMs
                if (transiciones % 2 == 1 && tMs - ultimaRepMs >= intervaloMinMs && !calibrando) {
                    repeticiones++
                    ultimaRepMs = tMs
                    conto = true
                }
            }
        } else if (estado != 0 && tMs - ultimoExtremoMs > 2000) {
            estado = 0
            transiciones = 0
        }
        return conto
    }
}