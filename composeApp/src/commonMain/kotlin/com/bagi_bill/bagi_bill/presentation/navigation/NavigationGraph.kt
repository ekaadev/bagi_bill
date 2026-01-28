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
import com.bagi_bill.bagi_bill.domain.parser.parseReceiptText
import com.bagi_bill.bagi_bill.presentation.camera.CameraScreen
import com.bagi_bill.bagi_bill.presentation.contacts.rememberContactState
import com.bagi_bill.bagi_bill.presentation.ocr.TextRecognitionService
import com.bagi_bill.bagi_bill.presentation.screens.HistoryScreen
import com.bagi_bill.bagi_bill.presentation.screens.HomeScreen
import com.bagi_bill.bagi_bill.presentation.screens.rincian.RincianScreen
import com.bagi_bill.bagi_bill.presentation.screens.rincian.UbahRincianScreen
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.SelectMemberScreen
import com.bagi_bill.bagi_bill.presentation.screens.splitbill.SplitBillScreen
import com.bagi_bill.bagi_bill.presentation.viewmodel.ScanViewModel
import kotlinx.coroutines.launch

@Composable
fun NavigationGraph(
    navController: NavHostController
) {
    // Shared ViewModel for scan results
    val scanViewModel: ScanViewModel = viewModel { ScanViewModel() }
    val textService = remember { TextRecognitionService() }
    val scope = rememberCoroutineScope()

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
                        scanViewModel.clearData()
                        navController.navigate(Route.Home) {
                            popUpTo(Route.Home) { inclusive = true }
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
                    onEditMembers = {
                        navController.popBackStack()
                    },
                    onSend = { assignedItems ->
                        // TODO: Send to members / DoneScreen
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

