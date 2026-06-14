package com.example.immunify.ui.presentation.vaksin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaksinListViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository
) : ViewModel() {

    private val _vaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val vaksinList: StateFlow<List<VaksinModelResponse>> = _vaksinList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getDaftarVaksin()
    }

    fun getDaftarVaksin() = viewModelScope.launch {
        vaksinRepository.getVaksin().collect { result ->
            when (result) {
                is Resource.Loading -> _isLoading.value = true
                is Resource.Success -> {
                    _vaksinList.value = result.data ?: emptyList()
                    _isLoading.value = false
                }
                is Resource.Error -> _isLoading.value = false
            }
        }
    }
}