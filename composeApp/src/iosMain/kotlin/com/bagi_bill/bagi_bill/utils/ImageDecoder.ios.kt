package com.bagi_bill.bagi_bill.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
actual fun decodeByteArrayToImageBitmap(byteArray: ByteArray): ImageBitmap? {
    return try {
        val nsData = byteArray.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = byteArray.size.toULong())
        }
        val uiImage = UIImage.imageWithData(nsData) ?: return null
        Image.makeFromEncoded(byteArray).toComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

