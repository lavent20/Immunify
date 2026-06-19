// data/model/Artikel.kt
package com.example.immunify.data.model

data class Artikel(
    val id: String,
    val judul: String,
    val tag: String,
    val durasiMenit: Int,
    val waktuLabel: String,
    val emoji: String,
    val konten: String = "" // Menyimpan isi paragraf berita biasa
)