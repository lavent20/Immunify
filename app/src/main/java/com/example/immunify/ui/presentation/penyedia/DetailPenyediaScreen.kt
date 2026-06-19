package com.example.immunify.ui.presentation.penyedia

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.immunify.R
import com.example.immunify.data.model.UrgencyLevel
import com.example.immunify.data.model.VaccineData
import com.example.immunify.data.model.VaksinJadwal
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel
import com.example.immunify.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailPenyediaScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    vaksinViewModel: CurrentVaksinViewModel,
    penyediaViewModel: PenyediaViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    riwayatViewModel: RiwayatViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val namaPenyediaSelected by userViewModel.namaPenyedia.collectAsState()
    
    // Gunakan ViewModel detail agar data beneran sinkron dari cache repository
    val detailViewModel: DetailPenyediaViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val penyedia by detailViewModel.penyedia.collectAsState()

    LaunchedEffect(namaPenyediaSelected) {
        detailViewModel.getDetailPenyedia(namaPenyediaSelected)
    }

    var currentStep by remember { mutableStateOf(0) } 
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedTime by remember { mutableStateOf("08:00") }
    var selectedVaccine by remember { mutableStateOf(VaccineData.list[0]) }

    when (currentStep) {
        0 -> ClinicInfoView(penyedia, onBack = { navController.popBackStack() }, onSetAppointment = { currentStep = 1 })
        1 -> SetAppointmentView(
            penyedia = penyedia, 
            username = username,
            selectedDate = selectedDate,
            onDateChange = { selectedDate = it },
            selectedTime = selectedTime,
            onTimeChange = { selectedTime = it },
            selectedVaccine = selectedVaccine,
            onVaccineChange = { selectedVaccine = it },
            onBack = { currentStep = 0 }, 
            onContinue = { currentStep = 2 }
        )
        2 -> AppointmentSummaryView(
            penyedia = penyedia, 
            username = username,
            dateMillis = selectedDate,
            timeStr = selectedTime,
            vaccineName = selectedVaccine,
            onBack = { currentStep = 1 }, 
            onConfirm = {
                riwayatViewModel.addUpcomingJadwal(
                    username = username,
                    jadwal = VaksinJadwal(
                        id = UUID.randomUUID().toString(),
                        namaVaksin = selectedVaccine,
                        jenis = penyedia.namaLengkap, // Menyimpan nama klinik/penyedia
                        dosis = "Dosis ke-1",
                        hariLagi = 0,
                        urgencyLevel = UrgencyLevel.SOON,
                        scheduledTimestamp = selectedDate
                    )
                )
                currentStep = 3 
            }
        )
        3 -> SuccessView(onBackHome = { navController.navigate(Route.HOME) { popUpTo(0) } })
    }
}

