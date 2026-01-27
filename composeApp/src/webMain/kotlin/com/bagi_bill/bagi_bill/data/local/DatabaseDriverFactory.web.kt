package com.bagi_bill.bagi_bill.data.local

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException(
            "SQLDelight driver is not used on Web platform. WebBillRepository uses LocalStorage instead."
        )
    }
}

