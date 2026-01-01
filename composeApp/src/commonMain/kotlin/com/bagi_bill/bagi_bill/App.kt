package com.bagi_bill.bagi_bill

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.bagi_bill.bagi_bill.navigation.AppNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        AppNavigator()
    }
}