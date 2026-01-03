package com.bagi_bill.bagi_bill.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun decodeByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap? {
    return try {
        Image.makeFromEncoded(byteArray).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

