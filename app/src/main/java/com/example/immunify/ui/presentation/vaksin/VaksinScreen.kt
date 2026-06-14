package com.example.immunify.ui.presentation.vaksin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.immunify.ui.theme.LightMint
import com.example.immunify.ui.theme.TextPrimary
import com.example.immunify.ui.theme.TextSecondary

@Composable
fun VaksinScreen(
    navController: NavController,
    currentVaksinViewModel: CurrentVaksinViewModel,
    viewModel: VaksinListViewModel = hiltViewModel()
) {
    val vaksinList by viewModel.vaksinList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
        HeaderVaksin(navController)

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorResource(id = R.color.blue1))
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(vaksinList.size) { index ->
                    val itemData = vaksinList[index].item
                    if (itemData != null) {
                        DaftarVaksinCard(
                            namaVaksin = itemData.namaVaksin,
                            jenis = itemData.jenis,
                            navController = navController,
                            currentVaksinViewModel = currentVaksinViewModel,
                            vaksinIndex = index,
                            id = itemData.id // Mengirim ID yang benar
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderVaksin(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 6.dp, end = 10.dp, bottom = 6.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = "Daftar Vaksin",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun DaftarVaksinCard(
    namaVaksin: String,
    jenis: String,
    navController: NavController,
    currentVaksinViewModel: CurrentVaksinViewModel,
    vaksinIndex: Int,
    id: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable {
                // Logika navigasi yang benar: Mengirim ID yang tepat
                currentVaksinViewModel.setCurrentVaksin(id.toIntOrNull() ?: vaksinIndex)
                currentVaksinViewModel.setCurrentNamaVaksin(namaVaksin)
                navController.navigate(Route.DETAIL_VAKSIN)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kotak Ikon Seragam
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = Color(0xFF1B7A4E),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Nama dan Jenis Vaksin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = namaVaksin,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = jenis,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}