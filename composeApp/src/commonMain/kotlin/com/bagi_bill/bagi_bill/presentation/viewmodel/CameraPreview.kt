package com.bagi_bill.bagi_bill

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * ============================================================================
 * [LANGKAH 3B] CAMERA PREVIEW - Deklarasi expect/actual
 * ============================================================================
 *
 * File ini berisi DEKLARASI (expect) untuk CameraPreview.
 * Implementasi sebenarnya (actual) ada di folder platform masing-masing:
 * - androidMain: CameraPreview.android.kt (menggunakan CameraX)
 * - iosMain: CameraScreen.ios.kt (menggunakan AVFoundation)
 *
 * KONSEP EXPECT/ACTUAL:
 * → expect = deklarasi interface yang harus diimplementasikan setiap platform
 * → actual = implementasi spesifik untuk setiap platform
 *
 * PARAMETER:
 * - modifier: Modifier untuk mengatur ukuran dan posisi preview
 * - controller: CameraController untuk mengontrol kamera (capture, flash)
 * - onPhotoCaptured: Callback yang dipanggil saat foto berhasil diambil
 *   → Parameter ByteArray? berisi data gambar (null jika gagal)
 * - onPermissionGranted: Callback untuk status izin kamera
 * - permissionDeniedContent: UI yang ditampilkan jika izin ditolak
 *
 * ALUR SAAT FOTO DIAMBIL:
 *
 * [CameraUI]
 *     │ controller.capture()
 *     ▼
 * [CameraController]
 *     │ triggerCapture?.invoke()
 *     ▼
 * [CameraPreview.android.kt / CameraPreview.ios.kt]
 *     │ - Ambil foto dari kamera device
 *     │ - Konversi ke ByteArray
 *     ▼
 * [onPhotoCaptured(bytes)]
 *     │
 *     ▼
 * [CameraUI menerima ByteArray]
 *     │ onPhotoCaptured(bytes, false)
 *     ▼
 * [CameraScreen menerima dan simpan ke state capturedPhoto]
 *
 * LANJUT KE: CameraPreview.android.kt untuk melihat implementasi Android
 * ============================================================================
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit = {},
    permissionDeniedContent: @Composable (onRequest: () -> Unit) -> Unit
)