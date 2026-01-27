package com.bagi_bill.bagi_bill.domain.parser

/**
 * Single item from a receipt.
 */
data class ReceiptItem(
    val qty: Int,
    val name: String,
    val price: Int
) {
    val totalPrice: Int get() = qty * price
}

/**
 * Summary section of a receipt.
 */
data class ReceiptSummary(
    val subtotal: Int = 0,
    val pajak: Int = 0,
    val diskon: Int = 0,
    val servis: Int = 0,
    val lainnya: Int = 0,
    val total: Int = 0
)

/**
 * Complete parsed receipt data.
 */
data class ParsedReceipt(
    val name: String,
    val items: List<ReceiptItem>,
    val summary: ReceiptSummary
) {
    val calculatedSubtotal: Int get() = items.sumOf { it.totalPrice }
    val calculatedTotal: Int get() = calculatedSubtotal + summary.pajak + summary.servis - summary.diskon + summary.lainnya
}
