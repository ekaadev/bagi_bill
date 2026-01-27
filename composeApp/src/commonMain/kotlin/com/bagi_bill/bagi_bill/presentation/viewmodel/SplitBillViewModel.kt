package com.bagi_bill.bagi_bill.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagi_bill.bagi_bill.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * ViewModel untuk mengelola flow pembuatan Split Bill
 * Shared across multiple screens: ItemDetail, SelectMember, SplitAssignment, Result
 */
@OptIn(ExperimentalTime::class)
class SplitBillViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplitBillState())
    val state: StateFlow<SplitBillState> = _state.asStateFlow()

    private val _memberResults = MutableStateFlow<List<MemberSplitResult>>(emptyList())
    val memberResults: StateFlow<List<MemberSplitResult>> = _memberResults.asStateFlow()

    // ==================== BILL NAME ====================

    fun updateBillName(name: String) {
        _state.update { it.copy(billName = name) }
    }

    // ==================== ITEMS ====================

    fun addItem(item: BillItem) {
        _state.update { currentState ->
            val newId = (currentState.items.maxOfOrNull { it.id } ?: 0) + 1
            currentState.copy(
                items = currentState.items + item.copy(id = newId)
            )
        }
    }

    fun updateItem(itemId: Long, name: String, quantity: Int, unitPrice: Long, discount: Long = 0) {
        _state.update { currentState ->
            currentState.copy(
                items = currentState.items.map { item ->
                    if (item.id == itemId) {
                        item.copy(
                            name = name,
                            quantity = quantity,
                            unitPrice = unitPrice,
                            discount = discount
                        )
                    } else item
                }
            )
        }
    }

    fun removeItem(itemId: Long) {
        _state.update { currentState ->
            currentState.copy(
                items = currentState.items.filter { it.id != itemId }
            )
        }
    }

    fun setItems(items: List<BillItem>) {
        _state.update { it.copy(items = items) }
    }

    // ==================== CHARGES ====================

    fun updateCharges(taxPercent: Double, servicePercent: Double, otherCharges: Long = 0) {
        _state.update { currentState ->
            currentState.copy(
                charges = BillCharges(
                    taxPercent = taxPercent,
                    servicePercent = servicePercent,
                    otherCharges = otherCharges
                )
            )
        }
    }

    // ==================== MEMBERS ====================

    fun addMember(name: String, walletNumber: String? = null, walletType: WalletType? = null) {
        if (name.isBlank()) return

        _state.update { currentState ->
            val newId = Clock.System.now().toEpochMilliseconds()
            currentState.copy(
                members = currentState.members + SplitMember(
                    id = newId,
                    name = name.trim(),
                    walletNumber = walletNumber,
                    walletType = walletType
                )
            )
        }
    }

    fun removeMember(memberId: Long) {
        _state.update { currentState ->
            // Also remove member from all item assignments
            val updatedItems = currentState.items.map { item ->
                item.copy(
                    assignedMemberIds = item.assignedMemberIds.filter { it != memberId }
                )
            }
            currentState.copy(
                members = currentState.members.filter { it.id != memberId },
                items = updatedItems
            )
        }
    }

    fun updateMember(memberId: Long, name: String, walletNumber: String?, walletType: WalletType?) {
        _state.update { currentState ->
            currentState.copy(
                members = currentState.members.map { member ->
                    if (member.id == memberId) {
                        member.copy(
                            name = name,
                            walletNumber = walletNumber,
                            walletType = walletType
                        )
                    } else member
                }
            )
        }
    }

    // ==================== ITEM ASSIGNMENT ====================

    fun assignItemToMembers(itemId: Long, memberIds: List<Long>) {
        _state.update { currentState ->
            currentState.copy(
                items = currentState.items.map { item ->
                    if (item.id == itemId) {
                        item.copy(assignedMemberIds = memberIds)
                    } else item
                }
            )
        }
    }

    fun toggleMemberForItem(itemId: Long, memberId: Long) {
        _state.update { currentState ->
            currentState.copy(
                items = currentState.items.map { item ->
                    if (item.id == itemId) {
                        val currentAssigned = item.assignedMemberIds.toMutableList()
                        if (memberId in currentAssigned) {
                            currentAssigned.remove(memberId)
                        } else {
                            currentAssigned.add(memberId)
                        }
                        item.copy(assignedMemberIds = currentAssigned)
                    } else item
                }
            )
        }
    }

    /**
     * Bagi Rata - Assign semua item ke semua member
     */
    fun assignAllItemsToAllMembers() {
        _state.update { currentState ->
            val allMemberIds = currentState.members.map { it.id }
            currentState.copy(
                items = currentState.items.map { item ->
                    item.copy(assignedMemberIds = allMemberIds)
                }
            )
        }
    }

    /**
     * Clear semua assignment
     */
    fun clearAllAssignments() {
        _state.update { currentState ->
            currentState.copy(
                items = currentState.items.map { item ->
                    item.copy(assignedMemberIds = emptyList())
                }
            )
        }
    }

    // ==================== CALCULATION ====================

    fun calculateResults() {
        viewModelScope.launch {
            val currentState = _state.value
            val results = mutableListOf<MemberSplitResult>()

            currentState.members.forEach { member ->
                // Get items assigned to this member
                val memberItems = currentState.items.filter { member.id in it.assignedMemberIds }

                // Calculate subtotal for this member
                val subtotal = memberItems.sumOf { it.getPricePerMember() }

                // Calculate proportional charges
                val totalSubtotal = currentState.getSubtotal()
                val proportion = if (totalSubtotal > 0) subtotal.toDouble() / totalSubtotal else 0.0

                val taxAmount = (currentState.charges.calculateTax(totalSubtotal) * proportion).toLong()
                val serviceAmount = (currentState.charges.calculateService(totalSubtotal) * proportion).toLong()
                val otherCharges = (currentState.charges.otherCharges * proportion).toLong()

                val total = subtotal + taxAmount + serviceAmount + otherCharges

                results.add(
                    MemberSplitResult(
                        member = member,
                        items = memberItems,
                        subtotal = subtotal,
                        taxAmount = taxAmount,
                        serviceAmount = serviceAmount,
                        otherCharges = otherCharges,
                        total = total
                    )
                )
            }

            _memberResults.value = results
        }
    }

    // ==================== IMAGE ====================

    fun setCapturedImage(imagePath: String?) {
        _state.update { it.copy(capturedImagePath = imagePath) }
    }

    // ==================== RESET ====================

    fun reset() {
        _state.value = SplitBillState()
        _memberResults.value = emptyList()
    }

    // ==================== SHARE TEXT ====================

    fun generateShareableText(): String {
        val currentState = _state.value
        val results = _memberResults.value

        val sb = StringBuilder()
        sb.appendLine("🧾 ${currentState.billName.ifEmpty { "Split Bill" }}")
        sb.appendLine("════════════════════")
        sb.appendLine()

        results.forEach { result ->
            sb.appendLine(result.toShareableText())
            sb.appendLine()
        }

        sb.appendLine("════════════════════")
        sb.appendLine("💵 Grand Total: Rp ${formatCurrency(currentState.getGrandTotal())}")
        sb.appendLine()
        sb.appendLine("Dibuat dengan BagiBill 📱")

        return sb.toString()
    }
}

