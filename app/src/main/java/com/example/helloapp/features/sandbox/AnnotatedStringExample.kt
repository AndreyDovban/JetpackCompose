package com.example.helloapp.features.sandbox

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun AnnotatedStringExample(){
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp)) {
                append("H")
            }
            append("ello ")

            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 23.sp, color = Color.Blue)) {
                append("METANIT.COM")
            }
        }
    )

    Text(
        buildAnnotatedString {
            append("Все мы сейчас желаем кушать, потому что утомились и уже четвертый час, но это")
            withStyle(
                ParagraphStyle(
                    lineHeight = 25.sp,
                    textIndent = TextIndent(firstLine = 30.sp, restLine = 8.sp)
                )
            ) {
                append("Все мы сейчас желаем кушать, потому что утомились и уже четвертый час, но это")
            }
        },
        fontSize = 22.sp)
}