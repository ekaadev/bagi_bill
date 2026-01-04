package com.bagi_bill.bagi_bill.model

import androidx.compose.ui.graphics.Color


data class BillItem(
    val name: String,
    val qty: Int,
    val price: Int
)

// 2. Model Anggota avatar Split
data class Member(
    val id: String,
    val name: String,
    val initial: String,
    val avatarColor: Color
)

// 3. Model Item Assignment (Untuk Halaman Pembagian Bill)
data class AssignableBillItem(
    val id: String,
    val name: String,
    val price: Int,
    val qty: Int,
    // List ID member yang memilih menu ini
    val assignedMemberIds: List<String> = emptyList()
)