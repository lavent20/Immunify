package com.example.immunify.ui.presentation.penyedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel

@Composable
fun DetailPenyediaScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    vaksinViewModel: CurrentVaksinViewModel,
    detailViewModel: DetailPenyediaViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel() // Untuk mencocokkan ID Vaksin
) {
    val namaPenyedia by userViewModel.namaPenyedia.collectAsState()
    val penyedia by detailViewModel.penyedia.collectAsState()
    val vaksinMasterList by homeViewModel.vaksinList.collectAsState()

    // Memuat data saat layar dibuka (Jenis Vaksin dikosongkan karena kita ambil semua)
    LaunchedEffect(namaPenyedia) {
        detailViewModel.getDetailPenyedia("", namaPenyedia)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorResource(id = R.color.blue2), Color.White)))
    ) {
        HeaderDetailPenyedia(navController)

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        ) {
            // Gambar Profil Faskes
            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.foto_profil),
                    contentDescription = null, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Nama Klinik
                Text(
                    text = if (penyedia.namaLengkap.isEmpty()) namaPenyedia else penyedia.namaLengkap,
                    fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Alamat / Deskripsi
                Text(text = "Alamat / Lokasi", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Text(
                    text = if (penyedia.pengalaman.isEmpty()) "Belum ada informasi lokasi." else penyedia.pengalaman,
                    fontSize = 15.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))
                Text(text = "Vaksin yang Disediakan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))

                // Menampilkan Daftar Vaksin dari stok_vaksin
                if (penyedia.stok_vaksin.isEmpty()) {
                    Text("Klinik ini belum menyediakan vaksin.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    penyedia.stok_vaksin.forEach { (namaVaksin, stok) ->
                        if (stok > 0) {
                            // Mencari ID Vaksin dari Master Data agar halaman detail tidak tertukar
                            val targetVaksin = vaksinMasterList.find { it.item?.namaVaksin == namaVaksin }
                            val vaksinId = targetVaksin?.key?.toIntOrNull() ?: 1

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        // Arahkan pengguna ke halaman Informasi Vaksin
                                        vaksinViewModel.setCurrentVaksin(vaksinId)
                                        vaksinViewModel.setCurrentNamaVaksin(namaVaksin)
                                        navController.navigate(Route.DETAIL_VAKSIN)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.MedicalServices,
                                        contentDescription = null,
                                        tint = colorResource(R.color.blue1),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = namaVaksin, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Text(text = "Tersedia: $stok Dosis", fontSize = 13.sp, color = colorResource(R.color.blue1), fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDetailPenyedia(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
        }
        Text(text = "Profil Fasilitas Kesehatan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}