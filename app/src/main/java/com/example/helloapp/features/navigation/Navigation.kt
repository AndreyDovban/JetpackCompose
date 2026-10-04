@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.helloapp.features.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helloapp.features.counter.MainScreen
import com.example.helloapp.features.sandbox.SandboxScreen

@Composable
fun Navigation(
    navViewModel: NavigationViewModel = viewModel()
) {
    Scaffold(
        containerColor = Color.Transparent, topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Main) }) {
                        Icon(Icons.Filled.Home,
                            contentDescription = "Главная",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Main) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Sandbox) }) {
                        Icon(Icons.Filled.Settings,
                            contentDescription = "Песочница",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Sandbox) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Profile) }) {
                        Icon(Icons.Filled.Person,
                            contentDescription = "Профиль",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                    }
                })
        }) { innerPadding ->
        AnimatedContent(
            targetState = navViewModel.currentScreen,
            modifier = Modifier
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
                Screen.Profile -> SandboxScreen()
            }
        }
    }
}