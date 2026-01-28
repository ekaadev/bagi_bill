package com.bagi_bill.bagi_bill.presentation.camera

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Android implementation of gallery picker using system file picker.
 */
@Composable
actual fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                onImagePicked(bytes)
            } catch (e: Exception) {
                onImagePicked(null)
            }
        } else {
            onImagePicked(null)
        }
    }
    
    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }
}
