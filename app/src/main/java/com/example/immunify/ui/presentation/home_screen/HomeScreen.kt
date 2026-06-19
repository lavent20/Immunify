package com.example.immunify.ui.presentation.home_screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.immunify.data.model.Artikel
import com.example.immunify.data.model.UrgencyLevel
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import com.example.immunify.ui.theme.*
import com.example.immunify.util.Resource

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModelUser: UserViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    riwayatViewModel: RiwayatViewModel = hiltViewModel()
) {
    val username by viewModelUser.username.collectAsState()
    val artikelList by homeViewModel.artikelList.collectAsState()
    val clinicsResource by homeViewModel.clinicsList.collectAsState()
    
    // Gunakan data asli dari Firebase untuk jadwal mendatang
    val jadwalVaksin by riwayatViewModel.jadwalVaksin.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            homeViewModel.updateClinicsByLocation()
        }
    }

    LaunchedEffect(username) {
        locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        if (username.isNotEmpty()) {
            riwayatViewModel.getUpcomingJadwal(username)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BackgroundPage),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item { HomeHeader(nama = username, navController = navController) }

        item { SectionHeader(title = "Jadwal Imunisasi Mendatang", onViewAll = { navController.navigate(Route.TRACKER) }) }

        if (jadwalVaksin.isEmpty()) {
            item { 
                Text(
                    text = "Belum ada jadwal vaksinasi mendatang.", 
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    fontSize = 13.sp, color = TextSecondary
                ) 
            }
        } else {
            // Ambil 3 jadwal terdekat saja untuk Home
            val latestJadwal = jadwalVaksin.take(3)
            items(latestJadwal.size, key = { index -> "home_jadwal_${latestJadwal[index].id}" }) { index ->
                VaksinJadwalCard(jadwal = latestJadwal[index], navController = navController)
            }
        }

        item { SectionHeader(title = "Klinik Terdekat", onViewAll = { navController.navigate(Route.DAFTAR_PENYEDIA) }) }
        
        item { FaskesTerdekatRow(clinicsResource = clinicsResource, navController = navController, userViewModel = viewModelUser) }

        item { SectionHeader(title = "Edukasi Vaksin", onViewAll = { navController.navigate(Route.INSIGHTS) }) }

        val totalArtikel = artikelList.take(5)
        items(totalArtikel.size, key = { index -> "artikel_${totalArtikel[index].id}" }) { index ->
            ArtikelCard(artikel = totalArtikel[index], onClick = { navController.navigate("detailInsights/${totalArtikel[index].id}") })
        }
    }
}

@Composable
fun HomeHeader(nama: String, navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Selamat pagi", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Text(text = nama.ifBlank { "Pengguna" }, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        }
        Box(
            modifier = Modifier.size(42.dp).clip(CircleShape).background(Color.White).clickable { navController.navigate("notification") },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifikasi", tint = TextPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Lihat Semua", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MintGreen, modifier = Modifier.clickable { onViewAll() })
    }
}

@Composable
fun VaksinJadwalCard(jadwal: VaksinJadwal, navController: NavController) {
    val (leftColor, badgeBg, badgeText, iconVector) = when (jadwal.urgencyLevel) {
        UrgencyLevel.URGENT -> Quadruple(UrgentRed, UrgentRedBg, Color(0xFFE53935), Icons.Default.Warning)
        UrgencyLevel.SOON   -> Quadruple(SoonAmber, SoonAmberBg, Color(0xFFE65100), Icons.Default.Info)
        UrgencyLevel.LATER  -> Quadruple(MintGreen, LightMint, Color(0xFF1B7A4E), Icons.Default.CheckCircle)
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable { navController.navigate(Route.TRACKER) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.width(4.dp).height(72.dp).background(leftColor, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)))
            Spacer(Modifier.width(12.dp))
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(badgeBg), contentAlignment = Alignment.Center) {
                Icon(imageVector = iconVector, contentDescription = null, tint = badgeText, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jadwal.namaVaksin, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "${jadwal.dosis} · ${jadwal.jenis}", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
            }
            Surface(shape = RoundedCornerShape(20.dp), color = badgeBg, modifier = Modifier.padding(end = 16.dp)) {
                Text(text = "Schedule", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeText)
            }
        }
    }
}

@Composable
fun FaskesTerdekatRow(clinicsResource: Resource<List<PenyediaVaksin>>, navController: NavController, userViewModel: UserViewModel) {
    val bgColors = listOf(LightMint, StatBlueBg, SoonAmberBg)
    val iconColors = listOf(Color(0xFF1B7A4E), Color(0xFF1E88E5), Color(0xFFE65100))

    when (clinicsResource) {
        is Resource.Loading -> { Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MintGreen, strokeWidth = 3.dp) } }
        is Resource.Error -> { Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), contentAlignment = Alignment.Center) { Text(text = clinicsResource.message ?: "Gagal memuat data klinik", color = UrgentRed, fontSize = 12.sp, fontWeight = FontWeight.Medium) } }
        is Resource.Success -> {
            val clinics = clinicsResource.data ?: emptyList()
            if (clinics.isEmpty()) { Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) { Text(text = "Tidak ada klinik tersedia", fontSize = 12.sp, color = TextSecondary) } }
            else {
                LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(clinics.size) { index ->
                        FaskesCard(faskes = clinics[index], bgColor = bgColors[index % bgColors.size], iconColor = iconColors[index % bgColors.size], navController = navController, userViewModel = userViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun FaskesCard(faskes: PenyediaVaksin, bgColor: Color, iconColor: Color, navController: NavController, userViewModel: UserViewModel) {
    val namaKlinik = faskes.namaLengkap.ifBlank { "Klinik Immunify" }
    val jarakKlinik = faskes.pengalaman.ifBlank { "1.2 km" }
    val ratingKlinik = (faskes.poin / 10.0).toString()
    val alamatKlinik = faskes.alamat.ifBlank { "Lokasi terdeteksi" }

    Card(
        modifier = Modifier.width(180.dp).clickable { 
            userViewModel.setNamaPenyedia(namaKlinik)
            navController.navigate(Route.DETAIL_PENYEDIA) 
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(96.dp)) {
                AsyncImage(model = faskes.imageUrl.ifBlank { "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=400" }, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.05f)))
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(text = namaKlinik, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = alamatKlinik, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MintGreen, modifier = Modifier.size(13.dp))
                    Text(text = jarakKlinik, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                    Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Border))
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(13.dp))
                    Text(text = ratingKlinik, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun ArtikelCard(artikel: Artikel, onClick: () -> Unit) {
    val artikelBgColors = listOf(LightMint, SoonAmberBg, UrgentRedBg)
    val artikelIconColors = listOf(Color(0xFF1B7A4E), Color(0xFFE65100), Color(0xFFE53935))
    val index = artikel.id.toIntOrNull()?.minus(1) ?: 0
    val bgColor = artikelBgColors.getOrElse(index) { LightMint }
    val iconColor = artikelIconColors.getOrElse(index) { Color(0xFF1B7A4E) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(68.dp).clip(RoundedCornerShape(12.dp)).background(bgColor), contentAlignment = Alignment.Center) { Icon(imageVector = Icons.Default.Book, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp)) }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = artikel.tag.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MintGreen, letterSpacing = 0.5.sp)
                Text(text = artikel.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                Text(text = "${artikel.durasiMenit} menit baca · ${artikel.waktuLabel}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
