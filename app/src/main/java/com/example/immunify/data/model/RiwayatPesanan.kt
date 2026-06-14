package com.example.immunify.model

data class RiwayatPesanan(
    val namaPenyedia: String = "",
    val jenisVaksin: String = "",
    val waktuPesanan: String = "",
    val tanggalVaksin: String = "",
    val status: String = "Menunggu"
)