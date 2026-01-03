package com.bagi_bill.bagi_bill.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Implementasi Android untuk decode ByteArray ke ImageBitmap
 * Menggunakan BitmapFactory dari Android SDK
 */
actual fun decodeByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap? {
    return try {
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
