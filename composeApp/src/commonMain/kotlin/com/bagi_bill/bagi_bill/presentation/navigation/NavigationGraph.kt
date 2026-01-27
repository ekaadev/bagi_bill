package com.bagi_bill.bagi_bill.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bagi_bill.bagi_bill.presentation.screens.*
import com.bagi_bill.bagi_bill.presentation.viewmodel.SplitBillViewModel
import org.koin.compose.viewmodel.koinViewModel

// Setup Navigation Graph untuk mengatur navigasi antar screen
@Composable
fun NavigationGraph(
    navController: NavHostController
) {
    // Shared ViewModel untuk flow Split Bill - menggunakan Koin
    // koinViewModel() akan memberikan instance yang sama selama dalam scope yang sama
    val splitBillViewModel: SplitBillViewModel = koinViewModel()

    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        // Home Screen
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = {
                    // Reset state sebelum memulai flow baru (untuk scan)
                    splitBillViewModel.reset()
                    navController.navigate(Route.ItemDetail)
                },
                onNavigateToManual = {
                    // Reset state sebelum memulai flow baru (untuk manual input)
                    splitBillViewModel.reset()
                    navController.navigate(Route.ItemDetail)
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                }
            )
        }

        // History Screen
        composable<Route.History> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Item Detail Screen - Setelah OCR/Manual input
        composable<Route.ItemDetail> {
            ItemDetailScreen(
                viewModel = splitBillViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSelectMember = {
                    navController.navigate(Route.SelectMember)
                }
            )
        }

        // Select Member Screen - Pilih teman untuk split
        composable<Route.SelectMember> {
            SelectMemberScreen(
                viewModel = splitBillViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSplitAssignment = {
                    navController.navigate(Route.SplitAssignment)
                }
            )
        }

        // Split Assignment Screen - Pembagian item ke member
        composable<Route.SplitAssignment> {
            SplitAssignmentScreen(
                viewModel = splitBillViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToResult = {
                    navController.navigate(Route.Result)
                }
            )
        }

        // Result Screen - Hasil akhir split bill
        composable<Route.Result> {
            ResultScreen(
                viewModel = splitBillViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToHome = {
                    // Clear backstack dan kembali ke Home
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Home) { inclusive = true }
                    }
                }
            )
        }

        // Camera Screen (untuk nanti)
        composable<Route.Camera> {
            // TODO: Implementasi Camera Screen
        }
    }
}
