package com.bagi_bill.bagi_bill.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bagi_bill.bagi_bill.domain.model.BillItem
import com.bagi_bill.bagi_bill.domain.model.formatCurrency
import com.bagi_bill.bagi_bill.presentation.components.AppContainer
import com.bagi_bill.bagi_bill.presentation.viewmodel.SplitBillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    viewModel: SplitBillViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToSelectMember: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BillItem?>(null) }

    // Tax & Service input
    var taxPercent by remember { mutableStateOf(state.charges.taxPercent.toString()) }
    var servicePercent by remember { mutableStateOf(state.charges.servicePercent.toString()) }

    // Update charges when input changes
    LaunchedEffect(taxPercent, servicePercent) {
        viewModel.updateCharges(
            taxPercent = taxPercent.toDoubleOrNull() ?: 0.0,
            servicePercent = servicePercent.toDoubleOrNull() ?: 0.0
        )
    }

    AppContainer {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rincian Item") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                // Footer dengan Grand Total dan Button Lanjut
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Grand Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grand Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rp ${formatCurrency(state.getGrandTotal())}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Button Lanjut
                        Button(
                            onClick = onNavigateToSelectMember,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.items.isNotEmpty(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Lanjut Pilih Teman",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bill Name
                item {
                    OutlinedTextField(
                        value = state.billName,
                        onValueChange = { viewModel.updateBillName(it) },
                        label = { Text("Nama Split Bill") },
                        placeholder = { Text("Makan siang kantor...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Items Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Item (${state.items.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        TextButton(onClick = { showAddItemDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Item")
                        }
                    }
                }

                // Items List
                if (state.items.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Belum ada item",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Tambahkan item secara manual",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(state.items) { index, item ->
                        ItemCard(
                            item = item,
                            onEdit = { editingItem = item },
                            onDelete = { viewModel.removeItem(item.id) }
                        )
                    }
                }

                // Subtotal
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Subtotal",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Rp ${formatCurrency(state.getSubtotal())}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Tax & Service Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Pajak & Service",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = taxPercent,
                                    onValueChange = { taxPercent = it.filter { c -> c.isDigit() || c == '.' } },
                                    label = { Text("Pajak (%)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = servicePercent,
                                    onValueChange = { servicePercent = it.filter { c -> c.isDigit() || c == '.' } },
                                    label = { Text("Service (%)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            // Show calculated amounts
                            val taxAmount = state.charges.calculateTax(state.getSubtotal())
                            val serviceAmount = state.charges.calculateService(state.getSubtotal())

                            if (taxAmount > 0 || serviceAmount > 0) {
                                HorizontalDivider()
                                if (taxAmount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Pajak ${taxPercent}%",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Rp ${formatCurrency(taxAmount)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                if (serviceAmount > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Service ${servicePercent}%",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "Rp ${formatCurrency(serviceAmount)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Spacer for bottom bar
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AddEditItemDialog(
            item = null,
            onDismiss = { showAddItemDialog = false },
            onSave = { name, qty, price ->
                viewModel.addItem(
                    BillItem(
                        name = name,
                        quantity = qty,
                        unitPrice = price
                    )
                )
                showAddItemDialog = false
            }
        )
    }

    // Edit Item Dialog
    editingItem?.let { item ->
        AddEditItemDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { name, qty, price ->
                viewModel.updateItem(item.id, name, qty, price)
                editingItem = null
            }
        )
    }
}

@Composable
private fun ItemCard(
    item: BillItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item number/icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Item details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${item.quantity}x @ Rp ${formatCurrency(item.unitPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Total price
            Text(
                text = "Rp ${formatCurrency(item.getTotalPrice())}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Actions
            IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditItemDialog(
    item: BillItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, quantity: Int, unitPrice: Long) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var quantity by remember { mutableStateOf((item?.quantity ?: 1).toString()) }
    var price by remember { mutableStateOf((item?.unitPrice ?: 0L).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (item == null) "Tambah Item" else "Edit Item")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Item") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.filter { c -> c.isDigit() } },
                        label = { Text("Qty") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter { c -> c.isDigit() } },
                        label = { Text("Harga") },
                        modifier = Modifier.weight(2f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        prefix = { Text("Rp ") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toIntOrNull() ?: 1
                    val unitPrice = price.toLongOrNull() ?: 0L
                    if (name.isNotBlank() && unitPrice > 0) {
                        onSave(name, qty, unitPrice)
                    }
                },
                enabled = name.isNotBlank() && (price.toLongOrNull() ?: 0L) > 0
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

