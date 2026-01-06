package com.bagi_bill.bagi_bill.ui.screens

// 1. IMPORT DATA DARI TEMAN (Source)

// 2. IMPORT DATA UI KITA (Target)

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
import com.bagi_bill.bagi_bill.ParsedReceipt
import com.bagi_bill.bagi_bill.model.AssignableBillItem
import com.bagi_bill.bagi_bill.model.SplitBillData
import com.bagi_bill.bagi_bill.ui.components.*
import com.bagi_bill.bagi_bill.model.Member as UiMember

// Warna Background
val BoneWhite = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PembagianBillScreen(
    splitBillData: SplitBillData,
    parsedReceipt: ParsedReceipt,
    onBackClick: () -> Unit = {},
    onEditMembers: () -> Unit = {},
    onSendClick: (List<AssignableBillItem>) -> Unit = {}
) {
    // 3. KONVERSI DATA ANGGOTA (SourceMember -> UiMember)
    // Kita tambahkan logic pemberian warna random dan inisial di sini
    val members = remember(splitBillData) {
        // Gabungkan payer + members lainnya dari data teman
        val allSourceMembers = listOf(splitBillData.payer) + splitBillData.members

        allSourceMembers.map { source ->
            UiMember(
                id = source.id,
                name = source.name,
                // Ambil huruf pertama nama untuk inisial
                initial = source.name.firstOrNull()?.uppercase() ?: "?",
                // Generate warna konsisten berdasarkan nama
                avatarColor = generateColorForName(source.name)
            )
        }
    }

    // 4. KONVERSI ITEM BILL
    val billItems = remember(parsedReceipt) {
        mutableStateListOf<AssignableBillItem>().apply {
            addAll(
                parsedReceipt.items.mapIndexed { index, item ->
                    AssignableBillItem(
                        id = index.toString(),
                        name = item.name,
                        price = item.price,
                        qty = item.qty,
                        assignedMemberIds = emptyList()
                    )
                }
            )
        }
    }

    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }

    // 5. LOGIC SUMMARY
    val subtotal = remember(billItems.toList()) { billItems.sumOf { it.price * it.qty } }
    val pajak = parsedReceipt.summary.pajak
    val servis = parsedReceipt.summary.servis
    val diskon = parsedReceipt.summary.diskon
    val lainnya = parsedReceipt.summary.lainnya
    val total = subtotal + pajak + servis + lainnya - diskon

    // 6. LOGIC PROGRESS CARD KUNING
    val assignedItemCount = billItems.count { it.assignedMemberIds.isNotEmpty() }
    val totalItems = billItems.sumOf { it.qty }
    val unassignedAmount = billItems
        .filter { it.assignedMemberIds.isEmpty() }
        .sumOf { it.price * it.qty }


    Scaffold(
        containerColor = BoneWhite,
        topBar = {
            TopAppBar(
                title = { Text("Pembagian split bill", fontWeight = FontWeight.Medium, fontSize = 18.sp) },
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
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), clip = false, spotColor = Color.Black.copy(alpha = 0.6f), ambientColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
                    ) {
                        Button(
                            onClick = { onSendClick(billItems) },
                            modifier = Modifier.fillMaxWidth().height(43.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(text = "Kirim ke anggota", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 0.dp)
                ) {
                    // A1. HEADER
                    item {
                        MemberHeaderTitle(
                            // 2. Hubungkan callback disini
                            onEditMembersClick = onEditMembers
                        )
                    }

                    // A2. STICKY AVATAR
                    stickyHeader {
                        MemberAvatarRow(
                            members = members,
                            selectedMemberId = selectedMemberId,
                            onMemberClick = { id -> selectedMemberId = id }
                        )
                    }

                    // A3. TOMBOL BAGI RATA
                    item {
                        SplitEvenlyButton(
                            onSplitEvenlyClick = {
                                val allMemberIds = members.map { it.id }
                                val updatedItems = billItems.map { item -> item.copy(assignedMemberIds = allMemberIds) }
                                billItems.clear()
                                billItems.addAll(updatedItems)
                            }
                        )
                    }

                    // B. LIST ITEMS
                    items(items = billItems) { item ->
                        AssignmentBillItemRow(
                            item = item,
                            allMembers = members,
                            isAssignedToCurrentUser = item.assignedMemberIds.contains(selectedMemberId),
                            onToggle = {
                                val currentList = item.assignedMemberIds.toMutableList()
                                if (currentList.contains(selectedMemberId)) currentList.remove(selectedMemberId) else currentList.add(selectedMemberId)
                                val index = billItems.indexOf(item)
                                if (index != -1) billItems[index] = item.copy(assignedMemberIds = currentList)
                            }
                        )
                    }

                    // C. SUMMARY
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            BillSummaryRow("Subtotal", formatPrice(subtotal))
                            BillSummaryRow("Pajak", formatPrice(pajak))
                            BillSummaryRow("Servis", formatPrice(servis))
                            BillSummaryRow("Diskon", formatPrice(diskon))
                            BillSummaryRow("Lainnya", formatPrice(lainnya))
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(16.dp))
                            BillSummaryRow("Jumlah total", formatPrice(total), isTotal = true)
                        }
                    }

                    // D. DIVIDER
                    item {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))
                    }

                    // E. FOOTER (MAGIC SECTION)
                    item {
                        Box(modifier = Modifier.fillMaxWidth().background(BoneWhite)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                                    .padding(bottom = 24.dp)
                            ) {
                                Spacer(modifier = Modifier.height(24.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                    border = BorderStroke(1.dp, Color(0xFFFFD54F))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "$assignedItemCount dari $totalItems pesanan dihitung",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Rp${formatPrice(unassignedAmount)} belum masuk itungan",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(BoneWhite)) }
                }
            }
        }
    }
}

// === HELPER FUNCTIONS ===

// 1. Helper Format Price
private fun formatPrice(price: Int): String {
    if (price == 0) return "0"
    return price.toString().reversed().chunked(3).joinToString(".").reversed()
}

// 2. Helper Generator Warna Avatar (Agar konsisten dengan nama)
fun generateColorForName(name: String): Color {
    val colors = listOf(
        Color(0xFF00897B), Color(0xFF1976D2), Color(0xFFE53935),
        Color(0xFFFB8C00), Color(0xFF8E24AA), Color(0xFF43A047)
    )
    if (name.isEmpty()) return colors[0]
    return colors[name.first().uppercaseChar().code % colors.size]
}