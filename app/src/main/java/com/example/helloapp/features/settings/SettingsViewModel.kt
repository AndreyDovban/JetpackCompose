package com.example.helloapp.features.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.helloapp.data.ConfigRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

// Простая модель данных, которая будет превращаться в JSON для Go
data class ConnectionConfig(
    val serverIp: String = "",
    val serverPort: String = "",
    val sshUser: String = "root",
    val keyText: String = "",
    val localProxyPort: String = "9090",
    val serverAliveInterval: Int = 15,

    val ipError: String? = null,
    val portError: String? = null,
    val userError: String? = null,
    val keyError: String? = null,
    val proxyError: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ConnectionConfig())
    val uiState: StateFlow<ConnectionConfig> = _uiState.asStateFlow()

    private val configRepository = ConfigRepository(application)

    init {
        loadConfigFromRepository()
    }

    private fun loadConfigFromRepository() {
        try {
            // Строка из зашифрованных настроек
            val savedJson = configRepository.getJsonConfig()

            if (savedJson != null) {
                // Парсинг JSON стандартным Android-пасером
                val jsonObject = JSONObject(savedJson)

                // Извлечение адреса сервера и разделение его на IP и Порт
                // На стороне Go они "${config.serverIp}:${config.serverPort}"
                val serverAddress = jsonObject.optString("server_address", "")
                val ipAndPort = serverAddress.split(":")

                val ip = ipAndPort.getOrNull(0) ?: "147.45.70.252"
                val port = ipAndPort.getOrNull(1) ?: "39221"

                // Извлечение полей, сопоставляя с ключами из generateJsonString() Go-ядра
                val user = jsonObject.optString("ssh_user", "root")

                // Возвращение экранированныч переносов строк приватного ключа обратно в нормальный вид
                val key = jsonObject.optString("key_text", "").replace("\\n", "\n")

                // Извлекение локальный порт прокси
                val localProxyAddress =
                    jsonObject.optString("local_proxy_address", "127.0.0.1:9090")
                val proxyPort = localProxyAddress.split(":").getOrNull(1) ?: "9090"

                val aliveInterval = jsonObject.optInt("server_alive_interval", 15)

                // Обновление состояние UI
                _uiState.update {
                    it.copy(
                        serverIp = ip,
                        serverPort = port,
                        sshUser = user,
                        keyText = key,
                        localProxyPort = proxyPort,
                        serverAliveInterval = aliveInterval
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("SETTINGS_VM", "Ошибка при чтении или парсинге конфига", e)
            // В случае ошибки парсинга UI останется с дефолтными значениями
        }
    }


    // Методы для обновления каждого поля по отдельности
    fun updateServerIp(value: String) {
        val filteredValue = value.filter { it.isDigit() || it == '.' }
        if (filteredValue.length <= 15) {
            _uiState.update { it.copy(serverIp = filteredValue, ipError = null) }
        }
    }

    fun updateServerPort(value: String) {
        val filteredValue = value.filter { it.isDigit() }
        if (filteredValue.length <= 5) {
            _uiState.update { it.copy(serverPort = filteredValue, portError = null) }
        }
    }

    fun updateSshUser(value: String) {
        if (value.length <= 32) {
            _uiState.update { it.copy(sshUser = value, userError = null) }
        }
    }

    fun updateKeyText(value: String) {
        _uiState.update { it.copy(keyText = value, keyError = null) }
    }

    fun updateLocalProxyPort(value: String) {
        val filteredValue = value.filter { it.isDigit() }
        if (filteredValue.length <= 5) {
            _uiState.update { it.copy(localProxyPort = filteredValue, proxyError = null) }
        }
    }

    // Функция валидации
    fun validateForm(): Boolean {
        val current = _uiState.value
        var isValid = true

        // 1. Проверка Ip сервера
        val ipRegex =
            "^(?:(?:25[0-5]|2[0-4][0-5]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-5]|[01]?[0-9][0-9]?)$".toRegex()

        if (current.serverIp.isBlank()) {
            _uiState.update { it.copy(ipError = "IP не может быть пустым") }
            isValid = false
        } else if (!current.serverIp.matches(ipRegex)) {
            _uiState.update { it.copy(ipError = "Неверный формат IPv4 адреса") }
            isValid = false
        }

        // 2. Проверка порта SSH (должно быть число от 1 до 65535)
        val portInt = current.serverPort.toIntOrNull()
        if (portInt == null || portInt !in 1..65535) {
            _uiState.update { it.copy(portError = "Неверный порт") }
            isValid = false
        }

        // 3. Проверка пользователя
        if (current.sshUser.isBlank()) {
            _uiState.update { it.copy(userError = "Укажите пользователя") }
            isValid = false
        }

        // 4. Проверка ключа/пароля
        if (current.keyText.isBlank()) {
            _uiState.update { it.copy(keyError = "Ключ или пароль обязателен") }
            isValid = false
        }

        // 5. Проверка локального порта прокси
        val proxyPortInt = current.localProxyPort.toIntOrNull()
        if (proxyPortInt == null || proxyPortInt !in 1..65535) {
            _uiState.update { it.copy(proxyError = "Неверный порт") }
            isValid = false
        }

        return isValid
    }

    fun generateJsonString(): String {
        val config = _uiState.value
        val fullServerAddress = "${config.serverIp}:${config.serverPort}"

        return """
            {
                "server_address": "$fullServerAddress",
                "ssh_user": "${config.sshUser}",
                "key_text": "${config.keyText.replace("\n", "\\n")}",
                "local_proxy_address": "127.0.0.1:${config.localProxyPort}",
                "server_alive_interval": ${config.serverAliveInterval}
            }
        """.trimIndent()
    }

    fun saveConfig() {
        if (validateForm()) {
            val jsonString = generateJsonString()
            configRepository.saveJsonConfig(jsonString)
        }
    }
}