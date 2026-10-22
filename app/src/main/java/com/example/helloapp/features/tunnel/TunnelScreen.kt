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
    // Подписка на состояние туннеля из ViewModel
    val currentStatus by viewModel.status.collectAsState()

    // Отслеживание сосстояния для отправки уведомления
    LaunchedEffect(currentStatus) {
        when (currentStatus) {
            TunnelState.Connected -> onStartTunnel("Туннель успешно запущен!")
            TunnelState.Disconnected -> {}
            TunnelState.Connecting -> {}
            is TunnelState.Error -> onStartTunnel("Ошибка: ${(currentStatus as TunnelState.Error).message}")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Етекст кнопки на основе состояния
        val buttonText = when (currentStatus) {
            TunnelState.Disconnected -> "Подключиться"
            TunnelState.Connecting -> "Подключение..."
            TunnelState.Connected -> "Отключить туннель"
            is TunnelState.Error -> "Подключиться"
        }

        // 2. Цвета кнопки на основе состояния (адаптивные под тему)
        val buttonColors = when (currentStatus) {
            TunnelState.Disconnected -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )

            TunnelState.Connecting -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            TunnelState.Connected -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )

            is TunnelState.Error -> ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Информативный статус под кнопкой
        val statusText = when (currentStatus) {
            TunnelState.Disconnected -> "Статус: Отключено"
            TunnelState.Connecting -> "Статус: Проверка конфигурации и SSH..."
            TunnelState.Connected -> "Статус: Подключено (Локальный прокси: 127.0.0.1:9090)"
            is TunnelState.Error -> "Статус: Сбой подключения"
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MyButton(
            onClick = {
                if (currentStatus != TunnelState.Connecting) {
                    viewModel.toggleTunnel()
                }
            }, colors = buttonColors
        ) {
            Text(
                text = buttonText, style = MaterialTheme.typography.labelLarge
            )
        }
    }
}