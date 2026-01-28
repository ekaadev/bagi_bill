package com.bagi_bill.bagi_bill.data.repository

import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.BillType
import com.bagi_bill.bagi_bill.domain.model.Member
import com.bagi_bill.bagi_bill.domain.model.WalletType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

/**
 * Web implementation of BillRepository using in-memory storage with localStorage persistence.
 * This is a practical approach since SQLDelight web-worker-driver doesn't fully support WASM yet.
 */
@OptIn(ExperimentalTime::class)
class WebBillRepository : BillRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    // In-memory storage backed by localStorage
    private val billsFlow = MutableStateFlow<List<BillData>>(emptyList())
    private var nextBillId = 1L
    private var nextMemberId = 1L

    init {
        loadFromLocalStorage()
    }

    // Serializable data classes for localStorage persistence
    @Serializable
    private data class BillData(
        val id: Long,
        val name: String,
        val totalAmount: Long,
        val createdDate: Long, // epoch millis
        val status: String,
        val isDraft: Boolean,
        val billType: String,
        val members: List<MemberData>
    )

    @Serializable
    private data class MemberData(
        val id: Long,
        val billId: Long,
        val name: String,
        val walletNumber: String?,
        val walletType: String?,
        val amount: Long,
        val isPaid: Boolean
    )

    @Serializable
    private data class StorageData(
        val bills: List<BillData>,
        val nextBillId: Long,
        val nextMemberId: Long
    )

    private fun loadFromLocalStorage() {
        try {
            val stored = WebStorage.getItem(STORAGE_KEY)
            if (stored != null) {
                val data = json.decodeFromString<StorageData>(stored)
                billsFlow.value = data.bills
                nextBillId = data.nextBillId
                nextMemberId = data.nextMemberId
            }
        } catch (e: Exception) {
            console.log("Failed to load from localStorage: ${e.message}")
        }
    }

    private fun saveToLocalStorage() {
        try {
            val data = StorageData(
                bills = billsFlow.value,
                nextBillId = nextBillId,
                nextMemberId = nextMemberId
            )
            WebStorage.setItem(STORAGE_KEY, json.encodeToString(data))
        } catch (e: Exception) {
            console.log("Failed to save to localStorage: ${e.message}")
        }
    }

    private fun BillData.toDomainModel(): Bill = Bill(
        id = id,
        name = name,
        totalAmount = totalAmount,
        createdDate = Instant.fromEpochMilliseconds(createdDate),
        status = BillStatus.valueOf(status),
        isDraft = isDraft,
        billType = BillType.valueOf(billType),
        members = members.map { it.toDomainModel() }
    )

    private fun MemberData.toDomainModel(): Member = Member(
        id = id,
        billId = billId,
        name = name,
        walletNumber = walletNumber,
        walletType = walletType?.let { WalletType.valueOf(it) },
        amount = amount,
        isPaid = isPaid
    )

    private fun Bill.toDataModel(members: List<Member>): BillData = BillData(
        id = id,
        name = name,
        totalAmount = totalAmount,
        createdDate = createdDate.toEpochMilliseconds(),
        status = status.name,
        isDraft = isDraft,
        billType = billType.name,
        members = members.map { it.toDataModel() }
    )

    private fun Member.toDataModel(): MemberData = MemberData(
        id = id,
        billId = billId,
        name = name,
        walletNumber = walletNumber,
        walletType = walletType?.name,
        amount = amount,
        isPaid = isPaid
    )

    override suspend fun createBill(bill: Bill, members: List<Member>): Long {
        val billId = nextBillId++
        val memberDataList = members.mapIndexed { index, member ->
            val memberId = nextMemberId++
            MemberData(
                id = memberId,
                billId = billId,
                name = member.name,
                walletNumber = member.walletNumber,
                walletType = member.walletType?.name,
                amount = member.amount,
                isPaid = member.isPaid
            )
        }

        val billData = BillData(
            id = billId,
            name = bill.name,
            totalAmount = bill.totalAmount,
            createdDate = bill.createdDate.toEpochMilliseconds(),
            status = bill.status.name,
            isDraft = bill.isDraft,
            billType = bill.billType.name,
            members = memberDataList
        )

        billsFlow.value = billsFlow.value + billData
        saveToLocalStorage()
        return billId
    }

    override suspend fun updateBill(bill: Bill) {
        billsFlow.value = billsFlow.value.map { existing ->
            if (existing.id == bill.id) {
                existing.copy(
                    name = bill.name,
                    totalAmount = bill.totalAmount,
                    status = bill.status.name,
                    isDraft = bill.isDraft
                )
            } else existing
        }
        saveToLocalStorage()
    }

    override suspend fun deleteBill(billId: Long) {
        billsFlow.value = billsFlow.value.filter { it.id != billId }
        saveToLocalStorage()
    }

    override suspend fun getBillById(billId: Long): Bill? {
        return billsFlow.value.find { it.id == billId }?.toDomainModel()
    }

    override fun getAllBills(): Flow<List<Bill>> {
        return billsFlow.map { bills -> bills.map { it.toDomainModel() } }
    }

    override fun getBillsByStatus(status: BillStatus): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { it.status == status.name }.map { it.toDomainModel() }
        }
    }

    override fun getBillsByDateRange(startDate: Instant, endDate: Instant): Flow<List<Bill>> {
        val startMillis = startDate.toEpochMilliseconds()
        val endMillis = endDate.toEpochMilliseconds()
        return billsFlow.map { bills ->
            bills.filter { it.createdDate in startMillis..endMillis }.map { it.toDomainModel() }
        }
    }

    override fun getDraftBills(): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.filter { it.isDraft }.map { it.toDomainModel() }
        }
    }

    override fun getRecentBills(limit: Long): Flow<List<Bill>> {
        return billsFlow.map { bills ->
            bills.sortedByDescending { it.createdDate }
                .take(limit.toInt())
                .map { it.toDomainModel() }
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
            if (bill.id == billId) bill.copy(status = status.name) else bill
        }
        saveToLocalStorage()
    }

    companion object {
        private const val STORAGE_KEY = "bagi_bill_data"
    }
}

// External declarations for browser APIs compatible with both JS and WASM
private external object console {
    fun log(message: String)
}

internal expect object WebStorage {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}

