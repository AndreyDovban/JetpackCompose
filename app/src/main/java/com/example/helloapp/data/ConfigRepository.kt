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

class ConfigRepository(context: Context) {

    // Стандартный SharedPreferences
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("tunnel_config_prefs", Context.MODE_PRIVATE)

    private val keyAlias = "tunnel_core_key"

    init {
        // Безопасный ключ внутри Android Keystore (на уровне железа)
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            val spec = android.security.keystore.KeyGenParameterSpec.Builder(
                keyAlias,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                        android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    // Сохранение: шифрование строки
    fun saveJsonConfig(jsonString: String) {
        try {
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(jsonString.toByteArray(Charsets.UTF_8))

            // Зашифрованный текст и IV (вектор инициализации), склеив их через ":"
            val resultToSave = Base64.encodeToString(iv, Base64.DEFAULT) + ":" +
                    Base64.encodeToString(encryptedBytes, Base64.DEFAULT)

            sharedPreferences.edit { putString("saved_json_config", resultToSave) }
        } catch (e: Exception) {
            android.util.Log.e("CRYPTO", "Ошибка сохранения", e)
        }
    }

    // Чтение: расшифровка строки и возвращение чистого JSON
    fun getJsonConfig(): String? {
        val savedData = sharedPreferences.getString("saved_json_config", null) ?: return null
        return try {
            val parts = savedData.split(":")
            val iv = Base64.decode(parts[0], Base64.DEFAULT)
            val encryptedBytes = Base64.decode(parts[1], Base64.DEFAULT)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            String(cipher.doFinal(encryptedBytes), Charsets.UTF_8)
        } catch (e: Exception) {
            android.util.Log.e("CRYPTO", "Ошибка чтения", e)
            null
        }
    }
}
