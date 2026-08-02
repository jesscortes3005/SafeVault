package com.example.safevault.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.example.safevault.biometric.BiometricAuthenticator
import com.example.safevault.utils.SecurityUtils
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * Esta pantalla es la que pide la huella o cara para entrar.
 * Tiene todo el sistema de seguridad de los 3 intentos y bloqueo.
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    // LocalContext nos sirve para sacar Toasts y otras cosas del cel
    val context = LocalContext.current
    // El lifecycleOwner lo ocupamos para que la cámara sepa cuándo prenderse/apagarse
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Launcher para pedir permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Se requiere permiso de cámara para seguridad", Toast.LENGTH_LONG).show()
        }
    }

    // Pedimos el permiso al entrar si no lo tenemos
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Nuestra herramienta para checar la huella
    val biometricAuthenticator = remember { BiometricAuthenticator(context) }
    
    // --- ESTADOS DE LA PANTALLA ---
    // failedAttempts: cuenta cuántas veces la regó el usuario
    var failedAttempts by remember { mutableIntStateOf(0) }
    // lockoutTimeRemaining: segundos que faltan para desbloquear
    var lockoutTimeRemaining by remember { mutableIntStateOf(0) }
    // isLockedOut: ¿estamos en modo "castigado"?
    val isLockedOut = lockoutTimeRemaining > 0

    // Colores chidos del tema azul
    val vaultBlueStart = Color(0xFF0F172A)
    val vaultBlueEnd = Color(0xFF1E293B)
    val vaultAccentColor = Color(0xFF38BDF8)

    // Animación de pulso del escudo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // El reloj que cuenta hacia atrás cuando nos bloquean
    LaunchedEffect(lockoutTimeRemaining) {
        if (lockoutTimeRemaining > 0) {
            delay(1.seconds)
            lockoutTimeRemaining -= 1
        }
    }

    /**
     * Esta función lanza el cuadrito de la huella.
     * Si falla 3 veces, nos bloquea y toma foto al intruso.
     */
    val launchBiometric = {
        if (!isLockedOut) {
            if (biometricAuthenticator.isBiometricAvailable()) {
                biometricAuthenticator.promptBiometricAuth(
                    title = "Ingresar a SafeVault",
                    subtitle = "Usa tu huella o rostro para continuar",
                    negativeButtonText = "Usar PIN",
                    onSuccess = { 
                        // Si entró bien, reseteamos los intentos
                        failedAttempts = 0
                        onLoginSuccess() 
                    },
                    onError = { code, _ -> 
                        // Si el error no es que el usuario canceló...
                        if (code != 13) {
                            failedAttempts += 1
                            // Checamos si ya llegó al límite de 3
                            handleFailedAttempt(failedAttempts, context, { lockoutTimeRemaining = 30 }) {
                                // CAPTURA SILENCIOSA: Tomamos foto y la mandamos a la base de datos
                                // Verificamos permiso de cámara antes de capturar
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    SecurityUtils.captureAnomalyPhoto(context, lifecycleOwner) { path ->
                                        authViewModel.recordAnomaly(path)
                                    }
                                }
                            }
                        }
                    },
                    onFailed = {
                        // Si el sensor no reconoció la huella
                        failedAttempts += 1
                        handleFailedAttempt(failedAttempts, context, { lockoutTimeRemaining = 30 }) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                SecurityUtils.captureAnomalyPhoto(context, lifecycleOwner) { path ->
                                    authViewModel.recordAnomaly(path)
                                }
                            }
                        }
                    }
                )
            } else {
                Toast.makeText(context, "Biometría no configurada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Al abrir la pantalla, intentamos pedir la huella de una vez
    LaunchedEffect(Unit) {
        launchBiometric()
    }

    // --- DISEÑO DE LA INTERFAZ ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(vaultBlueStart, vaultBlueEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Parte de arriba: Logo y Mensajes
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SafeVault",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                // Texto que cambia si estás bloqueado o no
                AnimatedContent(
                    targetState = isLockedOut,
                    label = "statusText"
                ) { locked ->
                    if (locked) {
                        Text(
                            text = "Acceso bloqueado.\nReintenta en $lockoutTimeRemaining segundos.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFEF4444), // Rojo alerta
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        )
                    } else {
                        Text(
                            text = "Protegemos tu información con los más altos estándares de seguridad.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        )
                    }
                }
            }

            // El escudo central con animación de pulso
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize().scale(if (isLockedOut) 1f else scale),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = if (isLockedOut) Color.Red.copy(alpha = 0.1f) else vaultAccentColor.copy(alpha = 0.1f)
                ) {}
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (isLockedOut) Color.Red.copy(alpha = 0.6f) else vaultAccentColor,
                    modifier = Modifier.size(72.dp)
                )
            }

            // Parte de abajo: Los botones para entrar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón principal de biometría
                Button(
                    onClick = { launchBiometric() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .alpha(if (isLockedOut) 0.5f else 1f),
                    enabled = !isLockedOut,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = vaultAccentColor,
                        contentColor = vaultBlueStart
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (isLockedOut) "BLOQUEADO ($lockoutTimeRemaining s)" else "INGRESAR",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Botón para PIN (por si no sirve la huella)
                TextButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLockedOut
                ) {
                    Text(
                        "INGRESAR CON CONTRASEÑA",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (isLockedOut) Color.White.copy(alpha = 0.2f) else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

/**
 * Función chiquita para manejar cuando el usuario la riega al poner la huella.
 * Avisa cuántos intentos lleva o si ya lo bloqueamos.
 */
private fun handleFailedAttempt(
    attempts: Int,
    context: android.content.Context,
    onLockout: () -> Unit,
    onCapture: () -> Unit
) {
    if (attempts >= 3) {
        onLockout()
        // Agregamos un pequeño retraso para asegurar que el cuadro de biometría se cerró
        onCapture()
    } else {
        Toast.makeText(context, "Intento fallido: $attempts/3", Toast.LENGTH_SHORT).show()
    }
}
