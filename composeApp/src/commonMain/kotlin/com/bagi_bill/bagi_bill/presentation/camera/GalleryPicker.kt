package com.bagi_bill.bagi_bill.presentation.camera

import androidx.compose.runtime.Composable

/**
 * Platform-specific gallery image picker.
 * Opens system file picker and returns selected image as ByteArray.
 */
@Composable
expect fun GalleryImagePicker(onImagePicked: (ByteArray?) -> Unit)
