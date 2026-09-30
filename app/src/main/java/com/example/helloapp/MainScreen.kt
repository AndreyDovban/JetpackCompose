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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
                MyCard(
                    content = { CardText(text = "Вниз") },
                    onClick = {
                        coroutineScope.launch{
                            listState.scrollToItem(index = 199)
                        }
                    }
                )
            }
            items(count = 200, key = { index -> "card_$index" }) {index ->
                MyCard(
                    content = {
                        CardText(text = "Clicks: $index: ")
                        AnimatedContent(
                            targetState = counts[index],
                            modifier = Modifier.widthIn(min = 40.dp),
                            transitionSpec = {
                                (slideInVertically { height -> -height } + fadeIn()) togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            },
                            label = "CounterAnimation"
                        ) { animatedCount -> CardText(text = "$animatedCount") }
                    },
                    onClick = {counts[index] += 1}
                )

            }
            item {
                MyCard(
                    content = { CardText(text = "Вверх") },
                    onClick = {coroutineScope.launch{listState.scrollToItem(index = 0)}}
                )
            }
        }
    }
}

