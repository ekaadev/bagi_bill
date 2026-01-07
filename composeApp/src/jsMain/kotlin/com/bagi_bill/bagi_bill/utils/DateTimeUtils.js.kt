package com.bagi_bill.bagi_bill.utils

import kotlin.js.Date

actual fun getCurrentFormattedDateTime(): String {
    val now = Date()
    // Get UTC time and add 7 hours for WIB
    val utcTime = now.getTime()
    val wibOffset = 7 * 60 * 60 * 1000.0
    val wibDate = Date(utcTime + wibOffset)
    
    val day = wibDate.getUTCDate().toString().padStart(2, '0')
    val month = (wibDate.getUTCMonth() + 1).toString().padStart(2, '0')
    val year = wibDate.getUTCFullYear().toString()
    val hour = wibDate.getUTCHours().toString().padStart(2, '0')
    val minute = wibDate.getUTCMinutes().toString().padStart(2, '0')
    
    return "$day/$month/$year - $hour:$minute"
}
