package com.kindeev.swipelauncher.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class MainActivityNav {

    @Serializable
    object Launcher: MainActivityNav()

    @Serializable
    object OnBoarding: MainActivityNav()
}