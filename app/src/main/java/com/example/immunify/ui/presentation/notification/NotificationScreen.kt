package com.example.immunify.ui.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.immunify.data.model.NotifikasiItem
import com.example.immunify.data.model.TipeNotifikasi
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val notifikasiList by homeViewModel.notifikasiList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            if (notifikasiList.isEmpty()) {
                // ─── EMPTY STATE (Sesuai Desain Gambar notification.png) ───
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📦", fontSize = 64.sp) // Ilustrasi box kosong sesuai desain mockup figma kamu
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Oops!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF008080) // Warna Teal maskot figma
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nothing's here",
                        fontSize = 15.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // ─── LIST NOTIFIKASI JIKA ADA DATA MASUK ───
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(notifikasiList) { notif ->
                        NotifikasiRowItem(notif = notif)
                    }
                }
            }
        }
    }
}

@Composable
fun NotifikasiRowItem(notif: NotifikasiItem) {
    val iconBg = if (notif.tipe == TipeNotifikasi.PENGINGAT_VAKSIN) UrgentRedBg else LightMint
    val iconColor = if (notif.tipe == TipeNotifikasi.PENGINGAT_VAKSIN) UrgentRed else MintGreen
    val iconVector = if (notif.tipe == TipeNotifikasi.PENGINGAT_VAKSIN) Icons.Default.NotificationsActive else Icons.Default.Book

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = notif.judul, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = notif.waktuLabel, fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.deskripsi,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
        HorizontalDivider(color = Border, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))
    }
}