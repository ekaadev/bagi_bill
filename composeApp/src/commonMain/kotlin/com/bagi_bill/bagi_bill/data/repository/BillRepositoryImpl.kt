package com.bagi_bill.bagi_bill.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bagi_bill.bagi_bill.database.AppDatabase
import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.BillType
import com.bagi_bill.bagi_bill.domain.model.Member
import com.bagi_bill.bagi_bill.domain.model.WalletType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BillRepositoryImpl(
    private val database: AppDatabase
) : BillRepository {

    private val queries = database.appDatabaseQueries

    override suspend fun createBill(bill: Bill, members: List<Member>): Long = withContext(Dispatchers.Default) {
        return@withContext queries.transactionWithResult {
            // Insert bill
            queries.insertBill(
                name = bill.name,
                total_amount = bill.totalAmount,
                created_date = bill.createdDate.toEpochMilliseconds(),
                status = bill.status.name,
                is_draft = if (bill.isDraft) 1 else 0,
                bill_type = bill.billType.name
            )

            // Get last inserted bill ID
            val billId = queries.getLastInsertedBillId().executeAsOne()

            // Insert members
            members.forEach { member ->
                queries.insertMember(
                    bill_id = billId,
                    name = member.name,
                    wallet_number = member.walletNumber,
                    wallet_type = member.walletType?.name,
                    amount = member.amount,
                    is_paid = if (member.isPaid) 1 else 0
                )
            }

            billId
        }
    }

    override suspend fun updateBill(bill: Bill): Unit = withContext(Dispatchers.Default) {
        queries.updateBill(
            name = bill.name,
            total_amount = bill.totalAmount,
            status = bill.status.name,
            is_draft = if (bill.isDraft) 1 else 0,
            id = bill.id
        )
    }

    override suspend fun deleteBill(billId: Long): Unit = withContext(Dispatchers.Default) {
        queries.deleteBill(billId)
    }

    override suspend fun getBillById(billId: Long): Bill? = withContext(Dispatchers.Default) {
        val billEntity = queries.getBillById(billId).executeAsOneOrNull() ?: return@withContext null
        val memberEntities = queries.getMembersByBillId(billId).executeAsList()

        Bill(
            id = billEntity.id,
            name = billEntity.name,
            totalAmount = billEntity.total_amount,
            createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
            status = BillStatus.valueOf(billEntity.status),
            isDraft = billEntity.is_draft == 1L,
            billType = BillType.valueOf(billEntity.bill_type),
            members = memberEntities.map { memberEntity ->
                Member(
                    id = memberEntity.id,
                    billId = memberEntity.bill_id,
                    name = memberEntity.name,
                    walletNumber = memberEntity.wallet_number,
                    walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                    amount = memberEntity.amount,
                    isPaid = memberEntity.is_paid == 1L
                )
            }
        )
    }

    override fun getAllBills(): Flow<List<Bill>> {
        return queries.getAllBills()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { bills ->
                bills.map { billEntity ->
                    val memberEntities = queries.getMembersByBillId(billEntity.id).executeAsList()
                    Bill(
                        id = billEntity.id,
                        name = billEntity.name,
                        totalAmount = billEntity.total_amount,
                        createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
                        status = BillStatus.valueOf(billEntity.status),
                        isDraft = billEntity.is_draft == 1L,
                        billType = BillType.valueOf(billEntity.bill_type),
                        members = memberEntities.map { memberEntity ->
                            Member(
                                id = memberEntity.id,
                                billId = memberEntity.bill_id,
                                name = memberEntity.name,
                                walletNumber = memberEntity.wallet_number,
                                walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                                amount = memberEntity.amount,
                                isPaid = memberEntity.is_paid == 1L
                            )
                        }
                    )
                }
            }
    }

    override fun getBillsByStatus(status: BillStatus): Flow<List<Bill>> {
        return queries.getBillsByStatus(status.name)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { bills ->
                bills.map { billEntity ->
                    val memberEntities = queries.getMembersByBillId(billEntity.id).executeAsList()
                    Bill(
                        id = billEntity.id,
                        name = billEntity.name,
                        totalAmount = billEntity.total_amount,
                        createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
                        status = BillStatus.valueOf(billEntity.status),
                        isDraft = billEntity.is_draft == 1L,
                        billType = BillType.valueOf(billEntity.bill_type),
                        members = memberEntities.map { memberEntity ->
                            Member(
                                id = memberEntity.id,
                                billId = memberEntity.bill_id,
                                name = memberEntity.name,
                                walletNumber = memberEntity.wallet_number,
                                walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                                amount = memberEntity.amount,
                                isPaid = memberEntity.is_paid == 1L
                            )
                        }
                    )
                }
            }
    }

    override fun getBillsByDateRange(startDate: Instant, endDate: Instant): Flow<List<Bill>> {
        return queries.getBillsByDateRange(
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds()
        )
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { bills ->
                bills.map { billEntity ->
                    val memberEntities = queries.getMembersByBillId(billEntity.id).executeAsList()
                    Bill(
                        id = billEntity.id,
                        name = billEntity.name,
                        totalAmount = billEntity.total_amount,
                        createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
                        status = BillStatus.valueOf(billEntity.status),
                        isDraft = billEntity.is_draft == 1L,
                        billType = BillType.valueOf(billEntity.bill_type),
                        members = memberEntities.map { memberEntity ->
                            Member(
                                id = memberEntity.id,
                                billId = memberEntity.bill_id,
                                name = memberEntity.name,
                                walletNumber = memberEntity.wallet_number,
                                walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                                amount = memberEntity.amount,
                                isPaid = memberEntity.is_paid == 1L
                            )
                        }
                    )
                }
            }
    }

    override fun getDraftBills(): Flow<List<Bill>> {
        return queries.getDraftBills()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { bills ->
                bills.map { billEntity ->
                    val memberEntities = queries.getMembersByBillId(billEntity.id).executeAsList()
                    Bill(
                        id = billEntity.id,
                        name = billEntity.name,
                        totalAmount = billEntity.total_amount,
                        createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
                        status = BillStatus.valueOf(billEntity.status),
                        isDraft = billEntity.is_draft == 1L,
                        billType = BillType.valueOf(billEntity.bill_type),
                        members = memberEntities.map { memberEntity ->
                            Member(
                                id = memberEntity.id,
                                billId = memberEntity.bill_id,
                                name = memberEntity.name,
                                walletNumber = memberEntity.wallet_number,
                                walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                                amount = memberEntity.amount,
                                isPaid = memberEntity.is_paid == 1L
                            )
                        }
                    )
                }
            }
    }

    override fun getRecentBills(limit: Long): Flow<List<Bill>> {
        return queries.getRecentBills(limit)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { bills ->
                bills.map { billEntity ->
                    val memberEntities = queries.getMembersByBillId(billEntity.id).executeAsList()
                    Bill(
                        id = billEntity.id,
                        name = billEntity.name,
                        totalAmount = billEntity.total_amount,
                        createdDate = Instant.fromEpochMilliseconds(billEntity.created_date),
                        status = BillStatus.valueOf(billEntity.status),
                        isDraft = billEntity.is_draft == 1L,
                        billType = BillType.valueOf(billEntity.bill_type),
                        members = memberEntities.map { memberEntity ->
                            Member(
                                id = memberEntity.id,
                                billId = memberEntity.bill_id,
                                name = memberEntity.name,
                                walletNumber = memberEntity.wallet_number,
                                walletType = memberEntity.wallet_type?.let { WalletType.valueOf(it) },
                                amount = memberEntity.amount,
                                isPaid = memberEntity.is_paid == 1L
                            )
                        }
                    )
                }
            }
    }

    override suspend fun countDraftBills(): Long = withContext(Dispatchers.Default) {
        queries.countDraftBills().executeAsOne()
    }

    override suspend fun updateMemberPaymentStatus(memberId: Long, isPaid: Boolean): Unit = withContext(Dispatchers.Default) {
        queries.updateMemberPaymentStatus(
            is_paid = if (isPaid) 1 else 0,
            id = memberId
        )
    }

    override suspend fun updateBillStatus(billId: Long, status: BillStatus) = withContext(Dispatchers.Default) {
        val bill = getBillById(billId) ?: return@withContext
        queries.updateBill(
            name = bill.name,
            total_amount = bill.totalAmount,
            status = status.name,
            is_draft = if (bill.isDraft) 1 else 0,
            id = billId
        )
    }
}

