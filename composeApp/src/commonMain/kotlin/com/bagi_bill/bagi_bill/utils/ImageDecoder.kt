package com.bagi_bill.bagi_bill.utils

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific function untuk decode ByteArray ke ImageBitmap
 * Setiap platform (Android, iOS, Desktop, Web) akan memiliki implementasi sendiri
 */
expect fun decodeByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap?

