package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap

actual class ShareHelper {
    actual suspend fun shareBillImage(image: ImageBitmap) {
        // Implementasi iOS membutuhkan konversi SkiaBitmap -> UIImage -> UIActivityViewController
        // Untuk saat ini dibiarkan kosong
        println("Share image belum diimplementasikan di iOS")
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    return remember { ShareHelper() }
}
