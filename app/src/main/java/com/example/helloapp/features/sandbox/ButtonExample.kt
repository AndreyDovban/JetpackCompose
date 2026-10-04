package com.example.helloapp.features.sandbox

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.helloapp.ui.components.MyButton

@Composable
fun ButtonExample(

){
    MyButton(
        content = { Text("Click" ) },
        onClick = { Log.d("MY_TAG", "Кнопка была нажата!") }
    )
}