package com.bagi_bill.bagi_bill.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.model.AssignableBillItem
import com.bagi_bill.bagi_bill.model.Member
import com.bagi_bill.bagi_bill.ui.components.*
import org.jetbrains.compose.ui.tooling.preview.Preview

// Warna Background
val BoneWhite = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun PembagianBillScreen(
    onBackClick: () -> Unit = {}
) {
    // 1. DATA DUMMY & STATE
    val members = remember {
        listOf(
            Member("1", "Kamu", "K", Color(0xFF4CAF50)),
            Member("2", "Albertt", "A", Color(0xFF2196F3)),
            Member("3", "Yohanes", "Y", Color(0xFFFFC107)),
            Member("4", "Jeffrey", "J", Color(0xFF9C27B0))
        )
    }

    val billItems = remember {
        mutableStateListOf(
            AssignableBillItem("1", "House Blend Coffee (H)", 25000, 1, listOf("1")),
            AssignableBillItem("2", "Hazelnut Choco MT (L)", 28000, 1, emptyList()),
            AssignableBillItem("3", "Nasi Goreng Spesial", 35000, 1, listOf("2", "4")),
            AssignableBillItem("4", "Es Teh Manis", 5000, 2, emptyList())
        )
    }

    var selectedMemberId by remember { mutableStateOf("1") }

    Scaffold(
        containerColor = BoneWhite,

        // 2. TOP BAR
        topBar = {
            TopAppBar(
                title = {
                    Text("Pembagian split bill", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Bantuan", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BoneWhite)
            )
        },

        // 3. BOTTOM BAR
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        clip = false,
                        spotColor = Color.Black.copy(alpha = 0.6f),
                        ambientColor = Color.Black.copy(alpha = 0.6f)
                    )
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 24.dp,
                            bottom = 24.dp,
                        ),
                    ) {
                        Button(
                            onClick = { /* Todo: Logic Kirim */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(43.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Kirim ke anggota",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        // 4. KONTEN LAZY COLUMN
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
            // [NOTE]: Kita TIDAK pakai padding bottom disini, biar Surface Putih bisa tembus sampai bawah
            // Efek 'Lurus' saat scroll didapat dari Surface yang memanjang ke bawah tombol.
        ) {
            // WADAH KARTU PUTIH UTAMA (Background Lurus)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                color = Color.White,
                // [PENTING]: Hanya Top yang rounded. Bottom Lurus (0.dp).
                // Ini membuat efek "kartu panjang tak berujung" saat scroll.
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // Padding bottom 0 agar footer bisa menempel pas di bawah
                    contentPadding = PaddingValues(bottom = 0.dp)
                ) {

                    // BAGIAN A1: HEADER TITLE
                    item {
                        MemberHeaderTitle(onEditMembersClick = { /* Todo */ })
                    }

                    // BAGIAN A2: AVATAR ROW (Sticky)
                    stickyHeader {
                        MemberAvatarRow(
                            members = members,
                            selectedMemberId = selectedMemberId,
                            onMemberClick = { id -> selectedMemberId = id }
                        )
                    }

                    // BAGIAN A3: TOMBOL BAGI RATA
                    item {
                        SplitEvenlyButton(
                            onSplitEvenlyClick = {
                                val allMemberIds = members.map { it.id }
                                val updatedItems = billItems.map { item ->
                                    item.copy(assignedMemberIds = allMemberIds)
                                }
                                billItems.clear()
                                billItems.addAll(updatedItems)
                            }
                        )
                    }

                    // BAGIAN B: LIST ITEMS
                    items(items = billItems) { item ->
                        AssignmentBillItemRow(
                            item = item,
                            allMembers = members,
                            isAssignedToCurrentUser = item.assignedMemberIds.contains(selectedMemberId),
                            onToggle = {
                                val currentList = item.assignedMemberIds.toMutableList()
                                if (currentList.contains(selectedMemberId)) {
                                    currentList.remove(selectedMemberId)
                                } else {
                                    currentList.add(selectedMemberId)
                                }
                                val index = billItems.indexOf(item)
                                if (index != -1) {
                                    billItems[index] = item.copy(assignedMemberIds = currentList)
                                }
                            }
                        )
                    }

                    // BAGIAN C: SUMMARY
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            BillSummaryRow("Subtotal", "53.000")
                            BillSummaryRow("Pajak", "0")
                            BillSummaryRow("Servis", "0")
                            BillSummaryRow("Diskon", "0")
                            BillSummaryRow("Lainnya", "0")

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))

                            BillSummaryRow("Jumlah total", "53.000", isTotal = true)
                        }
                    }

                    // D. DIVIDER SEBELUM INFO CARD
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.2f)
                        )
                    }

                    // E. FOOTER & INFO CARD (MAGIC SECTION) 🪄
                    item {
                        // [TRIK VISUAL]:
                        // Kita bungkus Card Kuning ini dengan Box berwarna BoneWhite (Abu-abu).
                        // Tapi kita buat Column Putih di dalamnya yang Rounded Bawah.
                        // Saat item ini scroll naik, Background BoneWhite akan menutupi Surface Putih lurus di belakangnya.

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BoneWhite) // Layer Paling Bawah: ABU-ABU
                        ) {
                            // Layer Tengah: PUTIH DENGAN ROUNDED BAWAH
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color.White,
                                        // Ini yang bikin efek rounded saat mentok bawah
                                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                    )
                                    .padding(bottom = 24.dp) // Jarak dari ujung kertas putih ke Card Kuning
                            ) {
                                // Spacer untuk jarak dari divider atas
                                Spacer(modifier = Modifier.height(24.dp))

                                // Layer Atas: CARD KUNING
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                    border = BorderStroke(1.dp, Color(0xFFFFD54F))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "0 dari ${billItems.size} pesanan dihitung",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Rp53.000 belum masuk itungan",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Spacer akhir di luar area putih (agar bisa scroll lebih naik lagi)
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(BoneWhite))
                    }
                }
            }
        }
    }
}