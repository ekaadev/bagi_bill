package com.bagi_bill.bagi_bill.ui.screens.rincian

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.ParsedReceipt
import com.bagi_bill.bagi_bill.ReceiptItem
import com.bagi_bill.bagi_bill.ReceiptSummary
import org.jetbrains.compose.ui.tooling.preview.Preview

// Warna Background
val BackgroundColor = Color(0xFFF5F5F5)

data class EditableItem(
    var qty: String = "1",
    var name: String = "",
    var price: String = "0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UbahRincianScreen(
    parsedReceipt: ParsedReceipt = ParsedReceipt("", emptyList(), ReceiptSummary(0, 0, 0, 0, 0)),
    onBack: () -> Unit = {},
    onConfirm: (ParsedReceipt) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 1. STATE INITIALIZATION
    var editableItems by remember {
        mutableStateOf(
            parsedReceipt.items.map { item ->
                EditableItem(
                    qty = if (item.qty <= 0) "1" else item.qty.toString(),
                    name = item.name,
                    price = item.price.toString()
                )
            }.toMutableList()
        )
    }

    var pajak by remember { mutableStateOf(parsedReceipt.summary.pajak.toString()) }
    var servis by remember { mutableStateOf("0") }
    var globalDiskon by remember { mutableStateOf(parsedReceipt.summary.diskon.toString()) }

    // [UPDATE] Total Input sekarang Editable (Mutable State), default ambil dari hasil scan
    var totalInput by remember { mutableStateOf(parsedReceipt.summary.total.toString()) }

    var showItemMenu by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // 2. CALCULATION LOGIC
    val calculatedSubtotal = remember(editableItems) {
        derivedStateOf {
            editableItems.sumOf { (it.qty.toIntOrNull() ?: 0) * (it.price.toIntOrNull() ?: 0) }
        }
    }

    // [UPDATE LOGIC] Lainnya adalah PENYEIMBANG (Balancing Figure)
    // Rumus: Lainnya = TotalInput - (Subtotal + Pajak + Servis - Diskon)
    val calculatedLainnya = remember(totalInput, calculatedSubtotal.value, pajak, servis, globalDiskon) {
        derivedStateOf {
            val total = totalInput.toIntOrNull() ?: 0
            val sub = calculatedSubtotal.value
            val tax = pajak.toIntOrNull() ?: 0
            val srv = servis.toIntOrNull() ?: 0
            val disc = globalDiskon.toIntOrNull() ?: 0

            // Hitung selisih
            total - (sub + tax + srv - disc)
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Ubah rincian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Ubah pesanan, harga, atau jumlah", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundColor)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(Modifier.padding(16.dp, 24.dp)) {
                    Button(
                        onClick = {
                            val updatedItems = editableItems.map { item ->
                                ReceiptItem(
                                    qty = item.qty.toIntOrNull() ?: 1,
                                    name = item.name,
                                    price = item.price.toIntOrNull() ?: 0
                                )
                            }
                            val updatedReceipt = parsedReceipt.copy(
                                items = updatedItems,
                                summary = ReceiptSummary(
                                    subtotal = calculatedSubtotal.value,
                                    pajak = pajak.toIntOrNull() ?: 0,
                                    diskon = globalDiskon.toIntOrNull() ?: 0,
                                    lainnya = calculatedLainnya.value,
                                    total = totalInput.toIntOrNull() ?: 0
                                )
                            )
                            onConfirm(updatedReceipt)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Konfirmasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // CARD UTAMA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {

                        // A. LIST ITEMS
                        editableItems.forEachIndexed { index, item ->
                            CompactItemRow(
                                item = item,
                                onItemChange = { updated ->
                                    editableItems = editableItems.toMutableList().apply { this[index] = updated }
                                },
                                onMenuClick = {
                                    selectedItemIndex = index
                                    showItemMenu = true
                                }
                            )
                            if (index < editableItems.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = Color.LightGray.copy(alpha = 0.3f)
                                )
                            }
                        }

                        // B. TOMBOL TAMBAH
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            OutlinedButton(
                                onClick = {
                                    editableItems = editableItems.toMutableList().apply {
                                        add(EditableItem(qty = "1", name = "Nama pesanan", price = "0"))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tambah pesanan", fontSize = 14.sp)
                            }
                        }

                        // DIVIDER
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        // C. SUMMARY SECTION
                        Column(modifier = Modifier.padding(16.dp)) {
                            SummarySection(
                                subtotal = calculatedSubtotal.value,
                                pajak = pajak, onPajakChange = { pajak = it },
                                servis = servis, onServisChange = { servis = it },
                                globalDiskon = globalDiskon, onGlobalDiskonChange = { globalDiskon = it },
                                lainnya = calculatedLainnya.value, // Otomatis terhitung
                                total = totalInput,
                                onTotalChange = { totalInput = it } // Update Total Manual
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }

        // BOTTOM SHEET (Menu Hapus Saja - Item Extra dihapus karena Lainnya sudah otomatis)
        if (showItemMenu && selectedItemIndex >= 0) {
            ModalBottomSheet(
                onDismissRequest = { showItemMenu = false },
                containerColor = Color.White
            ) {
                Column(Modifier.padding(bottom = 32.dp)) {
                    Surface(
                        onClick = {
                            if (editableItems.size > 1) {
                                editableItems = editableItems.toMutableList().apply { removeAt(selectedItemIndex) }
                            }
                            showItemMenu = false
                        },
                        color = Color.White
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                            Spacer(Modifier.width(16.dp))
                            Text("Hapus pesanan ini", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactItemRow(
    item: EditableItem,
    onItemChange: (EditableItem) -> Unit,
    onMenuClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.width(42.dp).height(32.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = item.qty,
                    onValueChange = { onItemChange(item.copy(qty = it)) },
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            inner(); Text("x", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 2.dp))
                        }
                    }
                )
            }
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value = item.name,
                onValueChange = { onItemChange(item.copy(name = it)) },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                singleLine = true,
                decorationBox = { inner -> if (item.name.isEmpty()) Text("Nama pesanan", color = Color.Gray); inner() }
            )
            IconButton(onClick = onMenuClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, "Menu", tint = Color.Gray)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier.width(120.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFFAFAFA)).padding(8.dp, 6.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                BasicTextField(
                    value = item.price,
                    onValueChange = { onItemChange(item.copy(price = it)) },
                    textStyle = TextStyle(textAlign = TextAlign.End, fontWeight = FontWeight.Medium, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    subtotal: Int,
    pajak: String, onPajakChange: (String) -> Unit,
    servis: String, onServisChange: (String) -> Unit,
    globalDiskon: String, onGlobalDiskonChange: (String) -> Unit,
    lainnya: Int,
    total: String, onTotalChange: (String) -> Unit
) {
    Column {
        SummaryRow("Subtotal", formatPrice(subtotal), false)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Pajak", pajak, true, onPajakChange)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Servis", servis, true, onServisChange)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Diskon", globalDiskon, true, onGlobalDiskonChange)
        Spacer(Modifier.height(8.dp))
        SummaryRow("Lainnya", formatPrice(lainnya), false) // Lainnya otomatis (Read Only)

        Spacer(Modifier.height(16.dp))

        // [UPDATE] Total Row sekarang sama seperti lainnya, tapi Bold dan Editable
        SummaryRow(
            label = "Jumlah total",
            value = total,
            isEditable = true,
            onValueChange = onTotalChange,
            isBold = true // Parameter baru untuk menebalkan teks
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit = {},
    isBold: Boolean = false // Parameter Baru
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isBold) Color.Black else Color.Gray,
            fontSize = if (isBold) 16.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )

        if (isEditable) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    textAlign = TextAlign.End,
                    fontSize = if (isBold) 16.sp else 14.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    color = if (isBold) MaterialTheme.colorScheme.primary else Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(120.dp) // Sedikit diperlebar untuk total
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp))
                    .padding(4.dp)
            )
        } else {
            Text(
                text = value,
                fontSize = if (isBold) 16.sp else 14.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = if (isBold) MaterialTheme.colorScheme.primary else Color.Black
            )
        }
    }
}

private fun formatPrice(price: Int): String {
    // Handle negatif format (misal diskon/lainnya negatif)
    val absolutePrice = kotlin.math.abs(price)
    val formatted = absolutePrice.toString().reversed().chunked(3).joinToString(".").reversed()
    return if (price < 0) "-$formatted" else formatted
}