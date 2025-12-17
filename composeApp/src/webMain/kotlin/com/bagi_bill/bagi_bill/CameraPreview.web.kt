package com.bagi_bill.bagi_bill

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun CameraPreview(
    modifier: Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit,
    permissionDeniedContent: @Composable ((onRequest: () -> Unit) -> Unit)
) {
}