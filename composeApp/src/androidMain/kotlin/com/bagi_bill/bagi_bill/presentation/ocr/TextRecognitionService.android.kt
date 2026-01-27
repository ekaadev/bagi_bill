package com.bagi_bill.bagi_bill.presentation.ocr

import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Android implementation using Google ML Kit Text Recognition.
 */
actual class TextRecognitionService actual constructor() {
    
    private val recognizer by lazy { 
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) 
    }

    companion object {
        private const val TAG = "TextRecognitionService"
        private const val MAX_IMAGE_SIZE = 2000
    }

    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Starting OCR, image bytes size: ${imageBytes.size}")

            try {
                // Decode ByteArray to Bitmap
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                if (bitmap == null) {
                    Log.e(TAG, "Failed to decode image bytes to bitmap")
                    continuation.resumeWithException(
                        IllegalArgumentException("Failed to decode image bytes to bitmap")
                    )
                    return@suspendCancellableCoroutine
                }

                // Scale down large images for better OCR performance
                val processedBitmap = if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
                    val scale = minOf(
                        MAX_IMAGE_SIZE.toFloat() / bitmap.width,
                        MAX_IMAGE_SIZE.toFloat() / bitmap.height
                    )
                    val newWidth = (bitmap.width * scale).toInt()
                    val newHeight = (bitmap.height * scale).toInt()
                    Log.d(TAG, "Scaling image from ${bitmap.width}x${bitmap.height} to ${newWidth}x${newHeight}")
                    bitmap.scale(newWidth, newHeight)
                } else {
                    bitmap
                }

                // Create InputImage (rotation 0 - already handled by CameraX)
                val image = InputImage.fromBitmap(processedBitmap, 0)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val resultBuilder = StringBuilder()
                        for (block in visionText.textBlocks) {
                            for (line in block.lines) {
                                resultBuilder.appendLine(line.text)
                            }
                        }
                        val fullResult = resultBuilder.toString().trim()
                        Log.d(TAG, "OCR completed, result length: ${fullResult.length}")
                        continuation.resume(fullResult)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "OCR failed: ${e.message}")
                        continuation.resumeWithException(e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "OCR exception: ${e.message}")
                continuation.resumeWithException(e)
            }
        }
    }
}
