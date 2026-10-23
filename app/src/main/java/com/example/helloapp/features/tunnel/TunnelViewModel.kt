package com.example.helloapp.features.tunnel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.helloapp.data.ConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Описываем возможные состояния туннеля
sealed interface TunnelState {
    data object Disconnected : TunnelState
    data object Connecting : TunnelState
    data object Connected : TunnelState
    data class Error(val message: String) : TunnelState
}


class TunnelViewModel(application: Application) : AndroidViewModel(application) {

    // Состояние подключения к туннелю
    private val _status = MutableStateFlow<TunnelState>(TunnelState.Disconnected)
    val status: StateFlow<TunnelState> = _status.asStateFlow()

    // Канал для одноразовых UI-событий (Snackbar, алерты)
    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Репозиторий для сохраниения настроек подключения
    private val configRepository = ConfigRepository(application)

    init {
        // Передаем коллбэк в Go-ядро в виде компактной лямбды
        tunnel.Tunnel.setStatusListener { reason ->
            viewModelScope.launch(Dispatchers.Main) {
                _status.value = TunnelState.Error("Соединение разорвано: $reason")
                _events.send("Внимание: Связь с сервером потеряна!")
            }
        }
    }

    fun toggleTunnel() {
        // Если уже есть подключение — выключаем туннель
        if (_status.value is TunnelState.Connected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    tunnel.Tunnel.stopTunnel()
                    _status.value = TunnelState.Disconnected
                    Log.d("GO_TUNNEL", "Туннель остановлен пользователем")
                } catch (e: Exception) {
                    Log.e("GO_TUNNEL", "Ошибка при остановке", e)
                }
            }
            return
        }

        // Запуск туннеля
        _status.value = TunnelState.Connecting

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val configJson = configRepository.getJsonConfig()

                if (configJson == null) {
                    _status.value =
                        TunnelState.Error("Конфигурация не найдена. Проверьте настройки!")
                    _events.send("Ошибка: Конфигурация не найдена!")
                    return@launch
                }

                val result = tunnel.Tunnel.startTunnel(configJson)
                Log.d("GO_TUNNEL", "Результат запуска из Go: $result")

                if (result == "Туннель успешно запущен") {
                    _status.value = TunnelState.Connected
                    _events.send("Туннель успешно запущен!")
                } else {
                    _status.value = TunnelState.Error("Что-то пошло не так")
                    _events.send("Ошибка запуска туннеля")
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Неизвестная ошибка ядра"
                _status.value = TunnelState.Error(errorMessage)
                _events.send("Ошибка: $errorMessage")
                Log.e("GO_TUNNEL", "Исключение при работе Go-ядра", e)
            }
        }
    }
}
