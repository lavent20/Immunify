package com.example.immunify.ui.presentation.be_a_mentor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.data.model.VaksinModelResponse
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTambahVaksin(
    navController: NavController,
    userViewModel: UserViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val username by userViewModel.username.collectAsState()
    val vaksinMasterList by homeViewModel.vaksinList.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedVaksin by remember { mutableStateOf<VaksinModelResponse?>(null) }
    var stokInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                colors = listOf(
                    colorResource(id = R.color.blue2),
                    Color.White
                )
            )
        ).padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
            Text(text = "Kelola Stok Vaksin", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tambahkan vaksin yang tersedia di klinik Anda dari daftar resmi.",
            color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 24.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedVaksin?.item?.namaVaksin ?: "Pilih Jenis Vaksin...",
                onValueChange = {}, readOnly = true, label = { Text("Jenis Vaksin") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                if (vaksinMasterList.isEmpty()) {
                    DropdownMenuItem(text = { Text("Memuat data vaksin...") }, onClick = { })
                } else {
                    vaksinMasterList.forEach { vaksin ->
                        DropdownMenuItem(
                            text = { Text(vaksin.item?.namaVaksin ?: "Tidak diketahui") },
                            onClick = {
                                selectedVaksin = vaksin
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = stokInput, onValueChange = { stokInput = it },
            label = { Text("Jumlah Stok (Dosis)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val namaVaksinId = selectedVaksin?.item?.namaVaksin
                if (namaVaksinId != null && stokInput.isNotEmpty() && username.isNotEmpty()) {
                    isLoading = true
                    val database = FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")

                    // PERBAIKAN: Targetkan spesifik ke child vaksinnya agar aman
                    val stokRef = database.getReference("penyedia_vaksin")
                        .child(username)
                        .child("stok_vaksin")
                        .child(namaVaksinId)

                    stokRef.setValue(stokInput.toInt()).addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Stok Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show()
                            stokInput = ""
                            selectedVaksin = null
                        } else {
                            Toast.makeText(context, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Harap lengkapi semua data!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue1)),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Menyimpan..." else "Tambahkan ke Klinik", color = Color.White)
        }
    }
}