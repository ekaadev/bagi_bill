package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

/**
 * iOS stub implementation of ShareHelper
 * TODO: Implement proper iOS sharing functionality
 */
actual class ShareHelper {
    actual suspend fun shareBillImage(image: ImageBitmap) {
        // TODO: Implement iOS sharing via UIActivityViewController
        println("ShareHelper: iOS sharing not implemented yet")
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    return remember { ShareHelper() }
}
