package com.example.helloapp.features.sandbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.helloapp.ui.components.MyTextField

@Composable
fun TextFieldExample(){
    var text by remember { mutableStateOf("Hello Work") }

    MyTextField(
        value = text,
        placeholder = "Введите ваше имя..."
    ){ text = it }
}