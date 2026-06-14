package com.example.immunify.ui.presentation.penyedia

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.data.repository.RiwayatRepository
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DetailPenyediaViewModel @Inject constructor(
    private val penyediaRepository: PenyediaRepository,
    private val riwayatRepository: RiwayatRepository
) : ViewModel() {

    private val _penyedia = MutableStateFlow(PenyediaVaksin())
    val penyedia: StateFlow<PenyediaVaksin> = _penyedia.asStateFlow()

    private val _orderSuccess = MutableStateFlow(false)
    val orderSuccess: StateFlow<Boolean> = _orderSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getDetailPenyedia(jenisVaksin: String, namaPenyedia: String) = viewModelScope.launch {
        penyediaRepository.getAllPenyedia().collect { result ->
            if (result is Resource.Success) {
                val match = result.data?.find { it.namaLengkap == namaPenyedia }
                if (match != null) {
                    _penyedia.value = match
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pesanVaksin(username: String, namaPenyedia: String, jenisVaksin: String, waktuSewa: String) = viewModelScope.launch {
        _isLoading.value = true
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)
        val waktuPesanan = "Waktu Vaksin: $waktuSewa | Dipesan: $formattedDateTime"

        val riwayat = RiwayatPesanan(
            namaPenyedia = namaPenyedia,
            jenisVaksin = jenisVaksin,
            waktuPesanan = waktuPesanan
        )

        riwayatRepository.tambahRiwayatPesanan(username, riwayat).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _orderSuccess.value = true
                }
                is Resource.Error -> _isLoading.value = false
                is Resource.Loading -> _isLoading.value = true
            }
        }
    }

    fun resetOrderState() {
        _orderSuccess.value = false
    }
}