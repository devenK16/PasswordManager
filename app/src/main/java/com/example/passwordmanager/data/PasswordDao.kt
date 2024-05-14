package com.example.passwordmanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PasswordDao {
    @Insert
    suspend fun insert(password: Password)

    @Query("SELECT * FROM passwords")
    suspend fun getAllPasswords(): List<Password>

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE passwords SET accountType = :accountType, username = :username, password = :password WHERE id = :id")
    suspend fun update(id: Int, accountType: String, username: String, password: String)
}