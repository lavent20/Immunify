package com.example.immunify.ui.presentation.vaksin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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

@Composable
fun DetailVaksinScreen(
    navController: NavController,
    currentVaksinViewModel: CurrentVaksinViewModel,
    viewModel: InformasiVaksinViewModel = hiltViewModel()
) {
    val currentVaksinId by currentVaksinViewModel.currentVaksin.collectAsState()

    LaunchedEffect(currentVaksinId) {
        viewModel.getVaksinDetail("vaksin $currentVaksinId")
    }

    val vaksinState by viewModel.vaksin
    val isLoading by viewModel.isLoading

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
        HeaderDetailVaksin(
            namaVaksin = if (isLoading) "Memuat..." else vaksinState.namaVaksin,
            navController = navController
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorResource(id = R.color.blue1))
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                DeskripsiVaksin(
                    namaVaksin = vaksinState.namaVaksin,
                    jenis = vaksinState.jenis,
                    deskripsi = vaksinState.deskripsi
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                ButtonCariPenyedia(
                    namaVaksin = vaksinState.namaVaksin,
                    currentVaksinViewModel = currentVaksinViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun HeaderDetailVaksin(
    namaVaksin: String,
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali"
            )
        }
        Text(
            text = namaVaksin,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun DeskripsiVaksin(
    namaVaksin: String,
    jenis: String,
    deskripsi: String
) {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.matakuliah),
                contentDescription = "Gambar Vaksin",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .shadow(8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = namaVaksin,
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = jenis,
                color = Color.DarkGray,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            Text(
                text = "Deskripsi",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = deskripsi,
                color = Color.DarkGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ButtonCariPenyedia(
    namaVaksin: String,
    currentVaksinViewModel: CurrentVaksinViewModel,
    navController: NavController
) {
    Button(
        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue1)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
            .height(54.dp),
        onClick = {
            currentVaksinViewModel.setCurrentNamaVaksin(namaVaksin)
            navController.navigate(Route.DAFTAR_PENYEDIA)
        }
    ) {
        Text(
            text = "Cari Fasilitas Kesehatan",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
