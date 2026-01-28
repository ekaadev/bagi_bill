package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Web stub - Share functionality not supported on web platform.
 */
actual class ShareHelper {
    actual suspend fun shareBillImage(image: ImageBitmap) {
        // No-op on web - sharing not supported
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    return remember { ShareHelper() }
}
