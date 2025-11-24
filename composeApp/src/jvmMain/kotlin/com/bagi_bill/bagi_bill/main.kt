package com.bagi_bill.bagi_bill

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "bagi_bill",
    ) {
        App()
    }
}