package com.bagi_bill.bagi_bill.domain.repository

import com.bagi_bill.bagi_bill.domain.model.BillItem

/**
 * Interface untuk Scanner Repository
 * Diimplementasikan secara berbeda untuk Android (CameraX + ML Kit) dan Web (Manual Input)
 */
interface ScannerRepository {
    /**
     * Parse text hasil OCR menjadi list BillItem
     */
    suspend fun parseReceiptText(text: String): List<BillItem>

    /**
     * Check apakah platform mendukung camera scanning
     */
    fun isCameraScanSupported(): Boolean
}

