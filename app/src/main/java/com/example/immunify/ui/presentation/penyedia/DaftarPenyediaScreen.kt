package com.example.immunify.ui.presentation.penyedia

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel

@Composable
fun DaftarPenyediaScreen(
    navController: NavController,
    vaksinViewModel: CurrentVaksinViewModel,
    userViewModel: UserViewModel,
    penyediaViewModel: PenyediaViewModel = hiltViewModel()
) {
    val currentNamaVaksin by vaksinViewModel.currentNamaVaksin.collectAsState()
    val penyediaList by penyediaViewModel.penyediaList.collectAsState()
    val isLoading by penyediaViewModel.isLoading.collectAsState()

    LaunchedEffect(currentNamaVaksin) {
        penyediaViewModel.getPenyedia(currentNamaVaksin)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorResource(id = R.color.blue2), Color.White)))
    ) {
        HeaderDaftarPenyedia(navController)

        Text(
            text = if (currentNamaVaksin.isEmpty()) "Menampilkan Semua Klinik Terdekat" else "Menampilkan Penyedia: $currentNamaVaksin",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.alpha(0.6f).padding(start = 20.dp, bottom = 12.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(id = R.color.blue1))
            }
        } else if (penyediaList.isEmpty()) {
            MataPenyediaKosongState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(penyediaList) { penyedia ->
                    // Logika keterangan dinamis
                    val keteranganStok = if (currentNamaVaksin.isNotEmpty()) {
                        val jumlahStok = penyedia.stok_vaksin[currentNamaVaksin] ?: 0
                        "Sisa Stok: $jumlahStok Dosis"
                    } else {
                        val totalVaksin = penyedia.stok_vaksin.size
                        "Menyediakan $totalVaksin Jenis Vaksin"
                    }

                    PenyediaItemRow(
                        namaPenyedia = penyedia.namaLengkap,
                        idPenyedia = penyedia.id,
                        keteranganStok = keteranganStok,
                        navController = navController,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderDaftarPenyedia(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
        }
        Text(text = "Penyedia Vaksin", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun PenyediaItemRow(
    namaPenyedia: String,
    idPenyedia: String, // <--- TAMBAHAN BARU DI SINI
    keteranganStok: String,
    navController: NavController,
    userViewModel: UserViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable {
                userViewModel.setNamaPenyedia(namaPenyedia)
                userViewModel.setIdPenyedia(idPenyedia) // <--- SIMPAN ID FASKES
                navController.navigate(Route.DETAIL_PENYEDIA)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Kotak Seragam (Mirip Daftar Vaksin)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8F7EF)), // LightMint
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = Color(0xFF1B7A4E), // Hijau Gelap
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = namaPenyedia, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    text = keteranganStok,
                    fontSize = 11.sp,
                    color = if (keteranganStok.contains(" 0 ")) Color.Red else Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun MataPenyediaKosongState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mohon maaf, belum ada fasilitas kesehatan atau instansi yang tersedia.",
            textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp
        )
    }
}