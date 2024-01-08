package com.kindeev.swipelauncher.data.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavGraph(
    navHostController: NavHostController,
    swipeScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.SwipeScreen.route,
    ) {
        composable(Screen.SwipeScreen.route) {
            swipeScreen()
        }
        composable(Screen.SettingsScreen.route) {
            settingsScreen()
        }
    }
}