package com.bagi_bill.bagi_bill.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bagi_bill.bagi_bill.presentation.camera.CameraScreen
import com.bagi_bill.bagi_bill.presentation.screens.HistoryScreen
import com.bagi_bill.bagi_bill.presentation.screens.HomeScreen

@Composable
fun NavigationGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = {
                    navController.navigate(Route.Camera)
                },
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                }
            )
        }

        composable<Route.History> {
            HistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Camera> {
            CameraScreen(
                onExit = {
                    navController.popBackStack()
                },
                onPhotoConfirmed = { photoBytes ->
                    // TODO: Navigate to OCR result screen with photoBytes
                    // For now, just go back to home
                    navController.popBackStack()
                }
            )
        }
    }
}
