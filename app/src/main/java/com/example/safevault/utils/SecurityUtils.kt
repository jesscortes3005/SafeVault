package com.example.safevault.utils

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.*

/**
 * Aquí guardamos trucos de seguridad, como tomar fotos a escondidas.
 */
object SecurityUtils {
    private const val TAG = "SecurityUtils"

    /**
     * Esta función toma una foto con la cámara frontal sin que nadie se entere.
     * Ideal para capturar al "chismoso" que intenta entrar a la app.
     */
    fun captureAnomalyPhoto(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onPhotoCaptured: (String) -> Unit
    ) {
        // Pedimos permiso al sistema para usar la cámara
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Configuramos la captura de imagen para que sea rápida (poca latencia)
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Siempre usamos la cámara frontal para verle la cara al intruso
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                Log.d(TAG, "Binding camera for silent capture...")
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageCapture
                )

                val photoFile = File(
                    context.filesDir,
                    "anomaly_${System.currentTimeMillis()}.jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                Log.d(TAG, "Taking picture...")
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            Log.d(TAG, "Photo saved successfully at: ${photoFile.absolutePath}")
                            onPhotoCaptured(photoFile.absolutePath)
                            cameraProvider.unbindAll()
                        }

                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                            cameraProvider.unbindAll()
                        }
                    }
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Error binding camera: ${exc.message}", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
