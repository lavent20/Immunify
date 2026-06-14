package com.example.immunify.ui.presentation.home_screen

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
import com.example.immunify.data.model.Artikel
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel
import com.example.immunify.ui.theme.*
import com.example.immunify.R

@Composable
fun HomeScreen(
    navController: NavController,
    viewModelUser: UserViewModel,
    homeViewModel: HomeViewModel = hiltViewModel(),
    vaksinViewModel: CurrentVaksinViewModel = hiltViewModel()
) {
    val username by viewModelUser.username.collectAsState()
    val vaksinList by homeViewModel.vaksinList.collectAsState()
    val artikelList by homeViewModel.artikelList.collectAsState()

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf( // <-- Pastikan tulisannya 'colors' pakai 's'
                        colorResource(id = R.color.blue2),
                        Color.White
                    )
                )
            ),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        // Header
        item { HomeHeader(nama = username, navController = navController) }

        // --- SEKSI DAFTAR VAKSIN (MENGGANTIKAN JADWAL) ---
        item {
            SectionHeader(
                title = "Daftar Vaksin", // Diubah judulnya
                onViewAll = { navController.navigate(Route.VAKSIN) }
            )
        }

        // Batasi hanya menampilkan maksimal 3 vaksin menggunakan .take(3)
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

        // --- SEKSI KLINIK TERDEKAT ---
        item {
            SectionHeader(
                title = "Klinik Terdekat",
                onViewAll = { navController.navigate(Route.DAFTAR_PENYEDIA) }
            )
            FaskesTerdekatRow(navController = navController)
        }

        // --- SEKSI EDUKASI VAKSIN ---
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
            Text(
                text = "Selamat pagi",
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = nama.ifBlank { "Pengguna" },
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { navController.navigate("notification") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
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
        Text(
            text = "Lihat Semua",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MintGreen,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

// --- KOMPONEN BARU KARTU VAKSIN HOMESCREEN ---
@Composable
fun HomeVaksinCard(
    namaVaksin: String,
    jenis: String,
    id: String,
    vaksinKey: String,
    navController: NavController,
    vaksinViewModel: CurrentVaksinViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable {
                // Konversi ID dengan aman untuk mencegah data tertukar saat masuk Detail
                val targetId = id.toIntOrNull() ?: vaksinKey.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 1
                vaksinViewModel.setCurrentVaksin(targetId)
                vaksinViewModel.setCurrentNamaVaksin(namaVaksin)
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
            // Kotak Ikon Medis Hijau Mint
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
            // Informasi Nama dan Jenis Utama
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

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

data class FaskesItem(
    val nama: String,
    val alamat: String,
    val jarak: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

@Composable
fun FaskesTerdekatRow(navController: NavController) {
    val faskesList = listOf(
        FaskesItem("RS EMC Pulomas", "Jl. Pulo Mas Bar. VI No.20", "2 km", Icons.Default.MedicalServices, LightMint, Color(0xFF1B7A4E)),
        FaskesItem("Columbia Asia", "Jl. Kayu Putih Raya No.8", "3 km", Icons.Default.MedicalServices, StatBlueBg, Color(0xFF1E88E5)),
        FaskesItem("Klinik Mitra Sehat", "Jl. Rawamangun No.15A", "4 km", Icons.Default.MedicalServices, SoonAmberBg, Color(0xFFE65100))
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(faskesList) { faskes ->
            FaskesCard(faskes = faskes, navController = navController)
        }
    }
}

@Composable
fun FaskesCard(faskes: FaskesItem, navController: NavController) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { navController.navigate(Route.DAFTAR_PENYEDIA) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(faskes.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = faskes.icon,
                    contentDescription = null,
                    tint = faskes.iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                Text(
                    text = faskes.nama,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = faskes.alamat,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MintGreen,
                        modifier = Modifier.size(13.dp)
                    )
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artikel.tag.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MintGreen,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = artikel.judul,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
                Text(
                    text = "${artikel.durasiMenit} menit baca · ${artikel.waktuLabel}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}