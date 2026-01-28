package com.bagi_bill.bagi_bill.presentation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.presentation.viewmodel.HomeViewModel
import com.bagi_bill.bagi_bill.getPlatform
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.bagi_bill.bagi_bill.presentation.viewmodel.HomeUiState
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDraft: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val platform = remember { getPlatform() }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToCamera = onNavigateToCamera,
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToDraft = onNavigateToDraft,
        isWeb = platform.isWeb
    )
}

@Composable
fun WalletSection(
    onNavigateToWallet: () -> Unit = {}
) {
    // Container Wallet Section
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        // Container Column di dalam Card
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row, sebagai card header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Dompet kamu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card (atur wallet sendiri
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                color = Color.Transparent,
                onClick = onNavigateToWallet
            ) {
                // Icon(kiri) + Text(title, description) (kanan)
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Wallet,
                        contentDescription = "Wallet",
                        modifier = Modifier
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(8.dp)
                            .size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(35))
                            .padding(4.dp),
                        tint = Color.White
                    )

                    // spacer
                    Spacer(modifier = Modifier.width(12.dp))

                    // text container
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Atur walletmu sendiri",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Buat wallet untuk simpan dana patungan biar lebih praktis.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    // spacer
                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Wallet",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNewSplitBillSection(
    onNavigateToManual: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    isWeb: Boolean = false
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bikin baru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pilih cara yang kamu mau",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card (atur jumlah sendiri)
            OptionCreateSplitBill(
                onNavigate = onNavigateToManual,
                icon = Icons.Filled.CallSplit,
                title = "Atur jumlah sendiri",
                description = "Kamu yang atur pembagian dan jumlahnya."
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCreateSplitBill(
                onNavigate = onNavigateToScan,
                icon = Icons.Filled.CameraAlt,
                title = "Hitung otomatis pake struk",
                description = if (isWeb) {
                    "Fitur ini hanya tersedia di aplikasi mobile"
                } else {
                    "Foto struk atau ambil dari galeri biar nanti bisa kami bantu itungin."
                },
                enabled = !isWeb
            )
        }
    }
}

@Composable
fun OptionCreateSplitBill(
    onNavigate: () -> Unit,
    icon: ImageVector,
    title: String,
    description: String,
    enabled: Boolean = true
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = if (enabled) 0.3f else 0.1f)),
        color = Color.Transparent,
        onClick = if (enabled) onNavigate else { {} },
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(8.dp)
                    .size(24.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = if (enabled) 1f else 0.5f),
                        RoundedCornerShape(35)
                    )
                    .padding(4.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) Color.Black else Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (enabled) Color.Black else Color.Gray,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HistorySection(
    bills: List<Bill> = emptyList(),
    onBillClick: (Long) -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        // Container dalam Card (paling awal)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Title card
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yang terakhir kamu buat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            // Spacer
            Spacer(modifier = Modifier.height(12.dp))

            // Section content card
            ListHistorySplitBill(
                bills = bills,
                onBillClick = onBillClick,
                onNavigateToHistory = onNavigateToHistory
            )
        }
    }
}

@Composable
fun ListHistorySplitBill(
    bills: List<Bill> = emptyList(),
    onBillClick: (Long) -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        if (bills.isEmpty()) {
            Text(
                text = "Belum ada riwayat split bill",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.1f))
            ) {
                bills.forEachIndexed { index, bill ->
                    BillHistoryItem(
                        bill = bill,
                        onClick = { onBillClick(bill.id) }
                    )

                    if (index < bills.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.Transparent,
            shape = RoundedCornerShape(50.dp),
            border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
            onClick = onNavigateToHistory
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lihat riwayat selengkapnya",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Lihat riwayat selengkapnya",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BillHistoryItem(
    bill: Bill,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CallSplit,
                contentDescription = null,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                    .padding(8.dp)
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(35))
                    .padding(4.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = bill.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatParticipantsList(bill.members.map { it.name }, maxVisible = 2),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = formatRupiah(bill.totalAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                val paidCount = bill.members.count { it.isPaid }
                val totalCount = bill.members.size
                val statusColor = when {
                    paidCount == totalCount -> Color(0xFF4CAF50)
                    paidCount > 0 -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }
                val statusIcon = when {
                    paidCount == totalCount -> Icons.Filled.CheckCircle
                    else -> Icons.Filled.AccessTime
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$paidCount dari $totalCount udah bayar",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Helper function untuk format Rupiah
private fun formatRupiah(amount: Long): String {
    val amountStr = amount.toString()
    val reversed = amountStr.reversed()
    val grouped = reversed.chunked(3).joinToString(".")
    return "Rp${grouped.reversed()}"
}

// Helper function untuk format participants
private fun formatParticipantsList(participants: List<String>, maxVisible: Int = 2): String {
    return when {
        participants.isEmpty() -> "Tidak ada peserta"
        participants.size <= maxVisible -> participants.joinToString(" and ")
        else -> {
            val visible = participants.take(maxVisible).joinToString(", ")
            val remaining = participants.size - maxVisible
            "$visible and $remaining other${if (remaining > 1) "s" else ""}"
        }
    }
}

// Preview function tanpa Koin untuk preview mode
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                recentBills = emptyList(),
                draftCount = 0
            )
        )
    }
}

// Extracted content untuk reusability
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToCamera: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToDraft: () -> Unit = {},
    isWeb: Boolean = false
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {},
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bagi Bill",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Surface(
                            onClick = onNavigateToDraft,
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            tonalElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Draft ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                                Text(
                                    text = "(${uiState.draftCount})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Fungsi Bantuan belum tersedia")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Help,
                            contentDescription = "Bantuan",
                            tint = Color.Gray
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = if (isWeb) {
                    Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxHeight()
                } else {
                    Modifier.fillMaxSize()
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 100.dp)
                ) {
                    WalletSection(
                        onNavigateToWallet = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Fitur Wallet belum tersedia")
                            }
                        }
                    )

                    CreateNewSplitBillSection(
                        onNavigateToManual = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Fitur Manual Input belum tersedia")
                            }
                        },
                        onNavigateToScan = {
                            if (isWeb) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Fitur Scan OCR hanya tersedia di aplikasi mobile")
                                }
                            } else {
                                onNavigateToCamera()
                            }
                        },
                        isWeb = isWeb
                    )

                    HistorySection(
                        bills = uiState.recentBills,
                        onBillClick = { _ ->
                            scope.launch {
                                snackbarHostState.showSnackbar("Detail Bill belum tersedia")
                            }
                        },
                        onNavigateToHistory = onNavigateToHistory
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 32.dp,
                    tonalElevation = 0.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 24.dp,
                                bottom = 24.dp
                            )
                    ) {
                        Button(
                            onClick = {
                                if (isWeb) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Fitur Scan OCR hanya tersedia di aplikasi mobile")
                                    }
                                } else {
                                    onNavigateToCamera()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(43.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Scan Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}


