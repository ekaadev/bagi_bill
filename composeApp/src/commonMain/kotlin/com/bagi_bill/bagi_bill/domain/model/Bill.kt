package com.bagi_bill.bagi_bill.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Domain model representing a split bill
 *
 * @param id Unique identifier for the bill
 * @param name Name/description of the bill
 * @param totalAmount Total amount to be split (in smallest currency unit)
 * @param createdDate Timestamp when the bill was created
 * @param status Current payment status
 * @param isDraft Whether this bill is a draft
 * @param billType Type of bill creation (SCAN or MANUAL)
 * @param members List of members involved in this bill
 */
data class Bill @OptIn(ExperimentalTime::class) constructor(
    val id: Long = 0,
    val name: String,
    val totalAmount: Long,
    val createdDate: Instant,
    val status: BillStatus,
    val isDraft: Boolean = false,
    val billType: BillType = BillType.MANUAL,
    val members: List<Member> = emptyList()
) {
    /**
     * Check if all members have paid their share
     */
    fun isFullyPaid(): Boolean {
        return members.isNotEmpty() && members.all { it.isPaid }
    }

    /**
     * Get the total amount that has been paid
     */
    fun getTotalPaid(): Long {
        return members.filter { it.isPaid }.sumOf { it.amount }
    }

    /**
     * Get the remaining amount to be paid
     */
    fun getRemainingAmount(): Long {
        return totalAmount - getTotalPaid()
    }
}


