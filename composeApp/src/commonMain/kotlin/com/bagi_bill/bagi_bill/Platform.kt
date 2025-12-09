package com.bagi_bill.bagi_bill

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform