package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.runtime.Composable
import com.kindeev.swipelauncher.presentation.navigation.ScreensSettings
import com.kindeev.swipelauncher.presentation.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState

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
                },
                navigateToWallpaper = {
                    navigationState.navigateTo(ScreensSettings.WallpaperScreenObject)
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
            EditCircleMenuScreen(
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
        },
        wallpaperScreen = {
            WallpaperScreen(
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        }
    )
}

