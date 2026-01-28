@file:OptIn(kotlin.time.ExperimentalTime::class)
package com.bagi_bill.bagi_bill.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bagi_bill.bagi_bill.data.repository.BillRepository
import com.bagi_bill.bagi_bill.domain.model.Bill
import com.bagi_bill.bagi_bill.domain.model.BillStatus
import com.bagi_bill.bagi_bill.domain.model.BillType
import com.bagi_bill.bagi_bill.domain.model.Member
import com.bagi_bill.bagi_bill.domain.model.WalletType
import com.bagi_bill.bagi_bill.domain.parser.parseReceiptText
import com.bagi_bill.bagi_bill.presentation.camera.CameraScreen
import com.bagi_bill.bagi_bill.presentation.contacts.rememberContactState
import com.bagi_bill.bagi_bill.presentation.ocr.TextRecognitionService
import com.bagi_bill.bagi_bill.presentation.screens.HistoryScreen
import com.bagi_bill.bagi_bill.presentation.screens.HomeScreen
import com.bagi_bill.bagi_bill.presentation.screens.DraftScreen
import com.bagi_bill.bagi_bill.presentation.screens.done.DoneScreen
import com.bagi_bill.bagi_bill.presentation.screens.rincian.RincianScreen
import com.bagi_bill.bagi_bill.presentation.screens.rincian.UbahRincianScreen
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SelectMemberScreen
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.SplitBillScreen
import com.bagi_bill.bagi_bill.presentation.viewmodel.ScanViewModel
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import kotlin.time.ExperimentalTime

/**
 * Format date for display
 */
@OptIn(ExperimentalTime::class)
private fun formatTransactionDate(): String {
    val now = kotlin.time.Clock.System.now()
    val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
    val dayOfWeek = when (localDateTime.dayOfWeek) {
        kotlinx.datetime.DayOfWeek.MONDAY -> "Senin"
        kotlinx.datetime.DayOfWeek.TUESDAY -> "Selasa"
        kotlinx.datetime.DayOfWeek.WEDNESDAY -> "Rabu"
        kotlinx.datetime.DayOfWeek.THURSDAY -> "Kamis"
        kotlinx.datetime.DayOfWeek.FRIDAY -> "Jumat"
        kotlinx.datetime.DayOfWeek.SATURDAY -> "Sabtu"
        kotlinx.datetime.DayOfWeek.SUNDAY -> "Minggu"
        else -> ""
    }
    val month = when (localDateTime.monthNumber) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "Mei"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Agu"
        9 -> "Sep"; 10 -> "Okt"; 11 -> "Nov"; 12 -> "Des"
        else -> ""
    }
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$dayOfWeek, ${localDateTime.dayOfMonth} $month ${localDateTime.year} $hour:$minute"
}

