package com.example.immunify.ui.presentation.profil

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.example.immunify.ui.presentation.login_screen.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: ProfilViewModel = hiltViewModel()
) {
    val username by userViewModel.username.collectAsState()
    val status by viewModel.status.collectAsState()

    // Cek status profil saat layar dibuka
    LaunchedEffect(username) {
        viewModel.checkUserStatus(username)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Profil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.blue2),
                            Color.White
                        ),
                        startY = 300f
                    )
                )
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = R.drawable.foto_profil),
                contentDescription = "Foto Profil",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = username.ifEmpty { "Pengguna" },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = status,
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(36.dp))

            ProfilMenu(navController = navController, status = status)
        }
    }
}

@Composable
fun ProfilMenu(navController: NavController, status: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Menu 1: Daftar Faskes atau Info Faskes
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "Daftar Faskes",
                    modifier = Modifier.size(28.dp),
                    tint = colorResource(id = R.color.blue1)
                )

                if (status == "Pengguna") {
                    ClickableText(
                        text = AnnotatedString("Daftar Sebagai Fasilitas Kesehatan"),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        onClick = { navController.navigate(Route.FORM_PENYEDIA) }
                    )
                } else {
                    ClickableText(
                        text = AnnotatedString("Kelola Stok Vaksin"),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        onClick = { navController.navigate(Route.FORM_TAMBAH_VAKSIN) }
                    )
                }
            }

            if (status == "Pengguna") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Riwayat",
                        modifier = Modifier.size(28.dp),
                        tint = colorResource(id = R.color.blue1)
                    )
                    ClickableText(
                        text = AnnotatedString("Riwayat Imunisasi"),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        onClick = { navController.navigate(Route.RIWAYAT) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

            // Menu 3: Logout
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Red
                )
                ClickableText(
                    text = AnnotatedString("Keluar (Logout)"),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, color = Color.Red),
                    onClick = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0)
                        }
                    }
                )
            }
        }
    }
}