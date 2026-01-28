package com.bagi_bill.bagi_bill

/**
 * ============================================================================
 * [LANGKAH 3A] CAMERA CONTROLLER - Pengontrol Kamera
 * ============================================================================
 *
 * CameraController berfungsi sebagai JEMBATAN antara UI (commonMain) dan
 * implementasi kamera native (androidMain/iosMain).
 *
 * KENAPA PERLU CONTROLLER?
 * → Compose Multiplatform tidak punya akses langsung ke kamera device
 * → Setiap platform punya API kamera sendiri (CameraX di Android, AVFoundation di iOS)
 * → Controller ini menjadi "remote control" yang bisa dipakai di UI
 *
 * CARA KERJA:
 *
 * [UI Layer - CameraScreen.kt]
 *     │
 *     │ controller.capture()
 *     ▼
 * [CameraController - file ini]
 *     │
 *     │ triggerCapture?.invoke()
 *     ▼
 * [Platform Layer - CameraPreview.android.kt / CameraPreview.ios.kt]
 *     │
 *     │ Ambil foto menggunakan CameraX / AVFoundation
 *     │ Konversi ke ByteArray
 *     ▼
 * [Callback onPhotoCaptured(bytes)]
 *     │
 *     ▼
 * [CameraScreen.kt menerima ByteArray]
 *
 * ALUR DETAIL SAAT USER TEKAN TOMBOL CAPTURE:
 *
 * 1. User tekan tombol capture di CameraUI
 * 2. controller.capture() dipanggil
 * 3. capture() memanggil triggerCapture?.invoke()
 * 4. triggerCapture sudah diisi oleh implementasi platform saat CameraPreview di-mount
 * 5. Implementasi platform (Android/iOS) mengambil foto
 * 6. Foto dikonversi ke ByteArray
 * 7. ByteArray dikirim ke callback onPhotoCaptured
 *
 * ============================================================================
 */
class CameraController {
    // ========== CALLBACK VARIABLES (Diisi oleh implementasi platform) ==========

    /**
     * Callback untuk mengambil foto
     * Diisi oleh implementasi platform (Android: CameraPreview.android.kt)
     * Saat triggerCapture?.invoke() dipanggil, platform akan mengambil foto
     */
    var triggerCapture: (() -> Unit)? = null

    /**
     * Callback untuk toggle flash
     * Diisi oleh implementasi platform
     * Saat toggleFlash?.invoke() dipanggil, platform akan toggle flash
     */
    var toggleFlash: (() -> Unit)? = null

    // ========== FUNGSI YANG DIPANGGIL DARI UI ==========

    /**
     * Fungsi untuk mengambil foto
     * Dipanggil dari CameraUI saat user tekan tombol capture
     */
    fun capture() {
        // Kirim sinyal ke implementasi platform untuk mengambil foto
        // Jika triggerCapture belum diisi (null), tidak terjadi apa-apa
        triggerCapture?.invoke()
    }

    /**
     * Fungsi untuk toggle flash on/off
     * Dipanggil dari CameraUI saat user tekan tombol flash
     */
    fun switchFlash() {
        // Kirim sinyal ke implementasi platform untuk toggle flash
        toggleFlash?.invoke()
    }
}