package com.example.immunify.ui.presentation.riwayat_sewa

import androidx.lifecycle.ViewModel
import com.example.immunify.model.RiwayatPesanan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _riwayatList = MutableStateFlow<List<RiwayatPesanan>>(emptyList())
    val riwayatList: StateFlow<List<RiwayatPesanan>> = _riwayatList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Fungsi untuk mengambil riwayat berdasarkan username
    fun getRiwayat(username: String) {
        _isLoading.value = true
        val riwayatRef = database.reference.child("users").child(username).child("riwayat")

        riwayatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<RiwayatPesanan>()
                for (data in snapshot.children) {
                    val riwayat = data.getValue(RiwayatPesanan::class.java)
                    if (riwayat != null) {
                        list.add(riwayat)
                    }
                }
                // Dibalik agar riwayat terbaru muncul paling atas
                _riwayatList.value = list.reversed()
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }
    // Tambahkan di dalam kelas RiwayatViewModel
    fun tambahRiwayat(username: String, riwayat: RiwayatPesanan, onComplete: (Boolean) -> Unit) {
        val riwayatRef = database.reference.child("users").child(username).child("riwayat").push()
        riwayatRef.setValue(riwayat).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }
}