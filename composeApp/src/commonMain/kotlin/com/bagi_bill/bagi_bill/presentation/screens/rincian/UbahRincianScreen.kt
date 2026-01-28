package com.bagi_bill.bagi_bill.presentation.screens.rincian

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.domain.parser.ParsedReceipt
import com.bagi_bill.bagi_bill.domain.parser.ReceiptItem
import com.bagi_bill.bagi_bill.domain.parser.ReceiptSummary
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * UbahRincianScreen - Edit mode for receipt details.
 * Features:
 * - Editable item rows (qty, name, price)
 * - Add new item (with empty name for better UX)
 * - Delete item via Bottom Sheet
 * - Edit Pajak, Servis, Diskon
 * - Auto-calculate Total (but still editable)
 * - Lainnya = balancing figure
 * - Clear "0" on focus for better UX
 * - Validate before confirm
 */

data class EditableItem(
    var qty: String = "1",
    var name: String = "",
    var price: String = "0"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbahRincianScreen(
    parsedReceipt: ParsedReceipt,
    onBack: () -> Unit = {},
    onConfirm: (ParsedReceipt) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // STATE
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
    var servis by remember { mutableStateOf(parsedReceipt.summary.servis.toString()) }
    var globalDiskon by remember { mutableStateOf(parsedReceipt.summary.diskon.toString()) }
    var totalInput by remember { mutableStateOf(parsedReceipt.summary.total.toString()) }

    // Track if user has manually edited total
    var isTotalManuallyEdited by remember { mutableStateOf(false) }

    var showItemMenu by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // CALCULATIONS
    val calculatedSubtotal by remember {
        derivedStateOf {
            editableItems.sumOf { (it.qty.toIntOrNull() ?: 0) * (it.price.toIntOrNull() ?: 0) }
        }
    }

    // Auto-calculated total = Subtotal + Pajak + Servis - Diskon
    val autoCalculatedTotal by remember {
        derivedStateOf {
            val sub = calculatedSubtotal
            val tax = pajak.toIntOrNull() ?: 0
            val srv = servis.toIntOrNull() ?: 0
            val disc = globalDiskon.toIntOrNull() ?: 0
            sub + tax + srv - disc
        }
    }

    // Auto-update total if not manually edited
    LaunchedEffect(autoCalculatedTotal) {
        if (!isTotalManuallyEdited) {
            totalInput = autoCalculatedTotal.toString()
        }
    }

    // Lainnya = Total - (Subtotal + Pajak + Servis - Diskon)
    val calculatedLainnya by remember {
        derivedStateOf {
            val total = totalInput.toIntOrNull() ?: 0
            val sub = calculatedSubtotal
            val tax = pajak.toIntOrNull() ?: 0
            val srv = servis.toIntOrNull() ?: 0
            val disc = globalDiskon.toIntOrNull() ?: 0
            total - (sub + tax + srv - disc)
        }
    }

    // Validation: All items must have price > 0 and name not blank
    val canConfirm by remember {
        derivedStateOf {
            editableItems.all { item ->
                val price = item.price.toIntOrNull() ?: 0
                price > 0 && item.name.isNotBlank()
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Ubah rincian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Ubah pesanan, harga, atau jumlah",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp,
                shape = AppTheme.Shapes.bottomSheet
            ) {
                Column(Modifier.padding(AppTheme.Spacing.large, AppTheme.Spacing.xLarge)) {
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
                                    subtotal = calculatedSubtotal,
                                    pajak = pajak.toIntOrNull() ?: 0,
                                    servis = servis.toIntOrNull() ?: 0,
                                    diskon = globalDiskon.toIntOrNull() ?: 0,
                                    lainnya = calculatedLainnya,
                                    total = totalInput.toIntOrNull() ?: 0
                                )
                            )
                            onConfirm(updatedReceipt)
                        },
                        enabled = canConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = AppTheme.Shapes.pill
                    ) {
                        Text(
                            "Konfirmasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canConfirm) Color.White else Color.Gray
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = AppTheme.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.large)
        ) {
            item { Spacer(modifier = Modifier.height(AppTheme.Spacing.xSmall)) }

            // MAIN CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = AppTheme.Shapes.card,
                    colors = CardDefaults.cardColors(containerColor = AppTheme.Colors.cardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.Elevation.none)
                ) {
                    Column(modifier = Modifier.padding(vertical = AppTheme.Spacing.small)) {

                        // ITEM LIST
                        editableItems.forEachIndexed { index, item ->
                            CompactItemRow(
                                item = item,
                                onItemChange = { updated ->
                                    editableItems = editableItems.toMutableList().apply {
                                        this[index] = updated
                                    }
                                },
                                onMenuClick = {
                                    selectedItemIndex = index
                                    showItemMenu = true
                                }
                            )
                            if (index < editableItems.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = AppTheme.Spacing.large),
                                    color = Color.LightGray.copy(alpha = 0.3f)
                                )
                            }
                        }

                        // ADD BUTTON - Now creates item with empty name
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.Spacing.large)) {
                            OutlinedButton(
                                onClick = {
                                    editableItems = editableItems.toMutableList().apply {
                                        add(EditableItem(qty = "1", name = "", price = "0"))
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = AppTheme.Shapes.pill,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(AppTheme.Size.iconSmall))
                                Spacer(modifier = Modifier.width(AppTheme.Spacing.small))
                                Text("Tambah pesanan", fontSize = 14.sp)
                            }
                        }

                        // DIVIDER
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = AppTheme.Spacing.large),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        // SUMMARY SECTION
                        Column(modifier = Modifier.padding(AppTheme.Spacing.large)) {
                            SummarySection(
                                subtotal = calculatedSubtotal,
                                pajak = pajak,
                                onPajakChange = { pajak = it },
                                servis = servis,
                                onServisChange = { servis = it },
                                globalDiskon = globalDiskon,
                                onGlobalDiskonChange = { globalDiskon = it },
                                lainnya = calculatedLainnya,
                                total = totalInput,
                                onTotalChange = { newValue ->
                                    totalInput = newValue
                                    isTotalManuallyEdited = true
                                }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(AppTheme.Spacing.xxLarge)) }
        }

        // BOTTOM SHEET (Delete Menu)
        if (showItemMenu && selectedItemIndex >= 0) {
            ModalBottomSheet(
                onDismissRequest = { showItemMenu = false },
                containerColor = Color.White
            ) {
                Column(Modifier.padding(bottom = AppTheme.Spacing.xxLarge)) {
                    Surface(
                        onClick = {
                            if (editableItems.size > 1) {
                                editableItems = editableItems.toMutableList().apply {
                                    removeAt(selectedItemIndex)
                                }
                            }
                            showItemMenu = false
                        },
                        color = Color.White
                    ) {
                        Row(
                            Modifier.padding(AppTheme.Spacing.large),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red)
                            Spacer(Modifier.width(AppTheme.Spacing.large))
                            Text("Hapus pesanan ini", color = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

// ==================== HELPER COMPOSABLES ====================

@Composable
private fun CompactItemRow(
    item: EditableItem,
    onItemChange: (EditableItem) -> Unit,
    onMenuClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(AppTheme.Spacing.large)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Qty Input - Clear "0" on focus
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                BasicTextField(
                    value = item.qty,
                    onValueChange = { onItemChange(item.copy(qty = it)) },
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused && item.qty == "0") {
                            onItemChange(item.copy(qty = ""))
                        }
                    },
                    decorationBox = { inner ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            inner()
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("x", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                )
            }

            Spacer(Modifier.width(AppTheme.Spacing.medium))

            // Name Input - Empty with placeholder
            BasicTextField(
                value = item.name,
                onValueChange = { onItemChange(item.copy(name = it)) },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                singleLine = true,
                decorationBox = { inner ->
                    Box {
                        if (item.name.isEmpty()) {
                            Text("Nama pesanan", color = Color.Gray)
                        }
                        inner()
                    }
                }
            )

            // Menu Button
            IconButton(onClick = onMenuClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.MoreVert, "Menu", tint = Color.Gray)
            }
        }

        Spacer(Modifier.height(AppTheme.Spacing.small))

        // Price Input - Clear "0" on focus
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFAFAFA))
                    .padding(AppTheme.Spacing.small, 6.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                BasicTextField(
                    value = item.price,
                    onValueChange = { onItemChange(item.copy(price = it)) },
                    textStyle = TextStyle(
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused && item.price == "0") {
                            onItemChange(item.copy(price = ""))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SummarySection(
    subtotal: Int,
    pajak: String,
    onPajakChange: (String) -> Unit,
    servis: String,
    onServisChange: (String) -> Unit,
    globalDiskon: String,
    onGlobalDiskonChange: (String) -> Unit,
    lainnya: Int,
    total: String,
    onTotalChange: (String) -> Unit
) {
    Column {
        SummaryRow("Subtotal", formatPrice(subtotal), false)
        Spacer(Modifier.height(AppTheme.Spacing.small))
        SummaryRow("Pajak", pajak, true, onPajakChange)
        Spacer(Modifier.height(AppTheme.Spacing.small))
        SummaryRow("Servis", servis, true, onServisChange)
        Spacer(Modifier.height(AppTheme.Spacing.small))
        SummaryRow("Diskon", globalDiskon, true, onGlobalDiskonChange)
        Spacer(Modifier.height(AppTheme.Spacing.small))
        
        // Lainnya - Read-only with color indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lainnya",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            Text(
                text = formatPrice(lainnya),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (lainnya < 0) Color.Red else Color.Black
            )
        }

        Spacer(Modifier.height(AppTheme.Spacing.large))

        // Total Row (Editable + Bold) - Clear "0" on focus
        SummaryRow(
            label = "Jumlah total",
            value = total,
            isEditable = true,
            onValueChange = onTotalChange,
            isBold = true
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit = {},
    isBold: Boolean = false
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
                    .width(120.dp)
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(4.dp))
                    .padding(AppTheme.Spacing.xSmall)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused && value == "0") {
                            onValueChange("")
                        }
                    }
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

// ==================== HELPER FUNCTIONS ====================

private fun formatPrice(price: Int): String {
    val absolutePrice = kotlin.math.abs(price)
    val formatted = absolutePrice.toString().reversed().chunked(3).joinToString(".").reversed()
    return if (price < 0) "-$formatted" else formatted
}
