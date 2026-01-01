package com.bagi_bill.bagi_bill.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bagi_bill.bagi_bill.*
import com.bagi_bill.bagi_bill.ui.screens.rincian.RincianScreen
import com.bagi_bill.bagi_bill.ui.screens.rincian.UbahRincianScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ========================================================================
 * NAVIGATION SYSTEM - JETPACK NAVIGATION COMPOSE (MULTIPLATFORM)
 * ========================================================================
 * Menggunakan library standar 'androidx.navigation:navigation-compose'
 * yang sudah di-porting oleh JetBrains untuk Multiplatform.
 *
 * Best Practice:
 * 1. Gunakan 'rememberNavController()'
 * 2. Gunakan 'NavHost' untuk mendefinisikan graph
 * 3. Gunakan 'SharedViewModel' untuk passing data kompleks (seperti gambar/objek)
 * ========================================================================
 */

// ==================== VIEW MODEL ====================
class SharedViewModel : ViewModel() {
    private val _parsedReceipt = MutableStateFlow<ParsedReceipt?>(null)
    val parsedReceipt = _parsedReceipt.asStateFlow()

    private val _imageBytes = MutableStateFlow<ByteArray?>(null)
    val imageBytes = _imageBytes.asStateFlow()

    fun setParsedReceipt(receipt: ParsedReceipt) {
        _parsedReceipt.value = receipt
    }

    fun setImageBytes(bytes: ByteArray) {
        _imageBytes.value = bytes
    }

    fun clearData() {
        _parsedReceipt.value = null
        _imageBytes.value = null
    }
}

// ==================== ROUTES ====================
object Routes {
    const val HOME = "home"
    const val CAMERA = "camera"
    const val RINCIAN = "rincian"
    const val UBAH_RINCIAN = "ubah_rincian"
    const val SELECT_MEMBER = "select_member"
    const val DONE = "done"
}

// ==================== NAVIGATOR ====================
@Composable
fun AppNavigator(
    onExitApp: () -> Unit = {}
) {
    val navController = rememberNavController()
    // ViewModel scoped to the AppNavigator (effectively Singleton for the flow)
    val sharedViewModel: SharedViewModel = viewModel { SharedViewModel() }

    val scope = rememberCoroutineScope()
    val textService = remember { TextRecognitionService() }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        // ===== HOME SCREEN =====
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Routes.CAMERA)
                }
            )
        }

        // ===== CAMERA SCREEN =====
        composable(Routes.CAMERA) {
            CameraScreen(
                onExit = {
                    if (!navController.popBackStack()) {
                        onExitApp()
                    }
                },
                onPhotoConfirmed = { bytes ->
                    scope.launch {
                        try {
                            val extractedText = textService.recognizeText(bytes)
                            val parsedReceipt = parserUtil(extractedText)

                            // Simpan data di ViewModel sebelum navigasi
                            sharedViewModel.setImageBytes(bytes)
                            sharedViewModel.setParsedReceipt(parsedReceipt)

                            // Navigate to Rincian, pop Camera agar tidak bisa back ke Camera
                            navController.navigate(Routes.RINCIAN) {
                                popUpTo(Routes.CAMERA) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            println("OCR Error: ${e.message}")
                            // Bisa tambahkan handling error UI disini
                        }
                    }
                }
            )
        }

        // ===== RINCIAN SCREEN =====
        composable(Routes.RINCIAN) {
            val parsedReceipt by sharedViewModel.parsedReceipt.collectAsState()
            val imageBytes by sharedViewModel.imageBytes.collectAsState()

            if (parsedReceipt != null) {
                RincianScreen(
                    parsedReceipt = parsedReceipt!!,
                    imageBytes = imageBytes,
                    onBack = { navController.popBackStack() },
                    onRetakePhoto = {
                        // Kembali ke Camera, clear stack sampai Home
                        navController.navigate(Routes.CAMERA) {
                            popUpTo(Routes.HOME) { inclusive = false }
                        }
                    },
                    onEditDetails = {
                        navController.navigate(Routes.UBAH_RINCIAN)
                    },
                    onConfirm = {
                        navController.navigate(Routes.SELECT_MEMBER)
                    }
                )
            } else {
                // Safety check: jika data null (misal process death), kembali ke Home
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            }
        }

        // ===== UBAH RINCIAN SCREEN =====
        composable(Routes.UBAH_RINCIAN) {
            val parsedReceipt by sharedViewModel.parsedReceipt.collectAsState()

            if (parsedReceipt != null) {
                UbahRincianScreen(
                    parsedReceipt = parsedReceipt!!,
                    onBack = { navController.popBackStack() },
                    onConfirm = { updatedReceipt ->
                        // Update data di ViewModel
                        sharedViewModel.setParsedReceipt(updatedReceipt)
                        navController.popBackStack()
                    }
                )
            }
        }

        // ===== SELECT MEMBER SCREEN =====
        composable(Routes.SELECT_MEMBER) {
            SelectMemberScreen(
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Routes.DONE)
                }
            )
        }
    }
}
