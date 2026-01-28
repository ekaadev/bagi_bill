package com.bagi_bill.bagi_bill.presentation.screens.selectmember

import kotlin.random.Random

/**
 * Represents a member in select member flow.
 * Different from domain.model.Member which is for database storage.
 */
data class SelectableMember(
    val id: String = Random.nextInt(100000, 999999).toString(),
    val name: String,
    val wallet: String? = null,
    val phoneNumber: String? = null
) {
    /**
     * A member can be a payer only if they have both wallet and phone number.
     */
    fun canBePayer(): Boolean = !wallet.isNullOrBlank() && !phoneNumber.isNullOrBlank()
    
    /**
     * Get the first letter of name for avatar display.
     */
    val initial: String get() = name.firstOrNull()?.uppercase() ?: "?"
}

/**
 * Data passed from SelectMember to SplitBill screen.
 */
data class SplitBillData(
    val payer: SelectableMember,
    val members: List<SelectableMember>,
    val totalMembers: Int,
    val membersWithPaymentInfo: Int
) {
    companion object {
        fun create(payer: SelectableMember, members: List<SelectableMember>): SplitBillData {
            val allMembers = listOf(payer) + members
            val withPaymentInfo = allMembers.count { it.canBePayer() }
            return SplitBillData(
                payer = payer,
                members = members,
                totalMembers = allMembers.size,
                membersWithPaymentInfo = withPaymentInfo
            )
        }
    }

    fun getAllMembers(): List<SelectableMember> = listOf(payer) + members
    fun getEligiblePayers(): List<SelectableMember> = getAllMembers().filter { it.canBePayer() }
    fun isValid(): Boolean = payer.canBePayer() && totalMembers >= 2
}

/**
 * Phone contact from device.
 */
data class PhoneContact(
    val id: String,
    val name: String,
    val phoneNumber: String
)

/**
 * List of Indonesian e-wallets.
 */
val indonesianWallets = listOf(
    "GoPay", "OVO", "DANA", "ShopeePay", "LinkAja",
    "Jenius", "BCA Mobile", "Mandiri e-Money", "BRI Mobile", "BNI Mobile"
)
