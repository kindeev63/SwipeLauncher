package com.kindeev.swipelauncher.presentation.navigation

sealed class ScreensOnBoarding(
    val route: String
) {
    object OnBoardingScreenObject: ScreensOnBoarding(ROUTE_ONBOARDING_SCREEN)

    object MainScreenObject: ScreensOnBoarding(ROUTE_MAIN_SCREEN)

    private companion object {
        const val ROUTE_ONBOARDING_SCREEN = "onboarding_screen"
        const val ROUTE_MAIN_SCREEN = "main_screen"
    }
}