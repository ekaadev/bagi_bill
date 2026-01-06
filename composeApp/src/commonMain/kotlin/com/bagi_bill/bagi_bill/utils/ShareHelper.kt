package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect class ShareHelper {
    suspend fun shareBillImage(image: ImageBitmap)
}

@Composable
expect fun rememberShareHelper(): ShareHelper
