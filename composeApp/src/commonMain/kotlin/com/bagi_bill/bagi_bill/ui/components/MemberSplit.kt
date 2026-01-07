package com.bagi_bill.bagi_bill.ui.components

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
import com.bagi_bill.bagi_bill.model.Member
import org.jetbrains.compose.ui.tooling.preview.Preview

// 1. COMPONENT AVATAR (TIDAK BERUBAH)
@Composable
fun MemberAvatar(
    member: Member,
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
            text = if (member.id == "0") "Kamu" else member.name,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

// -----------------------------------------------------------
// KOMPONEN YANG KITA UPDATE:
// -----------------------------------------------------------

// BAGIAN 1: JUDUL & TOMBOL UBAH
@Composable
fun MemberHeaderTitle(
    onEditMembersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
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
                shape = RoundedCornerShape(50),
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

// BAGIAN 2: ROW ICON PROFIL (Sticky) -> UPDATE PADDING
@Composable
fun MemberAvatarRow(
    members: List<Member>,
    selectedMemberId: String?,
    onMemberClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            // [UPDATE] Padding Vertikal diperbesar jadi 20.dp
            // Agar saat sticky, icon tidak terlihat mepet ke atap/lantai
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp),
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

// BAGIAN 3: TOMBOL BAGI RATA -> UPDATE ADA GARIS PEMISAH
@Composable
fun SplitEvenlyButton(
    onSplitEvenlyClick: () -> Unit
) {
    // Ubah jadi Column agar bisa menumpuk Tombol dan Divider
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Area Tombol
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
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

        // [UPDATE] Garis Pemisah (Divider) di bawah tombol
        // Ini akan memisahkan tombol dengan Item List di bawahnya
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.3f) // Abu transparan agar tidak terlalu keras
        )
    }
}

// Preview untuk memastikan padding dan garis terlihat pas
@Preview
@Composable
fun MemberComponentsPreview() {
    val dummyMembers = listOf(
        Member("1", "Kamu", "K"),
        Member("2", "Albert", "A")
    )
    Column {
        MemberHeaderTitle {}
        MemberAvatarRow(dummyMembers, "1") {}
        SplitEvenlyButton {}
    }
}