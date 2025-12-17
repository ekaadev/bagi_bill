package com.bagi_bill.bagi_bill

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.core.graphics.scale

actual class TextRecognitionService actual constructor() {
    // Instance dari ML Kit Text Recognizer
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    companion object {
        private const val TAG = "TextRecognitionService"
    }

    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        return suspendCancellableCoroutine { continuation ->
            Log.d(TAG, "Starting OCR, image bytes size: ${imageBytes.size}")

            // Decode ByteArray ke Bitmap
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            if (bitmap == null) {
                Log.e(TAG, "Failed to decode image bytes to bitmap")
                continuation.resumeWithException(
                    IllegalArgumentException("Failed to decode image bytes to bitmap")
                )
                return@suspendCancellableCoroutine
            }

            Log.d(TAG, "Bitmap decoded: ${bitmap.width}x${bitmap.height}")

            // Jika gambar terlalu besar, scale down untuk performa lebih baik
            val processedBitmap = if (bitmap.width > 2000 || bitmap.height > 2000) {
                val scale = minOf(2000f / bitmap.width, 2000f / bitmap.height)
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                Log.d(TAG, "Scaling bitmap to: ${newWidth}x${newHeight}")
                bitmap.scale(newWidth, newHeight)
            } else {
                bitmap
            }

            // Buat InputImage - rotasi 0 karena gambar sudah dirotasi di CameraPreviewViewModel
            val image = InputImage.fromBitmap(processedBitmap, 0)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    Log.d(TAG, "OCR Success!")
                    Log.d(TAG, "Number of blocks: ${visionText.textBlocks.size}")

                    // Bangun hasil dari semua blocks
                    val resultBuilder = StringBuilder()

                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            resultBuilder.appendLine(line.text)
                        }
                    }

                    val fullResult = resultBuilder.toString().trim()

                    Log.d(TAG, "Full result length: ${fullResult.length}")
                    Log.d(TAG, "Final OCR Result:\n$fullResult")

                    // Return hasil dari StringBuilder, bukan visionText.text
                    continuation.resume(fullResult)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "OCR Failed: ${e.message}", e)
                    continuation.resumeWithException(e)
                }
        }
    }
}