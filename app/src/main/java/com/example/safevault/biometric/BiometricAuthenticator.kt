package com.example.safevault.biometric

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Clase para manejar todo lo relacionado con la huella y el rostro (Biometría).
 * Así no tenemos todo el relajo en la pantalla y es más limpio.
 */
class BiometricAuthenticator(private val context: Context) {

    // Instancia que nos ayuda a checar si el cel tiene biometría
    private val biometricManager = BiometricManager.from(context)

    /**
     * Función para buscar la "Activity" (ventana) actual de forma segura.
     * Es como un buscador para encontrar dónde estamos parados.
     */
    private fun Context.findActivity(): FragmentActivity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is FragmentActivity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    /**
     * Checa si el celular tiene sensor de huella o cara configurado y listo.
     * Actualizado para incluir BIOMETRIC_WEAK (Face Unlock en muchos Android).
     */
    fun isBiometricAvailable(): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                           BiometricManager.Authenticators.BIOMETRIC_WEAK
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Esta función es la que saca el cuadrito del sistema pidiendo la huella o cara.
     * Le pasamos qué queremos que diga y qué hacer si sale bien o mal.
     */
    fun promptBiometricAuth(
        title: String,
        subtitle: String,
        negativeButtonText: String,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    ) {
        // Buscamos la activity porque BiometricPrompt la ocupa de a fuerzas
        val activity = context.findActivity() ?: return
        
        // El executor es para que se ejecute en el hilo principal (donde se ve la app)
        val executor = ContextCompat.getMainExecutor(context)
        
        // Configuramos las respuestas del sensor
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                // Si la huella/cara jaló bien
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                // Si hubo un error del sistema (como que el sensor esté sucio)
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString)
                }

                // Si puso una huella que no es del dueño
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )

        // Aquí armamos el diseño del aviso que sale en pantalla
        // Permitimos STRONG y WEAK para que Face Unlock funcione en más dispositivos
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            .build()

        // ¡Arrancamos la autenticación!
        biometricPrompt.authenticate(promptInfo)
    }
}
