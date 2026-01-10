package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Repository interface for managing split bills and members
 */
interface BillRepository {
    /**
     * Create a new bill with members
     * @return The ID of the created bill
     */
    suspend fun createBill(bill: Bill, members: List<Member>): Long

    /**
     * Update an existing bill
     */
    suspend fun updateBill(bill: Bill)

    /**
     * Delete a bill and all associated members
     */
    suspend fun deleteBill(billId: Long)

    /**
     * Get a specific bill by ID with its members
     */
    suspend fun getBillById(billId: Long): Bill?

    /**
     * Get all bills as a Flow for reactive updates
     */
    fun getAllBills(): Flow<List<Bill>>

    /**
     * Get bills filtered by status
     */
    fun getBillsByStatus(status: BillStatus): Flow<List<Bill>>

    /**
     * Get bills within a date range
     */
    @OptIn(ExperimentalTime::class)
    fun getBillsByDateRange(startDate: Instant, endDate: Instant): Flow<List<Bill>>

    /**
     * Get all draft bills
     */
    fun getDraftBills(): Flow<List<Bill>>

    /**
     * Get recent bills (non-draft) with limit
     */
    fun getRecentBills(limit: Long = 10): Flow<List<Bill>>

    /**
     * Get count of draft bills
     */
    suspend fun countDraftBills(): Long

    /**
     * Update payment status for a member
     */
    suspend fun updateMemberPaymentStatus(memberId: Long, isPaid: Boolean)

    /**
     * Update bill status (automatically or manually)
     */
    suspend fun updateBillStatus(billId: Long, status: BillStatus)
}


