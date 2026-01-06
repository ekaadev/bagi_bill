package com.bagi_bill.bagi_bill.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class BillItem(
    val name: String,
    val qty: Int,
    val price: Int
)

data class Member(
    val id: String = Random.nextInt(100000, 999999).toString(),
    val name: String,
    val wallet: String? = null,
    val phoneNumber: String? = null,
    val initial: String = if (name.isNotEmpty()) name.take(1).uppercase() else "?",
    val avatarColor: Color = Color.LightGray
) {
    fun canBePayer(): Boolean = !wallet.isNullOrBlank() && !phoneNumber.isNullOrBlank()
}

data class AssignableBillItem(
    val id: String,
    val name: String,
    val price: Int,
    val qty: Int,
    val assignedMemberIds: List<String> = emptyList()
)

data class SplitBillData(
    val merchantName: String = "Toko",
    val payer: Member,
    val members: List<Member>,
    val totalMembers: Int,
    val membersWithPaymentInfo: Int
) {
    companion object {
        fun create(merchantName: String = "Toko", payer: Member, members: List<Member>): SplitBillData {
            val allMembers = listOf(payer) + members
            val withPaymentInfo = allMembers.count { it.canBePayer() }

            return SplitBillData(
                merchantName = merchantName,
                payer = payer,
                members = members,
                totalMembers = allMembers.size,
                membersWithPaymentInfo = withPaymentInfo
            )
        }
    }

    fun getAllMembers(): List<Member> = listOf(payer) + members

    fun getEligiblePayers(): List<Member> = getAllMembers().filter { it.canBePayer() }

    fun isValid(): Boolean {
        return payer.canBePayer() && totalMembers >= 2
    }
}

// Data classes for DoneScreen usage
data class ProcessedItem(
    val name: String,
    val qty: Int,
    val sharePrice: Int
)

data class ProcessedMember(
    val member: Member,
    val isPayer: Boolean,
    val items: List<ProcessedItem>,
    val totalToPay: Int
)