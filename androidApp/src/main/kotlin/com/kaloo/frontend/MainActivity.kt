package com.kaloo.frontend

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sensores = AndroidSensores(this)
        setContent {
            App(sensores = sensores)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}