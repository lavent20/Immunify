package com.example.immunify.data.repository

import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class PenyediaRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val penyediaRef = database.reference.child("penyedia_vaksin")

    fun getAllPenyedia(): Flow<Resource<List<PenyediaVaksin>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // PERBAIKAN: Memasukkan key (username) ke dalam val id
                val items = snapshot.children.mapNotNull {
                    val faskes = it.getValue(PenyediaVaksin::class.java)
                    faskes?.copy(id = it.key ?: "")
                }
                trySend(Resource.Success(items))
            }
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message ?: "Terjadi kesalahan"))
            }
        }
        penyediaRef.addValueEventListener(listener)
        awaitClose { penyediaRef.removeEventListener(listener) }
    }
}