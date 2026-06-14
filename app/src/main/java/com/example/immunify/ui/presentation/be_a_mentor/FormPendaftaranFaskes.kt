package com.example.immunify.ui.presentation.be_a_mentor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FormPendaftaranFaskes(
    navController: NavController,
    viewModelUser: UserViewModel
) {
    val username by viewModelUser.username.collectAsState()
    val database = FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")

    var namaFaskes by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(colorResource(id = R.color.blue2), Color.White)))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Daftar Fasilitas Kesehatan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))

        InputFaskes(label = "Nama Fasilitas Kesehatan", value = namaFaskes, onValueChange = { namaFaskes = it })
        InputFaskes(label = "Deskripsi / Lokasi", value = deskripsi, onValueChange = { deskripsi = it })

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Text(text = "Saya menyatakan data ini benar.", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue1)),
            enabled = isChecked,
            onClick = {
                val faskesData = mapOf<String, Any>(
                    "namaLengkap" to namaFaskes,
                    "pengalaman" to deskripsi
                )

                database.getReference("penyedia_vaksin").child(username).updateChildren(faskesData)
                database.getReference("users").child(username).child("isPenyedia").setValue(true)

                navController.navigate(Route.PEMBERITAHUAN_DAFTAR_PENYEDIA)
            }
        ) {
            Text("Submit Pendaftaran")
        }
    }
}

@Composable
fun InputFaskes(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 16.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        shape = RoundedCornerShape(8.dp)
    )
}