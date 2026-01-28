package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

actual class ShareHelper {
    actual suspend fun shareBillImage(image: ImageBitmap) {
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    TODO("Not yet implemented")
}