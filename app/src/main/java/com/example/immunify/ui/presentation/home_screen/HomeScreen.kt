package com.example.immunify.ui.presentation.home_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.data.model.Artikel
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel
import com.example.immunify.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModelUser: UserViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    vaksinViewModel: CurrentVaksinViewModel = hiltViewModel(),
    riwayatViewModel: RiwayatViewModel = hiltViewModel() // TAMBAHAN: Memanggil ViewModel Riwayat
) {
    val username by viewModelUser.username.collectAsState()
    val vaksinList by homeViewModel.vaksinList.collectAsState()
    val artikelList by homeViewModel.artikelList.collectAsState()
    val penyediaList by homeViewModel.penyediaList.collectAsState()

    // Data jadwal dari RiwayatViewModel
    val riwayatList by riwayatViewModel.riwayatList.collectAsState()

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val database = com.google.firebase.database.FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")

            database.getReference("users").addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val currentEmail = currentUser.email?.trim()?.lowercase()
                    for (userSnapshot in snapshot.children) {
                        val dbEmail = userSnapshot.child("email").value.toString().trim().lowercase()
                        if (dbEmail == currentEmail) {
                            val foundName = userSnapshot.key ?: "Pengguna"
                            viewModelUser.setUsername(foundName)
                            break
                        }
                    }
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
            })
        }
    }

    // Memuat Riwayat Jadwal Vaksin pengguna yang sedang login
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            riwayatViewModel.getRiwayat(username)
        }
    }

    // LOGIKA: Mencari jadwal vaksin terdekat (hari ini atau masa depan)
    val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val today = LocalDate.now()
    val nearestSchedule = remember(riwayatList) {
        riwayatList.mapNotNull { riwayat ->
            try {
                val date = LocalDate.parse(riwayat.tanggalVaksin, dateFormatter)
                if (!date.isBefore(today)) Pair(riwayat, date) else null
            } catch (e: Exception) { null }
        }.minByOrNull { it.second } // Mengambil tanggal terkecil (paling dekat)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.blue2),
                        Color.White
                    )
                )
            ),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Header
        item { HomeHeader(nama = username, navController = navController) }

        // --- SEKSI JADWAL VAKSIN TERDEKAT (BARU) ---
        item {
            SectionHeader(
                title = "Jadwal Vaksin Terdekat",
                onViewAll = {
                    navController.navigate(Route.TRACKER) {
                        popUpTo(Route.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            if (nearestSchedule != null) {
                NearestScheduleCard(event = nearestSchedule.first, targetDate = nearestSchedule.second)
            } else {
                Text(
                    text = "Tidak ada jadwal imunisasi dalam waktu dekat.",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        item {
            SectionHeader(
                title = "Daftar Vaksin",
                onViewAll = {
                    navController.navigate(Route.VAKSIN) {
                        popUpTo(Route.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        items(vaksinList.take(3)) { vaksinResponse ->
            val itemData = vaksinResponse.item
            if (itemData != null) {
                HomeVaksinCard(
                    namaVaksin = itemData.namaVaksin,
                    jenis = itemData.jenis,
                    id = itemData.id,
                    vaksinKey = vaksinResponse.key ?: "",
                    navController = navController,
                    vaksinViewModel = vaksinViewModel
                )
            }
        }

        item {
            SectionHeader(
                title = "Klinik Terdekat",
                onViewAll = {
                    vaksinViewModel.setCurrentNamaVaksin("")
                    navController.navigate(Route.DAFTAR_PENYEDIA)
                }
            )
            FaskesTerdekatRow(
                penyediaList = penyediaList.take(5),
                navController = navController,
                userViewModel = viewModelUser
            )
        }

        item {
            SectionHeader(
                title = "Edukasi Vaksin",
                onViewAll = { navController.navigate(Route.INSIGHTS) }
            )
        }

        items(artikelList.take(5)) { artikel ->
            ArtikelCard(
                artikel = artikel,
                onClick = { navController.navigate("detailInsights/${artikel.id}") }
            )
        }
    }
}

@Composable
fun HomeHeader(nama: String, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
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
            Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = TextPrimary, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = "Lihat Semua", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MintGreen, modifier = Modifier.clickable { onViewAll() })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NearestScheduleCard(event: RiwayatPesanan, targetDate: LocalDate) {
    val today = LocalDate.now()
    val daysLeft = ChronoUnit.DAYS.between(today, targetDate)

    val (countdownText, bgColor, textColor) = when {
        daysLeft == 0L -> Triple("Hari Ini!", Color(0xFFFFEBEE), Color(0xFFE53935))
        daysLeft <= 3L -> Triple("$daysLeft Hari Lagi", Color(0xFFFFF7E6), Color(0xFFE65100))
        else -> Triple("$daysLeft Hari Lagi", LightMint, Color(0xFF1B7A4E))
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(R.color.blue1).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = colorResource(R.color.blue1))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.jenisVaksin, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(event.namaPenyedia, fontSize = 12.sp, color = Color.DarkGray)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = countdownText, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            }
        }
    }
}

@Composable
fun HomeVaksinCard(
    namaVaksin: String, jenis: String, id: String, vaksinKey: String, navController: NavController, vaksinViewModel: CurrentVaksinViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable {
            val targetId = id.toIntOrNull() ?: vaksinKey.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 1
            vaksinViewModel.setCurrentVaksin(targetId)
            vaksinViewModel.setCurrentNamaVaksin(namaVaksin)
            navController.navigate(Route.DETAIL_VAKSIN)
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(LightMint), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFF1B7A4E), modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = namaVaksin, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = jenis, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

data class FaskesItem(val id: String, val nama: String, val alamat: String, val jarak: String, val icon: ImageVector, val bgColor: Color, val iconColor: Color)

@Composable
fun FaskesTerdekatRow(penyediaList: List<PenyediaVaksin>, navController: NavController, userViewModel: UserViewModel) {
    if (penyediaList.isEmpty()) {
        Text("Belum ada fasilitas kesehatan terdaftar.", modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = Color.Gray, fontSize = 13.sp)
    } else {
        LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(penyediaList) { penyedia ->
                val randomDistance = remember(penyedia.id) { (1..12).random() }
                val randomBgIndex = remember(penyedia.id) { (0..2).random() }
                val bgColors = listOf(LightMint, StatBlueBg, SoonAmberBg)
                val iconColors = listOf(Color(0xFF1B7A4E), Color(0xFF1E88E5), Color(0xFFE65100))
                val faskes = FaskesItem(penyedia.id, penyedia.namaLengkap, penyedia.pengalaman.ifEmpty { "Alamat tidak tersedia" }, "$randomDistance km", Icons.Default.MedicalServices, bgColors[randomBgIndex], iconColors[randomBgIndex])
                FaskesCard(faskes, navController, userViewModel)
            }
        }
    }
}

@Composable
fun FaskesCard(faskes: FaskesItem, navController: NavController, userViewModel: UserViewModel) {
    Card(
        modifier = Modifier.width(180.dp).clickable {
            userViewModel.setNamaPenyedia(faskes.nama)
            userViewModel.setIdPenyedia(faskes.id)
            navController.navigate(Route.DETAIL_PENYEDIA)
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(96.dp).background(faskes.bgColor), contentAlignment = Alignment.Center) {
                Icon(faskes.icon, contentDescription = null, tint = faskes.iconColor, modifier = Modifier.size(36.dp))
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(text = faskes.nama, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = faskes.alamat, fontSize = 11.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MintGreen, modifier = Modifier.size(13.dp))
                    Text(text = faskes.jarak, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

val artikelBgColors = listOf(LightMint, SoonAmberBg, UrgentRedBg)
val artikelIconColors = listOf(Color(0xFF1B7A4E), Color(0xFFE65100), Color(0xFFE53935))

@Composable
fun ArtikelCard(artikel: Artikel, onClick: () -> Unit) {
    val index = artikel.id.toIntOrNull()?.minus(1) ?: 0
    val bgColor = artikelBgColors.getOrElse(index) { LightMint }
    val iconColor = artikelIconColors.getOrElse(index) { Color(0xFF1B7A4E) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(68.dp).clip(RoundedCornerShape(12.dp)).background(bgColor), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Book, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = artikel.tag.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MintGreen, letterSpacing = 0.5.sp)
                Text(text = artikel.judul, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, lineHeight = 18.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp))
                Text(text = "${artikel.durasiMenit} menit baca · ${artikel.waktuLabel}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}