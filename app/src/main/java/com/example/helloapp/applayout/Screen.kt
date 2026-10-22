package com.example.helloapp.applayout

sealed class Screen(val index: Int) {
    data object Main : Screen(index = 0)
    data object Settings : Screen(index = 1)
    data object Profile : Screen(index = 2)
}