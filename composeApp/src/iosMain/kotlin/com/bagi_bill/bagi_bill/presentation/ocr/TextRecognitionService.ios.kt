package com.bagi_bill.bagi_bill.presentation.ocr

/**
 * iOS stub - OCR not yet implemented.
 */
actual class TextRecognitionService actual constructor() {
    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        throw UnsupportedOperationException("Text recognition is not supported on iOS platform yet.")
    }
}
