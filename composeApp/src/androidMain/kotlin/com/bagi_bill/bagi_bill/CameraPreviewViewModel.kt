package com.bagi_bill.bagi_bill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.awaitCancellation
import androidx.concurrent.futures.await
import java.io.ByteArrayOutputStream

class CameraPreviewViewModel : ViewModel() {

    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    private val imageCaptureUseCase = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    private var camera: Camera? = null

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

    // === BAGIAN INI YANG PENTING (LOGIC ROTASI) ===
    fun captureImage(context: Context, onPhotoCaptured: (ByteArray?) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)

        imageCaptureUseCase.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    // 1. Ambil info rotasi dari sensor kamera (Misal: 90 derajat)
                    val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()

                    // 2. Ubah ImageProxy ke Bitmap agar bisa diedit
                    val bitmap = image.toBitmap()

                    // 3. Siapkan Matrix untuk memutar gambar
                    val matrix = Matrix()

                    // A. Putar gambar sesuai sensor (Biar TEGAK LURUS)
                    matrix.postRotate(rotationDegrees)

                    // 4. Buat Bitmap baru yang sudah diputar
                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )

                    // 5. Kompres balik jadi ByteArray (JPEG)
                    val stream = ByteArrayOutputStream()
                    // Quality 100 = Kualitas Maksimal
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val byteArray = stream.toByteArray()

                    // 6. Kirim ke UI
                    onPhotoCaptured(byteArray)

                } catch (e: Exception) {
                    e.printStackTrace()
                    onPhotoCaptured(null)
                } finally {
                    // PENTING: Wajib tutup image biar memori tidak bocor
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onPhotoCaptured(null)
            }
        })
    }

    fun toggleFlash() {
        camera?.let { cam ->
            val currentTorchState = cam.cameraInfo.torchState.value ?: 0
            cam.cameraControl.enableTorch(currentTorchState == 0)
        }
    }
}