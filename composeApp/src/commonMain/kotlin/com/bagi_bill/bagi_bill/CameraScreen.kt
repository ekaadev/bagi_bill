package com.bagi_bill.bagi_bill

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * [LANGKAH 2] CAMERA ROUTER - CameraScreen.kt
 * ============================================================================
 */
@Composable
fun CameraScreen(
    onExit: () -> Unit,
    onPhotoConfirmed: (ByteArray) -> Unit = {}
) {
    var capturedPhoto by remember { mutableStateOf<ByteArray?>(null) }
    var isFromGallery by remember { mutableStateOf(false) }

    if (capturedPhoto == null) {
        CameraUI(
            onBack = onExit,
            onPhotoCaptured = { bytes, fromGallery ->
                capturedPhoto = bytes
                isFromGallery = fromGallery
            }
        )
    } else {
        PreviewScreen(
            photoBytes = capturedPhoto!!,
            isFromGallery = isFromGallery,
            onRetake = {
                capturedPhoto = null
                isFromGallery = false
            },
            onConfirm = {
                onPhotoConfirmed(capturedPhoto!!)
                onExit()
            }
        )
    }
}

/**
 * ============================================================================
 * [LANGKAH 3] CAMERA UI - FIXED
 * ============================================================================
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraUI(
    onBack: () -> Unit,
    onPhotoCaptured: (ByteArray, Boolean) -> Unit
) {
    val controller = remember { CameraController() }
    var isFlashOn by remember { mutableStateOf(false) }
    var showGalleryPicker by remember { mutableStateOf(false) }
    var isCameraPermissionGranted by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle Android back button
    BackHandler(onBack = onBack)

    if (showGalleryPicker) {
        GalleryImagePicker { bytes ->
            if (bytes != null) onPhotoCaptured(bytes, true)
            showGalleryPicker = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // 1. Top Bar Transparan
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                actions = {
                    IconButton(onClick = {
                        scope.launch { snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia") }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Help,
                            contentDescription = "Bantuan",
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

        // 2. Bottom Bar untuk Kontrol
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .padding(bottom = 32.dp),
                containerColor = Color.Transparent,
                contentColor = Color.LightGray
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Galeri Button
                    IconButton(
                        onClick = { showGalleryPicker = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Galeri",
                            tint = Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 2. Shutter Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(78.dp)
                            .border(4.dp, if (isCameraPermissionGranted) Color.White else Color.Gray, CircleShape)
                            .padding(6.dp)
                            .clip(CircleShape)
                            .background(if (isCameraPermissionGranted) Color.White else Color.Gray)
                            .clickable(enabled = isCameraPermissionGranted) {
                                if (isCameraPermissionGranted) {
                                    controller.capture()
                                }
                            }
                    ) {
                        // KOSONG, TAPI WAJIB ADA { } KARENA PAKAI contentAlignment
                    }

                    // 3. Flash Button
                    IconButton(
                        onClick = {
                            if (isCameraPermissionGranted) {
                                isFlashOn = !isFlashOn
                                controller.switchFlash()
                            }
                        },
                        enabled = isCameraPermissionGranted,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        // 2. CONTENT AREA (FULL SCREEN)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // A. PREVIEW KAMERA (Full Screen)
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                controller = controller,
                onPhotoCaptured = { bytes ->
                    if (bytes != null) onPhotoCaptured(bytes, false)
                },
                onPermissionGranted = { granted ->
                    isCameraPermissionGranted = granted
                },
                permissionDeniedContent = { onRequest ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Bolehkah kami akses kameramu?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            textAlign = Center
                        )
                        Spacer(Modifier.height(16.dp))

                        // Teks Deskripsi
                        Text(
                            text = "Biar kamu bisa ambil foto struk buat hitung split billnya.",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(onClick = onRequest) {
                            Text("Izinkan Kamera", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            )

            // B. TIP (Floating) - Hanya muncul jika permission granted
            if (isCameraPermissionGranted) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 120.dp)
                ) {
                    // Main content box
                    Box(
                        modifier = Modifier
                            .padding(top = 14.dp) // Space for the "Tip" label to sit in
                            .fillMaxWidth()
                            .background(
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .padding(top = 14.dp), // Additional padding inside for text
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Biar hasilnya optimal, pastikan struknya kebaca dan difoto di tempat terang",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // "Tip" label on top
                    Row(
                        modifier = Modifier
                            .background(
                                color = Color.DarkGray,
                                shape = CircleShape
                            )
                            .border(1.dp, Color.LightGray, CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tip",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.EmojiObjects,
                            contentDescription = "Tip Icon",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
expect fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit)