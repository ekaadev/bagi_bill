package com.bagi_bill.bagi_bill.presentation.ocr

/**
 * JVM/Desktop stub - OCR not supported.
 */
actual class TextRecognitionService actual constructor() {
    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        throw UnsupportedOperationException("Text recognition is not supported on Desktop platform.")
    }
}
