package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.BillItem
import com.bagi_bill.bagi_bill.domain.repository.ScannerRepository

/**
 * Default implementation of ScannerRepository
 * Parses receipt text to extract items
 */
class ScannerRepositoryImpl : ScannerRepository {

    override suspend fun parseReceiptText(text: String): List<BillItem> {
        val items = mutableListOf<BillItem>()
        val lines = text.lines().filter { it.isNotBlank() }

        var itemId = 1L

        for (line in lines) {
            val parsed = parseLine(line)
            if (parsed != null) {
                items.add(parsed.copy(id = itemId++))
            }
        }

        return items
    }

    override fun isCameraScanSupported(): Boolean {
        // Default implementation - override in platform-specific code
        return false
    }

    /**
     * Parse a single line from receipt text
     * Tries to extract: item name, quantity, and price
     *
     * Common formats:
     * - "Nasi Goreng          25000"
     * - "Nasi Goreng x2       50000"
     * - "2x Nasi Goreng       50000"
     * - "Nasi Goreng 2 25000"
     */
    private fun parseLine(line: String): BillItem? {
        val trimmedLine = line.trim()

        // Skip common non-item lines
        val skipPatterns = listOf(
            "subtotal", "total", "tax", "pajak", "service",
            "diskon", "discount", "tunai", "cash", "kembalian",
            "change", "ppn", "pb1", "terima kasih", "thank you"
        )
        if (skipPatterns.any { trimmedLine.lowercase().contains(it) }) {
            return null
        }

        // Try to extract price (usually at the end, numeric)
        val pricePattern = Regex("""[\d.,]+\s*$""")
        val priceMatch = pricePattern.find(trimmedLine)

        if (priceMatch == null) return null

        val priceStr = priceMatch.value.replace(Regex("[.,\\s]"), "")
        val price = priceStr.toLongOrNull() ?: return null

        // Price should be reasonable (more than 100, assuming IDR)
        if (price < 100) return null

        // Get the remaining text as item name
        var itemPart = trimmedLine.substring(0, priceMatch.range.first).trim()

        // Try to extract quantity
        var quantity = 1

        // Pattern: "2x ItemName" or "x2 ItemName"
        val qtyPrefixPattern = Regex("""^(\d+)\s*[xX]\s*""")
        val qtyPrefixMatch = qtyPrefixPattern.find(itemPart)
        if (qtyPrefixMatch != null) {
            quantity = qtyPrefixMatch.groupValues[1].toIntOrNull() ?: 1
            itemPart = itemPart.substring(qtyPrefixMatch.range.last + 1).trim()
        } else {
            // Pattern: "ItemName x2" or "ItemName 2x"
            val qtySuffixPattern = Regex("""\s*[xX]?\s*(\d+)\s*[xX]?\s*$""")
            val qtySuffixMatch = qtySuffixPattern.find(itemPart)
            if (qtySuffixMatch != null) {
                val potentialQty = qtySuffixMatch.groupValues[1].toIntOrNull() ?: 1
                // Only consider as quantity if it's small (< 100)
                if (potentialQty < 100) {
                    quantity = potentialQty
                    itemPart = itemPart.substring(0, qtySuffixMatch.range.first).trim()
                }
            }
        }

        // Clean up item name
        val itemName = itemPart
            .replace(Regex("""[\[\](){}]"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()

        if (itemName.isEmpty() || itemName.length < 2) return null

        // Calculate unit price
        val unitPrice = if (quantity > 1) price / quantity else price

        return BillItem(
            id = 0,
            name = itemName,
            quantity = quantity,
            unitPrice = unitPrice,
            discount = 0
        )
    }
}

