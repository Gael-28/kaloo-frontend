package com.kaloo.frontend

import androidx.compose.ui.window.Window
import com.kaloo.frontend.ui.App
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kaloo-frontend",
    ) {
        App()
    }
}