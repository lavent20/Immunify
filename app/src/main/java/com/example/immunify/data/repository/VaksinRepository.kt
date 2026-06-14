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

class VaksinRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val vaksinRef = database.reference.child("Vaksin")

    // --- FUNGSI 1: Mengambil semua daftar vaksin ---
    fun getVaksin(): Flow<Resource<List<VaksinModelResponse>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it ->
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

        awaitClose {
            vaksinRef.removeEventListener(listener)
        }
    }

    // --- FUNGSI 2: Mengambil SATU vaksin berdasarkan ID ---
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
                trySend(Resource.Error(error.message ?: "Terjadi kesalahan"))
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}