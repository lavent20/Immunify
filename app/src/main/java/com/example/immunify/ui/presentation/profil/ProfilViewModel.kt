package com.example.immunify.ui.presentation.profil

import androidx.lifecycle.ViewModel
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
class ProfilViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _status = MutableStateFlow("Pengguna")
    val status: StateFlow<String> = _status.asStateFlow()

    fun checkUserStatus(username: String) {
        if (username.isEmpty()) return

        val userRef = database.reference.child("users").child(username)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isPenyedia = snapshot.child("mentor").value.toString() == "true" ||
                        snapshot.child("isPenyedia").value.toString() == "true"

                _status.value = if (isPenyedia) "Fasilitas Kesehatan" else "Pengguna"
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}