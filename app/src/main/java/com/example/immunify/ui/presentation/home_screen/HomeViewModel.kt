package com.example.immunify.ui.presentation.home_screen

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.model.Artikel
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.data.model.DiseaseInsight
import com.example.immunify.data.model.DataDummy
import com.example.immunify.data.model.NotifikasiItem
import com.example.immunify.data.model.TipeNotifikasi
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.data.repository.RiwayatRepository
import com.example.immunify.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository,
    private val penyediaRepository: PenyediaRepository,
    private val riwayatRepository: RiwayatRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _artikelList = MutableStateFlow<List<Artikel>>(emptyList())
    val artikelList: StateFlow<List<Artikel>> = _artikelList.asStateFlow()

    private val _clinicsList = MutableStateFlow<Resource<List<PenyediaVaksin>>>(Resource.Loading())
    val clinicsList: StateFlow<Resource<List<PenyediaVaksin>>> = _clinicsList.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredClinics = MutableStateFlow<List<PenyediaVaksin>>(emptyList())
    val filteredClinics: StateFlow<List<PenyediaVaksin>> = _filteredClinics.asStateFlow()

    private val _diseaseInsights = MutableStateFlow<List<DiseaseInsight>>(emptyList())
    val diseaseInsights: StateFlow<List<DiseaseInsight>> = _diseaseInsights.asStateFlow()

    private val _notifikasiList = MutableStateFlow<List<NotifikasiItem>>(emptyList())
    val notifikasiList: StateFlow<List<NotifikasiItem>> = _notifikasiList.asStateFlow()

    init {
        loadArtikelDummy()
        loadDiseaseInsightsDummy()
        loadNotifikasiDummy()
    }

    @SuppressLint("MissingPermission")
    fun updateClinicsByLocation() {
        // CEK CACHE: Jika data sudah sukses di-load dan isinya tidak kosong, JANGAN fetch lagi (mencegah loading berulang saat back)
        val currentData = _clinicsList.value.data
        if (currentData != null && currentData.isNotEmpty()) {
            return
        }

        _clinicsList.value = Resource.Loading()
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    fetchRealtimeClinics(location)
                } else {
                    _clinicsList.value = Resource.Error("Lokasi tidak ditemukan. Pastikan GPS dan lokasi di HP aktif.")
                }
            }
            .addOnFailureListener { 
                _clinicsList.value = Resource.Error("Gagal mendeteksi lokasi: ${it.message}") 
            }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterClinics()
    }

    private fun filterClinics() {
        val allClinics = _clinicsList.value.data ?: emptyList()
        val query = _searchQuery.value
        if (query.isBlank()) {
            _filteredClinics.value = allClinics
        } else {
            _filteredClinics.value = allClinics.filter {
                it.namaLengkap.contains(query, ignoreCase = true) ||
                        it.alamat.contains(query, ignoreCase = true)
            }
        }
    }

    private fun fetchRealtimeClinics(location: Location) = viewModelScope.launch {
        penyediaRepository.getRealtimeNearbyClinics(location).collect { result ->
            _clinicsList.value = result
            if (result is Resource.Success) {
                filterClinics()
            }
        }
    }

    private fun loadArtikelDummy() {
        _artikelList.value = DataDummy.artikelList
    }

    private fun loadDiseaseInsightsDummy() {
        _diseaseInsights.value = DataDummy.diseaseInsightsList
    }

    private fun loadNotifikasiDummy() {
        // Simulasi notifikasi statis
        _notifikasiList.value = listOf(
            NotifikasiItem("1", "Jadwal Vaksin!", "Vaksin HPV Dosis 2 dijadwalkan besok.", "1 jam lalu", TipeNotifikasi.PENGINGAT_VAKSIN),
            NotifikasiItem("2", "Artikel Baru", "Mengenal manfaat vaksin BCG untuk bayi.", "3 jam lalu", TipeNotifikasi.ARTIKEL_BARU)
        )
    }
}
