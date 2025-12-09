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
 * CameraScreen - Router untuk Camera Flow
 * 
 * Flow: Camera → Preview → Confirm
 * Mengatur routing antara CameraUI dan PreviewScreen
 */
@Composable
fun CameraScreen(
    onExit: () -> Unit,
    onPhotoConfirmed: (ByteArray) -> Unit = {}
) {
    var capturedPhoto by remember { mutableStateOf<ByteArray?>(null) }

    // Router
    if (capturedPhoto == null) {
        // Halaman Camera
        CameraUI(
            onBack = onExit,
            onPhotoCaptured = { bytes ->
                println("📸 Photo captured! Size: ${bytes.size} bytes")
                capturedPhoto = bytes
            }
        )
    } else {
        // Halaman Preview
        PreviewScreen(
            photoBytes = capturedPhoto!!,
            onRetake = { capturedPhoto = null },
            onConfirm = {
                onPhotoConfirmed(capturedPhoto!!)
                onExit()
            }
        )
    }
}

/**
 * CameraUI - UI untuk mengambil foto dari kamera atau gallery
 */
@Composable
private fun CameraUI(
    onBack: () -> Unit,
    onPhotoCaptured: (ByteArray) -> Unit
) {
    val controller = remember { CameraController() }
    var isFlashOn by remember { mutableStateOf(false) }
    var showGalleryPicker by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (showGalleryPicker) {
        GalleryImagePicker { bytes ->
            if (bytes != null) onPhotoCaptured(bytes)
            showGalleryPicker = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // 1. LAYER KAMERA & PERMISSION
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = controller,
            onPhotoCaptured = { bytes ->
                if (bytes != null) onPhotoCaptured(bytes)
            },

            // Set permission status
            onPermissionGranted =  { granted -> isCameraPermissionGranted = granted
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
                            text = "Biar kamu bisa ambil foto struk buat hitung split billnya",
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

        // 5. TOMBOL FLASH
        IconButton(
            onClick = {
                isFlashOn = !isFlashOn
                controller.switchFlash()
            },
            enabled = isCameraPermissionGranted(),
            modifier = Modifier.align(Alignment.BottomEnd).padding(64.dp)
        ) {
            Icon(
                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        // 6. TOMBOL GALLERY
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

        // 7. TOMBOL CAPTURE
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape)
                .padding(6.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable (enabled = isCamerapermissionGranted) { controller.capture() }
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
