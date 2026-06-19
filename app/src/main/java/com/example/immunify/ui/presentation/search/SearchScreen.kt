package com.example.immunify.ui.presentation.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    vaksinViewModel: CurrentVaksinViewModel,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredList by viewModel.filteredVaksinList.collectAsState()
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
        // --- 1. Header & Search Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp, start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    modifier = Modifier.size(28.dp)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) }, // Memanggil filter setiap mengetik
                placeholder = { Text(text = "Cari jenis vaksin...", fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Cari",
                        tint = Color.Gray
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(id = R.color.blue1),
                    unfocusedBorderColor = Color.Gray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // --- 2. Bagian Menampilkan Hasil ---
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(id = R.color.blue1))
            }
        } else if (searchQuery.isNotBlank() && filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(top = 100.dp), contentAlignment = Alignment.TopCenter) {
                Text(text = "Vaksin yang Anda cari tidak ditemukan.", color = Color.Gray, fontSize = 14.sp)
            }
        } else if (filteredList.isNotEmpty()) {
            Text(
                text = "Hasil Pencarian:",
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { itemData ->
                    val vaksin = itemData.item
                    val keyId = itemData.key ?: ""

                    // Ekstrak angka index dari key Firebase (misal: "vaksin 1" menjadi angka 1)
                    val indexStr = keyId.replace(Regex("[^0-9]"), "")
                    val vaksinIndex = if (indexStr.isNotEmpty()) indexStr.toInt() else 1

                    if (vaksin != null) {
                        ItemVaksinSearchCard(
                            namaVaksin = vaksin.namaVaksin,
                            jenis = vaksin.jenis,
                            onClick = {
                                // Simpan ke ViewModel Vaksin lalu lompat ke halaman detail
                                vaksinViewModel.setCurrentVaksin(vaksinIndex)
                                vaksinViewModel.setCurrentNamaVaksin(vaksin.namaVaksin)
                                navController.navigate(Route.DETAIL_VAKSIN)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemVaksinSearchCard(
    namaVaksin: String,
    jenis: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.matakuliah), // Nanti ganti gambar vaksin
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = namaVaksin,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = jenis,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}