@Composable
fun NavigationGraph(
    navController: NavHostController
) {
    // Shared ViewModel for scan results
    val scanViewModel: ScanViewModel = viewModel { ScanViewModel() }
    val textService = remember { TextRecognitionService() }
    val scope = rememberCoroutineScope()
    
    // Inject BillRepository from Koin
    val billRepository: BillRepository = koinInject()

    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        // ===== HOME SCREEN =====
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = {
                    scanViewModel.clearData()
                    navController.navigate(Route.Camera)
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                },
                onNavigateToDraft = {
                    navController.navigate(Route.Draft)
                }
            )
        }

        // ===== DRAFT SCREEN =====
        composable<Route.Draft> {
            DraftScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResumeDraft = { draft ->
                    // TODO: Load draft data into scanViewModel and navigate to Rincian
                    navController.popBackStack()
                }
            )
        }

        // ===== HISTORY SCREEN =====
        composable<Route.History> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ===== CAMERA SCREEN =====
        composable<Route.Camera> {
            val isProcessing by scanViewModel.isProcessing.collectAsState()
            val errorMessage by scanViewModel.errorMessage.collectAsState()

            if (isProcessing) {
                // Show loading while OCR processes
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                CameraScreen(
                    onExit = {
                        navController.popBackStack()
                    },
                    onPhotoConfirmed = { photoBytes ->
                        scope.launch {
                            try {
                                scanViewModel.setProcessing(true)
                                scanViewModel.clearError()
                                
                                // 1. Run OCR
                                val rawText = textService.recognizeText(photoBytes)
                                
                                // 2. Parse text
                                val receipt = parseReceiptText(rawText)
                                
                                // 3. Store results
                                scanViewModel.setImageBytes(photoBytes)
                                scanViewModel.setParsedReceipt(receipt)
                                
                                // 4. Navigate to Rincian
                                scanViewModel.setProcessing(false)
                                navController.navigate(Route.Rincian) {
                                    popUpTo(Route.Camera) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                scanViewModel.setProcessing(false)
                                scanViewModel.setError(e.message ?: "OCR gagal")
                                // Still navigate but with empty receipt
                                navController.popBackStack()
                            }
                        }
                    }
                )
            }
        }

        // ===== RINCIAN SCREEN =====
        composable<Route.Rincian> {
            val parsedReceipt by scanViewModel.parsedReceipt.collectAsState()
            val imageBytes by scanViewModel.imageBytes.collectAsState()

            if (parsedReceipt != null) {
                RincianScreen(
                    parsedReceipt = parsedReceipt!!,
                    imageBytes = imageBytes,
                    onBack = {
                        // Show draft dialog when pressing back
                        // The dialog is handled inside RincianScreen now
                        // This onBack is called when user wants to see the dialog
                        // We need to trigger the dialog from within RincianScreen
                        // For now, we call onExitToHome directly but user should see dialog first
                        // The RincianScreen handles showing the dialog internally
                    },
                    onExitToHome = {
                        scanViewModel.clearData()
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    },
                    onSaveDraft = {
                        scope.launch {
                            try {
                                val receipt = parsedReceipt!!
                                val now = kotlin.time.Clock.System.now()
                                val bill = Bill(
                                    name = receipt.name,
                                    totalAmount = receipt.summary.total.toLong(),
                                    createdDate = now,
                                    status = BillStatus.UNPAID,
                                    isDraft = true,
                                    billType = BillType.SCAN
                                )
                                billRepository.createBill(bill, emptyList())
                                scanViewModel.clearData()
                                navController.navigate(Route.Home) {
                                    popUpTo(Route.Home) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                // Handle error silently for now
                            }
                        }
                    },
                    onRetakePhoto = {
                        navController.navigate(Route.Camera)
                    },
                    onEditDetails = {
                        navController.navigate(Route.UbahRincian)
                    },
                    onConfirm = {
                        // Navigate to member selection
                        navController.navigate(Route.SelectMember)
                    }
                )
            } else {
                // Fallback if data is missing
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            }
        }

        // ===== UBAH RINCIAN SCREEN =====
        composable<Route.UbahRincian> {
            val parsedReceipt by scanViewModel.parsedReceipt.collectAsState()

            if (parsedReceipt != null) {
                UbahRincianScreen(
                    parsedReceipt = parsedReceipt!!,
                    onBack = {
                        navController.popBackStack()
                    },
                    onConfirm = { updatedReceipt ->
                        scanViewModel.setParsedReceipt(updatedReceipt)
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }

        // ===== SELECT MEMBER SCREEN =====
        composable<Route.SelectMember> {
            // Get contact state with permission handling
            val (contactState, requestPermission) = rememberContactState()
            
            // Check if we have existing data (coming back from SplitBill "Ubah anggota")
            val existingSplitBillData by scanViewModel.splitBillData.collectAsState()
            
            SelectMemberScreen(
                onBack = {
                    navController.popBackStack()
                },
                onExitToHome = {
                    scanViewModel.clearData()
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                },
                onSaveDraft = {
                    scope.launch {
                        try {
                            val receipt = scanViewModel.parsedReceipt.value!!
                            val now = kotlin.time.Clock.System.now()
                            val bill = Bill(
                                name = receipt.name,
                                totalAmount = receipt.summary.total.toLong(),
                                createdDate = now,
                                status = BillStatus.UNPAID,
                                isDraft = true,
                                billType = BillType.SCAN
                            )
                            billRepository.createBill(bill, emptyList())
                            scanViewModel.clearData()
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Home) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            // Handle error silently
                        }
                    }
                },
                onConfirm = { splitBillData ->
                    // Store data and navigate to SplitBill screen
                    scanViewModel.setSplitBillData(splitBillData)
                    navController.navigate(Route.SplitBill)
                },
                contacts = contactState.contacts,
                isLoadingContacts = contactState.isLoading,
                hasContactPermission = contactState.hasPermission,
                onRequestPermission = requestPermission,
                // Pass existing data for state persistence
                initialPayer = existingSplitBillData?.payer,
                initialMembers = existingSplitBillData?.members ?: emptyList()
            )
        }

        // ===== SPLIT BILL SCREEN =====
        composable<Route.SplitBill> {
            val parsedReceipt by scanViewModel.parsedReceipt.collectAsState()
            val splitBillData by scanViewModel.splitBillData.collectAsState()

            if (parsedReceipt != null && splitBillData != null) {
                SplitBillScreen(
                    splitBillData = splitBillData!!,
                    parsedReceipt = parsedReceipt!!,
                    onBack = {
                        navController.popBackStack()
                    },
                    onExitToHome = {
                        scanViewModel.clearData()
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    },
                    onSaveDraft = {
                        scope.launch {
                            try {
                                val receipt = parsedReceipt!!
                                val now = kotlin.time.Clock.System.now()
                                val bill = Bill(
                                    name = receipt.name,
                                    totalAmount = receipt.summary.total.toLong(),
                                    createdDate = now,
                                    status = BillStatus.UNPAID,
                                    isDraft = true,
                                    billType = BillType.SCAN
                                )
                                billRepository.createBill(bill, emptyList())
                                scanViewModel.clearData()
                                navController.navigate(Route.Home) {
                                    popUpTo(Route.Home) { inclusive = true }
                                }
                            } catch (e: Exception) {
                                // Handle error silently
                            }
                        }
                    },
                    onEditMembers = {
                        navController.popBackStack()
                    },
                    onSend = { assignedItems ->
                        scope.launch {
                            try {
                                // 1. Calculate total and prepare date
                                val totalAmount = assignedItems.sumOf { it.totalPrice }.toLong()
                                val now = kotlin.time.Clock.System.now()
                                val transactionDate = formatTransactionDate()

                                // 2. Create Bill record
                                val bill = Bill(
                                    name = splitBillData!!.merchantName,
                                    totalAmount = totalAmount,
                                    createdDate = now,
                                    status = BillStatus.UNPAID,
                                    isDraft = false,
                                    billType = BillType.SCAN
                                )

                                // 3. Create Member records from assigned items
                                val allMembers = listOf(splitBillData!!.payer) + splitBillData!!.members
                                val members = allMembers.map { member ->
                                    val memberAmount = assignedItems
                                        .filter { it.assignedMemberIds.contains(member.id) }
                                        .sumOf { it.getShareForMember(member.id) }

                                    Member(
                                        billId = 0,
                                        name = member.name,
                                        walletNumber = member.phoneNumber,
                                        walletType = null,
                                        amount = memberAmount.toLong(),
                                        isPaid = false
                                    )
                                }

                                // 4. Save to database
                                billRepository.createBill(bill, members)

                                // 5. Store data for DoneScreen
                                scanViewModel.setAssignedItems(assignedItems)
                                scanViewModel.setTransactionDate(transactionDate)

                                // 6. Navigate to DoneScreen
                                navController.navigate(Route.Done) {
                                    popUpTo(Route.Home)
                                }
                            } catch (e: Exception) {
                                // Handle error - still navigate but log the error
                                e.printStackTrace()
                                val transactionDate = formatTransactionDate()
                                scanViewModel.setAssignedItems(assignedItems)
                                scanViewModel.setTransactionDate(transactionDate)
                                navController.navigate(Route.Done) {
                                    popUpTo(Route.Home)
                                }
                            }
                        }
                    }
                )
            } else {
                // Fallback if data is missing
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            }
        }

        // ===== DONE SCREEN =====
        composable<Route.Done> {
            val splitBillData by scanViewModel.splitBillData.collectAsState()
            val assignedItems by scanViewModel.assignedItems.collectAsState()
            val imageBytes by scanViewModel.imageBytes.collectAsState()
            val transactionDate by scanViewModel.transactionDate.collectAsState()

            if (splitBillData != null && assignedItems.isNotEmpty()) {
                DoneScreen(
                    splitBillData = splitBillData!!,
                    assignedItems = assignedItems,
                    imageBytes = imageBytes,
                    transactionDate = transactionDate,
                    onNavigateToRincian = {
                        navController.navigate(Route.Rincian) {
                            popUpTo(Route.Done) { inclusive = true }
                        }
                    },
                    onGoHome = {
                        scanViewModel.clearData()
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
                        }
                    }
                )
            } else {
                // Fallback if data is missing
                LaunchedEffect(Unit) {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            }
        }
    }
}
