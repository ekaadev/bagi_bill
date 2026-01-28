package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific share helper for sharing bill images
 */
expect class ShareHelper {
    suspend fun shareBillImage(image: ImageBitmap)
}

/**
 * Remember and provide platform-specific ShareHelper
 */
@Composable
expect fun rememberShareHelper(): ShareHelper
