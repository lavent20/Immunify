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
import com.example.immunify.data.repository.VaksinRepository
import com.example.immunify.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vaksinRepository: VaksinRepository
) : ViewModel() {

    private val _vaksinList = MutableStateFlow<List<VaksinModelResponse>>(emptyList())
    val vaksinList: StateFlow<List<VaksinModelResponse>> = _vaksinList.asStateFlow()

    private val _jadwalVaksin = MutableStateFlow<List<VaksinJadwal>>(emptyList())
    val jadwalVaksin: StateFlow<List<VaksinJadwal>> = _jadwalVaksin.asStateFlow()

    private val _artikelList = MutableStateFlow<List<Artikel>>(emptyList())
    val artikelList: StateFlow<List<Artikel>> = _artikelList.asStateFlow()

    private val _diseaseInsights = MutableStateFlow<List<DiseaseInsight>>(emptyList())
    val diseaseInsights: StateFlow<List<DiseaseInsight>> = _diseaseInsights.asStateFlow()

    private val _notifikasiList = MutableStateFlow<List<NotifikasiItem>>(emptyList())
    val notifikasiList: StateFlow<List<NotifikasiItem>> = _notifikasiList.asStateFlow()

    private val _vaksinSelesai = MutableStateFlow(8)
    val vaksinSelesai: StateFlow<Int> = _vaksinSelesai.asStateFlow()

    init {
        getDaftarVaksin()
        loadJadwalVaksinDummy()
        loadArtikelDummy()
        loadDiseaseInsightsDummy()
        generateNotifikasiDinamis()
    }

    private fun getDaftarVaksin() = viewModelScope.launch {
        vaksinRepository.getVaksin().collect { result ->
            when (result) {
                is Resource.Success -> _vaksinList.value = result.data ?: emptyList()
                else -> {}
            }
        }
    }

    private fun loadJadwalVaksinDummy() {
        _jadwalVaksin.value = DataDummy.jadwalVaksinList
    }

    private fun loadArtikelDummy() {
        _artikelList.value = DataDummy.artikelList
    }

    private fun loadDiseaseInsightsDummy() {
        _diseaseInsights.value = DataDummy.diseaseInsightsList
    }

    private fun generateNotifikasiDinamis() {
        val listNotif = mutableListOf<NotifikasiItem>()

        DataDummy.artikelList.firstOrNull()?.let { artikel ->
            listNotif.add(
                NotifikasiItem(
                    id = UUID.randomUUID().toString(),
                    judul = "Artikel Baru Tersedia! 📝",
                    deskripsi = artikel.judul,
                    waktuLabel = "Baru saja",
                    tipe = TipeNotifikasi.ARTIKEL_BARU
                )
            )
        }

        DataDummy.jadwalVaksinList.forEach { jadwal ->
            when (jadwal.hariLagi) {
                0 -> {
                    listNotif.add(
                        NotifikasiItem(
                            id = UUID.randomUUID().toString(),
                            judul = "Hari Ini Jadwal Vaksin Anda! 💉",
                            deskripsi = "Jangan lupa datang ke faskes untuk menerima vaksin ${jadwal.namaVaksin} (${jadwal.dosis}).",
                            waktuLabel = "Hari ini",
                            tipe = TipeNotifikasi.PENGINGAT_VAKSIN
                        )
                    )
                }
                1 -> {
                    listNotif.add(
                        NotifikasiItem(
                            id = UUID.randomUUID().toString(),
                            judul = "Mengingatkan: Besok Jadwal Vaksin! 🩺",
                            deskripsi = "Jadwal vaksin ${jadwal.namaVaksin} Anda tersisa 1 hari lagi. Persiapkan diri Anda.",
                            waktuLabel = "Kemarin",
                            tipe = TipeNotifikasi.PENGINGAT_VAKSIN
                        )
                    )
                }
                2 -> {
                    listNotif.add(
                        NotifikasiItem(
                            id = UUID.randomUUID().toString(),
                            judul = "Pengingat Vaksin Terdekat 🗓️",
                            deskripsi = "Vaksin ${jadwal.namaVaksin} dijadwalkan dalam 2 hari lagi.",
                            waktuLabel = "2 hari lalu",
                            tipe = TipeNotifikasi.PENGINGAT_VAKSIN
                        )
                    )
                }
            }
        }
        _notifikasiList.value = listNotif
    }
}