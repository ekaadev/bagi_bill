package com.bagi_bill.bagi_bill.presentation.ocr

/**
 * Platform-specific text recognition service.
 * Android uses ML Kit, others use stubs.
 */
expect class TextRecognitionService() {
    suspend fun recognizeText(imageBytes: ByteArray): String
}
