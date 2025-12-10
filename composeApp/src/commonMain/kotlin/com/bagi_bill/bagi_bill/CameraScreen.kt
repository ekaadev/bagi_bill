package com.bagi_bill.bagi_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.AlignmentLine

/**
 * ============================================================================
 * [LANGKAH 2] CAMERA ROUTER - CameraScreen.kt
 * ============================================================================
 *
 * File ini berfungsi sebagai ROUTER untuk alur kamera.
 * Terdiri dari 2 state utama:
 *
 * 1. capturedPhoto == null → Tampilkan CameraUI (halaman pengambilan foto)
 * 2. capturedPhoto != null → Tampilkan PreviewScreen (halaman preview foto)
 *
 * ALUR:
 * → User masuk ke CameraScreen
 * → CameraUI ditampilkan (karena capturedPhoto masih null)
 * → User ambil foto atau pilih dari galeri
 * → onPhotoCaptured dipanggil dengan ByteArray hasil foto
 * → capturedPhoto diisi dengan ByteArray tersebut
 * → PreviewScreen ditampilkan (karena capturedPhoto tidak null lagi)
 * → User konfirmasi foto → onPhotoConfirmed dipanggil ke HomeScreen
 *
 * LANJUT KE:
 * - CameraUI (di bawah) untuk melihat proses pengambilan foto
 * - PreviewScreen.kt untuk melihat halaman preview
 * ============================================================================
 */
@Composable
fun CameraScreen(
    onExit: () -> Unit,
    onPhotoConfirmed: (ByteArray) -> Unit = {}
) {
    // State untuk menyimpan foto yang sudah diambil (ByteArray)
    // null = belum ada foto, ada nilai = sudah ada foto
    var capturedPhoto by remember { mutableStateOf<ByteArray?>(null) }

    // State untuk menandai apakah foto berasal dari galeri atau kamera
    var isFromGallery by remember { mutableStateOf(false) }

    // ========== ROUTING LOGIC ==========
    // Jika belum ada foto → tampilkan CameraUI
    // Jika sudah ada foto → tampilkan PreviewScreen
    if (capturedPhoto == null) {
        // [CABANG A] Halaman Camera - user belum ambil foto
        CameraUI(
            onBack = onExit,
            // Callback saat foto berhasil diambil
            // bytes = data gambar, fromGallery = true jika dari galeri
            onPhotoCaptured = { bytes, fromGallery ->
                println("Photo captured! Size: ${bytes.size} bytes, From Gallery: $fromGallery")
                // Simpan foto ke state → trigger recomposition → PreviewScreen ditampilkan
                capturedPhoto = bytes
                isFromGallery = fromGallery
            }
        )
    } else {
        // [CABANG B] Halaman Preview - user sudah ambil foto
        // Lihat file: PreviewScreen.kt
        PreviewScreen(
            photoBytes = capturedPhoto!!,
            isFromGallery = isFromGallery,
            // Callback saat user ingin foto ulang
            onRetake = {
                capturedPhoto = null  // Reset state → kembali ke CameraUI
                isFromGallery = false
            },
            // Callback saat user konfirmasi foto
            onConfirm = {
                // Kirim ByteArray foto ke HomeScreen melalui callback
                onPhotoConfirmed(capturedPhoto!!)
                onExit()  // Kembali ke HomeScreen
            }
        )
    }
}

/**
 * ============================================================================
 * [LANGKAH 3] CAMERA UI - Tampilan Pengambilan Foto
 * ============================================================================
 *
 * Fungsi ini menampilkan UI untuk mengambil foto, terdiri dari:
 *
 * KOMPONEN UTAMA:
 * 1. CameraPreview → Menampilkan preview kamera (expect/actual per platform)
 *    - Android: Menggunakan CameraX
 *    - iOS: Menggunakan AVFoundation
 *    Lihat file: CameraPreview.kt (commonMain) dan CameraPreview.android.kt (androidMain)
 *
 * 2. CameraController → Mengontrol kamera (capture foto, toggle flash)
 *    Lihat file: CameraController.kt
 *
 * 3. GalleryImagePicker → Mengambil gambar dari galeri (expect/actual per platform)
 *
 * ALUR PENGAMBILAN FOTO:
 *
 * [CARA 1 - DARI KAMERA]
 * → User tekan tombol capture (lingkaran putih di bawah)
 * → controller.capture() dipanggil
 * → CameraController mengirim sinyal ke implementasi platform
 * → Platform mengambil foto dan konversi ke ByteArray
 * → onPhotoCaptured(bytes, false) dipanggil (false = bukan dari galeri)
 * → CameraScreen menerima ByteArray dan pindah ke PreviewScreen
 *
 * [CARA 2 - DARI GALERI]
 * → User tekan tombol galeri (icon gambar di kiri bawah)
 * → showGalleryPicker = true
 * → GalleryImagePicker ditampilkan
 * → User pilih gambar
 * → Gambar dikonversi ke ByteArray
 * → onPhotoCaptured(bytes, true) dipanggil (true = dari galeri)
 * → CameraScreen menerima ByteArray dan pindah ke PreviewScreen
 *
 * ============================================================================
 */
