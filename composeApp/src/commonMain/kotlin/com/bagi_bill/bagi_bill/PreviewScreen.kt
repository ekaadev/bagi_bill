package com.bagi_bill.bagi_bill

//import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
@OptIn(ExperimentalMaterial3Api::class)
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

    // Handle System Back Button
//    BackHandler(onBack = onRetake)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onRetake) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                                .padding(8.dp),
                            tint = Color.LightGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.padding(bottom = 16.dp),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = if (isFromGallery) Arrangement.Center else Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isFromGallery) {
                        OutlinedButton(
                            onClick = onRetake,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(
                                text = "Foto ulang",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text(
                            text = "Pakai foto ini",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
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
                    fontSize = 16.sp
                )
            }
        }
    }
}