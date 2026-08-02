package com.example.safevault.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.safevault.ui.splash.SplashScreen
import com.example.safevault.ui.auth.LoginScreen
import com.example.safevault.ui.auth.AuthViewModel
import com.example.safevault.ui.home.HomeScreen
import com.example.safevault.ui.notes.NotesScreen
import com.example.safevault.ui.gallery.GalleryScreen
import com.example.safevault.ui.documents.DocumentsScreen
import com.example.safevault.ui.history.HistoryScreen
import com.example.safevault.ui.settings.SettingsScreen
import com.example.safevault.ui.anomaly.AnomalyScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Notes : Screen("notes")
    object Gallery : Screen("gallery")
    object Documents : Screen("documents")
    object History : Screen("history")
    object Settings : Screen("settings")
    object Anomaly : Screen("anomaly")
}

@Composable
fun SafeVaultNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()

    // Animaciones profesionales: Mezcla de Fade y Escala suave
    val animSpec = tween<Float>(durationMillis = 400, easing = FastOutSlowInEasing)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn(animSpec) + scaleIn(initialScale = 0.92f, animationSpec = animSpec) },
        exitTransition = { fadeOut(animSpec) + scaleOut(targetScale = 0.92f, animationSpec = animSpec) },
        popEnterTransition = { fadeIn(animSpec) + scaleIn(initialScale = 1.08f, animationSpec = animSpec) },
        popExitTransition = { fadeOut(animSpec) + scaleOut(targetScale = 1.08f, animationSpec = animSpec) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNotes = { navController.navigate(Screen.Notes.route) },
                onNavigateToGallery = { navController.navigate(Screen.Gallery.route) },
                onNavigateToDocuments = { navController.navigate(Screen.Documents.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAnomaly = { navController.navigate(Screen.Anomaly.route) }
            )
        }
        composable(Screen.Notes.route) { NotesScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Gallery.route) { GalleryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Documents.route) { DocumentsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.History.route) { HistoryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Anomaly.route) { 
            val anomalies by authViewModel.anomalies.collectAsState(initial = emptyList())
            AnomalyScreen(onBack = { navController.popBackStack() }, anomalies = anomalies) 
        }
    }
}
