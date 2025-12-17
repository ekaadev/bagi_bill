package com.bagi_bill.bagi_bill

actual class TextRecognitionService actual constructor() {
    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        throw UnsupportedOperationException("Text recognition is not supported on WASM JS platform")
    }
}

