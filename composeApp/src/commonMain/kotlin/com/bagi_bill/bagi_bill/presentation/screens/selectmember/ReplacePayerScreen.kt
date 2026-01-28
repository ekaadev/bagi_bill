package com.bagi_bill.bagi_bill.presentation.screens.selectmember

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagi_bill.bagi_bill.presentation.theme.AppTheme

/**
 * Screen to replace/change the current payer.
 * Shows list of eligible members who can be payer (have wallet + phone).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplacePayerContent(
    currentPayer: SelectableMember,
    eligiblePayers: List<SelectableMember>,
    onBack: () -> Unit,
    onConfirm: (SelectableMember) -> Unit
) {
    var selectedPayer by remember { mutableStateOf(currentPayer) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ganti pembayar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            // Match RincianScreen bottom bar style exactly
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
                        onClick = { onConfirm(selectedPayer) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.Size.buttonHeight),
                        colors = ButtonDefaults.buttonColors(contentColor = Color.White),
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "Pilih siapa yang akan menerima pembayaran",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            items(eligiblePayers, key = { it.id }) { member ->
                PayerOptionItem(
                    member = member,
                    isSelected = member.id == selectedPayer.id,
                    isCurrent = member.id == currentPayer.id,
                    onClick = { selectedPayer = member }
                )
            }
            
            if (eligiblePayers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Wallet,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tidak ada anggota yang bisa menerima pembayaran",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Lengkapi nomor HP dan dompet untuk menjadi pembayar",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PayerOptionItem(
    member: SelectableMember,
    isSelected: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
               else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else Color.Gray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.initial,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                           else Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = member.name,
                        fontWeight = FontWeight.Medium
                    )
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Saat ini",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    member.wallet?.let { wallet ->
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = wallet,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    
                    member.phoneNumber?.let { phone ->
                        if (member.wallet != null) {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}
