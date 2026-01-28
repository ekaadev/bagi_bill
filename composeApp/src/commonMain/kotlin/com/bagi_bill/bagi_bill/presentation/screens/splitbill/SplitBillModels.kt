package com.bagi_bill.bagi_bill.presentation.screens.splitbill

import androidx.compose.ui.graphics.Color
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SelectableMember

/**
 * Represents a custom share amount for a specific member on a bill item.
 * Used when user wants to split an item unevenly (e.g., one person pays more).
 */
data class MemberShare(
    val memberId: String,
    val amount: Int  // Custom amount in rupiah
)

/**
 * A bill item that can be assigned to multiple members.
 * Supports both equal split and custom per-member amounts.
 */
data class AssignableBillItem(
    val id: String,
    val name: String,
    val price: Int,
    val qty: Int,
    val assignedMemberIds: List<String> = emptyList(),
    val customShares: List<MemberShare> = emptyList()
) {
    val totalPrice: Int get() = price * qty
    
    /**
     * Get the share amount for a specific member.
     * Returns 0 if member is not assigned to this item.
     * Uses custom share if defined, otherwise equal split.
     */
    fun getShareForMember(memberId: String): Int {
        if (!assignedMemberIds.contains(memberId)) return 0
        
        // Check for custom share first
        val customShare = customShares.find { it.memberId == memberId }
        if (customShare != null) return customShare.amount
        
        // Equal split
        return if (assignedMemberIds.isNotEmpty()) {
            totalPrice / assignedMemberIds.size
        } else 0
    }
    
    /**
     * Check if this item has custom (unequal) share distribution.
     */
    fun hasCustomSplit(): Boolean = customShares.isNotEmpty()
    
    /**
     * Reset to equal split by clearing custom shares.
     */
    fun resetToEqualSplit(): AssignableBillItem = copy(customShares = emptyList())
    
    /**
     * Apply custom shares, ensuring total equals item price.
     */
    fun applyCustomShares(shares: List<MemberShare>): AssignableBillItem {
        val total = shares.sumOf { it.amount }
        require(total == totalPrice) { "Custom shares must equal item total price" }
        return copy(customShares = shares)
    }
}

/**
 * UI-facing member with avatar color for display.
 */
data class SplitBillMember(
    val id: String,
    val name: String,
    val initial: String,
    val avatarColor: Color
) {
    companion object {
        fun fromSelectableMember(member: SelectableMember): SplitBillMember {
            return SplitBillMember(
                id = member.id,
                name = member.name,
                initial = member.initial,
                avatarColor = generateColorForName(member.name)
            )
        }
    }
}

/**
 * Generate consistent color based on name for avatar display.
 */
fun generateColorForName(name: String): Color {
    val colors = listOf(
        Color(0xFF00897B), // Teal
        Color(0xFF1976D2), // Blue
        Color(0xFFE53935), // Red
        Color(0xFFFB8C00), // Orange
        Color(0xFF8E24AA), // Purple
        Color(0xFF43A047)  // Green
    )
    if (name.isEmpty()) return colors[0]
    return colors[name.first().uppercaseChar().code % colors.size]
}
