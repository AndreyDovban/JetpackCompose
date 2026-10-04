package com.example.helloapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.helloapp.features.counter.MainScreen
import com.example.helloapp.features.sandbox.SandboxScreen
import com.example.helloapp.ui.theme.HelloAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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

                var currentScreen by remember { mutableStateOf("main") }

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
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            androidx.compose.material3.TopAppBar(
                                title = {
                                    Text(if (currentScreen == "main") "Главный экран" else "Песочница")
                                },
                                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent
                                ),
                                actions = {
                                    androidx.compose.material3.IconButton(
                                        onClick = {
                                            currentScreen = if (currentScreen == "main") "sandbox" else "main"
                                        }
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = if (currentScreen == "main") {
                                                androidx.compose.material.icons.Icons.Filled.Build
                                            } else {
                                                androidx.compose.material.icons.Icons.Filled.Home
                                            },
                                            contentDescription = "Сменить экран"
                                        )
                                    }
                                }
                            )
                        }
                    ) { innerPadding ->
                        AnimatedContent(
                            targetState = currentScreen,
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(horizontal = 16.dp)
                                .fillMaxSize(),
                            // Описываем саму спецификацию анимации (в данном случае — красивый слайд с растворением)
                            transitionSpec = {
                                // Если переходим на sandbox — контент выплывает справа, старый уплывает налево
                                if (targetState == "sandbox") {
                                    (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                } else {
                                    // Если возвращаемся на main — контент выплывает слева, старый уплывает направо
                                    (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                            slideOutHorizontally { width -> width } + fadeOut()
                                } using SizeTransform(clip = false) // Чтобы элементы не обрезались жестко по краям при переходе
                            },
                            label = "ScreenNavigationAnimation"
                        ) { screen ->
                            when (screen) {
                                "main" -> MainScreen()
                                "sandbox" -> SandboxScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
