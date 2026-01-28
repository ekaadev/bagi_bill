package com.bagi_bill.bagi_bill

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bagi_bill.bagi_bill.di.appModules
import org.koin.core.context.startKoin

fun main() {
    // Initialize Koin before starting the application
    startKoin {
        modules(appModules)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "bagi_bill",
        ) {
            App()
        }
    }
}