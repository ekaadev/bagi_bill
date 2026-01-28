package com.bagi_bill.bagi_bill.presentation.contacts

import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact

/**
 * Platform-specific contact service.
 * Android uses ContactsContract, others return empty list.
 */
expect class ContactService() {
    /**
     * Request permission to read contacts if needed.
     */
    suspend fun requestPermission(): Boolean
    
    /**
     * Get all contacts from device.
     * Returns empty list if permission denied or not supported.
     */
    suspend fun getContacts(): List<PhoneContact>
    
    /**
     * Search contacts by name.
     */
    suspend fun searchContacts(query: String): List<PhoneContact>
}
