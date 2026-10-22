package com.example.helloapp.features.tunnel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.helloapp.data.ConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Описываем возможные состояния туннеля
sealed interface TunnelState {
    data object Disconnected : TunnelState
    data object Connecting : TunnelState
    data object Connected : TunnelState

    data class Error(val message: String) : TunnelState
}


class TunnelViewModel(application: Application) : AndroidViewModel(application) {

    // Внутреннее состояние (реактивное)
    private val _status = MutableStateFlow<TunnelState>(TunnelState.Disconnected)

    // Открытое состояние для чтения в Compose-экране
    val status: StateFlow<TunnelState> = _status.asStateFlow()

    // Репозиторий для сохраниения настроек подключения
    private val configRepository = ConfigRepository(application)

    fun toggleTunnel() {
        // Если уже есть подключение — выключаем туннель
        if (_status.value is TunnelState.Connected) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _status.value = TunnelState.Disconnected
                    Log.d("GO_TUNNEL", "Туннель остановлен пользователем")
                } catch (e: Exception) {
                    Log.e("GO_TUNNEL", "Ошибка при остановке", e)
                }
            }
            return
        }

        val configJson = configRepository.getJsonConfig()

        // Ранний выход при отсутствии настроек подключения
        if (configJson == null) {
            _status.value = TunnelState.Error("Конфигурация не найдена. Заполните настройки!")
            return
        }

        // Состояние "Подключение..."
        _status.value = TunnelState.Connecting

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = tunnel.Tunnel.startTunnel(configJson)
                Log.d("GO_TUNNEL", "Результат запуска из Go: $result")

                if (result == "Туннель успешно запущен") {
                    // Успех! Интерфейс в режиме работы
                    _status.value = TunnelState.Connected
                } else {
                    // Что-то пошло не так (ошибка портов, неверный json)
                    _status.value = TunnelState.Error("Что-то пошло не так")
                    Log.e("GO_TUNNEL", "Что-то пошло не так")
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Критическая ошибка сети"
                _status.value = TunnelState.Error(errorMessage)
                Log.e("GO_TUNNEL", "Исключение при вызове Go-кода", e)
            }
        }
    }
}
