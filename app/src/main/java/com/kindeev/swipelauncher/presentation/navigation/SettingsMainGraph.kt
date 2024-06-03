package com.kindeev.swipelauncher.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SettingsMainGraph(
    navHostController: NavHostController,
    mainSettingsScreen: @Composable () -> Unit,
    allCircleMenusScreen: @Composable () -> Unit,
    hiddenAppsScreen: @Composable () -> Unit,
    tutorialScreen: @Composable () -> Unit,
    editCircleMenuScreen: @Composable (Int?) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = ScreensSettings.MainSettingsScreenObject.route
    ) {
        composable(ScreensSettings.MainSettingsScreenObject.route) {
            mainSettingsScreen()
        }
        composable(ScreensSettings.AllCircleMenusScreenObject.route) {
            allCircleMenusScreen()
        }
        composable(ScreensSettings.EditCircleMenuScreenObject.route) {
            val circleMenuId = it.arguments?.getString("circleMenuId").toString().toIntOrNull()
            editCircleMenuScreen(circleMenuId)
        }
        composable(ScreensSettings.HiddenAppsScreenObject.route) {
            hiddenAppsScreen()
        }
        composable(ScreensSettings.TutorialScreenObject.route) {
            tutorialScreen()
        }
    }
}