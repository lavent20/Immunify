package com.example.immunify.ui.presentation.riwayat_sewa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.DateRange

@Composable
fun RiwayatScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: RiwayatViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val riwayatList by viewModel.riwayatList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            viewModel.getRiwayat(username)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.blue2),
                        Color.White
                    )
                )
            )
    ) {
        HeaderRiwayat(navController)

        Text(
            text = "Menampilkan Riwayat Pesanan:",
            modifier = Modifier
                .alpha(0.6f)
                .padding(bottom = 12.dp, start = 20.dp),
            fontSize = 14.sp
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(id = R.color.blue1))
            }
        } else if (riwayatList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Belum ada riwayat pesanan vaksin.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(riwayatList) { riwayat ->
                    ItemRiwayatCard(
                        jenisVaksin = riwayat.jenisVaksin,
                        namaPenyedia = riwayat.namaPenyedia,
                        tanggalVaksin = riwayat.tanggalVaksin, // PERBAIKAN 1: Mengambil Tanggal Kalender
                        waktuDipesan = riwayat.waktuPesanan,   // Mengambil Timestamp pemesanan
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderRiwayat(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, bottom = 16.dp, top = 8.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali"
            )
        }
        Text(
            text = "Riwayat Imunisasi",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun ItemRiwayatCard(
    jenisVaksin: String,
    namaPenyedia: String,
    tanggalVaksin: String,
    waktuDipesan: String,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // <--- PERBAIKANNYA DI SINI
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.blue1).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Ikon Riwayat",
                    tint = colorResource(R.color.blue1),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jenisVaksin,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = namaPenyedia,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Tgl Imunisasi: $tanggalVaksin",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.blue1)
                )

                Text(
                    text = "Dipesan: $waktuDipesan",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 10.dp, top = 2.dp)
                )

                Text(
                    text = "Beri Ulasan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(id = R.color.blue1),
                    modifier = Modifier.clickable {
                        // navController.navigate(Route.REVIEW_PENYEDIA)
                    }
                )
            }
        }
    }
}