package com.bagi_bill.bagi_bill.presentation.ocr

/**
 * Web stub - OCR not supported.
 */
actual class TextRecognitionService actual constructor() {
    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        throw UnsupportedOperationException("Text recognition is not supported on Web platform.")
    }
}
