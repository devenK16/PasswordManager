package com.example.passwordmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.data.Password
import com.example.passwordmanager.data.repository.PasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val repository: PasswordRepository
) : ViewModel() {
    private val _passwords = MutableStateFlow<List<Password>>(emptyList())
    val passwords: StateFlow<List<Password>> = _passwords

    init {
        loadPasswords()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            _passwords.value = repository.getPasswords()
        }
    }

    fun addPassword(accountType: String, username: String, password: String) {
        viewModelScope.launch {
            repository.addPassword(Password(accountType = accountType, username = username, password = password))
            loadPasswords()
        }
    }

    fun deletePassword(id: Int) {
        viewModelScope.launch {
            repository.deletePassword(id)
            loadPasswords()
        }
    }

    fun updatePassword(id: Int, accountType: String, username: String, password: String) {
        viewModelScope.launch {
            repository.updatePassword(id, accountType, username, password)
            loadPasswords()
        }
    }
}