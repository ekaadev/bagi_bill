package com.bagi_bill.bagi_bill

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.bagi_bill.bagi_bill.data.util.DatabaseSeeder
import com.bagi_bill.bagi_bill.presentation.navigation.NavigationGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val seeder: DatabaseSeeder = koinInject()
    val navController = rememberNavController()

    // Seed initial data hanya sekali saat app pertama kali dijalankan
    LaunchedEffect(Unit) {
        seeder.seedInitialData()
    }

    MaterialTheme {
        NavigationGraph(navController = navController)
    }
}

