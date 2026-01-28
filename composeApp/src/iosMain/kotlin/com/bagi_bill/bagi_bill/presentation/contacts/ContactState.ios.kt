package com.bagi_bill.bagi_bill.presentation.contacts

import androidx.compose.runtime.*

/**
 * iOS stub - returns empty state, no permission needed.
 */
@Composable
actual fun rememberContactState(): Pair<ContactState, () -> Unit> {
    val state = remember { ContactState(hasPermission = true) }
    return state to {}
}
