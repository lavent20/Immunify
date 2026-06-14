package com.example.immunify.ui.presentation.tracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import com.example.immunify.ui.theme.LightMint
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackerScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: RiwayatViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val riwayatList by viewModel.riwayatList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Memuat data pesanan
    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            viewModel.getRiwayat(username)
        }
    }

    // Fungsi parsing tanggal dari format "d/M/yyyy"
    val dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    // Mengelompokkan data berdasarkan tanggal
    val eventsByDate = remember(riwayatList) {
        riwayatList.groupBy {
            try {
                LocalDate.parse(it.tanggalVaksin, dateFormatter)
            } catch (e: Exception) {
                null
            }
        }.filterKeys { it != null } as Map<LocalDate, List<RiwayatPesanan>>
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colorResource(id = R.color.blue2), Color.White)
                )
            )
            .padding(top = 16.dp, start = 20.dp, end = 20.dp)
    ) {
        Text("Jadwal Imunisasi", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
        Text("Jadwal imunisasi yang akan datang", color = Color.DarkGray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, "Bulan Sebelumnya")
                    }
                    Text(
                        text = "${currentMonth.month.name.capitalize()} ${currentMonth.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ChevronRight, "Bulan Selanjutnya")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hari dalam seminggu
                val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                Row(modifier = Modifier.fillMaxWidth()) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Grid Tanggal
                val daysInMonth = currentMonth.lengthOfMonth()
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
                val totalCells = daysInMonth + firstDayOfWeek - 1
                val rows = Math.ceil(totalCells / 7.0).toInt()

                var currentDay = 1
                for (i in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        for (j in 1..7) {
                            val isBeforeFirstDay = i == 0 && j < firstDayOfWeek
                            if (isBeforeFirstDay || currentDay > daysInMonth) {
                                Box(modifier = Modifier.weight(1f)) // Sel kosong
                            } else {
                                val thisDate = currentMonth.atDay(currentDay)
                                val isSelected = thisDate == selectedDate
                                val hasEvent = eventsByDate.containsKey(thisDate)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(if (isSelected) colorResource(R.color.blue1) else Color.Transparent)
                                        .clickable { selectedDate = thisDate },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = currentDay.toString(),
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        // Titik penanda jika ada jadwal vaksin
                                        if (hasEvent) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else Color(0xFFFF9800))
                                            )
                                        }
                                    }
                                }
                                currentDay++
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- PREVIEW JADWAL VAKSIN (HITUNG MUNDUR) ---
        Text(
            text = "Jadwal pada ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorResource(R.color.blue1))
            }
        } else {
            val eventsToday = eventsByDate[selectedDate] ?: emptyList()
            if (eventsToday.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada jadwal imunisasi pada hari ini.", color = Color.Gray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(eventsToday) { event ->
                        TrackerEventCard(event = event, targetDate = selectedDate)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrackerEventCard(event: RiwayatPesanan, targetDate: LocalDate) {
    val today = LocalDate.now()
    val daysLeft = ChronoUnit.DAYS.between(today, targetDate)

    // Menentukan teks dan warna Highlight Hitung Mundur
    val (countdownText, bgColor, textColor) = when {
        daysLeft < 0 -> Triple("Selesai", Color(0xFFF5F5F5), Color.Gray)
        daysLeft == 0L -> Triple("Hari Ini!", Color(0xFFFFEBEE), Color(0xFFE53935)) // Merah Urgent
        daysLeft <= 3L -> Triple("$daysLeft Hari Lagi", Color(0xFFFFF7E6), Color(0xFFE65100)) // Orange Soon
        else -> Triple("$daysLeft Hari Lagi", LightMint, Color(0xFF1B7A4E)) // Hijau Aman
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Medis
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

            // Info Vaksin
            Column(modifier = Modifier.weight(1f)) {
                Text(event.jenisVaksin, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(event.namaPenyedia, fontSize = 12.sp, color = Color.DarkGray)
            }

            // BADGE HITUNG MUNDUR (Sangat ternotice)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = countdownText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
            }
        }
    }
}