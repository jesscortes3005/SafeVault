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
import com.example.safevault.biometric.BiometricAuthenticator
import com.example.safevault.utils.SecurityUtils
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val biometricAuthenticator = remember { BiometricAuthenticator(context) }
    
    // Estados de seguridad
    var failedAttempts by remember { mutableIntStateOf(0) }
    var lockoutTimeRemaining by remember { mutableIntStateOf(0) }
    val isLockedOut = lockoutTimeRemaining > 0

    // Colores del tema azul
    val vaultBlueStart = Color(0xFF0F172A)
    val vaultBlueEnd = Color(0xFF1E293B)
    val vaultAccentColor = Color(0xFF38BDF8)

    // Animación de pulso
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

    // Lógica del temporizador de bloqueo
    LaunchedEffect(lockoutTimeRemaining) {
        if (lockoutTimeRemaining > 0) {
            delay(1.seconds)
            lockoutTimeRemaining -= 1
        }
    }

    val launchBiometric = {
        if (!isLockedOut) {
            if (biometricAuthenticator.isBiometricAvailable()) {
                biometricAuthenticator.promptBiometricAuth(
                    title = "Ingresar a SafeVault",
                    subtitle = "Usa tu huella o rostro para continuar",
                    negativeButtonText = "Usar PIN",
                    onSuccess = { 
                        failedAttempts = 0
                        onLoginSuccess() 
                    },
                    onError = { code, err -> 
                        if (code != 13) {
                            failedAttempts += 1
                            handleFailedAttempt(failedAttempts, context, { lockoutTimeRemaining = 30 }) {
                                // Captura de foto silenciosa al bloquearse
                                SecurityUtils.captureAnomalyPhoto(context, lifecycleOwner) { path ->
                                    authViewModel.recordAnomaly(path)
                                }
                            }
                        }
                    },
                    onFailed = {
                        failedAttempts += 1
                        handleFailedAttempt(failedAttempts, context, { lockoutTimeRemaining = 30 }) {
                            SecurityUtils.captureAnomalyPhoto(context, lifecycleOwner) { path ->
                                authViewModel.recordAnomaly(path)
                            }
                        }
                    }
                )
            } else {
                Toast.makeText(context, "Biometría no configurada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        launchBiometric()
    }

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
            // Sección Superior
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
                
                AnimatedContent(
                    targetState = isLockedOut,
                    label = "statusText"
                ) { locked ->
                    if (locked) {
                        Text(
                            text = "Acceso bloqueado.\nReintenta en $lockoutTimeRemaining segundos.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFEF4444),
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

            // Icono Central
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

            // Sección Inferior
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

private fun handleFailedAttempt(
    attempts: Int,
    context: android.content.Context,
    onLockout: () -> Unit,
    onCapture: () -> Unit
) {
    if (attempts >= 3) {
        onLockout()
        onCapture()
    } else {
        Toast.makeText(context, "Intento fallido: $attempts/3", Toast.LENGTH_SHORT).show()
    }
}
