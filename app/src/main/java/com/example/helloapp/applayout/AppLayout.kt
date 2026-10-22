@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.helloapp.applayout


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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel



@Composable
fun AppLayout(
    navViewModel: AppViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Прослушивание событий
    LaunchedEffect(navViewModel.uiEvents) {
        navViewModel.uiEvents.collect { event ->
            when (event) {
                is AppUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message, withDismissAction = event.hasDismissAction
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Main) }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "Главная",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Main) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Settings) }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Песочница",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Settings) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = { navViewModel.navigateTo(Screen.Profile) }) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Профиль",
                            modifier = Modifier.size(48.dp),
                            tint = if (navViewModel.currentScreen == Screen.Profile) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                })
        }) { innerPadding ->
        AppNavGraph(
            currentScreen = navViewModel.currentScreen,
            innerPadding = innerPadding,
            onShowSnackbar = { message -> navViewModel.showSnackbar(message) }
        )
    }
}

