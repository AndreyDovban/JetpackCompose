package com.example.helloapp.features.navigation

sealed class Screen( val index: Int) {
    object Main : Screen( index = 0)
    object Sandbox : Screen( index = 1)
    object Profile : Screen( index = 2)
}