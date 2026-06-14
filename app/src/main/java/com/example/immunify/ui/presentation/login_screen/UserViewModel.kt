package com.example.immunify.ui.presentation.login_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _namaPenyedia = MutableStateFlow("")
    val namaPenyedia: StateFlow<String> = _namaPenyedia.asStateFlow()

    private val _idPenyedia = MutableStateFlow("")
    val idPenyedia: StateFlow<String> = _idPenyedia.asStateFlow()

    fun setUsername(value: String) { _username.value = value }
    fun setNamaPenyedia(value: String) { _namaPenyedia.value = value }
    fun setIdPenyedia(value: String) { _idPenyedia.value = value } // BARU
}