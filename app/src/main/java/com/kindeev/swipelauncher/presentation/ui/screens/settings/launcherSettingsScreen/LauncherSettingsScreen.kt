package com.kindeev.swipelauncher.presentation.ui.screens.settings.launcherSettingsScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems.CircleMenuActionListItem
import com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems.SwitchItem
import com.kindeev.swipelauncher.presentation.viewModels.settings.launcherSettingsScreen.LauncherSettingsScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherSettingsScreen(
    viewModel: LauncherSettingsScreenVM
) {

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val settingsList by viewModel.settingsList.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LauncherSettingsTopAppBar(
                scrollBehavior = scrollBehavior,
                onBackPressed = viewModel::onBackPressed
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(
                items = settingsList
            ) { item ->
                when (item) {
                    is SettingsListItem.SwitchWithAction -> {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SwitchItem(
                                title = item.title,
                                description = item.description,
                                iconUnicode = item.iconUnicode,
                                chacked = item.checked,
                                onChackedChange = { checked ->
                                    viewModel.switch(item.id, checked)
                                }
                            )
                            item.actionItemData?.let {
                                AnimatedVisibility(
                                    visible = item.checked
                                ) {
                                    CircleMenuActionListItem(
                                        actionItemData = item.actionItemData,
                                        changeAction = viewModel::changeClickOnClockAction,
                                        openActionDialog = viewModel::pickActionForClickOnClock
                                    )
                                }
                            }
                        }
                    }

                    is SettingsListItem.Switch -> {
                        SwitchItem(
                            title = item.title,
                            description = item.description,
                            iconUnicode = item.iconUnicode,
                            chacked = item.checked,
                            onChackedChange = { checked ->
                                viewModel.switch(item.id, checked)
                            }
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}