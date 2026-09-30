package com.example.helloapp.features.counter

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    // Состояние переехало сюда. Оно выживет даже при повороте экрана!
    val counts = mutableStateListOf(*Array(300) { 0 })

    // Бизнес-логика клика тоже теперь здесь
    fun incrementCounter(index: Int) {
        if (index in counts.indices) {
            counts[index] += 1
        }
    }
}
