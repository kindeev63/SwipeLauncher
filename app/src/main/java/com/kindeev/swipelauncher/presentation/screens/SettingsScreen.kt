package com.kindeev.swipelauncher.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.settings.SettingTypes
import com.kindeev.swipelauncher.data.navigation.Screen
import com.kindeev.swipelauncher.data.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.data.navigation.rememberNavigationState
import com.kindeev.swipelauncher.presentation.uiElements.ClickableSettingsItem
import com.kindeev.swipelauncher.presentation.uiElements.SwitchSettingsItem
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel

@Composable
fun SettingsScreen(
    mainAppViewModel: MainAppViewModel
) {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            SettingsScreenContent(
                mainAppViewModel = mainAppViewModel
            ) {
                navigationState.navigateTo(Screen.AllCircleMenusScreenObject.route)
            }
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
            EditCircleMenuScreen(
                mainAppViewModel = mainAppViewModel,
                circleMenuId = circleMenuId,
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        }
    )
}

@Composable
fun SettingsScreenContent(
    mainAppViewModel: MainAppViewModel,
    navigateToAllCircleMenus: () -> Unit
) {
    val allSettings by mainAppViewModel.allSettings.observeAsState(emptyList())
    Log.e("test", allSettings.toString())
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(items = allSettings) { settingData ->
                val settingName =
                    stringResource(id = DataObject.getSettingNameId(settingData.setting))
                when (settingData.value.type) {
                    SettingTypes.Switch -> {
                        SwitchSettingsItem(
                            text = settingName,
                            checked = settingData.value.data as Boolean,
                            onCheckedChange = { newValue ->
                                DataObject.executeSwitchSetting(
                                    mainAppViewModel = mainAppViewModel,
                                    settingData = settingData,
                                    data = newValue
                                )
                            }
                        )
                    }

                    SettingTypes.Clickable -> {
                        ClickableSettingsItem(
                            text = settingName,
                            onClick = {
                                DataObject.executeClickableSetting(
                                    applicationSetting = settingData.setting,
                                    openAllCircleMenuScreen = navigateToAllCircleMenus
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}