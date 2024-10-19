package com.kindeev.swipelauncher.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class ScreensOnBoarding {
    @Serializable
    object OnBoardingScreenObject: ScreensOnBoarding()

    @Serializable
    object MainScreenObject: ScreensOnBoarding()
}