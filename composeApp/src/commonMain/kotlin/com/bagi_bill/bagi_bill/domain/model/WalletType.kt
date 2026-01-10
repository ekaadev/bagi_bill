package com.bagi_bill.bagi_bill.domain.model

/**
 * Enum representing supported wallet types for payment identification
 */
enum class WalletType(val displayName: String) {
    GOPAY("GoPay"),
    OVO("OVO"),
    DANA("Dana"),
    SHOPEEPAY("ShopeePay"),
    LINKAJA("LinkAja"),
    BANK_TRANSFER("Bank Transfer");

    companion object {
        fun fromString(value: String?): WalletType? {
            return entries.find { it.name == value }
        }
    }
}

