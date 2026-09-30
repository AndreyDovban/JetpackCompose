package com.example.helloapp.features.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.helloapp.ui.components.CardText
import com.example.helloapp.ui.components.MyCard

@Composable
fun SandboxScreen() {
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->

         Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             val message = remember{mutableStateOf("Hello METANIT.COM")}
             MyCard(
                 content = { CardText(text = message.value) },
                 onClick = { message.value = "Hello Work!" }
             )

        }
    }
}