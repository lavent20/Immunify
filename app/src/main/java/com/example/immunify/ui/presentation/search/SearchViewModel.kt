package com.example.immunify.ui.presentation.search

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
class SearchViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository
) : ViewModel() {

    // Menyimpan seluruh data vaksin asli dari database
    private val _allVaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())

    // Menyimpan teks query pencarian
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Menyimpan hasil filter yang akan ditampilkan ke layar
    private val _filteredVaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val filteredVaksinList: StateFlow<List<VaksinModelResponse>> = _filteredVaksinList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getAllVaksin()
    }

    private fun getAllVaksin() = viewModelScope.launch {
        vaksinRepository.getVaksin().collect { result ->
            when (result) {
                is Resource.Loading -> _isLoading.value = true
                is Resource.Success -> {
                    _allVaksinList.value = result.data ?: emptyList()
                    _isLoading.value = false
                }
                is Resource.Error -> _isLoading.value = false
            }
        }
    }

    // Fungsi ini dipanggil setiap kali pengguna mengetik huruf baru
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _filteredVaksinList.value = emptyList() // Sembunyikan hasil jika kosong
        } else {
            // Mencari kecocokan dari nama vaksin atau jenis kategorinya
            _filteredVaksinList.value = _allVaksinList.value.filter {
                it.item?.namaVaksin?.contains(query, ignoreCase = true) == true ||
                        it.item?.jenis?.contains(query, ignoreCase = true) == true
            }
        }
    }
}