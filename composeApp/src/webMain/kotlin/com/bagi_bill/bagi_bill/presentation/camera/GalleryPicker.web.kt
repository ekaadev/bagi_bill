package com.bagi_bill.bagi_bill.presentation.camera

import androidx.compose.runtime.Composable

/**
 * Web stub - Gallery picker is not supported on web platform.
 */
@Composable
actual fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit) {
    // No-op on web - immediately return null
    onImagePicked(null)
}
