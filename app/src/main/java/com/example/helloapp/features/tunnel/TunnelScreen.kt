package com.example.helloapp.features.tunnel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helloapp.ui.components.MyButton

@Composable
fun TunnelScreen(
    onStartTunnel: (String) -> Unit, viewModel: TunnelViewModel = viewModel()
) {
    // Подписываемся на состояние туннеля из ViewModel
    val currentStatus by viewModel.status.collectAsState()

    // Следим за состоянием. Как только оно изменится на Connected, выскочит уведомление
    LaunchedEffect(currentStatus) {
        when (currentStatus) {
            is TunnelState.Connected -> onStartTunnel("Туннель успешно запущен!")
            is TunnelState.Disconnected -> {
                // Можно раскомментировать, если хотите уведомление и при отключении:
                // onStartTunnel("Туннель остановлен")
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Вычисляем текст кнопки на основе состояния
        val buttonText = when (currentStatus) {
            is TunnelState.Disconnected -> "Подключиться"
            is TunnelState.Connecting -> "Подключение..."
            is TunnelState.Connected -> "Отключить туннель"
        }

        // 2. Вычисляем цвета кнопки на основе состояния (адаптивные под тему)
        val buttonColors = when (currentStatus) {
            is TunnelState.Disconnected -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            is TunnelState.Connecting -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
            is TunnelState.Connected -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }

        // Информативный статус под кнопкой
        val statusText = when (currentStatus) {
            is TunnelState.Disconnected -> "Статус: Отключено"
            is TunnelState.Connecting -> "Статус: Проверка конфигурации и SSH..."
            is TunnelState.Connected -> "Статус: Подключено (Локальный прокси: 127.0.0.1:9090)"
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Вызываем вашу обновленную кнопку
        MyButton(
            onClick = { viewModel.toggleTunnel() },
            colors = buttonColors
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.labelLarge
            )
        }


    }
}