package com.bagi_bill.bagi_bill.presentation.camera

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific camera preview composable.
 * Android uses CameraX, iOS would use AVFoundation.
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit = {},
    permissionDeniedContent: @Composable (onRequest: () -> Unit) -> Unit
)
