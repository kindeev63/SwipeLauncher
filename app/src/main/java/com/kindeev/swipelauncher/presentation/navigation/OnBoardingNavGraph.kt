package com.kindeev.swipelauncher.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun OnBoardingNavGraph(
    navHostController: NavHostController,
    mainScreen: @Composable () -> Unit,
    onboardingScreen: @Composable () -> Unit,
    startDestination: ScreensOnBoarding
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        composable<ScreensOnBoarding.MainScreenObject> {
            mainScreen()
        }
        composable<ScreensOnBoarding.OnBoardingScreenObject> {
            onboardingScreen()
        }
    }
}