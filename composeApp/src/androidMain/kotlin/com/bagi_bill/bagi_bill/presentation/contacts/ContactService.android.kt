package com.bagi_bill.bagi_bill.presentation.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation using ContactsContract.
 */
actual class ContactService {
    
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context.applicationContext
    }
    
    actual suspend fun requestPermission(): Boolean {
        val ctx = context ?: return false
        return ContextCompat.checkSelfPermission(
            ctx, 
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    actual suspend fun getContacts(): List<PhoneContact> = withContext(Dispatchers.IO) {
        val ctx = context ?: return@withContext emptyList()
        
        if (!requestPermission()) {
            return@withContext emptyList()
        }
        
        val contacts = mutableListOf<PhoneContact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        try {
            ctx.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                
                val seenNumbers = mutableSetOf<String>()
                
                while (cursor.moveToNext()) {
                    val id = cursor.getString(idIndex) ?: continue
                    val name = cursor.getString(nameIndex) ?: continue
                    val number = cursor.getString(numberIndex)?.replace(Regex("[^0-9+]"), "") ?: continue
                    
                    // Skip duplicates
                    if (number in seenNumbers) continue
                    seenNumbers.add(number)
                    
                    contacts.add(PhoneContact(id = id, name = name, phoneNumber = number))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        contacts
    }
    
    actual suspend fun searchContacts(query: String): List<PhoneContact> {
        if (query.isBlank()) return getContacts()
        
        return getContacts().filter { contact ->
            contact.name.contains(query, ignoreCase = true) ||
            contact.phoneNumber.contains(query)
        }
    }
}
