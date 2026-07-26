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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.utils.showLauncherSelection
import com.kindeev.swipelauncher.domain.utils.spacer
import com.kindeev.swipelauncher.presentation.viewModels.MainSettingsScreenVM
import com.kindeev.swipelauncher.presentation.ui.elements.EditCircleMenuAction
import com.kindeev.swipelauncher.presentation.ui.elements.settings.ClickableSettingItem
import com.kindeev.swipelauncher.presentation.ui.elements.settings.SwitchSettingItem
import kotlinx.coroutines.launch

@Composable
fun MainSettingsScreen(
    viewModel: MainSettingsScreenVM,
    navigateToAllCircleMenus: () -> Unit,
    navigateToTutorial: () -> Unit,
    openActionDialog: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settingsStateFlowUseCase.settings.collectAsState()
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
                    value = settings.openLastApp,
                    onChangeValue = {
                        scope.launch {
                            viewModel.dataRepository.insertSettings(
                                settings.copy(
                                    openLastApp = it
                                )
                            )
                        }
                    }
                )
            }

            spacer()

            // Click On Clock
            item {
                Column {
                    SwitchSettingItem(
                        text = stringResource(id = R.string.setting_click_on_clock),
                        value = settings.clickOnClock.enable,
                        onChangeValue = {
                            scope.launch {
                                viewModel.dataRepository.insertSettings(
                                    settings.copy(
                                        clickOnClock = settings.clickOnClock.copy(
                                            enable = it
                                        )
                                    )
                                )
                            }
                        },
                        last = !settings.clickOnClock.enable
                    )
                    AnimatedVisibility(
                        visible = settings.clickOnClock.enable,
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
                                action = settings.clickOnClock.action,
                                openActionDialog = openActionDialog,
                                getApplicationInfo = viewModel::getApplicationInfo,
                                getCircleMenuToDraw = viewModel::getCircleMenuToDraw,
                                size = Constants.minScreenLength / 6f,
                                onChangeAction = viewModel::changeClickOnClockAction
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
                    value = settings.blackTextColorOnWallpaper,
                    onChangeValue = {
                        scope.launch {
                            viewModel.dataRepository.insertSettings(
                                settings.copy(
                                    blackTextColorOnWallpaper = it
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
                    value = settings.pickAppActionWithImage,
                    onChangeValue = {
                        scope.launch {
                            viewModel.dataRepository.insertSettings(
                                settings.copy(
                                    pickAppActionWithImage = it
                                )
                            )
                        }
                    }
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