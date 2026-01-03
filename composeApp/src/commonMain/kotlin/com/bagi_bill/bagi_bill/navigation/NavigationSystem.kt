package com.bagi_bill.bagi_bill.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bagi_bill.bagi_bill.*
import com.bagi_bill.bagi_bill.ui.screens.PembagianBillScreen
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

    private val _splitBillData = MutableStateFlow<SplitBillData?>(null)
    val splitBillData = _splitBillData.asStateFlow()

    fun setParsedReceipt(receipt: ParsedReceipt) {
        _parsedReceipt.value = receipt
    }

    fun setImageBytes(bytes: ByteArray) {
        _imageBytes.value = bytes
    }

    fun setSplitBillData(data: SplitBillData) {
        _splitBillData.value = data
    }

    fun clearData() {
        _parsedReceipt.value = null
        _imageBytes.value = null
        _splitBillData.value = null
    }
}

// ==================== ROUTES ====================
object Routes {
    const val HOME = "home"
    const val CAMERA = "camera"
    const val RINCIAN = "rincian"
    const val UBAH_RINCIAN = "ubah_rincian"
    const val SELECT_MEMBER = "select_member"
    const val SPLIT_BILL = "split_bill"
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
                    // Logic: Jika previousBackStackEntry ada, popBackStack.
                    // Jika tidak ada (misal startDestination), exit app.
                    if (!navController.popBackStack()) {
                        onExitApp()
                    }
                },
                onPhotoConfirmed = { bytes ->
                    scope.launch {
                        try {
                            val extractedText = textService.recognizeText(bytes)
                            val parsedReceipt = parserUtil(extractedText)

                            // Simpan data di ViewModel
                            sharedViewModel.setImageBytes(bytes)
                            sharedViewModel.setParsedReceipt(parsedReceipt)

                            // Cek apakah kita datang dari Rincian (Retake Photo)
                            // Jika previousBackStackEntry adalah RINCIAN, kita pop back ke sana
                            // Tapi karena kita mau update data, lebih aman navigate ke RINCIAN
                            // dengan popUpTo CAMERA inclusive = true agar stack bersih.
                            
                            // Logic User:
                            // 1. Home -> Camera -> Rincian (Normal Flow)
                            // 2. Rincian -> Camera (Retake) -> Rincian (Update)
                            
                            // Saat ini implementasi di bawah ini akan selalu membuat Rincian baru
                            // dan menghapus Camera dari stack. Ini sesuai dengan flow 1.
                            // Untuk flow 2, karena kita navigate ke CAMERA dari RINCIAN tanpa pop Rincian,
                            // maka stacknya: Home -> Rincian -> Camera.
                            // Jika confirm foto baru, kita mau: Home -> Rincian (Updated).
                            // Jadi kita harus pop Camera dan kembali ke Rincian.
                            
                            val previousRoute = navController.previousBackStackEntry?.destination?.route
                            
                            if (previousRoute == Routes.RINCIAN) {
                                // Kasus Retake Photo: Kembali ke Rincian yang sudah ada di stack
                                // Data di ViewModel sudah diupdate, jadi Rincian akan recompose dengan data baru
                                navController.popBackStack()
                            } else {
                                // Kasus Normal: Home -> Camera -> Rincian
                                // Navigate ke Rincian, hapus Camera dari stack
                                navController.navigate(Routes.RINCIAN) {
                                    popUpTo(Routes.CAMERA) { inclusive = true }
                                }
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
                    onBack = { 
                        // Logic: Back dari Rincian selalu ke Home (sesuai request user)
                        // "jika di halaman rincian pencet icon back atau hapus jendela aktif maka dia kembali ke home"
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onRetakePhoto = {
                        // Logic: Masuk ke Camera untuk foto ulang.
                        // Stack saat ini: Home -> Rincian
                        // Navigate ke Camera tanpa pop Rincian agar bisa kembali jika cancel
                        navController.navigate(Routes.CAMERA)
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
            val existingData by sharedViewModel.splitBillData.collectAsState()
            SelectMemberScreen(
                initialData = existingData,
                onBack = {
                    // Kembali ke Rincian screen
                    navController.popBackStack()
                },
                onNavigateToSplitBill = { splitBillData ->
                    sharedViewModel.setSplitBillData(splitBillData)

                    // Debug log untuk melihat data yang dikirim
                    println("=== Data Member Tersimpan ===")
                    println("Payer: ${splitBillData.payer.name} - ${splitBillData.payer.wallet} - ${splitBillData.payer.phoneNumber}")
                    println("Total Members: ${splitBillData.totalMembers}")
                    println("Members dengan payment info: ${splitBillData.membersWithPaymentInfo}")
                    splitBillData.members.forEach { member ->
                        println("  - ${member.name}: ${member.wallet ?: "No wallet"} - ${member.phoneNumber ?: "No phone"}")
                    }

                    // Navigasi Ke Split Bill Screen
                    navController.navigate(Routes.SPLIT_BILL)

                }
            )
        }

        // ===== SPLIT BILL SCREEN =====
        composable(Routes.SPLIT_BILL) {
            // Ambil data dari ViewModel
            val splitBillData by sharedViewModel.splitBillData.collectAsState()
            val parsedReceipt by sharedViewModel.parsedReceipt.collectAsState()

            // Pastikan data ada sebelum render
            if (splitBillData != null && parsedReceipt != null) {
                PembagianBillScreen(
                    splitBillData = splitBillData!!,
                    parsedReceipt = parsedReceipt!!,
                    onBackClick = { navController.popBackStack() },
                    onEditMembers = { navController.popBackStack() },
                    onSendClick = {
                        // TODO: Implement logic kirim ke API/WhatsApp di sini
                        println("Kirim data pembagian bill...")
                        // navController.navigate(Routes.DONE)
                    }
                )
            } else {
                // Fallback jika data hilang (misal process death), balik ke Home
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                }
            }
        }
    }
}
