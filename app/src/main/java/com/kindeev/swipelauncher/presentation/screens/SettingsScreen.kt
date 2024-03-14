package com.kindeev.swipelauncher.presentation.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.SettingDataObject.getSettingNameId
import com.kindeev.swipelauncher.domain.DataObject.SettingDataObject.serializableSettingData
import com.kindeev.swipelauncher.domain.DataObject.getAs
import com.kindeev.swipelauncher.data.navigation.Screen
import com.kindeev.swipelauncher.data.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.data.navigation.rememberNavigationState
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickableClock
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.OpenLastApp
import com.kindeev.swipelauncher.presentation.uiElements.ClickableSettingsItem
import com.kindeev.swipelauncher.presentation.uiElements.SwitchAndActionSettingsItem
import com.kindeev.swipelauncher.presentation.uiElements.SwitchSettingsItem
import com.kindeev.swipelauncher.domain.viewModels.MainAppVM

@Composable
fun SettingsScreen(
    mainAppVM: MainAppVM
) {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            SettingsScreenContent(
                mainAppVM = mainAppVM
            ) {
                navigationState.navigateTo(Screen.AllCircleMenusScreenObject.route)
            }
        },
        allCircleMenusScreen = {
            AllCircleMenusScreen(
                mainAppVM = mainAppVM,
                navigateToCircleMenu = { circleMenuId ->
                    navigationState.navigateToEditCircleMenu(circleMenuId)
                }
            )
        },
        editCircleMenuScreen = { circleMenuId ->
            EditCircleMenuScreen(
                mainAppVM = mainAppVM,
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
    mainAppVM: MainAppVM,
    navigateToAllCircleMenus: () -> Unit
) {
    val allSettings by mainAppVM.allSettings.observeAsState(emptyList())
    val context = LocalContext.current
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
                    stringResource(id = getSettingNameId(settingData.setting))
                when (settingData.setting) {
                    ApplicationSetting.OpenLastApp -> {
                        SwitchSettingsItem(
                            text = settingName,
                            checked = settingData.getObjectData().getAs(OpenLastApp::class.java).value,
                            onCheckedChange = {
                                mainAppVM.insertSetting(
                                    settingData.copy(
                                        data = OpenLastApp(
                                            it
                                        ).serializableSettingData()
                                    )
                                )
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
                        SwitchAndActionSettingsItem(
                            text = settingName,
                            enabled = clickableClock.enabled,
                            onCheckedChange = {
                                mainAppVM.insertSetting(
                                    settingData.copy(
                                        data = clickableClock.copy(enabled = it)
                                            .serializableSettingData()
                                    )
                                )
                            },
                            onActionChange = {
                                mainAppVM.insertSetting(
                                    settingData.copy(
                                        data = clickableClock.copy(circleMenuAction = it)
                                            .serializableSettingData()
                                    )
                                )
                            },
                            circleMenuAction = clickableClock.circleMenuAction
                        )
                    }

                    ApplicationSetting.ChangeDefaultLauncher -> {
                        ClickableSettingsItem(
                            text = settingName,
                            onClick = {
                                DataObject.showLauncherSelection(context)
                            }
                        )
                    }
                }
            }
        }
    }
}