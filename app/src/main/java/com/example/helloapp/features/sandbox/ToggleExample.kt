package com.example.helloapp.features.sandbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.helloapp.ui.components.MyToggle

@Composable
fun ToggleExample(){
    var value by remember { mutableStateOf(false) }

    MyToggle(
        value = value
    ){ value = it }
}