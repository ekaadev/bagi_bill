package com.bagi_bill.bagi_bill.presentation.camera

import androidx.compose.runtime.Composable

/**
 * JVM/Desktop stub - Gallery picker is not supported on desktop.
 */
@Composable
actual fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit) {
    onImagePicked(null)
}
