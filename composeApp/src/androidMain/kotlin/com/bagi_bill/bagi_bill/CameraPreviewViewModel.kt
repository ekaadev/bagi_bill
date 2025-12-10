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

/**
 * ============================================================================
 * [LANGKAH 3E] CAMERA PREVIEW VIEWMODEL - Implementasi CameraX Detail
 * ============================================================================
 *
 * ViewModel ini mengelola semua operasi kamera menggunakan CameraX.
 * Bertanggung jawab untuk:
 * - Menampilkan preview kamera
 * - Mengambil foto
 * - Toggle flash
 *
 * KOMPONEN CAMERAX:
 * 1. Preview - untuk menampilkan preview kamera di layar
 * 2. ImageCapture - untuk mengambil foto
 * 3. Camera - referensi ke kamera device
 * 4. ProcessCameraProvider - untuk bind use case ke lifecycle
 *
 * ALUR PENGAMBILAN FOTO (captureImage):
 *
 * [controller.triggerCapture?.invoke()]
 *     │
 *     ▼
 * [captureImage() dipanggil]
 *     │
 *     ▼
 * [imageCaptureUseCase.takePicture()]
 *     │ - CameraX mengambil foto dari sensor
 *     ▼
 * [onCaptureSuccess(image: ImageProxy)]
 *     │
 *     ├── 1. Ambil info rotasi dari sensor (rotationDegrees)
 *     ├── 2. Konversi ImageProxy ke Bitmap
 *     ├── 3. Putar Bitmap sesuai rotasi sensor (agar tegak lurus)
 *     ├── 4. Kompres Bitmap ke JPEG
 *     ├── 5. Konversi ke ByteArray
 *     │
 *     ▼
 * [onPhotoCaptured(byteArray)]
 *     │
 *     ▼
 * [ByteArray dikirim kembali ke CameraUI → CameraScreen → PreviewScreen]
 *
 * ============================================================================
 * RINGKASAN ALUR LENGKAP (DARI AWAL SAMPAI AKHIR):
 * ============================================================================
 *
 * [1. HomeScreen] User tekan FAB
 *     │ showCamera = true
 *     ▼
 * [2. CameraScreen] Router - tampilkan CameraUI
 *     │ capturedPhoto == null
 *     ▼
 * [3. CameraUI] User tekan tombol capture
 *     │ controller.capture()
 *     ▼
 * [3A. CameraController] Jembatan ke platform
 *     │ triggerCapture?.invoke()
 *     ▼
 * [3B. CameraPreview] expect/actual declaration
 *     │
 *     ▼
 * [3C. CameraPreview.android] Cek permission
 *     │ granted → CameraPreviewContent
 *     ▼
 * [3D. CameraPreviewContent] Tampilkan preview
 *     │ viewModel.captureImage()
 *     ▼
 * [3E. CameraPreviewViewModel] Ambil foto dengan CameraX (FILE INI)
 *     │ imageCaptureUseCase.takePicture()
 *     │ → ImageProxy → Bitmap → ByteArray
 *     │ → onPhotoCaptured(byteArray)
 *     ▼
 * [Kembali ke CameraUI]
 *     │ onPhotoCaptured(bytes, false)
 *     ▼
 * [Kembali ke CameraScreen]
 *     │ capturedPhoto = bytes
 *     ▼
 * [4. PreviewScreen] Tampilkan preview foto
 *     │ User tekan "Pakai foto ini"
 *     │ onConfirm() → onPhotoConfirmed(bytes)
 *     ▼
 * [Kembali ke HomeScreen]
 *     │ onPhotoConfirmed { bytes -> ... }
 *     │ ByteArray siap digunakan!
 *     ▼
 * SELESAI - ByteArray berisi data gambar JPEG
 *
 * ============================================================================
 */
class CameraPreviewViewModel : ViewModel() {

    // State untuk surface request - digunakan oleh CameraXViewfinder
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    // ========== USE CASE: PREVIEW ==========
    // Untuk menampilkan preview kamera di layar
    private val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    // ========== USE CASE: IMAGE CAPTURE ==========
    // Untuk mengambil foto
    // CAPTURE_MODE_MINIMIZE_LATENCY = prioritaskan kecepatan, bukan kualitas
    private val imageCaptureUseCase = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    // Referensi ke kamera device
    private var camera: Camera? = null

    /**
     * Bind kamera ke lifecycle Activity
     * Kamera akan otomatis start/stop sesuai lifecycle
     */
    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.getInstance(appContext).await()
        processCameraProvider.unbindAll()

        try {
            // Bind preview dan imageCapture ke kamera belakang
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

    /**
     * ============================================================================
     * FUNGSI UTAMA: AMBIL FOTO DAN KONVERSI KE BYTEARRAY
     * ============================================================================
     *
     * Fungsi ini dipanggil saat user tekan tombol capture.
     * Proses:
     * 1. Ambil foto dari kamera menggunakan CameraX
     * 2. Rotasi gambar agar orientasinya benar
     * 3. Kompres ke format JPEG
     * 4. Konversi ke ByteArray
     * 5. Kirim ByteArray melalui callback
     */
    fun captureImage(context: Context, onPhotoCaptured: (ByteArray?) -> Unit) {
        val executor = ContextCompat.getMainExecutor(context)

        imageCaptureUseCase.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    // ========== 1. AMBIL INFO ROTASI ==========
                    // Sensor kamera punya orientasi default (biasanya landscape)
                    // rotationDegrees menunjukkan berapa derajat gambar harus diputar
                    val rotationDegrees = image.imageInfo.rotationDegrees.toFloat()

                    // ========== 2. KONVERSI KE BITMAP ==========
                    // ImageProxy adalah format internal CameraX
                    // Kita perlu konversi ke Bitmap agar bisa dimanipulasi
                    val bitmap = image.toBitmap()

                    // ========== 3. SIAPKAN MATRIX ROTASI ==========
                    val matrix = Matrix()
                    // Putar gambar sesuai rotasi sensor agar orientasinya benar
                    matrix.postRotate(rotationDegrees)

                    // ========== 4. BUAT BITMAP BARU YANG SUDAH DIPUTAR ==========
                    val rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )

                    // ========== 5. KOMPRES KE JPEG DAN KONVERSI KE BYTEARRAY ==========
                    val stream = ByteArrayOutputStream()
                    // Quality 100 = kualitas maksimal (0-100)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val byteArray = stream.toByteArray()

                    // ========== 6. KIRIM BYTEARRAY KE CALLBACK ==========
                    // ByteArray ini akan dikirim balik ke:
                    // CameraPreviewContent → CameraUI → CameraScreen → PreviewScreen
                    onPhotoCaptured(byteArray)

                } catch (e: Exception) {
                    e.printStackTrace()
                    onPhotoCaptured(null)  // Kirim null jika error
                } finally {
                    // PENTING: Wajib close ImageProxy untuk mencegah memory leak
                    image.close()
                }
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onPhotoCaptured(null)  // Kirim null jika error
            }
        })
    }

    /**
     * Toggle flash on/off
     */
    fun toggleFlash() {
        camera?.let { cam ->
            val currentTorchState = cam.cameraInfo.torchState.value ?: 0
            // Jika saat ini off (0), nyalakan. Jika on (1), matikan.
            cam.cameraControl.enableTorch(currentTorchState == 0)
        }
    }
}