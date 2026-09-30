package com.example.helloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.helloapp.features.counter.MainScreen
import com.example.helloapp.features.sandbox.SandboxScreen
import com.example.helloapp.ui.theme.HelloAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            HelloAppTheme {
                val systemStart = MaterialTheme.colorScheme.surfaceDim
                val systemEnd = MaterialTheme.colorScheme.secondaryContainer

                Surface(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(systemStart, systemEnd, systemStart, systemStart),
                                start = Offset(500f, 200f),
                                end = Offset.Infinite
                            )
                        )
                        .fillMaxSize(),
                    color = Color.Transparent // Делаем сам цвет прозрачным, чтобы был виден модификатор градиента
                ) {
                    MainScreen()
                    SandboxScreen()
                }
            }
        }
    }
}
