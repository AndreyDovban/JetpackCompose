package com.example.helloapp.features.tunnel

import android.app.Application
import android.util.Log // Импортируем стандартный логгер Android
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// Описываем возможные состояния туннеля
sealed interface TunnelState {
    object Disconnected : TunnelState
    object Connecting : TunnelState
    object Connected : TunnelState
}


class TunnelViewModel(application: Application) : AndroidViewModel(application) {

    // Внутреннее состояние (реактивное)
    private val _status = MutableStateFlow<TunnelState>(TunnelState.Disconnected)
    // Открытое состояние для чтения в Compose-экране
    val status: StateFlow<TunnelState> = _status.asStateFlow()

    private fun readConfigFromAssets(): String? {
        return try {
            val context = getApplication<Application>()
            context.assets.open("config.json").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun toggleTunnel() {
        // Если уже подключены — выключаем туннель
        if (_status.value is TunnelState.Connected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    tunnel.Tunnel.stopTunnel() // Дергаем остановку в Go
                    _status.value = TunnelState.Disconnected
                    Log.d("GO_TUNNEL", "Туннель остановлен пользователем")
                } catch (e: Exception) {
                    Log.e("GO_TUNNEL", "Ошибка при остановке", e)
                }
            }
            return
        }

        // Иначе — запускаем подключение
        val configJson = readConfigFromAssets() ?: return

        // Переводим UI в состояние "Подключение..."
        _status.value = TunnelState.Connecting

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = tunnel.Tunnel.startTunnel(configJson)
                Log.d("GO_TUNNEL", "Результат запуска из Go: $result")

                if (result == "Туннель успешно запущен") {
                    // Успех! Переключаем интерфейс в режим работы
                    _status.value = TunnelState.Connected
                } else {
                    // Что-то пошло не так (ошибка портов, неверный json)
                    _status.value = TunnelState.Disconnected
                }
            } catch (e: Exception) {
                Log.e("GO_TUNNEL", "Ошибка при вызове Go-кода", e)
                _status.value = TunnelState.Disconnected
            }
        }
    }
}
