package com.bagi_bill.bagi_bill.di

import com.bagi_bill.bagi_bill.data.repository.BillRepository
import com.bagi_bill.bagi_bill.data.repository.BillRepositoryImpl
import com.bagi_bill.bagi_bill.data.util.DatabaseSeeder
import com.bagi_bill.bagi_bill.presentation.viewmodel.DraftViewModel
import com.bagi_bill.bagi_bill.presentation.viewmodel.HistoryViewModel
import com.bagi_bill.bagi_bill.presentation.viewmodel.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val repositoryModule = module {
    single<BillRepository> { BillRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::DraftViewModel)
}

val utilityModule = module {
    single { DatabaseSeeder(get()) }
}

