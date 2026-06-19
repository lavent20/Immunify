package com.example.immunify.data.model

enum class UrgencyLevel { URGENT, SOON, LATER }

data class VaksinJadwal(
    val id: String = "",
    val namaVaksin: String = "",
    val jenis: String = "",
    val dosis: String = "",
    val hariLagi: Int = 0,
    val urgencyLevel: UrgencyLevel = UrgencyLevel.LATER,
    val scheduledTimestamp: Long = 0L // Field baru untuk penanda di kalender (milliseconds)
)
