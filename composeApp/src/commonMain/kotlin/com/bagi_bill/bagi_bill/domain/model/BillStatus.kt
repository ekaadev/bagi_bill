package com.bagi_bill.bagi_bill.domain.model

/**
 * Enum representing the payment status of a split bill
 */
enum class BillStatus(val displayName: String) {
    PAID("Lunas"),
    UNPAID("Belum Bayar"),
    PARTIALLY_PAID("Sebagian Lunas");

    companion object {
        fun fromString(value: String): BillStatus {
            return entries.find { it.name == value } ?: UNPAID
        }
    }
}

