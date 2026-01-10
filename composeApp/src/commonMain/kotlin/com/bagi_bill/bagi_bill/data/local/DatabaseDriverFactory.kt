package com.bagi_bill.bagi_bill.data.local

import app.cash.sqldelight.db.SqlDriver
import com.bagi_bill.bagi_bill.database.AppDatabase

/**
 * Expect function to create platform-specific database driver
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/**
 * Create an instance of AppDatabase with the platform-specific driver
 */
fun createDatabase(driverFactory: DatabaseDriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    return AppDatabase(driver)
}
