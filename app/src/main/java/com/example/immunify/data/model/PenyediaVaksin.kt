package com.example.immunify.model

data class PenyediaVaksin(
    val id: String = "",
    val namaLengkap: String = "",
    val pengalaman: String = "",
    val poin: Int = 0,
    val stok_vaksin: Map<String, Int> = emptyMap()
)