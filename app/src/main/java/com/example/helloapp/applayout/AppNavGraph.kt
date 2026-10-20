package com.example.helloapp.applayout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.helloapp.features.counter.MainScreen
import com.example.helloapp.features.profile.ProfileScreen
import com.example.helloapp.features.sandbox.SandboxScreen

@Composable
fun AppNavGraph(
    currentScreen: Screen,
    innerPadding: PaddingValues,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier

    ) {
    AnimatedContent(
        targetState = currentScreen,
        modifier = modifier
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        transitionSpec = {
            val isMovingForward = targetState.index > initialState.index

            if (isMovingForward) {
                (slideInHorizontally { width -> width } + fadeIn()) togetherWith slideOutHorizontally { width -> -width } + fadeOut()
            } else {
                (slideInHorizontally { width -> -width } + fadeIn()) togetherWith slideOutHorizontally { width -> width } + fadeOut()
            } using SizeTransform(clip = false)
        },
        label = "ScreenNavigationAnimation"
    ) { screen ->
        when (screen) {
            Screen.Main -> MainScreen()
            Screen.Sandbox -> SandboxScreen()
            Screen.Profile -> ProfileScreen(
                onProfileSaved = { message -> onShowSnackbar(message) }
            )
        }
    }
}
