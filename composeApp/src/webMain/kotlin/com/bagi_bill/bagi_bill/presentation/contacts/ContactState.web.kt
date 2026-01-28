package com.bagi_bill.bagi_bill.presentation.contacts

import androidx.compose.runtime.*

/**
 * Web stub - returns empty state.
 */
@Composable
actual fun rememberContactState(): Pair<ContactState, () -> Unit> {
    val state = remember { ContactState(hasPermission = true) }
    return state to {}
}
