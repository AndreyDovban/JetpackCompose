package com.example.helloapp.features.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.helloapp.ui.components.MyButton
import com.example.helloapp.ui.components.MyTextField

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(), onConfigSaved: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        // IP и Порт в одну горизонтальную строку для компактности
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Поле для IP
            MyTextField(
                value = state.serverIp,
                placeholder = "IP сервера",
                onValueChange = { viewModel.updateServerIp(it) },
                isError = state.ipError != null,
                supportingText = state.ipError?.let {
                    {
                        Text(
                            text = it, color = Color(0xFFFF1744)
                        )
                    }
                },
                modifier = Modifier.weight(2f)
            )

            // Поле для Порта
            MyTextField(
                value = state.serverPort,
                placeholder = "Порт",
                onValueChange = { viewModel.updateServerPort(it) },
                isError = state.portError != null,
                supportingText = state.portError?.let {
                    {
                        Text(
                            text = it, color = Color(0xFFFF1744)
                        )
                    }
                },

                modifier = Modifier.weight(1f)
            )
        }

        MyTextField(
            value = state.sshUser,
            placeholder = "Логин пользователя",
            onValueChange = { viewModel.updateSshUser(it) },
            isError = state.userError != null,
            supportingText = state.userError?.let {
                {
                    Text(
                        text = it, color = Color(0xFFFF1744)
                    )
                }
            }

        )

        MyTextField(
            value = state.keyText,
            placeholder = "Пароль или приватный ключ",
            onValueChange = { viewModel.updateKeyText(it) },
            isError = state.keyError != null,
            supportingText = state.keyError?.let {
                {
                    Text(
                        text = it, color = Color(0xFFFF1744)
                    )
                }
            })

        MyTextField(
            value = state.localProxyPort,
            placeholder = "Локальный порт",
            onValueChange = { viewModel.updateLocalProxyPort(it) },
            isError = state.proxyError != null,
            supportingText = state.proxyError?.let {
                {
                    Text(
                        text = it, color = Color(0xFFFF1744)
                    )
                }
            }

        )

        MyButton(
            onClick = {
                if (viewModel.validateForm()) {
                    viewModel.saveConfig()
                    onConfigSaved("Данные сохранены!")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text(text = "Сохранить конфигурацию")
        }
    }
}