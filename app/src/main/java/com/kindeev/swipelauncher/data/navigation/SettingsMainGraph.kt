package com.kindeev.swipelauncher.data.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SettingsMainGraph(
    navHostController: NavHostController,
    mainSettingsScreen: @Composable () -> Unit,
    allCircleMenusScreen: @Composable () -> Unit,
    editCircleMenuScreen: @Composable (Int?) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainSettingsScreen.route
    ) {
        composable(Screen.MainSettingsScreen.route) {
            mainSettingsScreen()
        }
        composable(Screen.AllCircleMenusScreen.route) {
            allCircleMenusScreen()
        }
        composable(Screen.EditCircleMenuScreen.route) {
            val circleMenuId = it.arguments?.get("circleMenuId").toString().toIntOrNull()
            editCircleMenuScreen(circleMenuId)
        }
    }
}