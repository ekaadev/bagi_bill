package com.bagi_bill.bagi_bill.data.local

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException(
            "SQLDelight database is not supported on Web platform. Use WebBillRepository instead."
        )
    }
}


