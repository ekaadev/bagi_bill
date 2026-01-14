package com.bagi_bill.bagi_bill

interface Platform {
    val name: String
    val isWeb: Boolean get() = false
    val isMobile: Boolean get() = !isWeb
}

expect fun getPlatform(): Platform