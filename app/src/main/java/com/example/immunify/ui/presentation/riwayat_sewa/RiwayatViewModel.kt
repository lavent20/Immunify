package com.example.immunify.ui.presentation.riwayat_sewa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.immunify.data.local.JadwalDao
import com.example.immunify.data.local.LocalJadwal
import com.example.immunify.data.model.UrgencyLevel
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.util.NotificationWorker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val userViewModel: UserViewModel,
    private val jadwalDao: JadwalDao,
    application: Application
) : AndroidViewModel(application) {

    private val _riwayatList = MutableStateFlow<List<RiwayatPesanan>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatPesanan>> = _riwayatList.asStateFlow()

    private val _jadwalVaksin = MutableStateFlow<List<VaksinJadwal>>(emptyList())
    val jadwalVaksin: StateFlow<List<VaksinJadwal>> = _jadwalVaksin.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val workManager = WorkManager.getInstance(application)

    init {
        viewModelScope.launch {
            userViewModel.username.collect { name ->
                if (name.isNotEmpty() && name != "Pengguna") {
                    // LISTEN LOCAL (ROOM) - DIJAMIN MUNCUL MESKI FIREBASE ERROR
                    launch {
                        jadwalDao.getUpcomingJadwal(name).collectLatest { localList ->
                            val converted = localList.map { 
                                VaksinJadwal(
                                    id = it.id,
                                    namaVaksin = it.namaVaksin,
                                    jenis = it.jenis,
                                    dosis = it.dosis,
                                    scheduledTimestamp = it.scheduledTimestamp,
                                    urgencyLevel = UrgencyLevel.valueOf(it.urgencyLevel)
                                )
                            }
                            _jadwalVaksin.value = converted
                            android.util.Log.d("RiwayatViewModel", "Local Data Updated: ${converted.size}")
                        }
                    }
                    getUpcomingJadwal(name)
                    getRiwayat(name)
                }
            }
        }
    }

    fun getRiwayat(username: String) {
        if (username.isBlank() || username == "Pengguna") return
        _isLoading.value = true
        val riwayatRef = database.reference.child("users").child(username).child("riwayat")
        riwayatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(RiwayatPesanan::class.java) }
                _riwayatList.value = list.reversed()
                _isLoading.value = false
            }
            override fun onCancelled(error: DatabaseError) { _isLoading.value = false }
        })
    }

    fun getUpcomingJadwal(username: String) {
        if (username.isBlank() || username == "Pengguna") return
        val jadwalRef = database.reference.child("users").child(username).child("upcoming")
        jadwalRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(VaksinJadwal::class.java) }
                // Backup firebase data to Local
                viewModelScope.launch {
                    list.forEach { item ->
                        jadwalDao.insertJadwal(LocalJadwal(
                            id = item.id,
                            namaVaksin = item.namaVaksin,
                            jenis = item.jenis,
                            dosis = item.dosis,
                            scheduledTimestamp = item.scheduledTimestamp,
                            urgencyLevel = item.urgencyLevel.name,
                            username = username
                        ))
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addRiwayatManually(username: String, riwayat: RiwayatPesanan) {
        if (username.isBlank() || username == "Pengguna") return
        database.reference.child("users").child(username).child("riwayat").push().setValue(riwayat)
    }

    fun addUpcomingJadwal(username: String, jadwal: VaksinJadwal) {
        val safeUsername = if (username.isBlank() || username == "Pengguna") userViewModel.username.value else username
        if (safeUsername == "Pengguna") return

        viewModelScope.launch {
            // 1. SIMPAN KE LOCAL DULU (GARANSI MUNCUL DI TRACKER/HOME)
            jadwalDao.insertJadwal(LocalJadwal(
                id = jadwal.id,
                namaVaksin = jadwal.namaVaksin,
                jenis = jadwal.jenis,
                dosis = jadwal.dosis,
                scheduledTimestamp = jadwal.scheduledTimestamp,
                urgencyLevel = jadwal.urgencyLevel.name,
                username = safeUsername
            ))
            
            // 2. JADWALKAN NOTIFIKASI
            scheduleNotification(jadwal)
            
            // 3. COBA SIMPAN KE FIREBASE (SYINC)
            database.reference.child("users").child(safeUsername).child("upcoming")
                .child(jadwal.id)
                .setValue(jadwal)
                .addOnFailureListener {
                    android.util.Log.e("RiwayatViewModel", "Firebase Error: ${it.message}")
                }
        }
    }

    private fun scheduleNotification(jadwal: VaksinJadwal) {
        val currentTime = System.currentTimeMillis()
        val scheduleTime = jadwal.scheduledTimestamp
        var delay = (scheduleTime - TimeUnit.HOURS.toMillis(1)) - currentTime
        if (delay < 0) delay = 10000

        if (scheduleTime > currentTime) {
            val data = Data.Builder()
                .putString("title", "Pengingat Vaksin!")
                .putString("message", "Jangan lupa jadwal vaksin ${jadwal.namaVaksin} kamu di ${jadwal.jenis}.")
                .build()

            val notificationRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            workManager.enqueue(notificationRequest)
        }
    }
}
