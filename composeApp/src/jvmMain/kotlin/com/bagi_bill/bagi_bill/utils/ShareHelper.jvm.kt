package com.bagi_bill.bagi_bill.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import java.awt.Desktop
import java.io.File
import javax.imageio.ImageIO

actual class ShareHelper {
    actual suspend fun shareBillImage(image: ImageBitmap) {
        try {
            // Convert ImageBitmap to AWT BufferedImage
            val awtImage = image.toAwtImage()
            
            // Create temp file
            val tempFile = File.createTempFile("bagi_bill_share_", ".png")
            tempFile.deleteOnExit()
            
            // Save image to temp file
            ImageIO.write(awtImage, "PNG", tempFile)
            
            // Open the image with system default viewer
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    return remember { ShareHelper() }
}
