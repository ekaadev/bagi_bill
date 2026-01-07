package com.bagi_bill.bagi_bill.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

actual fun getCurrentFormattedDateTime(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    return sdf.format(Date())
}
