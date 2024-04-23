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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.presentation.navigation.Screen
import com.kindeev.swipelauncher.presentation.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickableClock
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.OpenLastApp
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.serializableSettingData
import com.kindeev.swipelauncher.domain.showLauncherSelection
import com.kindeev.swipelauncher.presentation.ui.elements.ClickableSettingsItem
import com.kindeev.swipelauncher.presentation.ui.elements.SwitchAndActionSettingsItem
import com.kindeev.swipelauncher.presentation.ui.elements.SwitchSettingsItem
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            SettingsScreenContent {
                navigationState.navigateTo(Screen.AllCircleMenusScreenObject.route)
            }
        },
        allCircleMenusScreen = {
            AllCircleMenusScreen(
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        },
        editCircleMenuScreen = { circleMenuId ->
            EditCircleMenuScreen(
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
    navigateToAllCircleMenus: () -> Unit
) {
    val allSettings by LauncherData.allSettings.observeAsState(emptyList())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(items = allSettings.sortedBy { it.setting.name }) { settingData ->
                val settingName =
                    stringResource(id = Constants.settingsNames[settingData.setting] ?: 0)
                when (settingData.setting) {
                    ApplicationSetting.OpenLastApp -> {
                        SwitchSettingsItem(
                            text = settingName,
                            checked = settingData.getObjectData().getAs(OpenLastApp::class.java).value,
                            onCheckedChange = {
                                scope.launch {
                                    LauncherData.insertSetting(
                                        settingData.copy(
                                            data = OpenLastApp(
                                                it
                                            ).serializableSettingData()
                                        )
                                    )
                                }

                            }
                        )
                    }

                    ApplicationSetting.OpenAllCircleMenus -> {
                        ClickableSettingsItem(
                            text = settingName,
                            onClick = navigateToAllCircleMenus
                        )
                    }

                    ApplicationSetting.ClickableClock -> {
                        val clickableClock = settingData.getObjectData().getAs(ClickableClock::class.java)
                        Log.e("test", clickableClock.toString())
                        SwitchAndActionSettingsItem(
                            text = settingName,
                            enabled = clickableClock.enabled,
                            onCheckedChange = {
                                scope.launch {
                                    LauncherData.insertSetting(
                                        settingData.copy(
                                            data = clickableClock.copy(enabled = it)
                                                .serializableSettingData()
                                        )
                                    )
                                }

                            },
                            onActionChange = {
                                scope.launch {
                                    LauncherData.insertSetting(
                                        settingData.copy(
                                            data = clickableClock.copy(circleMenuAction = it)
                                                .serializableSettingData()
                                        )
                                    )
                                }
                            },
                            circleMenuAction = clickableClock.circleMenuAction
                        )
                    }

                    ApplicationSetting.ChangeDefaultLauncher -> {
                        ClickableSettingsItem(
                            text = settingName,
                            onClick = {
                                context.showLauncherSelection()
                            }
                        )
                    }
                }
            }
        }
    }
}