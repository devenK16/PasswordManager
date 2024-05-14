package com.example.passwordmanager.data.repository

import android.content.Context
import com.example.passwordmanager.data.Password
import com.example.passwordmanager.data.PasswordDao
import com.example.passwordmanager.utils.EncryptionUtil

class PasswordRepository(private val passwordDao: PasswordDao, private val context: Context) {
    suspend fun addPassword(password: Password) {
        val encryptedPassword = password.password // Retrieve original password
        EncryptionUtil.encrypt(context, password.password, password.password)
        passwordDao.insert(password.copy(password = encryptedPassword))
    }

    suspend fun getPasswords(): List<Password> {
        return passwordDao.getAllPasswords().map {
            it.copy(password = EncryptionUtil.decrypt(context, it.password) ?: it.password)
        }
    }

    suspend fun deletePassword(id: Int) {
        passwordDao.deleteById(id)
    }

    suspend fun updatePassword(id: Int, accountType: String, username: String, password: String) {
        val encryptedPassword = password // Use the new password directly
        EncryptionUtil.encrypt(context, password, password)
        passwordDao.update(id, accountType, username, encryptedPassword)
    }
}
