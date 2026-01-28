package com.bagi_bill.bagi_bill.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * JVM/Desktop stub - can implement using javax.imageio if needed.
 */
@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return null
}
