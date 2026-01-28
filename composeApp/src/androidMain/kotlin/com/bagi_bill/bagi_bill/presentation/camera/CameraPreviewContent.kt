package com.bagi_bill.bagi_bill.presentation.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * CameraX preview content - displays live camera feed and handles capture.
 */
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

    // Bind camera to lifecycle
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    // Connect controller to viewmodel
    LaunchedEffect(controller) {
        controller.triggerCapture = {
            viewModel.captureImage(context, onPhotoCaptured)
        }
        controller.toggleFlash = {
            viewModel.toggleFlash()
        }
    }

    // Display camera preview
    surfaceRequest?.let { request ->
        CameraXViewfinder(surfaceRequest = request, modifier = modifier)
    }
}
