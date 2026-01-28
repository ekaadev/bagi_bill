package com.bagi_bill.bagi_bill.presentation.screens.done

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SelectableMember
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.generateColorForName
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Wallet Card Component showing payment destination with copy functionality
 */
@Composable
fun WalletCard(
    payer: SelectableMember,
    snackbarHostState: SnackbarHostState
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // Build formatted text for copying
    val walletType = payer.wallet ?: "Wallet"
    val phoneNumber = payer.phoneNumber ?: "-"
    val copyText = buildString {
        appendLine("Transfer ke:")
        appendLine("$walletType - $phoneNumber")
        append("a/n ${payer.name}")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppTheme.Shapes.card,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
        border = BorderStroke(1.dp, Color(0xFFFFD54F))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.Spacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Wallet Icon Circle
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD54F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💳",
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(AppTheme.Spacing.medium))

                // Wallet Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = walletType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = phoneNumber,
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "a/n ${payer.name}",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.Spacing.medium))

            // Copy Button
            OutlinedButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(copyText))
                    scope.launch {
                        snackbarHostState.showSnackbar("Tujuan pembayaran berhasil disalin")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = AppTheme.Shapes.pill,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF8F00)
                ),
                border = BorderStroke(1.dp, Color(0xFFFF8F00))
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salin Tujuan Pembayaran",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Members List with expandable items
 */
@Composable
fun MembersList(
    processedMembers: List<ProcessedMember>,
    formatCurrency: (Int) -> String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Anggota",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.Spacing.large))

        processedMembers.forEachIndexed { index, member ->
            MemberItemRow(
                member = member,
                formatCurrency = formatCurrency
            )
            if (index < processedMembers.size - 1) {
                Spacer(modifier = Modifier.height(AppTheme.Spacing.small))
            }
        }
    }
}

/**
 * Individual member row with expandable items
 */
@Composable
fun MemberItemRow(
    member: ProcessedMember,
    formatCurrency: (Int) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    val avatarColor = generateColorForName(member.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppTheme.Shapes.card,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, AppTheme.Colors.borderLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(AppTheme.Spacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.name.first().uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(AppTheme.Spacing.medium))

                // Name and badges
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        if (member.isPayer) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = AppTheme.Shapes.pill
                            ) {
                                Text(
                                    text = "Pembuat",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    if (member.phoneNumber != null) {
                        Text(
                            text = member.phoneNumber,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Amount
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(member.totalToPay),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${member.items.size} item",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Expand icon
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Gray
                )
            }

            // Expanded Items
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.Spacing.medium)
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(AppTheme.Spacing.small))

                    member.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.name} x${item.qty}",
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                            Text(
                                text = formatCurrency(item.sharePrice),
                                fontSize = 14.sp,
                                color = Color.Black.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dashed divider line
 */
@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    dashWidth: Float = 8f,
    dashGap: Float = 4f,
    strokeWidth: Float = 1f
) {
    androidx.compose.foundation.Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
    }
}
