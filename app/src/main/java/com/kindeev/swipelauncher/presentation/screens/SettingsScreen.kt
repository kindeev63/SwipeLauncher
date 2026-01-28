package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.runtime.Composable
import com.kindeev.swipelauncher.presentation.navigation.ScreensSettings
import com.kindeev.swipelauncher.presentation.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.EditCircleMenuScreenUI

@Composable
fun SettingsScreen() {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            MainSettingsScreen(
                navigateToAllCircleMenus = {
                    navigationState.navigateTo(ScreensSettings.AllCircleMenusScreenObject)
                },
                navigateToHiddenApps = {
                    navigationState.navigateTo(ScreensSettings.HiddenAppsScreenObject)
                },
                navigateToTutorial = {
                    navigationState.navigateTo(ScreensSettings.TutorialScreenObject)
                }
            )
        },
        allCircleMenusScreen = {
            AllCircleMenusScreen(
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                },
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        },
        editCircleMenuScreen = { circleMenuId ->
            EditCircleMenuScreenUI(
                circleMenuId = circleMenuId,
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        },
        hiddenAppsScreen = {
            HiddenAppsScreen(
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        },
        tutorialScreen = {
            OnboardingScreen(
                onFinish = { navigationState.navHostController.popBackStack() }
            )
        }
    )
}

