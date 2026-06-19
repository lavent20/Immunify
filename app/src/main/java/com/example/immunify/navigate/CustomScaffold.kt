package com.example.immunify.navigate

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun CustomScaffold(
    navController: NavController,
    showBottomBar: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigasiBar(navController = navController)
            }
        },
        content = content
    )
}