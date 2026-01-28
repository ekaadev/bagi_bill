package com.bagi_bill.bagi_bill.presentation.contacts

import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact

/**
 * Web stub - Contact access not supported in browser.
 */
actual class ContactService actual constructor() {
    
    actual suspend fun requestPermission(): Boolean {
        return false
    }
    
    actual suspend fun getContacts(): List<PhoneContact> {
        return emptyList()
    }
    
    actual suspend fun searchContacts(query: String): List<PhoneContact> {
        return emptyList()
    }
}
