package com.bagi_bill.bagi_bill.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bagi_bill.bagi_bill.domain.model.BillItem
import com.bagi_bill.bagi_bill.domain.model.SplitMember
import com.bagi_bill.bagi_bill.domain.model.formatCurrency
import com.bagi_bill.bagi_bill.presentation.components.AppContainer
import com.bagi_bill.bagi_bill.presentation.viewmodel.SplitBillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitAssignmentScreen(
    viewModel: SplitBillViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToResult: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedItem by remember { mutableStateOf<BillItem?>(null) }

    // Avatar colors
    val avatarColors = listOf(
        Color(0xFF6750A4), Color(0xFF625B71), Color(0xFF7D5260),
        Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFF57C00),
        Color(0xFFC2185B), Color(0xFF7B1FA2)
    )

    // Calculate assignment stats
    val assignedCount = state.items.count { it.isAssigned() }
    val totalCount = state.items.size
    val progress = if (totalCount > 0) assignedCount.toFloat() / totalCount else 0f

    AppContainer {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pembagian Item") },
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Progress indicator
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$assignedCount dari $totalCount item sudah dibagi",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (progress == 1f) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (progress == 1f) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.calculateResults()
                                onNavigateToResult()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.allItemsAssigned(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Lihat Hasil Split",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null
                            )
                        }

                        if (!state.allItemsAssigned()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pastikan semua item sudah dibagikan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Members horizontal scroll
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Teman (${state.members.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.members) { member ->
                                val color = avatarColors[state.members.indexOf(member) % avatarColors.size]
                                MemberChip(member = member, color = color)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Items header with Bagi Rata button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Klik item untuk memilih siapa yang makan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    // Tombol Bagi Rata
                    FilledTonalButton(
                        onClick = { viewModel.assignAllItemsToAllMembers() },
                        modifier = Modifier.padding(start = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bagi Rata",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Items list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items) { item ->
                        AssignmentItemCard(
                            item = item,
                            members = state.members,
                            avatarColors = avatarColors,
                            onClick = { selectedItem = item }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Assignment dialog
    selectedItem?.let { item ->
        AssignmentDialog(
            item = item,
            members = state.members,
            avatarColors = avatarColors,
            onDismiss = { selectedItem = null },
            onAssign = { memberIds ->
                viewModel.assignItemToMembers(item.id, memberIds)
                selectedItem = null
            }
        )
    }
}

@Composable
private fun MemberChip(
    member: SplitMember,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = member.getInitial(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AssignmentItemCard(
    item: BillItem,
    members: List<SplitMember>,
    avatarColors: List<Color>,
    onClick: () -> Unit
) {
    val isAssigned = item.isAssigned()
    val assignedMembers = members.filter { it.id in item.assignedMemberIds }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAssigned)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isAssigned)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Icon(
                imageVector = if (isAssigned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isAssigned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Item info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${item.quantity}x @ Rp ${formatCurrency(item.unitPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Assigned members preview
                if (assignedMembers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-8).dp)
                    ) {
                        assignedMembers.take(4).forEach { member ->
                            val color = avatarColors[members.indexOf(member) % avatarColors.size]
                            Surface(
                                shape = CircleShape,
                                color = color,
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = member.getInitial(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        if (assignedMembers.size > 4) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "+${assignedMembers.size - 4}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Price
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${formatCurrency(item.getTotalPrice())}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isAssigned && assignedMembers.size > 1) {
                    Text(
                        text = "@${formatCurrency(item.getPricePerMember())}/org",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentDialog(
    item: BillItem,
    members: List<SplitMember>,
    avatarColors: List<Color>,
    onDismiss: () -> Unit,
    onAssign: (List<Long>) -> Unit
) {
    var selectedIds by remember { mutableStateOf(item.assignedMemberIds.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Siapa yang makan ini?")
        },
        text = {
            Column {
                // Item info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${item.quantity}x @ Rp ${formatCurrency(item.unitPrice)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "Rp ${formatCurrency(item.getTotalPrice())}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedIds.size == members.size,
                        onClick = {
                            selectedIds = if (selectedIds.size == members.size) {
                                emptySet()
                            } else {
                                members.map { it.id }.toSet()
                            }
                        },
                        label = { Text("Semua") },
                        leadingIcon = if (selectedIds.size == members.size) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Member selection
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    members.forEach { member ->
                        val isSelected = member.id in selectedIds
                        val color = avatarColors[members.indexOf(member) % avatarColors.size]

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds = if (isSelected) {
                                        selectedIds - member.id
                                    } else {
                                        selectedIds + member.id
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected)
                                color.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, color) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = color,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = member.getInitial(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )

                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        selectedIds = if (isSelected) {
                                            selectedIds - member.id
                                        } else {
                                            selectedIds + member.id
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Price per person preview
                if (selectedIds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Per orang (${selectedIds.size} orang)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Rp ${formatCurrency(item.getTotalPrice() / selectedIds.size)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAssign(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty()
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

