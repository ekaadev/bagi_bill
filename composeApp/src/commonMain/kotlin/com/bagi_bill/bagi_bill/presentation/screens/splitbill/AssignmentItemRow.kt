package com.bagi_bill.bagi_bill.presentation.screens.splitbill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * Bill item row with checkbox for assignment and "Atur pembagian" button.
 */
@Composable
fun AssignmentItemRow(
    item: AssignableBillItem,
    allMembers: List<SplitBillMember>,
    isAssignedToCurrentUser: Boolean,
    onToggle: () -> Unit,
    onCustomSplitClick: () -> Unit
) {
    val backgroundColor = if (isAssignedToCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onToggle() }
            .padding(vertical = 12.dp, horizontal = AppTheme.Spacing.large)
    ) {
        // Row 1: Item name (left) & Checkbox (right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            Box(modifier = Modifier.size(24.dp)) {
                Checkbox(
                    checked = isAssignedToCurrentUser,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color.LightGray,
                        checkmarkColor = Color.White
                    )
                )
            }
        }

        // Row 2: Qty & Price (right aligned)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "x${item.qty}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(end = AppTheme.Spacing.large)
            )
            Text(
                text = formatPrice(item.price),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        // Row 3: Assigned members avatars & "Atur pembagian" button
        if (item.assignedMemberIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stacked avatars
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.assignedMemberIds.take(5).forEach { memberId ->
                        val member = allMembers.find { it.id == memberId }
                        if (member != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(1.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(member.avatarColor)
                                ) {
                                    Text(
                                        text = member.initial,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    
                    // Show +N if more than 5 members
                    if (item.assignedMemberIds.size > 5) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            Text(
                                text = "+${item.assignedMemberIds.size - 5}",
                                fontSize = 8.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // "Atur pembagian" chip
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(28.dp)
                        .clickable { onCustomSplitClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (item.hasCustomSplit()) "Pembagian custom" else "Atur pembagian",
                            fontSize = 10.sp,
                            color = if (item.hasCustomSplit()) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (item.hasCustomSplit()) MaterialTheme.colorScheme.primary else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Divider
    HorizontalDivider(
        color = AppTheme.Colors.divider,
        thickness = 1.dp
    )
}
