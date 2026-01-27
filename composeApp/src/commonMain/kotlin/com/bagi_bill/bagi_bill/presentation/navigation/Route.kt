package com.bagi_bill.bagi_bill.presentation.navigation

import kotlinx.serialization.Serializable

// Route untuk navigasi antar screen
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object History : Route

    @Serializable
    data object Camera : Route

    @Serializable
    data object ItemDetail : Route

    @Serializable
    data object SelectMember : Route

    @Serializable
    data object SplitAssignment : Route

    @Serializable
    data object Result : Route
}

