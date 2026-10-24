package com.example.helloapp.features.tunnel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.helloapp.data.ConfigRepository
import com.example.helloapp.services.TunnelService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Состояния экрана
sealed interface TunnelState {
    data object Disconnected : TunnelState
    data object Connecting : TunnelState
    data object Connected : TunnelState
    data class Error(val message: String) : TunnelState
}

class TunnelViewModel(application: Application) : AndroidViewModel(application) {

    // Глобальное состояние из companion object Сервиса
    val status: StateFlow<TunnelState> = TunnelService.serviceStatus

    // Канал для отправки уведомлений
    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Репозиторий
    private val configRepository = ConfigRepository(application)

    // Конетекст приложения
    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    fun toggleTunnel() {
        val currentStatus = status.value

        // 1. Если туннель запущен — отправляется на остановку службы
        if (currentStatus is TunnelState.Connected) {
            val stopIntent = Intent(context, TunnelService::class.java).apply {
                action = TunnelService.ACTION_STOP
            }
            context.startService(stopIntent)
            return
        }

        // 2. Если туннель отключен (или в ошибке) — начинается запуск службы
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val configJson = configRepository.getJsonConfig()

                if (configJson == null) {
                    _events.send("Ошибка: Сначала заполните настройки!")
                    return@launch
                }

                val startIntent = Intent(context, TunnelService::class.java).apply {
                    action = TunnelService.ACTION_START
                    putExtra(TunnelService.EXTRA_CONFIG_JSON, configJson)
                }

                // Отправка команда запуска Foreground-службы в систему Android
                ContextCompat.startForegroundService(context, startIntent)
                _events.send("Команда на запуск туннеля отправлена!")

            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Сбой отправки команды"
                _events.send("Ошибка: $errorMessage")
            }
        }
    }
}
