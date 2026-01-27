package com.bagi_bill.bagi_bill.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.ByteArrayOutputStream

/**
 * ViewModel managing CameraX operations: preview, capture, and flash toggle.
 */
class CameraPreviewViewModel : ViewModel() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val previewUseCase = Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
        .apply {
            setSurfaceProvider { newSurfaceRequest ->
                _surfaceRequest.update { newSurfaceRequest }
            }
        }

    private val imageCaptureUseCase = ImageCapture.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    private var camera: Camera? = null

    /** Bind camera to lifecycle - auto starts/stops with activity */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.getInstance(appContext).await()
        processCameraProvider.unbindAll()

        try {
            camera = processCameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                previewUseCase,
                imageCaptureUseCase
            )
            awaitCancellation()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            processCameraProvider.unbindAll()
        }
    }

    /** Capture image, rotate to correct orientation, compress to JPEG ByteArray */
    fun captureImage(context: Context, onPhotoCaptured: (ByteArray?) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)

        imageCaptureUseCase.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()
                    val bitmap = image.toBitmap()
                    
                    val matrix = Matrix().apply { postRotate(rotationDegrees) }
                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )

                    val stream = ByteArrayOutputStream()
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    onPhotoCaptured(stream.toByteArray())
                } catch (e: Exception) {
                    e.printStackTrace()
                    onPhotoCaptured(null)
                } finally {
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onPhotoCaptured(null)
            }
        })
    }

    /** Toggle flash on/off */
    fun toggleFlash() {
        camera?.let { cam ->
            val currentTorchState = cam.cameraInfo.torchState.value ?: 0
            cam.cameraControl.enableTorch(currentTorchState == 0)
        }
    }
}
