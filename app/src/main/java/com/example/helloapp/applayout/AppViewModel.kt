package com.example.helloapp.applayout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// Глобальные события для всего приложения (например, показ Снабара)
sealed interface AppUiEvent {
    data class ShowSnackbar(val message: String, val hasDismissAction: Boolean = true) : AppUiEvent
}

class AppViewModel : ViewModel() {
    // Навигационное состояние
    var currentScreen by mutableStateOf<Screen>(Screen.Main)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    // Единый канал уведомлений для всех фич приложения
    private val _uiEvents = Channel<AppUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    // Функция, которую будут вызывать другие экраны через колбэк
    fun showSnackbar(message: String, hasDismissAction: Boolean = true) {
        viewModelScope.launch {
            _uiEvents.send(AppUiEvent.ShowSnackbar(message, hasDismissAction))
        }
    }
}

