// data/model/VaksinJadwal.kt
package com.example.immunify.data.model

enum class UrgencyLevel { URGENT, SOON, LATER }

data class VaksinJadwal(
    val id: String,
    val namaVaksin: String,
    val jenis: String,
    val dosis: String,
    val hariLagi: Int,
    val urgencyLevel: UrgencyLevel
)