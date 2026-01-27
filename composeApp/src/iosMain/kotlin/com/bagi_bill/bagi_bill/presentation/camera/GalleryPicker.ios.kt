package com.bagi_bill.bagi_bill.presentation.camera

import androidx.compose.runtime.Composable

/**
 * iOS stub - Gallery picker would use PHPickerViewController.
 */
@Composable
actual fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit) {
    onImagePicked(null)
}
