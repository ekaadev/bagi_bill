package com.bagi_bill.bagi_bill.presentation.screens.splitbill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * Screen for customizing per-member cost distribution for a single item.
 * Allows users to set custom amounts for each assigned member.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSplitScreen(
    item: AssignableBillItem,
    members: List<SplitBillMember>,
    onBack: () -> Unit,
    onConfirm: (List<MemberShare>) -> Unit
) {
    // Initialize with current shares or equal split
    val assignedMembers = members.filter { item.assignedMemberIds.contains(it.id) }
    
    // State for each member's amount
    val memberAmounts = remember(item) {
        mutableStateMapOf<String, Int>().apply {
            assignedMembers.forEach { member ->
                val existingShare = item.customShares.find { it.memberId == member.id }
                this[member.id] = existingShare?.amount 
                    ?: (item.totalPrice / assignedMembers.size)
            }
        }
    }
    
    val totalAssigned = memberAmounts.values.sum()
    val remaining = item.totalPrice - totalAssigned
    val isValid = remaining == 0
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Atur pembagian", 
                        fontWeight = FontWeight.Medium, 
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                        onClick = {
                            val shares = memberAmounts.map { (memberId, amount) ->
                                MemberShare(memberId, amount)
                            }
                            onConfirm(shares)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.Size.buttonHeight),
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                            disabledContentColor = Color.White
                        ),
                        shape = AppTheme.Shapes.pill
                    ) {
                        Text(
                            text = "Konfirmasi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = AppTheme.Spacing.large),
            verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.medium)
        ) {
            // Item header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.Spacing.large),
                    shape = RoundedCornerShape(AppTheme.Radius.card),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.name.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total: Rp ${formatPrice(item.totalPrice)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Member amount inputs
            items(assignedMembers) { member ->
                MemberAmountCard(
                    member = member,
                    amount = memberAmounts[member.id] ?: 0,
                    onAmountChange = { newAmount ->
                        memberAmounts[member.id] = newAmount
                    }
                )
            }
            
            // Remaining indicator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppTheme.Radius.card),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            remaining == 0 -> Color(0xFFE8F5E9)  // Green
                            remaining > 0 -> Color(0xFFFFF8E1)   // Yellow
                            else -> Color(0xFFFFEBEE)            // Red
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.Spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                remaining == 0 -> "✓ Pembagian sudah pas!"
                                remaining > 0 -> "Sisa Rp ${formatPrice(remaining)} belum dibagi"
                                else -> "Melebihi Rp ${formatPrice(-remaining)}"
                            },
                            fontWeight = FontWeight.Medium,
                            color = when {
                                remaining == 0 -> Color(0xFF2E7D32)
                                remaining > 0 -> Color(0xFFE65100)
                                else -> Color(0xFFC62828)
                            }
                        )
                    }
                }
            }
            
            // Spacer at bottom
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * Card for a single member's amount input.
 */
@Composable
private fun MemberAmountCard(
    member: SplitBillMember,
    amount: Int,
    onAmountChange: (Int) -> Unit
) {
    var textValue by remember(amount) { mutableStateOf(amount.toString()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppTheme.Radius.card),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.Spacing.large),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(member.avatarColor)
            ) {
                Text(
                    text = member.initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Member name
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            // Amount input
            Surface(
                modifier = Modifier.width(120.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    BasicTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            // Only allow digits
                            val filtered = newValue.filter { it.isDigit() }
                            textValue = filtered
                            val parsed = filtered.toIntOrNull() ?: 0
                            onAmountChange(parsed)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
