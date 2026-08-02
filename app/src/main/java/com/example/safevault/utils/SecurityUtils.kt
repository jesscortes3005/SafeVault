package com.example.safevault.utils

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object SecurityUtils {

    fun captureAnomalyPhoto(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        onPhotoCaptured: (String) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
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

                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onPhotoCaptured(photoFile.absolutePath)
                            cameraProvider.unbindAll()
                        }

                        override fun onError(exc: ImageCaptureException) {
                            cameraProvider.unbindAll()
                        }
                    }
                )
            } catch (exc: Exception) {
                // Error binding camera
            }
        }, ContextCompat.getMainExecutor(context))
    }
}
