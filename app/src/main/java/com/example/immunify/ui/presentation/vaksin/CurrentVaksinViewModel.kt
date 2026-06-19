package com.example.immunify.ui.presentation.vaksin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CurrentVaksinViewModel @Inject constructor() : ViewModel() {
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