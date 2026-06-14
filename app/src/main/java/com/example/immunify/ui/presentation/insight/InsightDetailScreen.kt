package com.example.immunify.ui.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightDetailScreen(
    diseaseId: String?, // Parameter ID dari Navigasi
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val artikelList by homeViewModel.artikelList.collectAsState()
    val diseaseList by homeViewModel.diseaseInsights.collectAsState()

    // Cari tahu apakah ID yang dikirim milik Artikel Berita atau Info Penyakit
    val artikelBerita = artikelList.find { it.id == diseaseId }
    val infoPenyakit = diseaseList.find { it.id == diseaseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (artikelBerita != null) "Artikel Berita" else "Detail Penyakit", 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 18.sp,
                        color = TextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                if (artikelBerita != null) {
                    // ─── 1. TAMPILAN ARTIKEL BERITA BIASA ───
                    Text(
                        text = artikelBerita.tag.uppercase(),
                        color = MintGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = artikelBerita.judul,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${artikelBerita.durasiMenit} menit baca · ${artikelBerita.waktuLabel}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Border)
                    
                    // Isi artikel mengalir lurus tanpa kotak komponen penunjang
                    Text(
                        text = artikelBerita.konten,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(40.dp))

                } else if (infoPenyakit != null) {
                    // ─── 2. TAMPILAN KHUSUS KENALI PENYAKIT ───
                    Text(
                        text = infoPenyakit.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Kotak info Overview Penyakit
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // PERBAIKAN DI SINI: Mengubah 'Wood' menjadi 'fontWeight'
                            Text(text = "Overview", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = infoPenyakit.overview, 
                                fontSize = 13.sp, 
                                color = Color(0xFF333333), 
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Kotak poin-poin Key Facts Penyakit
                    Text(text = "Fakta Kunci (Key Facts)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            for (fakta in infoPenyakit.keyFacts) {
                                Row(
                                    modifier = Modifier.padding(vertical = 5.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Circle,
                                        contentDescription = null,
                                        tint = MintGreen,
                                        modifier = Modifier
                                            .padding(top = 6.dp, end = 10.dp)
                                            .size(6.dp)
                                    )
                                    Text(
                                        text = fakta, 
                                        fontSize = 13.sp, 
                                        color = Color(0xFF333333), 
                                        lineHeight = 19.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Berikan jarak ekstra di bawah sebelum tombol sticky/melayang bawah
                    Spacer(modifier = Modifier.height(100.dp))
                } else {
                    // Fallback jika tidak sengaja memuat ID kosong
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Konten tidak ditemukan", color = TextSecondary)
                    }
                }
            }

            // Tombol Set Appointment (Hanya muncul jika halaman menampilkan INFO PENYAKIT)
            if (infoPenyakit != null) {
                Button(
                    onClick = { /* Aksi menuju faskes */ },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008080)) // Warna hijau teal faskes
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Set Appointment", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}