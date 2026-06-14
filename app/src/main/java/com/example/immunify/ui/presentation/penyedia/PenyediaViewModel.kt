package com.example.immunify.ui.presentation.penyedia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PenyediaViewModel @Inject constructor(
    private val penyediaRepository: PenyediaRepository
) : ViewModel() {

    private val _penyediaList = MutableStateFlow<List<PenyediaVaksin>>(emptyList())
    val penyediaList: StateFlow<List<PenyediaVaksin>> = _penyediaList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getPenyedia(namaVaksin: String) = viewModelScope.launch {
        _isLoading.value = true
        penyediaRepository.getAllPenyedia().collect { result ->
            if (result is Resource.Success) {
                val listData = result.data ?: emptyList()
                if (namaVaksin.isNotEmpty()) {
                    // Hanya tampilkan klinik yang stok vaksin spesifiknya lebih dari 0
                    _penyediaList.value = listData.filter { faskes ->
                        val stok = faskes.stok_vaksin[namaVaksin] ?: 0
                        stok > 0
                    }
                } else {
                    // Tampilkan semua klinik jika tidak ada filter (dari menu Home)
                    _penyediaList.value = listData
                }
            }
            _isLoading.value = false
        }
    }
}