package com.example.helloapp.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// 1. Описываем, какие типы уведомлений наш экран умеет отправлять наверх
sealed interface ProfileUiEvent {
    data class ShowSnackbar(val message: String, val hasDismissAction: Boolean = true) : ProfileUiEvent
}

class ProfileViewModel : ViewModel() {
    // 2. Создаем канал для отправки одноразовых событий
    private val _uiEvents = Channel<ProfileUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow() //UI будет слушать этот поток данных

    fun saveProfileChanges() {
        // Здесь идет реальный код: отправка на сервер, валидация и т.д.

        viewModelScope.launch {
            // Имитируем успешное сохранение и пуляем событие снакбара
            _uiEvents.send(
                ProfileUiEvent.ShowSnackbar(
                    message = "Профиль успешно обновлен!",
                    hasDismissAction = true // КРЕСТИК НАСТРАИВАЕТСЯ ЗДЕСЬ!
                )
            )
        }
    }
}
