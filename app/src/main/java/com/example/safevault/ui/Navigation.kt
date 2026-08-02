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

/**
 * Aquí definimos todas las rutas (direcciones) de las pantallas de la app.
 * Es como el mapa de un GPS para movernos entre vistas.
 */
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
    // El "timón" para navegar
    val navController = rememberNavController()
    
    // Obtenemos el ViewModel para manejar los datos de login y anomalías
    val authViewModel: AuthViewModel = hiltViewModel()

    // Configuración de animaciones chulas para que las pantallas no aparezcan de golpe
    val animSpec = tween<Float>(durationMillis = 400, easing = FastOutSlowInEasing)

    // El NavHost es el contenedor donde se van a intercambiar las pantallas
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        // Animaciones de entrada y salida (estilo premium)
        enterTransition = { fadeIn(animSpec) + scaleIn(initialScale = 0.92f, animationSpec = animSpec) },
        exitTransition = { fadeOut(animSpec) + scaleOut(targetScale = 0.92f, animationSpec = animSpec) },
        popEnterTransition = { fadeIn(animSpec) + scaleIn(initialScale = 1.08f, animationSpec = animSpec) },
        popExitTransition = { fadeOut(animSpec) + scaleOut(targetScale = 1.08f, animationSpec = animSpec) }
    ) {
        // Pantalla de Inicio (Splash)
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        
        // Pantalla de Login Biométrico
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
        
        // Pantalla Principal (Menú de Cuadrícula)
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
        
        // Pantallas de las funciones (todas con su botón de atrás)
        composable(Screen.Notes.route) { NotesScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Gallery.route) { GalleryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Documents.route) { DocumentsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.History.route) { HistoryScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        
        // Pantalla donde se ven las fotos de los intrusos
        composable(Screen.Anomaly.route) { 
            // Escuchamos la lista de anomalías en tiempo real
            val anomalies by authViewModel.anomalies.collectAsState(initial = emptyList())
            AnomalyScreen(
                onBack = { navController.popBackStack() }, 
                anomalies = anomalies,
                onDelete = { authViewModel.deleteAnomaly(it) }
            )
        }
    }
}
