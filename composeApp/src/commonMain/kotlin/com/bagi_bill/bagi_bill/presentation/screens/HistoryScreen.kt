package com.bagi_bill.bagi_bill.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.presentation.viewmodel.DateRangeOption
import com.bagi_bill.bagi_bill.presentation.viewmodel.HistoryViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    val dateOptions = listOf(
        DateRangeOption.LAST_7_DAYS,
        DateRangeOption.LAST_30_DAYS,
        DateRangeOption.LAST_90_DAYS,
    )

    val actualDateOptions = remember(uiState.selectedDateRange) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        dateOptions.map { option ->
            val startDate = today.minus(option.days - 1, DateTimeUnit.DAY)
            formatDateRange(startDate, today)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    Text(
                        text = "Riwayat transaksi",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = { showBottomSheet = true },
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.selectedDateRange.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Terjadi kesalahan",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                uiState.groupedBills.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada riwayat transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        uiState.groupedBills.forEach { (date, bills) ->
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp),
                                text = formatDateHeader(date),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                            )

                            BillsCard(bills = bills)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                    ) {
                        Text(
                            text = "Pilih tanggal transaksi",
                            style = MaterialTheme.typography.titleLarge,
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(modifier = Modifier.selectableGroup()) {
                            dateOptions.forEach { option ->
                                val currentSelection = uiState.tempSelectedDateRange ?: uiState.selectedDateRange
                                Row(
                                    modifier = Modifier
                                        .selectable(
                                            selected = (option == currentSelection),
                                            onClick = {
                                                viewModel.setTempDateRange(option)
                                            },
                                            role = Role.RadioButton,
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        )
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = option.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = actualDateOptions[dateOptions.indexOf(option)],
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                        )
                                    }

                                    RadioButton(
                                        selected = (option == currentSelection),
                                        onClick = null
                                    )
                                }

                                HorizontalDivider(
                                    color = Color.Gray.copy(alpha = 0.1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                viewModel.applyFilter()
                                showBottomSheet = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Pasang filter",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BillsCard(bills: List<Bill>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        bills.forEachIndexed { index, bill ->
            BillItem(bill = bill)

            if (index < bills.size - 1) {
                HorizontalDivider(
                    color = Color.Gray.copy(alpha = 0.1f),
                    thickness = 0.5.dp,
                )
            }
        }
    }
}

@Composable
fun BillItem(bill: Bill) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.CallSplit,
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatParticipants(bill.members.map { it.name }, maxVisible = 2),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatCurrency(bill.totalAmount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            val paidCount = bill.members.count { it.isPaid }
            val totalCount = bill.members.size
            val statusColor = when (bill.status) {
                BillStatus.PAID -> Color(0xFF4CAF50)
                BillStatus.PARTIALLY_PAID -> Color(0xFFFF9800)
                BillStatus.UNPAID -> Color(0xFFF44336)
            }
            val statusIcon = when (bill.status) {
                BillStatus.PAID -> Icons.Filled.CheckCircle
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

fun formatDateHeader(date: LocalDate): String {
    val dayNames = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

    val dayName = dayNames[date.dayOfWeek.ordinal % 7]
    val monthName = monthNames[date.month.number - 1]

    return "$dayName, ${date.day} $monthName ${date.year}"
}


fun formatDateRange(startDate: LocalDate, endDate: LocalDate): String {
    val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

    val startDay = dayNames[startDate.dayOfWeek.ordinal % 7]
    val startMonth = monthNames[startDate.month.number - 1]

    val endDay = dayNames[endDate.dayOfWeek.ordinal % 7]
    val endMonth = monthNames[endDate.month.number - 1]

    return "$startDay, ${startDate.day} $startMonth ${startDate.year} - $endDay, ${endDate.day} $endMonth ${endDate.year}"
}

fun formatParticipants(participants: List<String>, maxVisible: Int = 2): String {
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

fun formatCurrency(amount: Long): String {
    val absAmount = abs(amount)
    val formatted = absAmount.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
    return "Rp$formatted"
}
