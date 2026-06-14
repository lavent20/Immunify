package com.example.immunify.ui.presentation.vaksin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VaksinListViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository,
    private val penyediaRepository: PenyediaRepository // Tambahan untuk memantau stok klinik
) : ViewModel() {

    private val _vaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val vaksinList: StateFlow<List<VaksinModelResponse>> = _vaksinList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getDaftarVaksin()
    }

    fun getDaftarVaksin() = viewModelScope.launch {
        _isLoading.value = true

        // Menggabungkan data Master Vaksin dan Data Penyedia (Klinik)
        combine(
            vaksinRepository.getVaksin(),
            penyediaRepository.getAllPenyedia()
        ) { resultVaksin, resultPenyedia ->

            val masterVaksin = if (resultVaksin is Resource.Success) resultVaksin.data ?: emptyList() else emptyList()
            val listPenyedia = if (resultPenyedia is Resource.Success) resultPenyedia.data ?: emptyList() else emptyList()

            // 1. Kumpulkan semua nama vaksin yang stoknya > 0 dari SEMUA penyedia yang ada
            val vaksinTersedia = mutableSetOf<String>()
            listPenyedia.forEach { penyedia ->
                penyedia.stok_vaksin.forEach { (namaVaksin, stok) ->
                    if (stok > 0) {
                        vaksinTersedia.add(namaVaksin)
                    }
                }
            }

            // 2. Filter data master: Hanya tampilkan vaksin yang namanya ada di daftar 'vaksinTersedia'
            masterVaksin.filter { it.item?.namaVaksin in vaksinTersedia }

        }.collect { filteredList ->
            _vaksinList.value = filteredList
            _isLoading.value = false
        }
    }
}