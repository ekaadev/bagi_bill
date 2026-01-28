package com.bagi_bill.bagi_bill.presentation.contacts

import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact

/**
 * iOS stub - Contact access not yet implemented.
 */
actual class ContactService actual constructor() {
    
    actual suspend fun requestPermission(): Boolean {
        // iOS would use CNContactStore.authorizationStatus
        return false
    }
    
    actual suspend fun getContacts(): List<PhoneContact> {
        // iOS would use CNContactStore.enumerateContacts
        return emptyList()
    }
    
    actual suspend fun searchContacts(query: String): List<PhoneContact> {
        return emptyList()
    }
}
