package com.bagi_bill.bagi_bill

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS doesn't have back button, so this is empty
}
