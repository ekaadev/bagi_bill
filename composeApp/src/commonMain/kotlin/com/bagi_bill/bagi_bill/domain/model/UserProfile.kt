package com.bagi_bill.bagi_bill.domain.model

/**
 * User profile representing the app owner
 */
data class UserProfile(
    val id: Long = 0,
    val name: String,
    val walletNumber: String? = null,
    val walletType: WalletType? = null
)

