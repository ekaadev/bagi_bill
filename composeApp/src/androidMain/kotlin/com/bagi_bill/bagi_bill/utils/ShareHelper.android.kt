package com.bagi_bill.bagi_bill.utils

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of ShareHelper
 */
actual class ShareHelper(private val context: Context) {
    @Suppress("DEPRECATION")
    actual suspend fun shareBillImage(image: ImageBitmap) {
        withContext(Dispatchers.IO) {
            val bitmap = image.asAndroidBitmap()
            val uri = try {
                MediaStore.Images.Media.insertImage(
                    context.contentResolver, 
                    bitmap, 
                    "Rincian Split Bill", 
                    "Image generated from BagiBill"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            uri?.let {
                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, android.net.Uri.parse(it))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Bagikan Rincian")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                }
            }
        }
    }
}

@Composable
actual fun rememberShareHelper(): ShareHelper {
    val context = LocalContext.current
    return remember { ShareHelper(context) }
}
