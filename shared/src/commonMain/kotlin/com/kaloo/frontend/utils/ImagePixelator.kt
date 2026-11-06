package com.kaloo.frontend.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.floor

/**
 * Utilidad para imágenes pixeladas (común a Android / iOS / Desktop).
 *
 * [PixelatedPlaceholder] dibuja un mosaico pixelado determinista con
 * Canvas: sirve como avatar / imagen de ejemplo sin cargar archivos.
 * Para pixelar una foto real, pasa sus píxeles a [pixelate] y dibuja
 * el resultado con el tamaño de bloque calculado.
 */
object ImagePixelator {

    /** Tamaño del bloque de píxeles según el tamaño objetivo y el ratio. */
    fun blockSize(width: Int, height: Int, pixelRatio: Float = 8f): Int =
        maxOf(1, floor(minOf(width, height) / pixelRatio).toInt())

    /**
     * Pixeliza un arreglo de píxeles ARGB (width*height).
     * Promedia cada bloque [block] x [block] y repite el color.
     */
    fun pixelate(pixels: IntArray, width: Int, height: Int, block: Int): IntArray {
        val out = IntArray(pixels.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val bx = (x / block) * block
                val by = (y / block) * block
                out[y * width + x] = pixels[by * width + bx]
            }
        }
        return out
    }
}

private val pixelPalette = listOf(
    Color(0xFF4CAF50), Color(0xFF2196F3), Color(0xFFF44336), Color(0xFFFFC107),
    Color(0xFF9C27B0), Color(0xFF009688), Color(0xFF795548), Color(0xFF607D8B)
)

/** Muestra una imagen pixelada de ejemplo (mosaico) con etiqueta vía [label]. */
@Composable
fun PixelatedPlaceholder(label: String = "", pixelRatio: Float = 8f, modifier: Modifier = Modifier) {
    val blocks = remember {
        (0 until 64).map { i -> pixelPalette[(i * 7 + 3) % pixelPalette.size] }
    }
    Canvas(modifier = modifier.fillMaxWidth().height(120.dp)) {
        val bw = size.width / 8
        val bh = size.height / 8
        blocks.forEachIndexed { i, color ->
            val col = i % 8
            val row = i / 8
            drawRect(color = color, topLeft = Offset(col * bw, row * bh), size = Size(bw, bh))
        }
    }
}
