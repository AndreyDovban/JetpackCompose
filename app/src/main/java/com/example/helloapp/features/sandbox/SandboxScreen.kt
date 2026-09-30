package com.example.helloapp.features.sandbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             MyCard(
                 content = { CardText(text = "Добро пожаловать в Песочницу!") }
             )

        }
    }
}