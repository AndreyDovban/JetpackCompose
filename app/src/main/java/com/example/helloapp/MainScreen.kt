package com.example.helloapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        val counts = rememberSaveable(saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )) {
            mutableStateListOf(*Array(300) { 0 })
        }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()


        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    content = {
                        Text(
                            text = "Вниз",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W400)},
                    onClick = {coroutineScope.launch{
                        listState.scrollToItem(index = 199)
                    }
                    }
                )
            }
            items(count = 200, key = { index -> "card_$index" }) {index ->
                Card(
                    content = {
                        Text(
                            text = "Clicks: $index: ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W400,
                        )
                        AnimatedContent(
                            targetState = counts[index],
                            transitionSpec = {
                                (slideInVertically { height -> -height } + fadeIn()) togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            },
                            label = "CounterAnimation"
                        ) { animatedCount ->
                            Text(
                                text = "$animatedCount",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W500,
                            )
                        }
                    },
                    onClick = {counts[index] += 1}
                )

            }
            item {
                Card(
                    content = {
                        Text(
                            text = "Вверх",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.W500
                        )
                    },
                    onClick = {coroutineScope.launch{listState.scrollToItem(index = 0)}}
                )
            }
        }

    }
}

