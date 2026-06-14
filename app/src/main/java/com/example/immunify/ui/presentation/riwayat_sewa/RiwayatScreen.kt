package com.example.immunify.ui.presentation.riwayat_sewa

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
import com.example.immunify.ui.presentation.login_screen.UserViewModel

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
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        HeaderRiwayat(navController)

        Text(
            text = "Menampilkan Riwayat Pesanan:",
            modifier = Modifier
                .alpha(0.6f)
                .padding(bottom = 12.dp, start = 8.dp),
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
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(riwayatList) { riwayat ->
                    ItemRiwayatCard(
                        jenisVaksin = riwayat.jenisVaksin,
                        namaPenyedia = riwayat.namaPenyedia,
                        waktuPesanan = riwayat.waktuPesanan,
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
            .padding(bottom = 16.dp, top = 8.dp)
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
    waktuPesanan: String,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_immunify), // Nanti ganti dengan ikon vaksin
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = jenisVaksin,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = namaPenyedia,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Tanggal: $waktuPesanan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol untuk memberi ulasan ke faskes tersebut
            Text(
                text = "Beri Ulasan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(id = R.color.blue1),
                modifier = Modifier.clickable {
                    navController.navigate(Route.REVIEW_PENYEDIA) // Sebelumnya Route.REVIEWMENTOR
                }
            )
        }
    }
}