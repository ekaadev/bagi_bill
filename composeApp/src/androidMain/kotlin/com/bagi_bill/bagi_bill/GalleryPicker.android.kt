package com.bagi_bill.bagi_bill

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Gallery Picker - Android Implementation
 * Langsung buka system gallery picker
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
                println("✅ Image picked from gallery! Size: ${bytes?.size ?: 0} bytes")
            } catch (e: Exception) {
                println("❌ Failed to read image: ${e.message}")
                onImagePicked(null)
            }
        } else {
            onImagePicked(null)
        }
    }
    
    // Auto-launch gallery picker saat composable ini dipanggil
    LaunchedEffect(Unit) {
        launcher.launch("image/*")
    }
}
