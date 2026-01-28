package com.bagi_bill.bagi_bill.presentation.camera

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Android implementation of CameraPreview with permission handling.
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
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermissionState.status.isGranted) {
        onPermissionGranted(cameraPermissionState.status.isGranted)
    }

    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(
            modifier = modifier.fillMaxSize(),
            controller = controller,
            onPhotoCaptured = onPhotoCaptured
        )
    } else {
        permissionDeniedContent {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}
