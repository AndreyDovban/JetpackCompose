package com.example.helloapp.features.sandbox

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helloapp.ui.components.MyIconButton

@Composable
fun IconButtonExample(){
    var isChecked by remember { mutableStateOf(false) }

    MyIconButton(
        onClick = {isChecked = !isChecked}
    ){
        Icon(
            Icons.Filled.Home,
            contentDescription = "Информация о приложении",
            modifier = Modifier.size(48.dp),
            tint = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        )
    }
}