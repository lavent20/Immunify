package com.example.immunify.navigate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavigasiBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem(
            name = "Home",
            route = Route.HOME,
            icon = Icons.Outlined.Home,
            iconSelected = Icons.Filled.Home
        ),
        BottomNavItem(
            name = "Clinics",
            route = Route.DAFTAR_PENYEDIA,
            icon = Icons.Outlined.LocalHospital,
            iconSelected = Icons.Filled.LocalHospital
        ),
        BottomNavItem(
            name = "Tracker",
            route = Route.TRACKER,
            icon = Icons.Outlined.CalendarMonth,
            iconSelected = Icons.Filled.CalendarMonth
        ),
        BottomNavItem(
            name = "Profile",
            route = Route.PROFILE,
            icon = Icons.Outlined.AccountCircle,
            iconSelected = Icons.Filled.AccountCircle
        )
    )

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
                    navController.navigate(item.route) {
                        popUpTo(Route.HOME) { 
                            saveState = true 
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selected) item.iconSelected else item.icon,
                            contentDescription = item.name,
                            modifier = androidx.compose.ui.Modifier.size(24.dp)
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