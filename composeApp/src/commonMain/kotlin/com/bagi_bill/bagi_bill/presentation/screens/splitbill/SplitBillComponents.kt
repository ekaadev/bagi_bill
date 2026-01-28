package com.bagi_bill.bagi_bill.presentation.screens.splitbill

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * Single member avatar with selection state.
 */
@Composable
fun MemberAvatar(
    member: SplitBillMember,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(member.avatarColor)
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Text(
                text = member.initial,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Header with title and "Ubah anggota" button.
 */
@Composable
fun MemberHeaderTitle(
    onEditMembersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = AppTheme.Spacing.large,
                end = AppTheme.Spacing.large,
                top = AppTheme.Spacing.xLarge,
                bottom = AppTheme.Spacing.small
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rincian split bill",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(
                onClick = onEditMembersClick,
                shape = AppTheme.Shapes.pill,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ubah anggota", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

/**
 * Horizontal scrollable row of member avatars (sticky header).
 */
@Composable
fun MemberAvatarRow(
    members: List<SplitBillMember>,
    selectedMemberId: String?,
    onMemberClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.small),
            contentPadding = PaddingValues(
                horizontal = AppTheme.Spacing.small,
                vertical = 20.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members) { member ->
                MemberAvatar(
                    member = member,
                    isSelected = member.id == selectedMemberId,
                    onClick = { onMemberClick(member.id) }
                )
            }
        }
    }
}

/**
 * "Bagi rata semuanya" button with divider.
 */
@Composable
fun SplitEvenlyButton(
    onSplitEvenlyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = AppTheme.Spacing.large,
                vertical = AppTheme.Spacing.medium
            )
        ) {
            OutlinedButton(
                onClick = onSplitEvenlyClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Icon(
                    imageVector = Icons.Default.CallSplit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Bagi rata semuanya",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = AppTheme.Colors.divider
        )
    }
}

/**
 * Summary row for bill totals (Subtotal, Pajak, Total, etc.)
 */
@Composable
fun BillSummaryRow(
    label: String,
    price: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isTotal) Color.Black else Color.Gray,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Format price with thousand separators.
 */
fun formatPrice(price: Int): String {
    if (price == 0) return "0"
    return price.toString().reversed().chunked(3).joinToString(".").reversed()
}
