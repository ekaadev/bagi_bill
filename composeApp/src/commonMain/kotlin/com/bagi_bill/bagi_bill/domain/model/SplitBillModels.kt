package com.bagi_bill.bagi_bill.domain.model

import kotlinx.serialization.Serializable

/**
 * Domain model representing a single item from scanned receipt or manual input
 *
 * @param id Unique identifier for the item
 * @param name Item name (e.g., "Nasi Goreng")
 * @param quantity Quantity of this item
 * @param unitPrice Price per unit
 * @param discount Discount amount for this item (if any)
 * @param assignedMemberIds List of member IDs who will share this item
 */
@Serializable
data class BillItem(
    val id: Long = 0,
    val name: String,
    val quantity: Int = 1,
    val unitPrice: Long, // in smallest currency unit (e.g., cents)
    val discount: Long = 0,
    val assignedMemberIds: List<Long> = emptyList()
) {
    /**
     * Get total price for this item (quantity * unitPrice - discount)
     */
    fun getTotalPrice(): Long {
        return (quantity * unitPrice) - discount
    }

    /**
     * Get price per assigned member (split equally)
     */
    fun getPricePerMember(): Long {
        if (assignedMemberIds.isEmpty()) return getTotalPrice()
        return getTotalPrice() / assignedMemberIds.size
    }

    /**
     * Check if this item has been assigned to any member
     */
    fun isAssigned(): Boolean = assignedMemberIds.isNotEmpty()
}

/**
 * Domain model for bill additional charges
 */
@Serializable
data class BillCharges(
    val taxPercent: Double = 0.0,       // Tax percentage (e.g., 10.0 for 10%)
    val servicePercent: Double = 0.0,   // Service charge percentage
    val otherCharges: Long = 0          // Other fixed charges
) {
    /**
     * Calculate tax amount based on subtotal
     */
    fun calculateTax(subtotal: Long): Long {
        return (subtotal * taxPercent / 100).toLong()
    }

    /**
     * Calculate service charge based on subtotal
     */
    fun calculateService(subtotal: Long): Long {
        return (subtotal * servicePercent / 100).toLong()
    }

    /**
     * Calculate total charges
     */
    fun calculateTotalCharges(subtotal: Long): Long {
        return calculateTax(subtotal) + calculateService(subtotal) + otherCharges
    }
}

/**
 * State holder for split bill creation flow
 */
@Serializable
data class SplitBillState(
    val billName: String = "",
    val items: List<BillItem> = emptyList(),
    val charges: BillCharges = BillCharges(),
    val members: List<SplitMember> = emptyList(),
    val capturedImagePath: String? = null
) {
    /**
     * Get subtotal of all items
     */
    fun getSubtotal(): Long {
        return items.sumOf { it.getTotalPrice() }
    }

    /**
     * Get grand total including all charges
     */
    fun getGrandTotal(): Long {
        val subtotal = getSubtotal()
        return subtotal + charges.calculateTotalCharges(subtotal)
    }

    /**
     * Check if all items are assigned
     */
    fun allItemsAssigned(): Boolean {
        return items.all { it.isAssigned() }
    }

    /**
     * Get unassigned items
     */
    fun getUnassignedItems(): List<BillItem> {
        return items.filter { !it.isAssigned() }
    }
}

/**
 * Simple member model for split bill flow (before saving to database)
 */
@Serializable
data class SplitMember(
    val id: Long = 0, // Will be assigned by ViewModel
    val name: String,
    val walletNumber: String? = null,
    val walletType: WalletType? = null
) {
    fun getInitial(): String {
        return name.firstOrNull()?.uppercase() ?: "?"
    }
}

/**
 * Result for each member after split calculation
 */
@Serializable
data class MemberSplitResult(
    val member: SplitMember,
    val items: List<BillItem>,
    val subtotal: Long,
    val taxAmount: Long,
    val serviceAmount: Long,
    val otherCharges: Long,
    val total: Long
) {
    /**
     * Generate formatted text for sharing (e.g., WhatsApp)
     */
    fun toShareableText(): String {
        val sb = StringBuilder()
        sb.appendLine("👤 ${member.name}")
        sb.appendLine("─────────────")
        items.forEach { item ->
            val priceForMember = item.getPricePerMember()
            sb.appendLine("• ${item.name}: Rp ${formatCurrency(priceForMember)}")
        }
        sb.appendLine("─────────────")
        sb.appendLine("Subtotal: Rp ${formatCurrency(subtotal)}")
        if (taxAmount > 0) sb.appendLine("Pajak: Rp ${formatCurrency(taxAmount)}")
        if (serviceAmount > 0) sb.appendLine("Service: Rp ${formatCurrency(serviceAmount)}")
        if (otherCharges > 0) sb.appendLine("Lainnya: Rp ${formatCurrency(otherCharges)}")
        sb.appendLine("═══════════════")
        sb.appendLine("💰 Total: Rp ${formatCurrency(total)}")
        return sb.toString()
    }
}

/**
 * Helper function to format currency
 */
fun formatCurrency(amount: Long): String {
    return amount.toString().reversed().chunked(3).joinToString(".").reversed()
}

