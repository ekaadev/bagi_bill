package com.bagi_bill.bagi_bill

expect class TextRecognitionService() {
    suspend fun recognizeText(imageBytes: ByteArray): String
}