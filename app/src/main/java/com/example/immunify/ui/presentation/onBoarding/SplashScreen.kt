package com.example.immunify.ui.presentation.onBoarding

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.immunify.R
import com.example.immunify.navigate.Route
import com.google.firebase.auth.FirebaseAuth // Tambahkan import ini
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true){
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(3000L)
        
        // Memeriksa status keberadaan token autentikasi pengguna saat ini
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser != null) {
            // Sesi aktif ditemukan, bypass onboarding menuju Beranda langsung
            navController.navigate(Route.HOME) {
                popUpTo(Route.SPLASHSCREEN) { inclusive = true }
            }
        } else {
            // Pengguna baru atau belum login, diarahkan ke panduan Onboarding
            navController.navigate(Route.ONBOARDING1) {
                popUpTo(Route.SPLASHSCREEN) { inclusive = true }
            }
        }
    }
    Box {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.blue1)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ){
            Image(
                painter = painterResource(id = R.drawable.logo_immunify),
                contentDescription = "logo",
                modifier = Modifier.size(230.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}