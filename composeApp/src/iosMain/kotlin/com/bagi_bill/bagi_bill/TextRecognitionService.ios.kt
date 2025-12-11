package com.bagi_bill.bagi_bill

actual class TextRecognitionService actual constructor() {
    actual suspend fun recognizeText(imageBytes: ByteArray): String {
        // TODO: Implement using Apple Vision framework
        throw UnsupportedOperationException("Text recognition is not yet implemented for iOS")
    }
}

