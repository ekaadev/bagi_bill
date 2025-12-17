package com.bagi_bill.bagi_bill.ui.components

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
import com.bagi_bill.bagi_bill.model.AssignableBillItem
import com.bagi_bill.bagi_bill.model.Member
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AssignmentBillItemRow(
    item: AssignableBillItem,
    allMembers: List<Member>,
    isAssignedToCurrentUser: Boolean,
    onToggle: () -> Unit
) {
    // Logic Warna: Ungu Muda Transparan jika aktif, Putih jika tidak
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
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {

        // ==================================================
        // ROW 1: NAMA ITEM (Kiri) & CHECKBOX (Kanan)
        // ==================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Nama Item (Weight biar kalau panjang dia turun ke bawah, ga nabrak checkbox)
            Text(
                text = item.name.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            // Checkbox Custom Size
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

        // ==================================================
        // ROW 2: QTY & HARGA (Rata Kanan / Alignment End)
        // ==================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp), // Jarak dikit dari nama
            horizontalArrangement = Arrangement.End, // Dorong ke Kanan
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Qty "x1"
            Text(
                text = "x${item.qty}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(end = 16.dp) // Jarak antara Qty dan Harga
            )

            // Harga
            Text(
                text = "${item.price}", // Format: 25.000
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }

        // ==================================================
        // ROW 3: ICON PROFIL & TOMBOL "Atur Pembagian"
        // ==================================================
        // Muncul hanya jika ada yang ditugaskan (assigned)
        if (item.assignedMemberIds.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp)) // Jarak dari harga ke baris icon

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // A. List Icon Profil Kecil
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp), // Efek tumpuk dikit (-8dp)
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.assignedMemberIds.forEach { memberId ->
                        val member = allMembers.find { it.id == memberId }
                        if (member != null) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp) // Ukuran icon
                                    .clip(CircleShape)
                                    .background(Color.White) // Border putih tipis saat ditumpuk
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
                }

                Spacer(modifier = Modifier.width(8.dp))

                // B. Tombol "Atur pembagian >" (Chip Abu-abu)
                Surface(
                    color = Color(0xFFF5F5F5), // Abu sangat muda
                    shape = RoundedCornerShape(50), // Capsule shape
                    modifier = Modifier
                        .height(28.dp) // Tinggi disamakan dengan icon
                        .clickable { /* TODO: Buka Dialog Atur Porsi */ }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Atur pembagian",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // Divider Tipis
    HorizontalDivider(
        color = Color.LightGray.copy(alpha = 0.3f),
        thickness = 1.dp
    )
}

// PREVIEW
@Preview(showBackground = true)
@Composable
fun AssignmentItem3RowsPreview() {
    val dummyMembers = listOf(
        Member("1", "Kamu", "K", Color(0xFF4CAF50)),
        Member("2", "Albert", "A", Color(0xFF2196F3))
    )

    Column {
        AssignmentBillItemRow(
            item = AssignableBillItem("1", "House Blend Coffee (H)", 25000, 1, listOf("1", "2")),
            allMembers = dummyMembers,
            isAssignedToCurrentUser = true,
            onToggle = {}
        )
        AssignmentBillItemRow(
            item = AssignableBillItem("2", "Hazelnut Choco MT (L)", 28000, 1, emptyList()),
            allMembers = dummyMembers,
            isAssignedToCurrentUser = false,
            onToggle = {}
        )
    }
}