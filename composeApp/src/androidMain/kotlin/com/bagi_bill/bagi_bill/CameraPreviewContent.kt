package com.bagi_bill.bagi_bill

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * ============================================================================
 * [LANGKAH 3D] CAMERA PREVIEW CONTENT - Implementasi CameraX
 * ============================================================================
 *
 * File ini berisi implementasi kamera menggunakan CameraX.
 * Menampilkan preview kamera dan menangani pengambilan foto.
 *
 * KOMPONEN:
 * - CameraXViewfinder: Composable dari CameraX untuk menampilkan preview
 * - CameraPreviewViewModel: ViewModel untuk mengelola state kamera
 *   (Lihat file: CameraPreviewViewModel.kt)
 *
 * ALUR PENGAMBILAN FOTO:
 *
 * [User tekan tombol capture di CameraUI]
 *     │
 *     ▼
 * [controller.capture() dipanggil]
 *     │
 *     ▼
 * [controller.triggerCapture?.invoke()]
 *     │ (triggerCapture sudah diisi di LaunchedEffect di bawah)
 *     ▼
 * [viewModel.captureImage(context, onPhotoCaptured)]
 *     │
 *     ▼
 * [CameraPreviewViewModel mengambil foto dari kamera]
 *     │ - imageCapture.takePicture()
 *     │ - Simpan ke ByteArrayOutputStream
 *     │ - Konversi ke ByteArray
 *     ▼
 * [onPhotoCaptured(bytes) dipanggil]
 *     │
 *     ▼
 * [CameraUI menerima ByteArray]
 *     │ onPhotoCaptured(bytes, false)
 *     ▼
 * [CameraScreen menyimpan ke state capturedPhoto]
 *     │
 *     ▼
 * [PreviewScreen ditampilkan dengan foto yang diambil]
 *
 * LANJUT KE: CameraPreviewViewModel.kt untuk melihat implementasi CameraX detail
 * ============================================================================
 */
@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit = {},
    viewModel: CameraPreviewViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State untuk surface request dari CameraX
    val surfaceRequest by viewModel.surfaceRequest.collectAsState()

    // Notify bahwa permission sudah granted (composable ini hanya dipanggil jika granted)
    LaunchedEffect(Unit) {
        onPermissionGranted(true)
    }

    // ========== 1. BIND KAMERA KE LIFECYCLE ==========
    // Menghubungkan CameraX ke lifecycle Activity
    // Kamera akan start saat Activity resume, stop saat pause
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    // ========== 2. SAMBUNGKAN CONTROLLER KE VIEWMODEL ==========
    // Di sini kita mengisi callback di CameraController
    // Sehingga saat controller.capture() dipanggil, viewModel.captureImage() dijalankan
    LaunchedEffect(controller) {
        // Saat controller.capture() dipanggil dari UI
        // → triggerCapture?.invoke() dijalankan
        // → viewModel.captureImage() mengambil foto
        // → onPhotoCaptured(bytes) dipanggil dengan hasil foto
        controller.triggerCapture = {
            viewModel.captureImage(context, onPhotoCaptured)
        }

        // Saat controller.switchFlash() dipanggil dari UI
        // → toggleFlash?.invoke() dijalankan
        // → viewModel.toggleFlash() toggle flash on/off
        controller.toggleFlash = {
            viewModel.toggleFlash()
        }
    }

    // ========== 3. TAMPILKAN PREVIEW KAMERA ==========
    // CameraXViewfinder adalah composable dari CameraX
    // Menampilkan preview kamera secara real-time
    surfaceRequest?.let { request ->
        CameraXViewfinder(surfaceRequest = request, modifier = modifier)
    }
}