package com.example.helloapp.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ()->Unit
){


    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(10.dp)
            .defaultMinSize(minHeight = 1.dp, minWidth = 1.dp),
        shape = MaterialTheme.shapes.medium,

    ) {
        content()
    }
}