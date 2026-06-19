package com.example.immunify.ui.presentation.penyedia

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel
import com.example.immunify.util.Resource

@Composable
fun DaftarPenyediaScreen(
    navController: NavController,
    vaksinViewModel: CurrentVaksinViewModel,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(), // Gunakan HomeViewModel yang punya data real-time
    penyediaViewModel: PenyediaViewModel = hiltViewModel()
) {
    val currentNamaVaksin by vaksinViewModel.currentNamaVaksin.collectAsState()
    val clinicsResource by homeViewModel.clinicsList.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val filteredClinics by homeViewModel.filteredClinics.collectAsState()

    // Ambil data jika belum ada
    LaunchedEffect(Unit) {
        homeViewModel.updateClinicsByLocation()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(colorResource(id = R.color.blue2), Color.White)))
    ) {
        HeaderDaftarPenyedia(navController)

        // SEARCH BAR KLINIK
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { homeViewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            placeholder = { Text("Cari klinik atau alamat...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        if (currentNamaVaksin.isNotEmpty()) {
            Text(text = "Menampilkan Penyedia: $currentNamaVaksin", fontSize = 12.sp, modifier = Modifier.alpha(0.6f).padding(start = 20.dp, bottom = 12.dp))
        }

        when (clinicsResource) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = colorResource(id = R.color.blue1)) }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = clinicsResource.message ?: "Gagal memuat data", color = Color.Red, modifier = Modifier.padding(20.dp), textAlign = TextAlign.Center) }
            }
            is Resource.Success -> {
                if (filteredClinics.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = "Tidak ada hasil pencarian", color = Color.Gray) }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp), modifier = Modifier.fillMaxSize()) {
                        items(filteredClinics) { penyedia ->
                            PenyediaItemRow(
                                namaPenyedia = penyedia.namaLengkap,
                                jenisVaksin = penyedia.jenisVaksin,
                                dist = penyedia.pengalaman,
                                rate = (penyedia.poin / 10.0).toString(),
                                imgUrl = penyedia.imageUrl,
                                navController = navController,
                                userViewModel = userViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderDaftarPenyedia(navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
        IconButton(onClick = { navController.popBackStack() }) { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali") }
        Text(text = "Fasilitas Kesehatan Terdekat", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun PenyediaItemRow(
    namaPenyedia: String,
    jenisVaksin: String,
    dist: String,
    rate: String,
    imgUrl: String,
    navController: NavController,
    userViewModel: UserViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable {
            userViewModel.setNamaPenyedia(namaPenyedia)
            navController.navigate(Route.DETAIL_PENYEDIA)
        },
        border = BorderStroke(1.dp, colorResource(id = R.color.blue2).copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = imgUrl.ifBlank { "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=400" }, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)))
            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                Text(text = namaPenyedia, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$dist · ", fontSize = 12.sp, color = Color.Gray)
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                    Text(text = " $rate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = jenisVaksin, fontSize = 11.sp, color = colorResource(id = R.color.blue1), modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}
