package com.bagi_bill.bagi_bill.di

import android.content.Context
import com.bagi_bill.bagi_bill.data.local.DatabaseDriverFactory
import com.bagi_bill.bagi_bill.data.local.createDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseDriverFactory(get<Context>()) }
    single { createDatabase(get()) }
}

val appModules = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule,
    utilityModule
)

