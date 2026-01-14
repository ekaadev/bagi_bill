package com.bagi_bill.bagi_bill

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.bagi_bill.bagi_bill.di.appModules
import kotlinx.browser.document
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(appModules)
    }

    ComposeViewport(document.body!!) {
        App()
    }
}

