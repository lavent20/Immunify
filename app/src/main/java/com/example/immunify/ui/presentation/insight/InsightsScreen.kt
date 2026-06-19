// ui/presentation/insight/InsightsScreen.kt
package com.example.immunify.ui.presentation.insight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.immunify.data.model.Artikel
import com.example.immunify.data.model.DiseaseInsight
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val artikelList by homeViewModel.artikelList.collectAsState()
    val diseaseList by homeViewModel.diseaseInsights.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Logika Filter Dinamis Berdasarkan Teks Pencarian
    val filteredArtikel = remember(searchQuery, artikelList) {
        if (searchQuery.isBlank()) {
            artikelList
        } else {
            artikelList.filter {
                it.judul.contains(searchQuery, ignoreCase = true) ||
                        it.tag.contains(searchQuery, ignoreCase = true) ||
                        it.konten.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val filteredDisease = remember(searchQuery, diseaseList) {
        if (searchQuery.isBlank()) {
            diseaseList
        } else {
            diseaseList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.overview.contains(searchQuery, ignoreCase = true) ||
                        it.keyFacts.any { fakta -> fakta.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    placeholder = { Text("Cari berita atau info penyakit...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MintGreen,
                        unfocusedBorderColor = Border
                    )
                )
            }

            // Pesan jika pencarian kosong atau tidak ditemukan data sama sekali
            if (filteredArtikel.isEmpty() && filteredDisease.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pencarian \"$searchQuery\" tidak ditemukan.\nCoba kata kunci lain.",
                            textAlign = TextAlign.Center,
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Seksi 1: Berita Terbaru
                if (filteredArtikel.isNotEmpty()) {
                    item { SectionTitle(title = "Berita Terbaru") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(filteredArtikel) { index, artikel ->
                                LatestUpdateCard(
                                    artikel = artikel,
                                    index = index,
                                    onClick = { navController.navigate("detailInsights/${artikel.id}") }
                                )
                            }
                        }
                    }
                }

                // Seksi 2: Kenali Penyakit!
                if (filteredDisease.isNotEmpty()) {
                    item { SectionTitle(title = "Kenali Penyakit!") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredDisease) { disease ->
                                DiseaseCard(disease = disease, onClick = {
                                    navController.navigate("detailInsights/${disease.id}")
                                })
                            }
                        }
                    }
                }

                // Seksi 3: Hidup Sehat (Hanya mengambil artikel dengan tag 'Hidup Sehat' atau filter terkait)
                val hidupSehatList = filteredArtikel.filter { it.tag.contains("Hidup Sehat", ignoreCase = true) || searchQuery.isNotBlank() }
                if (hidupSehatList.isNotEmpty()) {
                    item { SectionTitle(title = "Hidup Sehat") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(hidupSehatList) { index, artikel ->
                                LatestUpdateCard(
                                    artikel = artikel,
                                    index = index + 5,
                                    onClick = { navController.navigate("detailInsights/${artikel.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp)
    )
}

@Composable
fun LatestUpdateCard(artikel: Artikel, index: Int, onClick: () -> Unit) {
    val bgColors = listOf(LightMint, SoonAmberBg, UrgentRedBg)
    val iconColors = listOf(Color(0xFF1B7A4E), Color(0xFFE65100), Color(0xFFE53935))
    val chosenBg = bgColors.getOrElse(index % bgColors.size) { LightMint }
    val chosenIconColor = iconColors.getOrElse(index % iconColors.size) { Color(0xFF1B7A4E) }

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(chosenBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = chosenIconColor,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = artikel.judul,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${artikel.durasiMenit} mnt · ${artikel.waktuLabel}", fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
fun DiseaseCard(disease: DiseaseInsight, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(disease.imageBgColor))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = disease.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}