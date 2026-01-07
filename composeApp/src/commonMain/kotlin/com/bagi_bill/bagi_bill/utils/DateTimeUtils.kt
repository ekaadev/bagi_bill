package com.bagi_bill.bagi_bill.utils

/**
 * Platform-agnostic date time utility
 * Returns formatted date string in WIB timezone (UTC+7)
 * Format: "DD/MM/YYYY - HH:mm"
 */
expect fun getCurrentFormattedDateTime(): String
