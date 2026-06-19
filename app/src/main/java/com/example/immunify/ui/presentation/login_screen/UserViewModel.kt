package com.example.immunify.ui.presentation.login_screen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel {
    private val _username = MutableStateFlow("Pengguna")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _namaPenyedia = MutableStateFlow("")
    val namaPenyedia: StateFlow<String> = _namaPenyedia.asStateFlow()

    fun setUsername(value: String) {
        if (value.isNotEmpty()) {
            _username.value = value
        }
    }

    fun setNamaPenyedia(value: String) {
        _namaPenyedia.value = value
    }
}
