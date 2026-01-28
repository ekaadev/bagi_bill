package com.bagi_bill.bagi_bill.presentation.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.bagi_bill.bagi_bill.presentation.screens.selectmember.PhoneContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "ContactState"

/**
 * Android implementation with runtime permission request.
 */
@Composable
actual fun rememberContactState(): Pair<ContactState, () -> Unit> {
    val context = LocalContext.current
    
    // Check initial permission state
    val initialHasPermission = remember {
        checkContactPermission(context)
    }
    
    var hasPermission by remember { mutableStateOf(initialHasPermission) }
    var isLoading by remember { mutableStateOf(false) }
    var contacts by remember { mutableStateOf<List<PhoneContact>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Permission launcher - must be created before any conditional returns
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permission result: $isGranted")
        hasPermission = isGranted
        if (isGranted) {
            isLoading = true
        }
    }
    
    // Load contacts when permission is granted
    LaunchedEffect(hasPermission) {
        Log.d(TAG, "LaunchedEffect: hasPermission=$hasPermission, contacts.size=${contacts.size}")
        if (hasPermission && contacts.isEmpty()) {
            isLoading = true
            try {
                val loadedContacts = loadContacts(context)
                Log.d(TAG, "Loaded ${loadedContacts.size} contacts")
                contacts = loadedContacts
                isLoading = false
            } catch (e: Exception) {
                Log.e(TAG, "Error loading contacts", e)
                isLoading = false
                error = e.message
            }
        }
    }
    
    // Request permission function
    val requestPermission: () -> Unit = remember(permissionLauncher) {
        {
            Log.d(TAG, "requestPermission called, hasPermission=$hasPermission")
            if (!hasPermission) {
                try {
                    permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    Log.d(TAG, "Permission launcher launched")
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching permission request", e)
                }
            }
        }
    }
    
    val contactState = ContactState(
        contacts = contacts,
        isLoading = isLoading,
        hasPermission = hasPermission,
        error = error
    )
    
    return contactState to requestPermission
}

private fun checkContactPermission(context: Context): Boolean {
    val result = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED
    Log.d(TAG, "checkContactPermission: $result")
    return result
}

private suspend fun loadContacts(context: Context): List<PhoneContact> = withContext(Dispatchers.IO) {
    val contacts = mutableListOf<PhoneContact>()
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )
    
    try {
        context.contentResolver.query(
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
                
                // Skip empty or duplicate numbers
                if (number.isBlank() || number in seenNumbers) continue
                seenNumbers.add(number)
                
                contacts.add(PhoneContact(id = id, name = name, phoneNumber = number))
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error querying contacts", e)
    }
    
    contacts
}
