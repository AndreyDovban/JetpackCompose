package com.example.helloapp.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class ConfigRepository(private val context: Context) {

    // Ленивая инициализация SharedPreferences (вызовется при первом обращении)
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Ленивая загрузка AndroidKeyStore без блокировки init-блока
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
    }

    /**
     * Получает существующий ключ или безопасно генерирует новый, если его нет.
     * Метод должен вызываться на фоновом потоке (например, через Dispatchers.IO).
     */
    @Synchronized
    private fun getOrCreateSecretKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES, KEYSTORE_PROVIDER)
            val spec = android.security.keystore.KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                        android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }

        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    /**
     * Шифрует и сохраняет JSON-строку конфигурации.
     */
    fun saveJsonConfig(jsonString: String) {
        try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(jsonString.toByteArray(Charsets.UTF_8))

            // Кодируем в Base64 без переносов строк (NO_WRAP чище, чем DEFAULT)
            val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

            val resultToSave = "$ivBase64$DELIMITER$encryptedBase64"

            sharedPreferences.edit { putString(KEY_SAVED_CONFIG, resultToSave) }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Ошибка при шифровании и сохранении конфига", e)
        }
    }

    /**
     * Читает и расшифровывает сохраненную конфигурацию.
     */
    fun getJsonConfig(): String? {
        val savedData = sharedPreferences.getString(KEY_SAVED_CONFIG, null) ?: return null

        // Безопасная проверка: если строка пустая или не содержит разделитель, выходим сразу
        if (savedData.isBlank() || !savedData.contains(DELIMITER)) {
            android.util.Log.w(TAG, "Сохраненные данные повреждены или имеют неверный формат")
            return null
        }

        return try {
            val parts = savedData.split(DELIMITER)
            if (parts.size != 2) {
                android.util.Log.w(TAG, "Неверная структура зашифрованных данных")
                return null
            }

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)

            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_AUTH_TAG_LENGTH, iv)

            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Ошибка при расшифровке конфигурации", e)
            null
        }
    }

    companion object {
        private const val TAG = "CRYPTO_REPOSITORY"

        // Настройки хранилища данных
        private const val PREFS_NAME = "tunnel_config_prefs"
        private const val KEY_SAVED_CONFIG = "saved_json_config"
        private const val DELIMITER = ":"

        // Настройки криптографии
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "tunnel_core_key"
        private const val ALGORITHM_AES = "AES"
        private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_AUTH_TAG_LENGTH = 128
    }
}
