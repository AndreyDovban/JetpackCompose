package com.example.helloapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.helloapp.features.tunnel.TunnelState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TunnelService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Служба onCreate: Инициализация канала и слушателя Go")
        createNotificationChannel()

        // [ПЕРЕЕХАЛ ИЗ INIT VIEWMODEL]: Слушаем обрывы связи прямо из Сервиса
        tunnel.Tunnel.setStatusListener { reason ->
            serviceScope.launch {
                Log.e(TAG, "Обрыв связи обнаружен в Go-ядре: $reason")
                updateState(TunnelState.Error("Соединение разорвано: $reason"))
                updateNotification("Соединение разорвано", "Ошибка: $reason")
                handleStopTunnel()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        Log.d(TAG, "Служба onStartCommand: Получен action = $action")

        when (action) {
            ACTION_START -> {
                val configJson = intent.getStringExtra(EXTRA_CONFIG_JSON)

                updateState(TunnelState.Connecting)
                val notification =
                    buildNotification("Туннель запускается", "Установка SSH соединения...")
                startForeground(NOTIFICATION_ID, notification)

                handleStartTunnel(configJson)
            }

            ACTION_STOP -> {
                handleStopTunnel()
            }
        }

        return START_STICKY
    }

    private fun handleStartTunnel(configJson: String?) {
        if (configJson.isNullOrBlank()) {
            updateState(TunnelState.Error("Пустая конфигурация"))
            updateNotification("Ошибка запуска", "Критическая ошибка: пустая конфигурация")
            return
        }

        serviceScope.launch {
            try {
                Log.d(TAG, "Запуск Go-ядра из фоновой корутины сервиса...")
                val result = tunnel.Tunnel.startTunnel(configJson)
                Log.d(TAG, "Результат работы Go-ядра: $result")

                if (result == "Туннель успешно запущен") {
                    updateState(TunnelState.Connected)
                    updateNotification(
                        "Туннель успешно работает", "Локальный прокси активен"
                    )
                } else {
                    updateState(TunnelState.Error(result ?: "Неизвестная ошибка"))
                    updateNotification("Ошибка подключения", result ?: "Неизвестная ошибка ядра")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Исключение при работе Go-ядра в сервисе", e)
                val errorMsg = e.localizedMessage ?: "Сбой SSH-соединения"
                updateState(TunnelState.Error(errorMsg))
                updateNotification("Ошибка подключения", errorMsg)
            }
        }
    }

    private fun handleStopTunnel() {
        serviceScope.launch {
            try {
                Log.d(TAG, "Остановка Go-ядра и освобождение портов...")
                tunnel.Tunnel.stopTunnel()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при вызове stopTunnel", e)
            } finally {
                updateState(TunnelState.Disconnected)
                Log.d(TAG, "Служба останавливает себя через stopSelf")
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Служба onDestroy: Полная отмена корутин")
        updateState(TunnelState.Disconnected)
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Состояние туннеля SSH", NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val notification = buildNotification(title, content)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "TUNNEL_SERVICE"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "tunnel_service_channel"

        const val ACTION_START = "com.example.helloapp.action.START_TUNNEL"
        const val ACTION_STOP = "com.example.helloapp.action.STOP_TUNNEL"
        const val EXTRA_CONFIG_JSON = "com.example.helloapp.extra.CONFIG_JSON"

        // Глобальный статус туннеля, к которому будет подключаться ViewModel
        private val _serviceStatus = MutableStateFlow<TunnelState>(TunnelState.Disconnected)
        val serviceStatus: StateFlow<TunnelState> = _serviceStatus.asStateFlow()

        // Вспомогательный метод для безопасного обновления статуса из любого потока
        private fun updateState(newState: TunnelState) {
            _serviceStatus.value = newState
        }
    }
}
