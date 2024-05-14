package com.example.passwordmanager.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object EncryptionUtil {
    private const val FILE_NAME = "passwords"
    private val MASTER_KEY_ALIAS = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
            FILE_NAME,
            MASTER_KEY_ALIAS,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun encrypt(context: Context, key: String, value: String): String {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        sharedPreferences.edit().putString(key, value).apply()
        return value // Return the original value
    }

    fun decrypt(context: Context, key: String): String? {
        val sharedPreferences = getEncryptedSharedPreferences(context)
        return sharedPreferences.getString(key, null)
    }
}