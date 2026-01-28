package com.bagi_bill.bagi_bill.presentation.contacts

import androidx.compose.runtime.*
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact

/**
 * State holder for contact access.
 */
data class ContactState(
    val contacts: List<PhoneContact> = emptyList(),
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null
)

/**
 * Expect function for platform-specific contact permission handling.
 * Returns a ContactState and a function to request permission.
 */
@Composable
expect fun rememberContactState(): Pair<ContactState, () -> Unit>
