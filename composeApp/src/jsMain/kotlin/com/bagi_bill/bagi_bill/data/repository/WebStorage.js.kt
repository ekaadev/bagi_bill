package com.bagi_bill.bagi_bill.data.repository

import kotlinx.browser.localStorage

internal actual object WebStorage {
    actual fun getItem(key: String): String? {
        return localStorage.getItem(key)
    }

    actual fun setItem(key: String, value: String) {
        localStorage.setItem(key, value)
    }
}

