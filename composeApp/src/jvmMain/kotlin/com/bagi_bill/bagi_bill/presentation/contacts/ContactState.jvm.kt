package com.bagi_bill.bagi_bill.presentation.contacts

import androidx.compose.runtime.*

/**
 * JVM/Desktop stub - returns empty state, no contacts on desktop.
 */
@Composable
actual fun rememberContactState(): Pair<ContactState, () -> Unit> {
    val state = remember { ContactState(hasPermission = true) }
    return state to {}
}
