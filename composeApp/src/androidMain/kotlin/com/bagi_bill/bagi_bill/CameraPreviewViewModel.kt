package com.bagi_bill.bagi_bill

import android.content.Context
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
import androidx.concurrent.futures.await // Wajib ada library concurrent-futures-ktx
import java.nio.ByteBuffer

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

    fun toggleFlash() {
        camera?.let { cam ->
            val currentTorchState = cam.cameraInfo.torchState.value ?: 0
            cam.cameraControl.enableTorch(currentTorchState == 0)
        }
    }

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

    fun captureImage(context: Context, onPhotoCaptured: (ByteArray?) -> Unit) {
        // Kita butuh Executor (Main Thread) buat jalanin kamera
        val executor = ContextCompat.getMainExecutor(context)

        // Perintah CameraX: "Ambil Gambar!"
        imageCaptureUseCase.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {

            // Kalau Berhasil:
            override fun onCaptureSuccess(image: ImageProxy) {
                // 1. Ambil data mentah (Buffer) dari memori kamera
                val buffer: ByteBuffer = image.planes[0].buffer

                // 2. Siapkan wadah ByteArray sesuai ukuran gambar
                val bytes = ByteArray(buffer.remaining())

                // 3. Salin data dari Buffer ke ByteArray
                buffer.get(bytes)

                // 4. Kirim paketnya ke UI Common
                onPhotoCaptured(bytes)

                // 5. WAJIB TUTUP IMAGE (Kalau tidak, kamera bakal macet)
                image.close()
            }

            // Kalau Gagal:
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onPhotoCaptured(null) // Lapor gagal
            }
        })
    }
}