package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.Member
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

class WebBillRepository : BillRepository {
    override suspend fun createBill(bill: Bill, members: List<Member>): Long = 0L

    override suspend fun updateBill(bill: Bill) {}

    override suspend fun deleteBill(billId: Long) {}

    override suspend fun getBillById(billId: Long): Bill? = null

    override fun getAllBills(): Flow<List<Bill>> = flowOf(emptyList())

    override fun getBillsByStatus(status: BillStatus): Flow<List<Bill>> = flowOf(emptyList())

    @OptIn(ExperimentalTime::class)
    override fun getBillsByDateRange(startDate: Instant, endDate: Instant): Flow<List<Bill>> =
        flowOf(emptyList())

    override fun getDraftBills(): Flow<List<Bill>> = flowOf(emptyList())

    override fun getRecentBills(limit: Long): Flow<List<Bill>> = flowOf(emptyList())

    override suspend fun countDraftBills(): Long = 0L

    override suspend fun updateMemberPaymentStatus(memberId: Long, isPaid: Boolean) {}

    override suspend fun updateBillStatus(billId: Long, status: BillStatus) {}
}

