package com.example.immunify.ui.presentation.vaksin


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class CurrentVaksinViewModel : ViewModel() {
    private val _currentVaksin = MutableStateFlow(1)
    val currentVaksin: StateFlow<Int> = _currentVaksin.asStateFlow()


    private val _currentNamaVaksin = MutableStateFlow("")
    val currentNamaVaksin: StateFlow<String> = _currentNamaVaksin.asStateFlow()


    fun setCurrentVaksin(value: Int) {
        _currentVaksin.value = value
    }


    fun setCurrentNamaVaksin(value: String) {
        _currentNamaVaksin.value = value
    }
}