@Composable
fun ClinicInfoView(penyedia: PenyediaVaksin, onBack: () -> Unit, onSetAppointment: () -> Unit) {
    Scaffold(
        bottomBar = {
            BottomButton(label = "Set Appointment", onClick = onSetAppointment)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Box {
                AsyncImage(
                    model = penyedia.imageUrl.ifBlank { "https://images.unsplash.com/photo-1519494026892-80bbd2d6fd0d?w=800" },
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).background(Color.White.copy(0.7f), CircleShape)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = penyedia.namaLengkap, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MintGreen, modifier = Modifier.size(16.dp))
                    Text(text = " ${penyedia.pengalaman} away", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(text = " ${(penyedia.poin / 10.0)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Information", fontWeight = FontWeight.Bold, color = MintGreen, fontSize = 16.sp)
                Divider(thickness = 2.dp, color = MintGreen, modifier = Modifier.width(80.dp).padding(top = 4.dp))
                
                Spacer(modifier = Modifier.height(16.dp))
                InfoItem(icon = Icons.Default.Place, label = penyedia.alamat)
                InfoItem(icon = Icons.Default.AccessTime, label = "Open · 08:00 - 17:00")
                
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Daftar Vaksin Tersedia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // MENAMPILKAN SEMUA VAKSIN DARI VACCINEDATA AGAR KONSISTEN
                VaccineData.list.forEach { v ->
                    VaccineItemStatic(v)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAppointmentView(
    penyedia: PenyediaVaksin, 
    username: String, 
    selectedDate: Long,
    onDateChange: (Long) -> Unit,
    selectedTime: String,
    onTimeChange: (String) -> Unit,
    selectedVaccine: String,
    onVaccineChange: (String) -> Unit,
    onBack: () -> Unit, 
    onContinue: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = { TopBar(title = "Set Appointment", onBack = onBack) },
        bottomBar = { BottomButton(label = "Continue", onClick = onContinue) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            SectionTitle("Parent/Guardian Information")
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Border)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MintGreen)
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(username.ifEmpty { "User Immunify" }, fontWeight = FontWeight.Bold)
                        Text("081234567890", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            
            SectionTitle("Location")
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Border)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MintGreen)
                    Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(penyedia.namaLengkap, fontWeight = FontWeight.Bold)
                        Text(penyedia.alamat, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
            
            SectionTitle("Date & Time")
            val dateStr = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate))
            AppointmentDetailItem(Icons.Default.CalendarToday, dateStr, onClick = { showDatePicker = true })
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Time Selector with 30 min intervals (08:00 - 17:00)
            Text(text = "Pilih Jam Kedatangan (Tersedia tiap 30 menit)", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                val times = listOf("08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30")
                times.forEach { t ->
                    TimeChip(t, selectedTime == t, onClick = { onTimeChange(t) })
                }
            }
            
            SectionTitle("Vaccine")
            VaccineDropdown(selectedVaccine, onVaccineChange)
        }
    }
}

@Composable
fun VaccineDropdown(selectedVaccine: String, onVaccineChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AppointmentDetailItem(Icons.Default.Vaccines, selectedVaccine, onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            VaccineData.list.forEach { v ->
                DropdownMenuItem(text = { Text(v) }, onClick = {
                    onVaccineChange(v)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun AppointmentSummaryView(penyedia: PenyediaVaksin, username: String, dateMillis: Long, timeStr: String, vaccineName: String, onBack: () -> Unit, onConfirm: () -> Unit) {
    val dateStr = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(dateMillis))
    
    Scaffold(
        topBar = { TopBar(title = "Appointment Summary", onBack = onBack) },
        bottomBar = { BottomButton(label = "Confirm", onClick = onConfirm) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            SummaryItem("Parent/Guardian Information", "${username.ifEmpty { "User" }}\n081234567890")
            SummaryItem("Location", "${penyedia.namaLengkap}\n${penyedia.alamat}")
            SummaryItem("Date & Time", "$dateStr\nAt $timeStr")
            SummaryItem("Vaccine", vaccineName)
            SummaryItem("Vaccinant", "$username\nChild 1")
        }
    }
}

@Composable
fun SuccessView(onBackHome: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_immunify),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text("Successful!", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MintGreen)
        Text(
            "Appointment has been made.\nCheck your tracker for the schedule.",
            textAlign = TextAlign.Center,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(100.dp))
        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Home", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
        Text(text = label, modifier = Modifier.padding(start = 12.dp), fontSize = 14.sp, color = TextPrimary)
    }
}

@Composable
fun VaccineItemStatic(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Border)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, modifier = Modifier.weight(1f), fontSize = 14.sp)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
}

@Composable
fun AppointmentDetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick), 
        colors = CardDefaults.cardColors(containerColor = Color.White), 
        border = BorderStroke(1.dp, Border)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MintGreen, modifier = Modifier.size(20.dp))
            Text(text = label, modifier = Modifier.padding(start = 12.dp).weight(1f), fontSize = 14.sp)
            Text("Change", color = MintGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TimeChip(time: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) MintGreen else Color.White,
        border = BorderStroke(1.dp, if (isSelected) MintGreen else Border),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(text = time, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = if (isSelected) Color.White else TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun SummaryItem(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(text = title, color = TextSecondary, fontSize = 13.sp)
        Text(text = content, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp))
        Divider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp, color = Border)
    }
}

@Composable
fun BottomButton(label: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().padding(20.dp).height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MintGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
    )
}

@Composable
fun FlowRow(
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
