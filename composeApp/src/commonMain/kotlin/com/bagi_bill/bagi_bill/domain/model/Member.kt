package com.bagi_bill.bagi_bill.domain.model

/**
 * Domain model representing a member in a split bill
 *
 * @param id Unique identifier for the member
 * @param billId Reference to the parent bill
 * @param name Member's name (required)
 * @param walletNumber Optional wallet/phone number for identification
 * @param walletType Type of wallet/payment method
 * @param amount Amount this member needs to pay (in smallest currency unit)
 * @param isPaid Whether this member has completed payment
 */
data class Member(
    val id: Long = 0,
    val billId: Long,
    val name: String,
    val walletNumber: String? = null,
    val walletType: WalletType? = null,
    val amount: Long,
    val isPaid: Boolean = false
) {
    /**
     * Get the initial letter of the member's name for avatar display
     */
    fun getInitial(): String {
        return name.firstOrNull()?.uppercase() ?: "?"
    }
}

