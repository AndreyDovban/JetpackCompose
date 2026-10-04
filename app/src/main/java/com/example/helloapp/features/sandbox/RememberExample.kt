package com.example.helloapp.features.sandbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.helloapp.ui.components.CardText
import com.example.helloapp.ui.components.MyCard

@Composable
fun RememberExample (){
    var message by remember{mutableStateOf("Hello METANIT.COM")}
    MyCard(
        content = { CardText(text = message) },
        onClick = { message = "Hello Word"}
    )
}