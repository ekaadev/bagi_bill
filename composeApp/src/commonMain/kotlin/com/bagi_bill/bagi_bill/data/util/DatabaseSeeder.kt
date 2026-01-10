package com.bagi_bill.bagi_bill.data.util

import com.bagi_bill.bagi_bill.data.repository.BillRepository
import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.BillType
import com.bagi_bill.bagi_bill.domain.model.Member
import com.bagi_bill.bagi_bill.domain.model.WalletType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

class DatabaseSeeder(
    private val repository: BillRepository
) {
    fun seedInitialData() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Seed 3 sample bills untuk riwayat
                seedHistoryBills()
            } catch (e: Exception) {
                println("Error seeding data: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun seedHistoryBills() {
        val now: Instant = kotlin.time.Clock.System.now()

        // Bill 1: Makan di Restoran (Lunas)
        val bill1 = Bill(
            name = "Makan di Restoran XYZ",
            totalAmount = 250000,
            createdDate = now.minus(5.days),
            status = BillStatus.PAID,
            isDraft = false,
            billType = BillType.SCAN,
            members = emptyList()
        )
        val members1 = listOf(
            Member(
                billId = 0,
                name = "Andi",
                walletNumber = "081234567890",
                walletType = WalletType.GOPAY,
                amount = 100000,
                isPaid = true
            ),
            Member(
                billId = 0,
                name = "Budi",
                walletNumber = "081234567891",
                walletType = WalletType.OVO,
                amount = 75000,
                isPaid = true
            ),
            Member(
                billId = 0,
                name = "Citra",
                walletNumber = "081234567892",
                walletType = WalletType.DANA,
                amount = 75000,
                isPaid = true
            )
        )
        repository.createBill(bill1, members1)

        // Bill 2: Belanja Bulanan (Belum Lunas)
        val bill2 = Bill(
            name = "Belanja Bulanan",
            totalAmount = 850000,
            createdDate = now.minus(3.days),
            status = BillStatus.PARTIALLY_PAID,
            isDraft = false,
            billType = BillType.MANUAL,
            members = emptyList()
        )
        val members2 = listOf(
            Member(
                billId = 0,
                name = "Dedi",
                walletNumber = "081234567893",
                walletType = WalletType.GOPAY,
                amount = 425000,
                isPaid = true
            ),
            Member(
                billId = 0,
                name = "Eka",
                walletNumber = "081234567894",
                walletType = WalletType.DANA,
                amount = 425000,
                isPaid = false
            )
        )
        repository.createBill(bill2, members2)

        // Bill 3: Nonton Bioskop (Lunas)
        val bill3 = Bill(
            name = "Nonton Bioskop",
            totalAmount = 120000,
            createdDate = now.minus(1.days),
            status = BillStatus.PAID,
            isDraft = false,
            billType = BillType.SCAN,
            members = emptyList()
        )
        val members3 = listOf(
            Member(
                billId = 0,
                name = "Faris",
                walletNumber = "081234567895",
                walletType = WalletType.OVO,
                amount = 40000,
                isPaid = true
            ),
            Member(
                billId = 0,
                name = "Gita",
                walletNumber = "081234567896",
                walletType = WalletType.GOPAY,
                amount = 40000,
                isPaid = true
            ),
            Member(
                billId = 0,
                name = "Hana",
                walletNumber = "081234567897",
                walletType = WalletType.DANA,
                amount = 40000,
                isPaid = true
            )
        )
        repository.createBill(bill3, members3)

        println("Initial data seeded successfully")
    }
}

