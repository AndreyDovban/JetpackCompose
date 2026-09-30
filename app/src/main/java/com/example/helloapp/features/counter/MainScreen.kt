package com.example.helloapp.features.counter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helloapp.ui.components.CardText
import com.example.helloapp.ui.components.MyCard
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    // Инжектируем нашу ViewModel. Она автоматически создается и сохраняется системой
    viewModel: CounterViewModel = viewModel()
) {
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        // Состояние скролла списка остается в UI-слое (ViewModel не должна управлять анимацией скролла)
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
                    onClick = { coroutineScope.launch { listState.scrollToItem(index = 299) } }
                )
            }

            // Отрисовываем элементы на основе размера массива из ViewModel
            items(
                count = viewModel.counts.size,
                key = { index -> "card_$index" }
            ) { index ->
                CounterCard(
                    index = index,
                    count = viewModel.counts[index], // Читаем состояние для конкретной карточки
                    onClick = { viewModel.incrementCounter(index) } // Вызываем бизнес-логику во ViewModel
                )
            }

            item {
                MyCard(
                    content = { CardText(text = "Вверх") },
                    onClick = { coroutineScope.launch { listState.scrollToItem(index = 0) } }
                )
            }
        }
    }
}
