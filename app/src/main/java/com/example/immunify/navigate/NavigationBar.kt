package com.example.immunify.navigate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun NavigasiBar(navController: NavController) {
    var isPenyedia by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance("https://immunify-2e6d6-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentEmail = currentUser.email?.trim()?.lowercase()
                    for (userSnapshot in snapshot.children) {
                        val dbEmail = userSnapshot.child("email").value.toString().trim().lowercase()
                        if (dbEmail == currentEmail) {
                            // Cek isPenyedia atau mentor
                            val statusPenyedia = userSnapshot.child("isPenyedia").value.toString() == "true" || userSnapshot.child("mentor").value.toString() == "true"
                            isPenyedia = statusPenyedia
                            break
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            database.getReference("users").addValueEventListener(listener)
        }
    }

    // List Navigasi Dinamis
    val items = mutableListOf(
        BottomNavItem("Home", Route.HOME, Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem("Clinics", Route.VAKSIN, Icons.Outlined.LocalHospital, Icons.Filled.LocalHospital)
    )

    // HANYA TAMBAHKAN MENU TRACKER JIKA BUKAN PENYEDIA (PENGGUNA BIASA)
    if (!isPenyedia) {
        items.add(BottomNavItem("Tracker", Route.TRACKER, Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth))
    }

    // Menu Profil selalu ada di ujung kanan
    items.add(BottomNavItem("Profile", Route.PROFILE, Icons.Outlined.AccountCircle, Icons.Filled.AccountCircle))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Route.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selected) item.iconSelected else item.icon,
                            contentDescription = item.name,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.name,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0D2B1E),
                    selectedTextColor = Color(0xFF0D2B1E),
                    unselectedIconColor = Color(0xFF9BB5A8),
                    unselectedTextColor = Color(0xFF9BB5A8),
                    indicatorColor = Color(0xFFE8F7EF)
                )
            )
        }
    }
}