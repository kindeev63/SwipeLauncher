package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.runtime.Composable
import com.kindeev.swipelauncher.data.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.data.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel

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