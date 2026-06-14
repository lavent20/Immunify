// ui/presentation/home_screen/HomeViewModel.kt
package com.example.immunify.ui.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.immunify.data.model.Artikel
import com.example.immunify.data.model.UrgencyLevel
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.data.model.DiseaseInsight
import com.example.immunify.data.model.DataDummy
import com.example.immunify.data.model.NotifikasiItem
import com.example.immunify.data.model.TipeNotifikasi
import com.example.immunify.data.repository.PenyediaRepository
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository,
    private val penyediaRepository: PenyediaRepository
) : ViewModel() {

    private val _vaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val vaksinList: StateFlow<List<VaksinModelResponse>> = _vaksinList.asStateFlow()

    private val _masterVaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val masterVaksinList: StateFlow<List<VaksinModelResponse>> = _masterVaksinList.asStateFlow()

    // BARU: Variabel untuk menyimpan daftar Faskes (Klinik Terdekat)
    private val _penyediaList = MutableStateFlow<List<PenyediaVaksin>>(emptyList())
    val penyediaList: StateFlow<List<PenyediaVaksin>> = _penyediaList.asStateFlow()

    private val _jadwalVaksin = MutableStateFlow<List<VaksinJadwal>>(emptyList())
    val jadwalVaksin: StateFlow<List<VaksinJadwal>> = _jadwalVaksin.asStateFlow()

    private val _artikelList = MutableStateFlow<List<Artikel>>(emptyList())
    val artikelList: StateFlow<List<Artikel>> = _artikelList.asStateFlow()

    private val _diseaseInsights = MutableStateFlow<List<DiseaseInsight>>(emptyList())
    val diseaseInsights: StateFlow<List<DiseaseInsight>> = _diseaseInsights.asStateFlow()

    private val _notifikasiList = MutableStateFlow<List<NotifikasiItem>>(emptyList())
    val notifikasiList: StateFlow<List<NotifikasiItem>> = _notifikasiList.asStateFlow()

    init {
        getDaftarVaksin()
        getPenyediaKlinik() // BARU: Memanggil fungsi data faskes
        loadJadwalVaksinDummy()
        loadArtikelDummy()
        loadDiseaseInsightsDummy()
        generateNotifikasiDinamis()
    }

    // BARU: Fungsi untuk mengambil data penyedia/faskes
    private fun getPenyediaKlinik() = viewModelScope.launch {
        penyediaRepository.getAllPenyedia().collect { result ->
            if (result is Resource.Success) {
                _penyediaList.value = result.data ?: emptyList()
            }
        }
    }

    private fun getDaftarVaksin() = viewModelScope.launch {
        combine(
            vaksinRepository.getVaksin(),
            penyediaRepository.getAllPenyedia()
        ) { resultVaksin, resultPenyedia ->
            val masterVaksin = if (resultVaksin is Resource.Success) resultVaksin.data ?: emptyList() else emptyList()
            val listPenyedia = if (resultPenyedia is Resource.Success) resultPenyedia.data ?: emptyList() else emptyList()

            _masterVaksinList.value = masterVaksin

            val vaksinTersedia = mutableSetOf<String>()
            listPenyedia.forEach { penyedia ->
                penyedia.stok_vaksin.forEach { (namaVaksin, stok) ->
                    if (stok > 0) vaksinTersedia.add(namaVaksin)
                }
            }
            masterVaksin.filter { it.item?.namaVaksin in vaksinTersedia }
        }.collect { filteredList ->
            _vaksinList.value = filteredList
        }
    }

    private fun loadJadwalVaksinDummy() { _jadwalVaksin.value = DataDummy.jadwalVaksinList }
    private fun loadArtikelDummy() { _artikelList.value = DataDummy.artikelList }
    private fun loadDiseaseInsightsDummy() { _diseaseInsights.value = DataDummy.diseaseInsightsList }

    private fun generateNotifikasiDinamis() {
        // ... (Fungsi notifikasi biarkan sama persis seperti sebelumnya)
        val listNotif = mutableListOf<NotifikasiItem>()
        DataDummy.artikelList.firstOrNull()?.let { artikel ->
            listNotif.add(NotifikasiItem(id = UUID.randomUUID().toString(), judul = "Artikel Baru Tersedia! \uD83D\uDCDD", deskripsi = artikel.judul, waktuLabel = "Baru saja", tipe = TipeNotifikasi.ARTIKEL_BARU))
        }
        _notifikasiList.value = listNotif
    }
}