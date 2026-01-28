package com.bagi_bill.bagi_bill.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Web stub - Bitmap conversion not available on web platform.
 */
@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    // Cannot convert ByteArray to ImageBitmap on web
    return null
}
