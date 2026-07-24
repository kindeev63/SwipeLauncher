package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kindeev.swipelauncher.presentation.navigation.ScreensSettings
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.EditCircleMenuScreenUI
import com.kindeev.swipelauncher.presentation.viewModels.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.diViewModel
import com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.MainSettingsScreenVM

@Composable
fun SettingsScreen() {
    val navigationState = rememberNavigationState()
    NavHost(
        navController = navigationState.navHostController,
        startDestination = ScreensSettings.MainSettingsScreenObject
    ) {
        composable<ScreensSettings.MainSettingsScreenObject> {
            val viewModel: MainSettingsScreenVM = diViewModel()
            MainSettingsScreen(
                viewModel = viewModel,
                navigateToAllCircleMenus = {
                    navigationState.navigateTo(ScreensSettings.AllCircleMenusScreenObject)
                },
                navigateToTutorial = {
                    navigationState.navigateTo(ScreensSettings.TutorialScreenObject)
                }
            )
        }
        composable<ScreensSettings.AllCircleMenusScreenObject> {
            val viewModel: AllCircleMenusScreenVM = diViewModel()
            AllCircleMenusScreen(
                viewModel = viewModel,
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                },
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        }
        composable<ScreensSettings.EditCircleMenuScreenObject> {
            val viewModel: EditCircleMenuScreenVM = diViewModel()
            EditCircleMenuScreenUI(
                viewModel = viewModel,
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        }
        composable<ScreensSettings.TutorialScreenObject> {
            OnboardingScreen(
                onFinish = { navigationState.navHostController.popBackStack() }
            )
        }
    }
}

