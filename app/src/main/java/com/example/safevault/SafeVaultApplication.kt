package com.example.safevault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Esta clase es el "cerebro" que arranca todo. 
 * Usamos @HiltAndroidApp para que la librería de Hilt sepa que aquí empieza 
 * la magia de la inyección de dependencias (para no andar creando objetos a mano).
 */
@HiltAndroidApp
class SafeVaultApplication : Application()
