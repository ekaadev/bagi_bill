package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.BillType
import com.bagi_bill.bagi_bill.domain.model.Member
import com.bagi_bill.bagi_bill.domain.model.WalletType
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
private data class StoredBill(
    val id: Long,
    val name: String,
    val totalAmount: Long,
    val createdDate: Long, // epoch millis
    val status: String,
    val isDraft: Boolean,
    val billType: String,
    val members: List<StoredMember>
)

@Serializable
private data class StoredMember(
    val id: Long,
    val billId: Long,
    val name: String,
    val walletNumber: String?,
    val walletType: String?,
    val amount: Long,
    val isPaid: Boolean
)

@OptIn(ExperimentalTime::class)
class WebBillRepository : BillRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val billsFlow = MutableStateFlow<List<Bill>>(emptyList())
    private var nextBillId = 1L
    private var nextMemberId = 1L

    companion object {
        private const val STORAGE_KEY = "bagi_bill_bills"
        private const val NEXT_BILL_ID_KEY = "bagi_bill_next_bill_id"
        private const val NEXT_MEMBER_ID_KEY = "bagi_bill_next_member_id"
    }

    init {
        loadFromLocalStorage()
    }

    private fun loadFromLocalStorage() {
        try {
            // Load next IDs
            localStorage.getItem(NEXT_BILL_ID_KEY)?.let {
                nextBillId = it.toLongOrNull() ?: 1L
            }
            localStorage.getItem(NEXT_MEMBER_ID_KEY)?.let {
                nextMemberId = it.toLongOrNull() ?: 1L
            }

            // Load bills
            val storedData = localStorage.getItem(STORAGE_KEY)
            if (storedData != null && storedData.isNotEmpty()) {
                val storedBills = json.decodeFromString<List<StoredBill>>(storedData)
                val bills = storedBills.map { it.toDomain() }
                billsFlow.value = bills
            }
        } catch (e: Exception) {
            console.log("Error loading from localStorage: ${e.message}")
            billsFlow.value = emptyList()
        }
    }

    private fun saveToLocalStorage() {
        try {
            val storedBills = billsFlow.value.map { it.toStored() }
            val jsonData = json.encodeToString(storedBills)
            localStorage.setItem(STORAGE_KEY, jsonData)
            localStorage.setItem(NEXT_BILL_ID_KEY, nextBillId.toString())
            localStorage.setItem(NEXT_MEMBER_ID_KEY, nextMemberId.toString())
        } catch (e: Exception) {
            console.log("Error saving to localStorage: ${e.message}")
        }
    }

    private fun StoredBill.toDomain(): Bill {
        return Bill(
            id = id,
            name = name,
            totalAmount = totalAmount,
            createdDate = Instant.fromEpochMilliseconds(createdDate),
            status = try { BillStatus.valueOf(status) } catch (e: Exception) { BillStatus.UNPAID },
            isDraft = isDraft,
            billType = try { BillType.valueOf(billType) } catch (e: Exception) { BillType.MANUAL },
            members = members.map { it.toDomain() }
        )
    }

    private fun StoredMember.toDomain(): Member {
        return Member(
            id = id,
            billId = billId,
            name = name,
            walletNumber = walletNumber,
            walletType = walletType?.let {
                try { WalletType.valueOf(it) } catch (e: Exception) { null }
            },
            amount = amount,
            isPaid = isPaid
        )
    }

    private fun Bill.toStored(): StoredBill {
        return StoredBill(
            id = id,
            name = name,
            totalAmount = totalAmount,
            createdDate = createdDate.toEpochMilliseconds(),
            status = status.name,
            isDraft = isDraft,
            billType = billType.name,
            members = members.map { it.toStored() }
        )
    }

    private fun Member.toStored(): StoredMember {
        return StoredMember(
            id = id,
            billId = billId,
            name = name,
            walletNumber = walletNumber,
            walletType = walletType?.name,
            amount = amount,
            isPaid = isPaid
        )
    }

    override suspend fun createBill(bill: Bill, members: List<Member>): Long {
        val billId = nextBillId++
        val storedMembers = members.mapIndexed { index, member ->
            member.copy(
                id = nextMemberId++,
                billId = billId
            )
        }

        val newBill = bill.copy(
            id = billId,
            members = storedMembers
        )

        billsFlow.value = billsFlow.value + newBill
        saveToLocalStorage()

        return billId
    }

    override suspend fun updateBill(bill: Bill) {
        billsFlow.value = billsFlow.value.map {
            if (it.id == bill.id) bill else it
        }
        saveToLocalStorage()
    }

    override suspend fun deleteBill(billId: Long) {
        billsFlow.value = billsFlow.value.filter { it.id != billId }
        saveToLocalStorage()
    }

    override suspend fun getBillById(billId: Long): Bill? {
        return billsFlow.value.find { it.id == billId }
    }

    override fun getAllBills(): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.sortedByDescending { it.createdDate }
        }
    }

    override fun getBillsByStatus(status: BillStatus): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { it.status == status }
                .sortedByDescending { it.createdDate }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun getBillsByDateRange(startDate: Instant, endDate: Instant): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { bill ->
                bill.createdDate >= startDate && bill.createdDate <= endDate
            }.sortedByDescending { it.createdDate }
        }
    }

    override fun getDraftBills(): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { it.isDraft }
                .sortedByDescending { it.createdDate }
        }
    }

    override fun getRecentBills(limit: Long): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { !it.isDraft }
                .sortedByDescending { it.createdDate }
                .take(limit.toInt())
        }
    }

    override suspend fun countDraftBills(): Long {
        return billsFlow.value.count { it.isDraft }.toLong()
    }

    override suspend fun updateMemberPaymentStatus(memberId: Long, isPaid: Boolean) {
        billsFlow.value = billsFlow.value.map { bill ->
            bill.copy(
                members = bill.members.map { member ->
                    if (member.id == memberId) member.copy(isPaid = isPaid) else member
                }
            )
        }
        saveToLocalStorage()
    }

    override suspend fun updateBillStatus(billId: Long, status: BillStatus) {
        billsFlow.value = billsFlow.value.map { bill ->
            if (bill.id == billId) bill.copy(status = status) else bill
        }
        saveToLocalStorage()
    }
}

// External console for logging
private external val console: Console

private external interface Console {
    fun log(message: String)
}
