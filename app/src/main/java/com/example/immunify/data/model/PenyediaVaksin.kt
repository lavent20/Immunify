package com.example.immunify.model

data class PenyediaVaksin(
    val id: String = "",
    val namaLengkap: String = "",
    val jenisVaksin: String = "",
    val pengalaman: String = "", // Dipakai untuk jarak (misal: "1.2 km")
    val poin: Int = 0, // Dipakai untuk rating (misal: 45 untuk 4.5)
    val imageUrl: String = "",
    val alamat: String = "",
    val deskripsi: String = "Fasilitas kesehatan terpercaya melayani imunisasi rutin dan tambahan untuk keluarga Anda."
)
