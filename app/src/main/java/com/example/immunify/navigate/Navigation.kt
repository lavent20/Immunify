package com.example.immunify.navigate

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.immunify.ui.presentation.be_a_mentor.FormPendaftaranFaskes
import com.example.immunify.ui.presentation.be_a_mentor.FormTambahVaksin
import com.example.immunify.ui.presentation.be_a_mentor.PemberitahuanDaftarPenyedia
import com.example.immunify.ui.presentation.home_screen.HomeScreen
import com.example.immunify.ui.presentation.home_screen.HomeViewModel
import com.example.immunify.ui.presentation.insight.InsightsScreen
import com.example.immunify.ui.presentation.insight.InsightDetailScreen
import com.example.immunify.ui.presentation.notification.NotificationScreen
import com.example.immunify.ui.presentation.login_screen.Login
import com.example.immunify.ui.presentation.login_screen.UserViewModel
import com.example.immunify.ui.presentation.onBoarding.SplashScreen
import com.example.immunify.ui.presentation.onBoarding.onBoarding1
import com.example.immunify.ui.presentation.onBoarding.onBoarding2
import com.example.immunify.ui.presentation.penyedia.DaftarPenyediaScreen
import com.example.immunify.ui.presentation.penyedia.DetailPenyediaScreen
import com.example.immunify.ui.presentation.profil.ProfilScreen
import com.example.immunify.ui.presentation.riwayat_sewa.RiwayatScreen
import com.example.immunify.ui.presentation.search.SearchScreen
import com.example.immunify.ui.presentation.signup_screen.SignUp
import com.example.immunify.ui.presentation.vaksin.CurrentVaksinViewModel
import com.example.immunify.ui.presentation.vaksin.DetailVaksinScreen
import com.example.immunify.ui.presentation.vaksin.VaksinScreen

@Composable
fun Navigasi(navController: NavHostController) {
    val userViewModel: UserViewModel = hiltViewModel()
    val vaksinViewModel: CurrentVaksinViewModel = hiltViewModel()
    
    val homeViewModel: HomeViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Route.SPLASHSCREEN
    ) {
        composable(Route.SPLASHSCREEN) {
            SplashScreen(navController = navController)
        }
        composable(Route.ONBOARDING1) {
            onBoarding1(navController = navController)
        }
        composable(Route.ONBOARDING2) {
            onBoarding2(navController = navController)
        }
        composable(Route.LOGIN) {
            Login(
                navController = navController,
                viewModelUser = userViewModel
            )
        }
        composable(Route.SIGNUP) {
            SignUp(navController = navController)
        }
        composable(Route.HOME) {
            HomeScreen(
                navController = navController,
                viewModelUser = userViewModel,
                homeViewModel = homeViewModel,
                vaksinViewModel = vaksinViewModel
            )
        }

        composable(Route.INSIGHTS) {
            InsightsScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }
        composable(
            route = Route.DETAIL_INSIGHTS,
            arguments = listOf(navArgument("diseaseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diseaseId = backStackEntry.arguments?.getString("diseaseId")
            InsightDetailScreen(
                diseaseId = diseaseId,
                navController = navController,
                homeViewModel = homeViewModel
            )
        }

        composable(Route.NOTIFICATION) {
            NotificationScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }

        composable(Route.VAKSIN) {
            VaksinScreen(
                navController = navController,
                currentVaksinViewModel = vaksinViewModel
            )
        }
        composable(Route.DETAIL_VAKSIN) {
            DetailVaksinScreen(
                navController = navController,
                currentVaksinViewModel = vaksinViewModel,
                userViewModel = userViewModel
            )
        }
        composable(Route.DAFTAR_PENYEDIA) {
            DaftarPenyediaScreen(
                navController = navController,
                vaksinViewModel = vaksinViewModel,
                userViewModel = userViewModel
            )
        }
        composable(Route.DETAIL_PENYEDIA) {
            DetailPenyediaScreen(
                navController = navController,
                userViewModel = userViewModel,
                vaksinViewModel = vaksinViewModel
            )
        }
        composable(Route.SEARCH) {
            SearchScreen(
                navController = navController,
                vaksinViewModel = vaksinViewModel
            )
        }
        composable(Route.PROFILE) {
            ProfilScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(Route.RIWAYAT) {
            RiwayatScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(Route.TRACKER) {
            com.example.immunify.ui.presentation.tracker.TrackerScreen(
                navController = navController,
                userViewModel = userViewModel
            )
        }
        composable(Route.FORM_PENYEDIA) {
            FormPendaftaranFaskes(
                navController = navController,
                viewModelUser = userViewModel)
        }
        composable(Route.PEMBERITAHUAN_DAFTAR_PENYEDIA) {
            PemberitahuanDaftarPenyedia(
                navController = navController)
        }
        composable(Route.FORM_TAMBAH_VAKSIN) {
            FormTambahVaksin(
                navController = navController,
                userViewModel = userViewModel
            )
        }
    }
}