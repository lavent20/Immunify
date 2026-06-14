package com.example.immunify

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.immunify.navigate.CustomScaffold
import com.example.immunify.navigate.Navigasi
import com.example.immunify.navigate.Route
import com.example.immunify.ui.theme.ImmunifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val showBar = listOf(
        Route.HOME,
        Route.VAKSIN,
        Route.DAFTAR_PENYEDIA,
        Route.TRACKER,
        Route.PROFILE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImmunifyTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currPage = backStackEntry?.destination?.route

                Surface {
                    CustomScaffold(
                        navController = navController,
                        showBottomBar = currPage in showBar
                    ) {
                        Navigasi(navController = navController)
                    }
                }
            }
        }
    }
}