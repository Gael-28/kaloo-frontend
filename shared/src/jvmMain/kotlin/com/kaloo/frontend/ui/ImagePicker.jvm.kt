package com.kaloo.frontend.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(onPicked: (String) -> Unit): ImagePickers = ImagePickers({}, {})