package com.bagi_bill.bagi_bill

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * CameraPreview - Platform-specific camera preview
 * 
 * commonMain: expect declaration
 * androidMain: actual implementation menggunakan CameraX
 * iosMain: actual implementation menggunakan AVFoundation (future)
 */
@Composable
expect fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    permissionDeniedContent: @Composable (onRequest: () -> Unit) -> Unit
)