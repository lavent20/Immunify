package com.example.immunify.ui.presentation.riwayat_sewa

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.data.model.UrgencyLevel
import com.example.immunify.data.model.VaccineData
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.theme.*
import com.example.immunify.util.Resource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: RiwayatViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val riwayatList by viewModel.riwayatList.collectAsState()
    val upcomingJadwal by viewModel.jadwalVaksin.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Tracker Kalender", "Riwayat Selesai")
    
    var showAddRecord by remember { mutableStateOf(false) }

    // State untuk Kalender
    val datePickerState = rememberDatePickerState()

    // Ambil daftar tanggal yang ada jadwalnya (Timestamp ke Date string "yyyy-MM-dd")
    val scheduledDates = remember(upcomingJadwal) {
        upcomingJadwal.map { 
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.scheduledTimestamp))
        }.toSet()
    }

    LaunchedEffect(username) {
        if (username.isNotEmpty()) {
            viewModel.getRiwayat(username)
            viewModel.getUpcomingJadwal(username)
        }
    }

    Scaffold(
        topBar = { HeaderRiwayat(navController, onAddClick = { showAddRecord = true }) },
        modifier = Modifier.fillMaxSize().background(BackgroundPage)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MintGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MintGreen) }
            } else {
                if (selectedTab == 0) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Border)) {
                            Column {
                                DatePicker(
                                    state = datePickerState,
                                    showModeToggle = false,
                                    title = null, headline = null,
                                    colors = DatePickerDefaults.colors(
                                        selectedDayContainerColor = MintGreen,
                                        todayContentColor = MintGreen,
                                        todayDateBorderColor = MintGreen
                                    )
                                )
                                
                                // Legend Penanda dengan Dot Visual
                                if (scheduledDates.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFE53935)))
                                        Text(" Tanggal terjadwal imunisasi", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        }
                        
                        Text(text = "Upcoming Appointments", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 16.dp))
                        
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 88.dp)) {
                            if (upcomingJadwal.isEmpty()) {
                                item { EmptyStateView("Belum ada jadwal imunisasi. Silakan pesan di menu Klinik.") }
                            } else {
                                items(upcomingJadwal) { item ->
                                    JadwalItemCard(item) { /* Navigate to detail */ }
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 88.dp, top = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (riwayatList.isEmpty()) item { EmptyStateView("Belum ada riwayat imunisasi.") }
                        else items(riwayatList) { ItemRiwayatCard(it.jenisVaksin, it.namaPenyedia, it.waktuPesanan, navController) }
                    }
                }
            }
        }
    }

    if (showAddRecord) {
        AddRecordBottomSheet(onDismiss = { showAddRecord = false }) { newRecord ->
            viewModel.addUpcomingJadwal(username, newRecord)
            showAddRecord = false
        }
    }
}

