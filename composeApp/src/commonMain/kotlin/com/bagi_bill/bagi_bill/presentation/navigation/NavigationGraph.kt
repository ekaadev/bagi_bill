package com.bagi_bill.bagi_bill.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bagi_bill.bagi_bill.presentation.screens.HistoryScreen
import com.bagi_bill.bagi_bill.presentation.screens.HomeScreen

// Setup Navigation Graph untuk mengatur navigasi antar screen
@Composable
fun NavigationGraph(
    navController: NavHostController
) {
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

