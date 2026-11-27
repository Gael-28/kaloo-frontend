package com.kaloo.frontend.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

@Composable
actual fun rememberImagePicker(onPicked: (String) -> Unit): ImagePickers {
    val context = LocalContext.current

    fun handleBitmap(bitmap: Bitmap) {
        runCatching {
            val scaled = scaleBitmap(bitmap, 320)
            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 70, out)
            onPicked(Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP))
            if (scaled !== bitmap) scaled.recycle()
            bitmap.recycle()
        }
    }

    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return@runCatching
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@runCatching
            handleBitmap(bitmap)
        }
    }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) handleBitmap(bitmap)
    }

    return remember(gallery, camera) {
        ImagePickers(
            pickGallery = { gallery.launch("image/*") },
            takePhoto = { camera.launch(null) }
        )
    }
}

private fun scaleBitmap(src: Bitmap, maxSide: Int): Bitmap {
    val w = src.width
    val h = src.height
    val maxDim = maxOf(w, h)
    if (maxDim <= maxSide) return src
    val scale = maxSide.toFloat() / maxDim.toFloat()
    return Bitmap.createScaledBitmap(src, (w * scale).toInt(), (h * scale).toInt(), true)
}