@Composable
fun HeaderRiwayat(navController: NavController, onAddClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp)) {
        IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) }
        Text(text = "Tracker & Riwayat", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(start = 8.dp))
        IconButton(onClick = onAddClick) { Icon(Icons.Default.Add, null, tint = MintGreen) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordBottomSheet(
    onDismiss: () -> Unit, 
    homeViewModel: HomeViewModel = hiltViewModel(),
    onDone: (VaksinJadwal) -> Unit
) {
    var vaccineType by remember { mutableStateOf(VaccineData.list[0]) }
    var dose by remember { mutableStateOf("Dosis 1") }
    var clinicSearchQuery by remember { mutableStateOf("") }
    var selectedClinicName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf("08:00") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var expandedVaccine by remember { mutableStateOf(false) }
    var expandedDose by remember { mutableStateOf(false) }
    var expandedClinic by remember { mutableStateOf(false) }

    val clinicsResource by homeViewModel.clinicsList.collectAsState()
    val allClinics = (clinicsResource as? Resource.Success)?.data ?: emptyList()
    
    val filteredClinics = remember(clinicSearchQuery, allClinics) {
        if (clinicSearchQuery.isBlank()) allClinics
        else allClinics.filter { it.namaLengkap.contains(clinicSearchQuery, ignoreCase = true) }
    }

    val doses = listOf("Dosis 1", "Dosis 2", "Dosis 3", "Booster")
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth().padding(bottom = 40.dp).verticalScroll(rememberScrollState())) {
            Text("Add Record / Appointment", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(24.dp))
            
            // Nama Klinik dengan Search & Dropdown
            Text("Clinic Name", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextSecondary)
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedTextField(
                    value = if (selectedClinicName.isNotEmpty() && !expandedClinic) selectedClinicName else clinicSearchQuery,
                    onValueChange = { 
                        clinicSearchQuery = it
                        expandedClinic = true
                    },
                    placeholder = { Text("Search clinic or hospital...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { 
                        IconButton(onClick = { expandedClinic = !expandedClinic }) {
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = expandedClinic && filteredClinics.isNotEmpty(),
                    onDismissRequest = { expandedClinic = false },
                    modifier = Modifier.fillMaxWidth(0.85f).heightIn(max = 300.dp)
                ) {
                    filteredClinics.forEach { clinic ->
                        DropdownMenuItem(
                            text = { Text(clinic.namaLengkap, fontSize = 14.sp) },
                            onClick = {
                                selectedClinicName = clinic.namaLengkap
                                clinicSearchQuery = clinic.namaLengkap
                                expandedClinic = false
                            }
                        )
                    }
                }
            }

            // Pemilihan Tanggal
            Text("Date", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextSecondary)
            val dateStr = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                border = BorderStroke(1.dp, Border)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(dateStr, modifier = Modifier.weight(1f), color = TextPrimary)
                    Icon(Icons.Default.CalendarToday, null, tint = MintGreen, modifier = Modifier.size(20.dp))
                }
            }

            // Pemilihan Jam (Time)
            Text("Time", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextSecondary)
            CustomFlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                val times = listOf("08:00", "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00")
                times.forEach { t ->
                    Surface(
                        color = if (selectedTime == t) MintGreen else Color.White,
                        border = BorderStroke(1.dp, if (selectedTime == t) MintGreen else Border),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).clickable { selectedTime = t }
                    ) {
                        Text(text = t, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = if (selectedTime == t) Color.White else TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Pemilihan Jenis Vaksin
            Text("Immunization Type", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextSecondary)
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedCard(
                    onClick = { expandedVaccine = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(vaccineType, modifier = Modifier.weight(1f), color = TextPrimary)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = expandedVaccine, onDismissRequest = { expandedVaccine = false }) {
                    VaccineData.list.forEach { v ->
                        DropdownMenuItem(text = { Text(v) }, onClick = { vaccineType = v; expandedVaccine = false })
                    }
                }
            }
            
            // Pemilihan Dosis
            Text("Dose", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextSecondary)
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                OutlinedCard(
                    onClick = { expandedDose = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(dose, modifier = Modifier.weight(1f), color = TextPrimary)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = expandedDose, onDismissRequest = { expandedDose = false }) {
                    doses.forEach { d ->
                        DropdownMenuItem(text = { Text(d) }, onClick = { dose = d; expandedDose = false })
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { 
                    // Gabungkan Date dan Time ke dalam satu timestamp
                    val cal = Calendar.getInstance().apply {
                        timeInMillis = selectedDate
                        val hour = selectedTime.substringBefore(":").toInt()
                        val min = selectedTime.substringAfter(":").toInt()
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, min)
                    }
                    
                    onDone(VaksinJadwal(
                        id = UUID.randomUUID().toString(),
                        namaVaksin = vaccineType,
                        jenis = selectedClinicName.ifBlank { clinicSearchQuery.ifBlank { "Klinik Mandiri" } },
                        dosis = dose,
                        scheduledTimestamp = cal.timeInMillis,
                        urgencyLevel = if (cal.timeInMillis - System.currentTimeMillis() < 86400000) UrgencyLevel.URGENT else UrgencyLevel.SOON
                    )) 
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save to Tracker", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun JadwalItemCard(jadwal: VaksinJadwal, onClick: () -> Unit) {
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(jadwal.scheduledTimestamp))
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), border = BorderStroke(1.dp, Border)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(UrgentRedBg), contentAlignment = Alignment.Center) { 
                Icon(Icons.Default.Event, null, tint = Color(0xFFE53935)) 
            }
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = jadwal.namaVaksin, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Text(text = "${jadwal.dosis} · Terjadwal pada $dateStr", fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ItemRiwayatCard(jenis: String, penyedia: String, waktu: String, nav: NavController) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), border = BorderStroke(1.dp, Border)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(StatBlueBg), contentAlignment = Alignment.Center) { 
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(24.dp)) 
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jenis, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = penyedia, fontSize = 13.sp, color = TextSecondary)
                Text(text = waktu, fontSize = 11.sp, color = Color.LightGray, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
fun EmptyStateView(pesan: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) { Text(text = pesan, color = TextSecondary, fontSize = 14.sp, textAlign = TextAlign.Center) }
}

@Composable
fun CustomFlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        var currentX = 0
        var currentY = 0
        var lineHeight = 0
        val positions = mutableListOf<Pair<Int, Int>>()
        
        placeables.forEach { placeable ->
            if (currentX + placeable.width > constraints.maxWidth) {
                currentX = 0
                currentY += lineHeight + crossAxisSpacing.roundToPx()
                lineHeight = 0
            }
            positions.add(currentX to currentY)
            lineHeight = maxOf(lineHeight, placeable.height)
            currentX += placeable.width + mainAxisSpacing.roundToPx()
        }
        
        layout(constraints.maxWidth, maxOf(currentY + lineHeight, 0)) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(positions[index].first, positions[index].second)
            }
        }
    }
}
