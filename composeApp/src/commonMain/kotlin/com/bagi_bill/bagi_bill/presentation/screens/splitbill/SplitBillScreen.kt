package com.bagi_bill.bagi_bill.presentation.screens.splitbill

import androidx.compose.animation.*
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
import com.bagi_bill.bagi_bill.domain.parser.ParsedReceipt
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SplitBillData
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme
import com.bagi_bill.bagi_bill.presentation.components.DraftConfirmationDialog

// Background color matching other screens
private val BoneWhite = Color(0xFFF5F5F5)

/**
 * Main SplitBill screen for assigning items to members.
 * Features:
 * - Member avatar selection
 * - Item assignment via checkbox
 * - "Bagi rata" (even split) button
 * - "Atur pembagian" for custom per-member amounts
 * - Progress tracking card
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SplitBillScreen(
    splitBillData: SplitBillData,
    parsedReceipt: ParsedReceipt,
    onBack: () -> Unit,
    onExitToHome: () -> Unit = {},
    onSaveDraft: () -> Unit = {},
    onEditMembers: () -> Unit,
    onSend: (List<AssignableBillItem>) -> Unit
) {
    // Convert SelectableMember to SplitBillMember for display
    val members = remember(splitBillData) {
        splitBillData.getAllMembers().map { SplitBillMember.fromSelectableMember(it) }
    }
    
    // State: Selected member for assignment
    var selectedMemberId by remember { mutableStateOf(members.firstOrNull()?.id ?: "") }
    
    // Draft dialog state
    var showDraftDialog by remember { mutableStateOf(false) }
    
    // State: Bill items with assignments
    val billItems = remember(parsedReceipt) {
        mutableStateListOf<AssignableBillItem>().apply {
            addAll(
                parsedReceipt.items.mapIndexed { index, item ->
                    AssignableBillItem(
                        id = index.toString(),
                        name = item.name,
                        price = item.price,
                        qty = item.qty,
                        assignedMemberIds = emptyList(),
                        customShares = emptyList()
                    )
                }
            )
        }
    }
    
    // State: Item being edited in CustomSplitScreen
    var editingItemIndex by remember { mutableStateOf<Int?>(null) }
    
    // Summary calculations
    val subtotal = remember(billItems.toList()) { billItems.sumOf { it.price * it.qty } }
    val pajak = parsedReceipt.summary.pajak
    val servis = parsedReceipt.summary.servis
    val diskon = parsedReceipt.summary.diskon
    val lainnya = parsedReceipt.summary.lainnya
    val total = subtotal + pajak + servis + lainnya - diskon
    
    // Progress tracking - count items not quantities
    val assignedItemCount = billItems.count { it.assignedMemberIds.isNotEmpty() }
    val totalItems = billItems.size // Fix: count items, not qty sum
    val unassignedAmount = billItems
        .filter { it.assignedMemberIds.isEmpty() }
        .sumOf { it.price * it.qty }
    
    // Confirm button enabled only when all items are assigned
    val allItemsAssigned = billItems.isNotEmpty() && billItems.all { it.assignedMemberIds.isNotEmpty() }
    
    // Draft confirmation dialog
    DraftConfirmationDialog(
        showDialog = showDraftDialog,
        onDismiss = { showDraftDialog = false },
        onSaveDraft = {
            showDraftDialog = false
            onSaveDraft()
        },
        onDiscard = {
            showDraftDialog = false
            onExitToHome()
        }
    )
    
    // Screen switching animation
    AnimatedContent(
        targetState = editingItemIndex,
        transitionSpec = {
            if (targetState != null) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        }
    ) { editIndex ->
        if (editIndex != null && editIndex < billItems.size) {
            // Custom Split Screen
            CustomSplitScreen(
                item = billItems[editIndex],
                members = members,
                onBack = { editingItemIndex = null },
                onConfirm = { shares ->
                    billItems[editIndex] = billItems[editIndex].copy(customShares = shares)
                    editingItemIndex = null
                }
            )
        } else {
            // Main SplitBill Screen
            Scaffold(
                containerColor = BoneWhite,
                topBar = {
                    TopAppBar(
                        title = { 
                            Text(
                                "Pembagian split bill", 
                                fontWeight = FontWeight.Medium, 
                                fontSize = 18.sp
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = { showDraftDialog = true }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Help */ }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Help, 
                                    contentDescription = "Bantuan", 
                                    tint = Color.Gray
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = AppTheme.Elevation.bottomSticky,
                                clip = false,
                                spotColor = Color.Black,
                                ambientColor = Color.Black
                            ),
                        color = Color.White,
                        shape = AppTheme.Shapes.bottomSheet
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                start = AppTheme.Spacing.large,
                                end = AppTheme.Spacing.large,
                                top = AppTheme.Spacing.xLarge,
                                bottom = AppTheme.Spacing.xLarge
                            )
                        ) {
                            Button(
                                onClick = { onSend(billItems.toList()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(AppTheme.Size.buttonHeight),
                                enabled = allItemsAssigned,
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                                    disabledContentColor = Color.White
                                ),
                                shape = AppTheme.Shapes.pill
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
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = AppTheme.Spacing.large)
                        .padding(top = AppTheme.Spacing.medium) // Add top padding for card rounded corner
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
                            // A1. Header
                            item {
                                MemberHeaderTitle(onEditMembersClick = onEditMembers)
                            }
                            
                            // A2. Sticky member avatar row
                            stickyHeader {
                                MemberAvatarRow(
                                    members = members,
                                    selectedMemberId = selectedMemberId,
                                    onMemberClick = { id -> selectedMemberId = id }
                                )
                            }
                            
                            // A3. Split evenly button
                            item {
                                SplitEvenlyButton(
                                    onSplitEvenlyClick = {
                                        // Reset all items to even split across all members
                                        val allMemberIds = members.map { it.id }
                                        val updatedItems = billItems.map { item ->
                                            item.copy(
                                                assignedMemberIds = allMemberIds,
                                                customShares = emptyList() // Clear custom splits!
                                            )
                                        }
                                        billItems.clear()
                                        billItems.addAll(updatedItems)
                                    }
                                )
                            }
                            
                            // B. Bill items list
                            items(
                                items = billItems,
                                key = { it.id }
                            ) { item ->
                                val index = billItems.indexOf(item)
                                AssignmentItemRow(
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
                                        // Clear custom shares when toggling members
                                        billItems[index] = item.copy(
                                            assignedMemberIds = currentList,
                                            customShares = emptyList()
                                        )
                                    },
                                    onCustomSplitClick = {
                                        if (item.assignedMemberIds.size >= 2) {
                                            editingItemIndex = index
                                        }
                                    }
                                )
                            }
                            
                            // C. Summary section
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(horizontal = 20.dp, vertical = 20.dp)
                                ) {
                                    BillSummaryRow("Subtotal", formatPrice(subtotal))
                                    BillSummaryRow("Pajak", formatPrice(pajak))
                                    BillSummaryRow("Servis", formatPrice(servis))
                                    BillSummaryRow("Diskon", formatPrice(diskon))
                                    BillSummaryRow("Lainnya", formatPrice(lainnya))
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(
                                        thickness = 1.dp, 
                                        color = Color.LightGray.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    BillSummaryRow("Jumlah total", formatPrice(total), isTotal = true)
                                }
                            }
                            
                            // D. Divider
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    thickness = 1.dp,
                                    color = Color.LightGray.copy(alpha = 0.2f)
                                )
                            }
                            
                            // E. Progress card
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(BoneWhite)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Color.White,
                                                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                            )
                                            .padding(bottom = 24.dp)
                                    ) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = Color(0xFFFFF8E1)
                                            ),
                                            border = BorderStroke(1.dp, Color(0xFFFFD54F))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(12.dp)
                                                    .fillMaxWidth(),
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
                            
                            // Footer spacer
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(16.dp)
                                        .background(BoneWhite)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
