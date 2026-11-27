package com.kaloo.frontend.ui

import androidx.compose.runtime.Composable

data class ImagePickers(val pickGallery: () -> Unit, val takePhoto: () -> Unit)

/** Devuelve launchers para galerIa y cAmara; entrega base64 JPEG o null. */
@Composable
expect fun rememberImagePicker(onPicked: (String) -> Unit): ImagePickers