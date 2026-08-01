package com.kindeev.swipelauncher.presentation.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.kindeev.swipelauncher.presentation.navigation.ScreensSettings
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.EditCircleMenuScreenUI
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.screens.OnboardingScreen
import com.kindeev.swipelauncher.presentation.viewModels.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.imageDialog.ImageDialogVM
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
        composable<ScreensSettings.MainSettingsScreenObject> { entry ->
            val viewModel: MainSettingsScreenVM = diViewModel(entry.savedStateHandle)
            MainSettingsScreen(
                viewModel = viewModel,
                navigateToAllCircleMenus = {
                    navigationState.navigateTo(ScreensSettings.AllCircleMenusScreenObject)
                },
                navigateToTutorial = {
                    navigationState.navigateTo(ScreensSettings.TutorialScreenObject)
                },
                openActionDialog = {
                    navigationState.navigateTo(ScreensSettings.ActionDialog)
                }
            )
        }
        composable<ScreensSettings.AllCircleMenusScreenObject> { entry ->
            val viewModel: AllCircleMenusScreenVM = diViewModel(entry.savedStateHandle)
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
        composable<ScreensSettings.EditCircleMenuScreenObject> { entry ->
            val viewModel: EditCircleMenuScreenVM = diViewModel(entry.savedStateHandle)
            EditCircleMenuScreenUI(
                viewModel = viewModel,
                openActionDialog = {
                    navigationState.navigateTo(ScreensSettings.ActionDialog)
                },
                openImageDialog = {
                    navigationState.navigateTo(ScreensSettings.ImageDialog)
                },
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
        dialog<ScreensSettings.ActionDialog> { entry ->
            val viewModel: ActionDialogVM = diViewModel(entry.savedStateHandle)
            ActionDialog(
                viewModel = viewModel,
                onDismissRequest = {
                    navigationState.navHostController.popBackStack()
                },
                onPick = { action ->
                    val savedStateHandle =
                        navigationState.navHostController.previousBackStackEntry?.savedStateHandle
                    if (savedStateHandle != null)
                        savedStateHandle["pickedAction"] = action
                }
            )
        }
        dialog<ScreensSettings.ImageDialog> { entry ->
            val viewModel: ImageDialogVM = diViewModel(entry.savedStateHandle)
            ImageDialog(
                viewModel = viewModel,
                onDismissRequest = {
                    navigationState.navHostController.popBackStack()
                },
                onPick = { image ->
                    val savedStateHandle =
                        navigationState.navHostController.previousBackStackEntry?.savedStateHandle
                    if (savedStateHandle != null)
                        savedStateHandle["pickedImage"] = image
                },
            )

        }
    }
}

