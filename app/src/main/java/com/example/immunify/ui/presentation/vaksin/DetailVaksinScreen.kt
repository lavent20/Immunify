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
import com.example.immunify.model.RiwayatPesanan
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.penyedia.DetailPenyediaViewModel
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailVaksinScreen(
    navController: NavController,
    currentVaksinViewModel: CurrentVaksinViewModel,
    userViewModel: UserViewModel,
    viewModel: InformasiVaksinViewModel = hiltViewModel(),
    penyediaViewModel: DetailPenyediaViewModel = hiltViewModel(),
    riwayatViewModel: RiwayatViewModel = hiltViewModel()
) {
    val currentVaksinId by currentVaksinViewModel.currentVaksin.collectAsState()
    val namaPenyedia by userViewModel.namaPenyedia.collectAsState()
    val idPenyedia by userViewModel.idPenyedia.collectAsState() // MENGAMBIL USERNAME FASKES
    val username by userViewModel.username.collectAsState()
    val vaksinState by viewModel.vaksin
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context, { _, year, month, day -> selectedDate = "$day/${month + 1}/$year" },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(currentVaksinId) { viewModel.getVaksinDetail(currentVaksinId.toString()) }

    Column(modifier = Modifier.fillMaxSize().background(
        Brush.verticalGradient(
            colors = listOf(
                colorResource(id = R.color.blue2),
                Color.White
            )
        )
    )
        .padding(20.dp).padding(20.dp)) {
        IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }

        Text("Informasi Vaksin", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Penyedia: $namaPenyedia", color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Nama: ${vaksinState.namaVaksin}", fontSize = 18.sp)
        Text("Jenis: ${vaksinState.jenis}", fontSize = 16.sp, color = Color.Gray)
        Text("Dosis: ${vaksinState.dosis}", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = selectedDate, onValueChange = {}, readOnly = true,
            label = { Text("Pilih Tanggal Imunisasi") },
            trailingIcon = { Icon(Icons.Default.DateRange, null, Modifier.clickable { datePickerDialog.show() }) },
            modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.blue1)),
            onClick = {
                if (selectedDate.isNotEmpty()) {
                    // PERBAIKAN 1: Tambahkan URL Firebase Singapura Anda
                    val db = com.google.firebase.database.FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")

                    // PERBAIKAN 2: Gunakan 'idPenyedia' (contoh: "panpan"), BUKAN namaPenyedia
                    val stokRef = db.getReference("penyedia_vaksin/$idPenyedia/stok_vaksin/${vaksinState.namaVaksin}")

                    stokRef.get().addOnSuccessListener { snapshot ->
                        val currentStok = snapshot.getValue(Int::class.java) ?: 0
                        if (currentStok > 0) {
                            // 1. Kurangi stok di Firebase
                            stokRef.setValue(currentStok - 1)

                            // 2. Simpan Riwayat
                            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                            val riwayatBaru = RiwayatPesanan(
                                namaPenyedia = namaPenyedia, // Tetap gunakan Nama Lengkap untuk UI Riwayat
                                jenisVaksin = vaksinState.namaVaksin,
                                waktuPesanan = currentDateTime,
                                tanggalVaksin = selectedDate
                            )

                            riwayatViewModel.tambahRiwayat(username, riwayatBaru) { success ->
                                if (success) {
                                    Toast.makeText(context, "Vaksin berhasil dipesan!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                } else {
                                    Toast.makeText(context, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Stok Habis!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Pilih tanggal dulu!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text("Pesan Vaksin", color = Color.White)
        }
    }
}