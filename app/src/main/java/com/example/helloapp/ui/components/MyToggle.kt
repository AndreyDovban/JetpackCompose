package com.example.helloapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MyToggle(
    value: Boolean,
    modifier: Modifier = Modifier,
    onChangeValue: (Boolean)->Unit
){

    Switch(
        checked = value,
        modifier = modifier,
        onCheckedChange = onChangeValue,
        thumbContent = {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        if (value) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.surface
                    )
            )
        },

        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.Transparent,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            checkedBorderColor = Color.Transparent,

            uncheckedThumbColor = Color.Transparent,
            uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant,
            uncheckedBorderColor = Color.Black.copy(alpha = 0.14f)
        )
    )
}