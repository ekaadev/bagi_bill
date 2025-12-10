package com.bagi_bill.bagi_bill

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * [LANGKAH 4] PREVIEW SCREEN - Konfirmasi Foto
 * ============================================================================
 *
 * Halaman ini ditampilkan setelah user mengambil foto atau memilih dari galeri.
 * User bisa melihat preview foto dan memutuskan untuk:
 * 1. Foto ulang (kembali ke CameraUI)
 * 2. Pakai foto ini (konfirmasi dan kirim ke HomeScreen)
 *
 * PARAMETER:
 * - photoBytes: ByteArray yang berisi data gambar (hasil capture atau galeri)
 * - isFromGallery: Boolean penanda apakah foto dari galeri atau kamera
 *   → Jika dari galeri, tombol "Foto ulang" tidak ditampilkan
 * - onRetake: Callback saat user ingin foto ulang
 * - onConfirm: Callback saat user konfirmasi foto
 *
 * ALUR:
 *
 * [CameraScreen]
 *     │ capturedPhoto != null
 *     ▼
 * [PreviewScreen ditampilkan dengan photoBytes]
 *     │
 *     ├── User tekan "Foto ulang"
 *     │       │ onRetake()
 *     │       ▼
 *     │   [CameraScreen reset capturedPhoto = null]
 *     │       │
 *     │       ▼
 *     │   [CameraUI ditampilkan lagi]
 *     │
 *     └── User tekan "Pakai foto ini"
 *             │ onConfirm()
 *             ▼
 *         [CameraScreen panggil onPhotoConfirmed(capturedPhoto)]
 *             │
 *             ▼
 *         [HomeScreen menerima ByteArray di callback onPhotoConfirmed]
 *
 * KONVERSI BYTEARRAY KE IMAGEBITMAP:
 * → rememberBitmapFromBytes(photoBytes) mengkonversi ByteArray ke ImageBitmap
 * → ImageBitmap bisa ditampilkan menggunakan Image() composable
 * → Lihat implementasi rememberBitmapFromBytes di file terpisah
 *
 * ============================================================================
 */
@Composable
fun PreviewScreen(
    photoBytes: ByteArray,    // Data gambar dalam bentuk ByteArray
    isFromGallery: Boolean,   // true = dari galeri, false = dari kamera
    onRetake: () -> Unit,     // Callback untuk foto ulang
    onConfirm: () -> Unit     // Callback untuk konfirmasi foto
) {
    // Konversi ByteArray ke ImageBitmap untuk ditampilkan
    // rememberBitmapFromBytes adalah helper function untuk konversi
    val imageBitmap = rememberBitmapFromBytes(photoBytes)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Tampilkan gambar
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Preview Foto",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = "Gagal memuat gambar",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Tombol Back (Kiri Atas) - Tetap ada agar user bisa batal
        IconButton(
            onClick = onRetake,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // ========== TOMBOL AKSI (BAWAH) ==========
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ========== TOMBOL FOTO ULANG ==========
            // Hanya muncul jika foto diambil dari kamera (bukan galeri)
            // Karena jika dari galeri, tidak masuk akal untuk "foto ulang"
            if (!isFromGallery) {
                Button(
                    onClick = onRetake,  // Kembali ke CameraUI
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = "Foto ulang",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ========== TOMBOL PAKAI FOTO INI ==========
            // Tombol konfirmasi - saat ditekan:
            // 1. onConfirm() dipanggil
            // 2. CameraScreen memanggil onPhotoConfirmed(capturedPhoto)
            // 3. HomeScreen menerima ByteArray di callback onPhotoConfirmed
            // 4. onExit() dipanggil untuk kembali ke HomeScreen
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Pakai foto ini",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}