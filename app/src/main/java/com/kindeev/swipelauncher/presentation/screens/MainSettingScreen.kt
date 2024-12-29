package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.utils.showLauncherSelection
import com.kindeev.swipelauncher.domain.utils.spacer
import com.kindeev.swipelauncher.domain.viewModels.screens.mainSettingsScreen.MainSettingsScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.mainSettingsScreen.MainSettingsScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.elements.EditCircleMenuAction
import com.kindeev.swipelauncher.presentation.ui.elements.settings.ClickableSettingItem
import com.kindeev.swipelauncher.presentation.ui.elements.settings.SwitchSettingItem
import kotlinx.coroutines.launch

@Composable
fun MainSettingsScreen(
    navigateToAllCircleMenus: () -> Unit,
    navigateToHiddenApps: () -> Unit,
    navigateToTutorial: () -> Unit,
    navigateToWallpaper: () -> Unit,
) {
    val settings by LauncherData.settings.observeAsState(emptyList())
    val allApplicationData by LauncherData.allApplicationData.observeAsState(emptyList())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: MainSettingsScreenVM = viewModel(
        factory = MainSettingsScreenVMFactory(context)
    )
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
                    value = settings.getValueOf(
                        SettingNames.OpenLastApp,
                        OpenLastApp::class.java
                    )?.enabled
                        ?: throw IllegalArgumentException("Illegal open app setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    name = SettingNames.OpenLastApp,
                                    value = OpenLastApp(it)
                                )
                            )
                        }
                    }
                )
            }

            spacer()

            // Click On Clock
            item {
                val value = settings.getValueOf(SettingNames.ClickOnClock, ClickOnClock::class.java)
                Column {
                    SwitchSettingItem(
                        text = stringResource(id = R.string.setting_click_on_clock),
                        value = value?.enabled == true,
                        onChangeValue = {
                            scope.launch {
                                LauncherData.insertSetting(
                                    SettingData(
                                        name = SettingNames.ClickOnClock,
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
                                .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.action),
                                fontWeight = FontWeight.Black
                            )
                            EditCircleMenuAction(
                                action = value?.action ?: throw IllegalAccessException(
                                    "Illegal action type"
                                ),
                                getAllApplicationsData = viewModel::getAllApplicationsData,
                                getApplicationInfo = viewModel::getApplicationInfo,
                                getItemImage = viewModel::getItemImage,
                                size = Constants.minScreenLength / 6f,
                                onChangeAction = {
                                    scope.launch {
                                        LauncherData.insertSetting(
                                            SettingData(
                                                name = SettingNames.ClickOnClock,
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
                    value = settings.getValueOf(
                        SettingNames.BlackTextColorOnWallpaper,
                        BlackTextColorOnWallpaper::class.java
                    )?.enabled
                        ?: throw IllegalArgumentException("Illegal black text color on wallpaper setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    name = SettingNames.BlackTextColorOnWallpaper,
                                    value = BlackTextColorOnWallpaper(it)
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
                    value = settings.getValueOf(
                        SettingNames.PickAppActionWithImage,
                        PickAppActionWithImage::class.java
                    )?.enabled
                        ?: throw IllegalArgumentException("Illegal pick app action with image setting value"),
                    onChangeValue = {
                        scope.launch {
                            LauncherData.insertSetting(
                                SettingData(
                                    name = SettingNames.PickAppActionWithImage,
                                    value = PickAppActionWithImage(it)
                                )
                            )
                        }
                    }
                )
            }

            spacer()

            // Wallpaper
            item {
                ClickableSettingItem(
                    text = stringResource(id = R.string.setting_wallpaper),
                    onClick = { navigateToWallpaper() }
                )
            }

            spacer()

            // Tutorial
            item {
                ClickableSettingItem(
                    text = stringResource(id = R.string.setting_tutorial),
                    onClick = { navigateToTutorial() }
                )
            }
        }
    }
}