package com.example.immunify.ui.presentation.vaksin

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.model.PenyediaVaksin
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.penyedia.PenyediaViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailVaksinScreen(
    navController: NavController,
    currentVaksinViewModel: CurrentVaksinViewModel,
    userViewModel: UserViewModel,
    viewModel: InformasiVaksinViewModel = hiltViewModel(),
    penyediaViewModel: PenyediaViewModel = hiltViewModel(), // Mengambil data faskes
    riwayatViewModel: RiwayatViewModel = hiltViewModel()    // Mengelola riwayat pesanan
) {
    val currentVaksinId by currentVaksinViewModel.currentVaksin.collectAsState()
    val username by userViewModel.username.collectAsState()
    val userPenyediaId by userViewModel.idPenyedia.collectAsState()

    val vaksinState by viewModel.vaksin
    val penyediaList by penyediaViewModel.penyediaList.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedPenyedia by remember { mutableStateOf<PenyediaVaksin?>(null) }
    var selectedDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context, { _, year, month, day -> selectedDate = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    // 1. Memuat informasi dasar vaksin (Nama, Jenis, Dosis)
    LaunchedEffect(currentVaksinId) {
        viewModel.getVaksinDetail(currentVaksinId.toString())
    }

    // 2. Memuat daftar klinik yang memiliki stok vaksin ini
    LaunchedEffect(vaksinState.namaVaksin) {
        if (vaksinState.namaVaksin.isNotEmpty()) {
            penyediaViewModel.getPenyedia(vaksinState.namaVaksin)
        }
    }

    // 3. Otomatis memilih klinik pertama (Atau klinik yang diklik dari halaman sebelumnya)
    LaunchedEffect(penyediaList) {
        if (penyediaList.isNotEmpty() && selectedPenyedia == null) {
            val match = penyediaList.find { it.id == userPenyediaId }
            selectedPenyedia = match ?: penyediaList.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorResource(id = R.color.blue2), Color.White)))
            .padding(20.dp)
    ) {
        // --- HEADER ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text("Informasi Vaksin", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- INFORMASI VAKSIN (KARTU PUTIH) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Nama Vaksin", fontSize = 12.sp, color = Color.Gray)
                Text(vaksinState.namaVaksin, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Jenis Vaksin", fontSize = 12.sp, color = Color.Gray)
                        Text(vaksinState.jenis, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Dosis", fontSize = 12.sp, color = Color.Gray)
                        Text(vaksinState.dosis, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- DROPDOWN PILIH KLINIK ---
        Text("Klinik Penyedia", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedPenyedia?.namaLengkap ?: "Mencari klinik...",
                onValueChange = {}, readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(R.color.blue1)
                ),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                if (penyediaList.isEmpty()) {
                    DropdownMenuItem(text = { Text("Tidak ada faskes yang sedia") }, onClick = { })
                } else {
                    penyediaList.forEach { faskes ->
                        DropdownMenuItem(
                            text = { Text(faskes.namaLengkap) },
                            onClick = {
                                selectedPenyedia = faskes
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- INFORMASI STOK DINAMIS ---
        val stokSaatIni = selectedPenyedia?.stok_vaksin?.get(vaksinState.namaVaksin) ?: 0
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Jumlah Stok: ", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "$stokSaatIni Dosis",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (stokSaatIni > 0) colorResource(R.color.blue1) else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- INPUT TANGGAL ---
        Text("Tanggal Imunisasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = selectedDate, onValueChange = {}, readOnly = true,
            placeholder = { Text("Pilih tanggal janji temu") },
            trailingIcon = { Icon(Icons.Default.DateRange, null, Modifier.clickable { datePickerDialog.show() }, tint = colorResource(R.color.blue1)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(R.color.blue1)
            ),
            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }
        )

        Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke posisi paling bawah layar

        // --- TOMBOL PESAN ---
        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue1)),
            enabled = stokSaatIni > 0 && selectedPenyedia != null, // Tombol mati jika stok habis
            onClick = {
                if (selectedDate.isNotEmpty()) {
                    val db = com.google.firebase.database.FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")

                    // Target URL Database ke stok klinik yang dipilih
                    val stokRef = db.getReference("penyedia_vaksin/${selectedPenyedia!!.id}/stok_vaksin/${vaksinState.namaVaksin}")

                    stokRef.get().addOnSuccessListener { snapshot ->
                        val currentStok = snapshot.getValue(Int::class.java) ?: 0
                        if (currentStok > 0) {
                            // 1. Kurangi stok di Firebase secara realtime
                            stokRef.setValue(currentStok - 1)

                            // 2. Buat objek Riwayat Pesanan
                            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                            val riwayatBaru = RiwayatPesanan(
                                namaPenyedia = selectedPenyedia!!.namaLengkap,
                                jenisVaksin = vaksinState.namaVaksin,
                                waktuPesanan = currentDateTime,
                                tanggalVaksin = selectedDate
                            )

                            // 3. Simpan Riwayat ke akun Pengguna
                            riwayatViewModel.tambahRiwayat(username, riwayatBaru) { success ->
                                if (success) {
                                    Toast.makeText(context, "Vaksin berhasil dipesan!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Stok faskes ini habis!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Mohon pilih tanggal imunisasi!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Pesan Vaksin", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}