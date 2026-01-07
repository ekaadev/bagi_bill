package com.bagi_bill.bagi_bill.utils

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneWithName

actual fun getCurrentFormattedDateTime(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "dd/MM/yyyy - HH:mm"
    formatter.timeZone = NSTimeZone.timeZoneWithName("Asia/Jakarta")
    return formatter.stringFromDate(NSDate())
}
