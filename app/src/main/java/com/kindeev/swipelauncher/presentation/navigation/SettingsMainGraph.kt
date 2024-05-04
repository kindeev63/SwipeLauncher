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
    editCircleMenuScreen: @Composable (Int?) -> Unit
) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.MainSettingsScreenObject.route
    ) {
        composable(Screen.MainSettingsScreenObject.route) {
            mainSettingsScreen()
        }
        composable(Screen.AllCircleMenusScreenObject.route) {
            allCircleMenusScreen()
        }
        composable(Screen.EditCircleMenuScreenObject.route) {
            val circleMenuId = it.arguments?.getString("circleMenuId").toString().toIntOrNull()
            editCircleMenuScreen(circleMenuId)
        }
    }
}