package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.presentation.navigation.ScreensSettings
import com.kindeev.swipelauncher.presentation.navigation.SettingsMainGraph
import com.kindeev.swipelauncher.presentation.navigation.rememberNavigationState
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickOnClock
import com.kindeev.swipelauncher.domain.getActionType
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.showLauncherSelection
import com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction.ActionDataByType
import com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction.ActionTypeItem
import com.kindeev.swipelauncher.presentation.ui.elements.settings.ClickableSettingItem
import com.kindeev.swipelauncher.presentation.ui.elements.settings.SwitchSettingItem
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val navigationState = rememberNavigationState()
    SettingsMainGraph(
        navHostController = navigationState.navHostController,
        mainSettingsScreen = {
            SettingsScreenContent(
                navigateToAllCircleMenus = {
                    navigationState.navigateTo(ScreensSettings.AllCircleMenusScreenObject.route)
                },
                navigateToHiddenApps = {
                    navigationState.navigateTo(ScreensSettings.HiddenAppsScreenObject.route)
                }
            )
        },
        allCircleMenusScreen = {
            AllCircleMenusScreen(
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                },
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
        },
        hiddenAppsScreen = {
            HiddenAppsScreen(
                onBackPressed = {
                    navigationState.navHostController.popBackStack()
                }
            )
        }
    )
}

@Composable
fun SettingsScreenContent(
    navigateToAllCircleMenus: () -> Unit,
    navigateToHiddenApps: () -> Unit
) {
    val settings by LauncherData.settings.observeAsState(emptyList())
    val allApplicationData by LauncherData.allApplicationData.observeAsState(emptyList())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
            .statusBarsPadding()
            .padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            // All circle menus
            item {
                ClickableSettingItem(
                    text = stringResource(id = R.string.setting_all_circle_menus),
                    onClick = navigateToAllCircleMenus
                )
            }

            spacer()

            // Hidden apps
            if (allApplicationData.any { it.hidden }) {
                item {
                    ClickableSettingItem(
                        text = stringResource(id = R.string.setting_hidden_apps),
                        onClick = navigateToHiddenApps
                    )
                }
                spacer()
            }

            // Change default launcher
            item {
                ClickableSettingItem(
                    text = stringResource(id = R.string.setting_change_default_launcher),
                    onClick = { context.showLauncherSelection() }
                )
            }

            spacer()

            // Open Last App
            item {
                SwitchSettingItem(
                    text = stringResource(id = R.string.setting_open_last_app),
                    value = settings.getValueOf(Setting.OpenLastApp, Boolean::class.java)
                        ?: throw IllegalArgumentException("Illegal open app setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    setting = Setting.OpenLastApp,
                                    value = it
                                )
                            )
                        }
                    }
                )
            }

            spacer()

            // Click On Clock
            item {
                val value = settings.getValueOf(Setting.ClickOnClock, ClickOnClock::class.java)
                Column {
                    SwitchSettingItem(
                        text = stringResource(id = R.string.setting_click_on_clock),
                        value = value?.enabled == true,
                        onChangeValue = {
                            scope.launch {
                                LauncherData.insertSetting(
                                    SettingData(
                                        setting = Setting.ClickOnClock,
                                        value = ClickOnClock(it, value?.action)
                                    )
                                )
                            }
                        },
                        last = value?.enabled == false
                    )
                    AnimatedVisibility(
                        visible = value?.enabled == true,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            ActionTypeItem(
                                actionType = value?.action?.type?.getActionType()
                                    ?: throw IllegalAccessException(
                                        "Illegal action type"
                                    ),
                                onChangeAction = {
                                    scope.launch {
                                        LauncherData.insertSetting(
                                            SettingData(
                                                setting = Setting.ClickOnClock,
                                                value = ClickOnClock(true, it)
                                            )
                                        )
                                    }
                                }
                            )
                            ActionDataByType(
                                action = value.action,
                                textColor = MaterialTheme.colorScheme.onBackground,
                                onChangeAction = {
                                    scope.launch {
                                        LauncherData.insertSetting(
                                            SettingData(
                                                setting = Setting.ClickOnClock,
                                                value = ClickOnClock(true, it)
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            spacer()

            // Black text color on wallpaper
            item {
                SwitchSettingItem(
                    text = stringResource(id = R.string.setting_black_text_color_on_wallpaper),
                    value = settings.getValueOf(Setting.BlackTextColorOnWallpaper, Boolean::class.java)
                        ?: throw IllegalArgumentException("Illegal black text color on wallpaper setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    setting = Setting.BlackTextColorOnWallpaper,
                                    value = it
                                )
                            )
                        }
                    }
                )
            }

            spacer()

            // Pick open app action with app image
            item {
                SwitchSettingItem(
                    text = stringResource(id = R.string.setting_pick_app_action_with_image),
                    value = settings.getValueOf(Setting.PickAppActionWithImage, Boolean::class.java)
                        ?: throw IllegalArgumentException("Illegal pick app action with image setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    setting = Setting.PickAppActionWithImage,
                                    value = it
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun LazyListScope.spacer() {
    item { Spacer(modifier = Modifier.height(5.dp)) }
}