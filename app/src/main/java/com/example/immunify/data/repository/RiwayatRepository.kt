package com.example.immunify.data.repository

import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiwayatRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    // Fungsi untuk menambah riwayat pesanan
    fun tambahRiwayatPesanan(username: String, riwayat: RiwayatPesanan): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val idPesanan = UUID.randomUUID().toString()
            val riwayatRef = database.reference
                .child("users")
                .child(username)
                .child("riwayat")
                .child(idPesanan)

            riwayatRef.setValue(riwayat).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal membuat pesanan vaksin"))
        }
    }

    /**
     * PEMBARUAN: Mengambil data riwayat vaksinasi pengguna secara realtime untuk Profile Screen
     */
    fun getRiwayatPesanan(username: String): Flow<Resource<List<RiwayatPesanan>>> = callbackFlow {
        trySend(Resource.Loading())

        val riwayatRef = database.reference
            .child("users")
            .child(username)
            .child("riwayat")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    it.getValue(RiwayatPesanan::class.java)
                }
                trySend(Resource.Success(items))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message ?: "Gagal memuat riwayat vaksinasi"))
            }
        }

        riwayatRef.addValueEventListener(listener)

        awaitClose {
            riwayatRef.removeEventListener(listener)
        }
    }
}