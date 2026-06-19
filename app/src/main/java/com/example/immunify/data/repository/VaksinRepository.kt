package com.example.immunify.data.repository

import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.model.Vaksin
import com.example.immunify.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

// Menggunakan @Inject constructor agar FirebaseDatabase otomatis diinjeksi oleh Hilt
@Singleton
class VaksinRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    // Referensi langsung ke node "Vaksin"
    private val vaksinRef = database.reference.child("Vaksin")

    fun getVaksin(): Flow<Resource<List<VaksinModelResponse>>> = callbackFlow {
        trySend(Resource.Loading())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    val vaksin = it.getValue(Vaksin::class.java)
                    if (vaksin != null) {
                        VaksinModelResponse(vaksin, it.key)
                    } else {
                        null
                    }
                }
                trySend(Resource.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error("Gagal mengambil data: ${error.message}"))
            }
        }

        vaksinRef.addValueEventListener(listener)

        // SANGAT PENTING: Menghapus listener saat flow ditutup agar tidak memory leak
        awaitClose {
            vaksinRef.removeEventListener(listener)
        }
    }

    // Optimasi: Mengambil by ID langsung ke child-nya, bukan mengambil semua lalu di-filter
    fun getVaksinById(id: String): Flow<Resource<VaksinModelResponse>> = callbackFlow {
        trySend(Resource.Loading())

        val ref = vaksinRef.child(id)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val vaksin = snapshot.getValue(Vaksin::class.java)
                if (vaksin != null) {
                    trySend(Resource.Success(VaksinModelResponse(vaksin, snapshot.key)))
                } else {
                    trySend(Resource.Error("Vaksin tidak ditemukan"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}