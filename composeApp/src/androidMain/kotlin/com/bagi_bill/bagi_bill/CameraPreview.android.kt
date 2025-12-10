package com.bagi_bill.bagi_bill

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * ============================================================================
 * [LANGKAH 3C] CAMERA PREVIEW ANDROID - Implementasi actual untuk Android
 * ============================================================================
 *
 * File ini adalah IMPLEMENTASI ACTUAL dari CameraPreview untuk platform Android.
 * Menggunakan:
 * - CameraX untuk mengakses kamera device
 * - Accompanist Permissions untuk menangani izin kamera
 *
 * ALUR:
 *
 * [CameraUI memanggil CameraPreview()]
 *     │
 *     ▼
 * [Cek izin kamera menggunakan rememberPermissionState]
 *     │
 *     ├── Izin GRANTED
 *     │       │
 *     │       ▼
 *     │   [CameraPreviewContent ditampilkan]
 *     │   (Lihat file: CameraPreviewContent.kt)
 *     │       │
 *     │       │ - Menampilkan preview kamera
 *     │       │ - Mengatur CameraController callbacks
 *     │       │ - Mengambil foto saat controller.capture() dipanggil
 *     │       ▼
 *     │   [Foto diambil → konversi ke ByteArray]
 *     │       │
 *     │       ▼
 *     │   [onPhotoCaptured(bytes) dipanggil]
 *     │
 *     └── Izin DENIED
 *             │
 *             ▼
 *         [permissionDeniedContent ditampilkan]
 *         (UI untuk meminta izin kamera)
 *
 * LANJUT KE: CameraPreviewContent.kt untuk melihat implementasi CameraX
 * ============================================================================
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit,
    permissionDeniedContent: @Composable (onRequest: () -> Unit) -> Unit
) {
    // ========== CEK PERMISSION KAMERA ==========
    // Menggunakan Accompanist Permissions untuk menangani izin
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Update status izin ke parent composable (CameraUI)
    // Ini memungkinkan CameraUI untuk mengatur state tombol capture dan flash
    androidx.compose.runtime.LaunchedEffect(cameraPermissionState.status.isGranted) {
        onPermissionGranted(cameraPermissionState.status.isGranted)
    }

    if (cameraPermissionState.status.isGranted) {
        // ========== IZIN DIBERIKAN ==========
        // Tampilkan preview kamera menggunakan CameraPreviewContent
        // Lihat file: CameraPreviewContent.kt
        CameraPreviewContent(
            modifier = modifier.fillMaxSize(),
            controller = controller,
            onPhotoCaptured = onPhotoCaptured
        )
    } else {
        // ========== IZIN DITOLAK ==========
        // Tampilkan UI untuk meminta izin (didefinisikan di CameraUI)
        // Lambda onRequest akan memanggil launchPermissionRequest()
        permissionDeniedContent {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}