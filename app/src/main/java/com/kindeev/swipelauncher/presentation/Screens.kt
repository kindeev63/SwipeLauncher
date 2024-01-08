package com.kindeev.swipelauncher.presentation

import androidx.compose.runtime.Composable
import com.kindeev.swipelauncher.data.navigation.MainNavGraph
import com.kindeev.swipelauncher.data.navigation.Screen
import com.kindeev.swipelauncher.data.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.data.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.uiElements.AllCircleMenusScreen
import com.kindeev.swipelauncher.presentation.uiElements.EditCircleMenuScreen
import com.kindeev.swipelauncher.presentation.uiElements.LauncherScreen

@Composable
fun MainScreen(
    mainAppViewModel: MainAppViewModel
) {
    val navigationState = rememberNavigationState()
    MainNavGraph(
        navHostController = navigationState.navHostController,
        swipeScreen = {
            LauncherScreen(
                mainAppViewModel = mainAppViewModel,
                navigateToSettings = {
                    navigationState.navigateTo(Screen.SettingsScreen.route)
                }
            )
        },
        settingsScreen = {
            SettingsScreen(mainAppViewModel = mainAppViewModel)
        }
    )
}

@Composable
fun SettingsScreen(
    mainAppViewModel: MainAppViewModel
) {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            AllCircleMenusScreen(
                mainAppViewModel = mainAppViewModel,
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        },
        allCircleMenusScreen = {
            AllCircleMenusScreen(
                mainAppViewModel = mainAppViewModel,
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        },
        editCircleMenuScreen = { circleMenuId ->
            if (circleMenuId == null) {
                AllCircleMenusScreen(
                    mainAppViewModel = mainAppViewModel,
                    navigateToCircleMenu = { circleMenuId ->
                        navigationState.navigateToEditCircleMenu(circleMenuId)
                    }
                )
            } else {
                EditCircleMenuScreen(
                    mainAppViewModel = mainAppViewModel,
                    circleMenuId = circleMenuId,
                    onBackPressed = {
                        navigationState.navHostController.popBackStack()
                    }
                )
            }

        }
    )
}