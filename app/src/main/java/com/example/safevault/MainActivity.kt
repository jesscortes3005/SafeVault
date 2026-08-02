package com.example.safevault

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.safevault.ui.SafeVaultNavigation
import com.example.safevault.ui.theme.SafeVaultTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Esta es la actividad principal, la puerta de entrada de la app.
 * Ponemos @AndroidEntryPoint para que Hilt pueda inyectar cosas aquí si lo necesitamos.
 * Heredamos de FragmentActivity porque el sistema de huella (Biometric) lo pide a gritos.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hacemos que la app use toda la pantalla (hasta los bordes)
        enableEdgeToEdge()
        
        // Aquí dibujamos la interfaz con Compose
        setContent {
            SafeVaultTheme {
                // Llamamos a nuestro sistema de navegación para que sepa qué pantalla mostrar
                SafeVaultNavigation()
            }
        }
    }
}
