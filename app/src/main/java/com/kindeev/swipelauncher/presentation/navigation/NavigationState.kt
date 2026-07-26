package com.kindeev.swipelauncher.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {
    fun navigateTo(screen: Any) {
        navHostController.navigate(screen) {
            launchSingleTop = true
        }
    }

    fun navigateToEditCircleMenu(circleMenuId: Int?) {
        navHostController.navigate(ScreensSettings.EditCircleMenuScreenObject(circleMenuId)) {
            launchSingleTop = true
        }
    }
}

@Composable
fun rememberNavigationState(
    navHostController: NavHostController = rememberNavController()
) = remember {
    NavigationState(navHostController)
}