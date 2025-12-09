package com.bagi_bill.bagi_bill

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    controller: CameraController,
    onPhotoCaptured: (ByteArray?) -> Unit,
    onPermissionGranted: (Boolean) -> Unit = {},
    viewModel: CameraPreviewViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val surfaceRequest by viewModel.surfaceRequest.collectAsState()

    LaunchedEffect(Unit) {
        onPermissionGranted(true)
    }

    // 1. Nyalakan Mesin
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    // 2. Sambungkan Tombol UI ke Mesin
    LaunchedEffect(controller) {
        controller.triggerCapture = {
            viewModel.captureImage(context, onPhotoCaptured)
        }
        controller.toggleFlash = {
            viewModel.toggleFlash()
        }
    }

    // 3. Tampilkan Layar
    surfaceRequest?.let { request ->
        CameraXViewfinder(surfaceRequest = request, modifier = modifier)
    }
}