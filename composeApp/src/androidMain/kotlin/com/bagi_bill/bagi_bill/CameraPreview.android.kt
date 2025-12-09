package com.bagi_bill.bagi_bill

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * CameraPreview - Android Implementation
 * Menggunakan CameraX + Accompanist Permissions
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    permissionDeniedContent: @Composable (onRequest: () -> Unit) -> Unit
) {
    // Cek permission kamera
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        // Permission granted → Tampilkan kamera
        CameraPreviewContent(
            modifier = modifier.fillMaxSize(),
            controller = controller,
            onPhotoCaptured = onPhotoCaptured
        )
    } else {
        // Permission denied → Tampilkan request button
        permissionDeniedContent {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}