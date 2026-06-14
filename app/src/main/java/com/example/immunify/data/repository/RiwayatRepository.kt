package com.example.immunify.data.repository

import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.util.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RiwayatRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    // Fungsi untuk menambah riwayat pesanan (menggantikan Sewa Mentor)
    fun tambahRiwayatPesanan(username: String, riwayat: RiwayatPesanan): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            // Generate UUID unik untuk setiap pesanan
            val idPesanan = UUID.randomUUID().toString()

            // Referensi ke path: users/{username}/riwayat/{idPesanan}
            val riwayatRef = database.reference
                .child("users")
                .child(username)
                .child("riwayat")
                .child(idPesanan)

            // Menyimpan data ke Firebase menggunakan coroutine await()
            riwayatRef.setValue(riwayat).await()

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Gagal membuat pesanan vaksin"))
        }
    }
}