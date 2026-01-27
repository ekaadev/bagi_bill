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

// Setup Navigation Graph untuk mengatur navigasi antar screen
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
        // Home Screen
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = {
                    // TODO: Implementasi navigasi ke Camera Screen
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

        // History Screen
        composable<Route.History> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Camera Screen (untuk nanti)
        composable<Route.Camera> {
            // TODO: Implementasi Camera Screen
        }
    }
}

