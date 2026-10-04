package com.example.helloapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun MyToggle(
    value: Boolean,
    modifier: Modifier = Modifier,
    onChangeValue: (Boolean)->Unit
){
    val thumbOffset by animateDpAsState(
        targetValue = if (value) 22.dp else 0.dp,
        label = "ToggleAnimation"
    )

    val trackColor = if (value) {
        MaterialTheme.colorScheme.primary // Цвет при включении (например, синий или фиолетовый)
    } else {
        MaterialTheme.colorScheme.outlineVariant  // Серый цвет со скрина при выключении
    }

    val thumbColor = if (value) {
        MaterialTheme.colorScheme.onPrimary // Белый/светлый внутри активного тоггла
    } else {
        MaterialTheme.colorScheme.surface // Чистый цвет фона карточки/поверхности
    }

    Box(
        modifier = modifier
            .padding(10.dp)
            .width(50.dp) // Пропорции стандартного переключателя
            .size(28.dp)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape) // Делаем углы полностью круглыми (овал)
            .background(trackColor)
            // Навешиваем кликабельность по стандарту доступности Android (Accessibility)
            .toggleable(
                value = value,
                role = Role.Switch,
                onValueChange = onChangeValue
            )
            .padding(4.dp), // Внутренний отступ, чтобы кружок не прилипал к краям
        contentAlignment = Alignment.CenterStart // По умолчанию кружок слева
    ) {
        // Сам круглый ползунок внутри
        Box(
            modifier = Modifier
                .offset { androidx.compose.ui.unit.IntOffset(x = thumbOffset.roundToPx(), y = 0) } // Анимированное смещение по горизонтали
                .size(20.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}