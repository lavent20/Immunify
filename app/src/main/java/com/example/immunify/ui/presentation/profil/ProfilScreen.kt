package com.example.immunify.ui.presentation.profil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.data.model.DataDummy
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import com.example.immunify.ui.theme.*
import com.example.immunify.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    profilViewModel: ProfilViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    riwayatViewModel: RiwayatViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val status by profilViewModel.status.collectAsState()
    val riwayatList by riwayatViewModel.riwayatList.collectAsState()
    val upcomingJadwal by riwayatViewModel.jadwalVaksin.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Completed")

    // Sinkronisasi data asli pengguna
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            profilViewModel.checkUserStatus(username)
            riwayatViewModel.getRiwayat(username)
            riwayatViewModel.getUpcomingJadwal(username)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Profil", fontWeight = FontWeight.Bold, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorResource(id = R.color.blue2), Color.White),
                        startY = 0f,
                        endY = 400f
                    )
                )
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.foto_profil),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = username.ifEmpty { "Pengguna" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = status,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Route.FORM_PENYEDIA) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = MintGreen
                        )
                        Text(
                            text = if (status == "Pengguna") "Daftar Sebagai Fasilitas Kesehatan" else "Profil Fasilitas Kesehatan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MintGreen,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedTab == 0) {
                if (upcomingJadwal.isEmpty()) {
                    item {
                        EmptyStateViewProfil(pesan = "Belum ada jadwal vaksinasi mendatang.")
                    }
                } else {
                    items(upcomingJadwal) { vaksin ->
                        RecommendedVaccineCard(vaksin = vaksin)
                    }
                }
            } else {
                if (riwayatList.isEmpty()) {
                    item {
                        EmptyStateViewProfil(pesan = "Belum ada riwayat vaksinasi yang diselesaikan.")
                    }
                } else {
                    items(riwayatList) { riwayat ->
                        CompletedVaccineCard(riwayat = riwayat)
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedVaccineCard(vaksin: VaksinJadwal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color(0xFF1B7A4E))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vaksin.namaVaksin, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${vaksin.dosis} · ${vaksin.jenis}", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun CompletedVaccineCard(riwayat: RiwayatPesanan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StatBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1E88E5))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = riwayat.jenisVaksin, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = riwayat.namaPenyedia, fontSize = 12.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun EmptyStateViewProfil(pesan: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = pesan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
