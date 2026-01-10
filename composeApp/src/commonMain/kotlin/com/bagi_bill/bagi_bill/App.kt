package com.bagi_bill.bagi_bill

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.bagi_bill.bagi_bill.data.util.DatabaseSeeder
import com.bagi_bill.bagi_bill.presentation.screens.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    val seeder: DatabaseSeeder = koinInject()

    // Seed initial data hanya sekali saat app pertama kali dijalankan
    LaunchedEffect(Unit) {
        seeder.seedInitialData()
    }

    MaterialTheme {
        HomeScreen()
    }
}

