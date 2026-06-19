// data/model/NotifikasiItem.kt
package com.example.immunify.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class NotifikasiItem(
    val id: String,
    val judul: String,
    val deskripsi: String,
    val waktuLabel: String,
    val tipe: TipeNotifikasi
)

enum class TipeNotifikasi {
    ARTIKEL_BARU,
    PENGINGAT_VAKSIN
}