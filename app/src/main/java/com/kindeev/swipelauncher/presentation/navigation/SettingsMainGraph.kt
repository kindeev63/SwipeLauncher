package com.kindeev.swipelauncher.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun SettingsMainGraph(
    navHostController: NavHostController,
    mainSettingsScreen: @Composable () -> Unit,
    allCircleMenusScreen: @Composable () -> Unit,
    hiddenAppsScreen: @Composable () -> Unit,
    tutorialScreen: @Composable () -> Unit,
    wallpaperScreen: @Composable () -> Unit,
    editCircleMenuScreen: @Composable (Int?) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = ScreensSettings.MainSettingsScreenObject
    ) {
        composable<ScreensSettings.MainSettingsScreenObject> {
            mainSettingsScreen()
        }
        composable<ScreensSettings.AllCircleMenusScreenObject> {
            allCircleMenusScreen()
        }
        composable<ScreensSettings.EditCircleMenuScreenObject> { navBackStackEntry ->
            val data = navBackStackEntry.toRoute<ScreensSettings.EditCircleMenuScreenObject>()
            editCircleMenuScreen(data.circleMenuId)
        }
        composable<ScreensSettings.HiddenAppsScreenObject> {
            hiddenAppsScreen()
        }
        composable<ScreensSettings.TutorialScreenObject> {
            tutorialScreen()
        }
        composable<ScreensSettings.WallpaperScreenObject> {
            wallpaperScreen()
        }
    }
}