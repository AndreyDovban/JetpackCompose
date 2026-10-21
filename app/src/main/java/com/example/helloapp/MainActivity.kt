package com.example.helloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.helloapp.applayout.AppLayout
import com.example.helloapp.ui.theme.HelloAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- ИНИЦИАЛИЗАЦИЯ GO ЛОГГЕРА ---
        try {
            // Создаем логгер через лямбда-выражение
            val goLogger = tunnel.Logger { msg ->
                android.util.Log.d("GO_CORE", "[Go-Engine]: $msg")
            }
            // Передаем наш логгер в глобальную переменную Go-пакета
            tunnel.Tunnel.setLogger(goLogger)
            android.util.Log.d("GO_CORE", "Логгер для Go успешно зарегистрирован")
        } catch (e: Exception) {
            android.util.Log.e("GO_CORE", "Не удалось инициализировать Go логгер", e)
        }
        // ---------------------------------

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
                        .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
                        .fillMaxSize(),
                    color = Color.Transparent
                ) {
                    AppLayout()
                }
            }
        }
    }
}
