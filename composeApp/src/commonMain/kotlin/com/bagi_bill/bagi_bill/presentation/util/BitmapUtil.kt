package com.bagi_bill.bagi_bill.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific utility to convert ByteArray to ImageBitmap.
 * Used by PreviewScreen to display captured photos.
 */
@Composable
expect fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap?