@Composable
private fun CameraUI(
    onBack: () -> Unit,
    onPhotoCaptured: (ByteArray, Boolean) -> Unit // Boolean = isFromGallery
) {
    // Controller untuk mengontrol kamera (capture, flash)
    // Lihat file: CameraController.kt
    val controller = remember { CameraController() }

    // State untuk toggle flash
    var isFlashOn by remember { mutableStateOf(false) }

    // State untuk menampilkan gallery picker
    var showGalleryPicker by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // State untuk mengecek apakah izin kamera sudah diberikan
    var isCameraPermissionGranted by remember { mutableStateOf(false) }

    // ========== GALLERY PICKER ==========
    // Jika showGalleryPicker = true, tampilkan picker galeri
    if (showGalleryPicker) {
        // GalleryImagePicker adalah expect/actual function
        // Implementasi berbeda per platform (Android, iOS, dll)
        GalleryImagePicker { bytes ->
            if (bytes != null) {
                // Gambar berhasil dipilih, kirim ke callback
                // true = menandakan gambar ini dari galeri
                onPhotoCaptured(bytes, true)
            }
            showGalleryPicker = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ========== 1. LAYER KAMERA & PERMISSION ==========
        // CameraPreview adalah expect/actual function
        // Lihat: CameraPreview.kt (deklarasi) dan CameraPreview.android.kt (implementasi Android)
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = controller,
            // Callback saat foto berhasil diambil dari kamera
            onPhotoCaptured = { bytes ->
                if (bytes != null) {
                    // false = menandakan gambar ini dari kamera, bukan galeri
                    onPhotoCaptured(bytes, false)
                }
            },

            // Set permission status - callback ini akan dipanggil ketika permission berubah
            onPermissionGranted = { granted -> 
                isCameraPermissionGranted = granted
                println("Camera permission granted: $granted")
            },

            // === UPDATE BAGIAN INI (TAMPILAN IZIN) ===
            permissionDeniedContent = { onRequest ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                ) {
                    // Gunakan Column agar Teks ada di ATAS Tombol dengan rapi
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp), // Margin kiri-kanan biar ga mentok
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Teks Judul
                        Text(
                            text = "Bolehkah kami akses kameramu?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Teks Deskripsi
                        Text(
                            text = "Tip: Biar kamu bisa ambil foto struk buat hitung split billnya",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Izin
                        Button(
                            onClick = onRequest,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Izinkan Kamera", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        )

        // 2. TOMBOL BACK
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // 3. ICON BANTUAN
        IconButton(
            onClick = {
                scope.launch { snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia") }
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = "Bantuan",
                modifier = Modifier
                    .size(40.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(100))
                    .padding(6.dp),
                tint = Color.Gray,
            )
        }

        // 4. BOX TIP (SARAN)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp)
                .padding(horizontal = 32.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Biar hasilnya optimal, pastiin struknya kebaca dan difoto di tempat terang",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // ========== 5. TOMBOL FLASH ==========
        // Toggle flash on/off melalui CameraController
        IconButton(
            onClick = {
                if (isCameraPermissionGranted) {
                    isFlashOn = !isFlashOn
                    // Kirim sinyal ke platform untuk toggle flash
                    controller.switchFlash()
                }
            },
            enabled = isCameraPermissionGranted,
            modifier = Modifier.align(Alignment.BottomEnd).padding(64.dp)
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash",
                tint = if (isCameraPermissionGranted) Color.White else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        // ========== 6. TOMBOL GALLERY ==========
        // Buka picker galeri untuk memilih gambar yang sudah ada
        IconButton(
            onClick = { showGalleryPicker = true },
            modifier = Modifier.align(Alignment.BottomStart).padding(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Galeri",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        // ========== 7. TOMBOL CAPTURE (SHUTTER) ==========
        // Tombol utama untuk mengambil foto
        // Saat ditekan → controller.capture() → platform mengambil foto
        // → foto dikonversi ke ByteArray → onPhotoCaptured dipanggil
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(80.dp)
                .border(4.dp, if (isCameraPermissionGranted) Color.White else Color.Gray, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(if (isCameraPermissionGranted) Color.White else Color.Gray)
                .clickable(enabled = isCameraPermissionGranted) { 
                    if (isCameraPermissionGranted) {
                        // Trigger capture foto melalui CameraController
                        // Controller akan mengirim sinyal ke implementasi platform
                        // Lihat: CameraController.kt
                        controller.capture()
                    }
                }
        )

        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
/**
 * Gallery Image Picker - expect/actual
 * Android: Buka system picker
 * iOS: Buka PHPickerViewController
 */
@Composable
expect fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit)
