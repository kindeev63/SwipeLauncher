package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.presentation.uiElements.AppItem
import com.kindeev.swipelauncher.presentation.uiElements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel

@Composable
fun PickActionDialog(
    onDismissRequest: () -> Unit,
    mainAppViewModel: MainAppViewModel,
    picked: CircleMenuAction,
    onPick: (CircleMenuAction) -> Unit
) {
    var selectedAction by remember {
        mutableStateOf(picked)
    }
    var selectedTab by remember {
        mutableStateOf(picked.type)
    }
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                DialogTabs(selectedTab = selectedTab, onSelectTab = { selectedTab = it })
                when (selectedTab) {
                    CircleMenuActionTypes.OpenApp -> {
                        PickAppAction(
                            picked =
                            if (selectedAction.type == CircleMenuActionTypes.OpenApp) {
                                selectedAction.data as OpenApp
                            } else null,
                            onPick = { selectedAction = it }
                        )
                    }

                    CircleMenuActionTypes.OpenCircleMenu -> {
                        PickCircleMenuAction(
                            picked =
                            if (selectedAction.type == CircleMenuActionTypes.OpenCircleMenu) {
                                selectedAction.data as OpenCircleMenu
                            } else null,
                            mainAppViewModel = mainAppViewModel,
                            onPick = { selectedAction = it }
                        )
                    }

                    CircleMenuActionTypes.OpenSettings -> {
                        PickOpenSettingsAction(
                            picked = selectedAction.type == CircleMenuActionTypes.OpenSettings,
                            onPick = { selectedAction = it }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = {
                        onPick(selectedAction)
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@Composable
private fun PickAppAction(
    picked: OpenApp?,
    onPick: (CircleMenuAction) -> Unit
) {
    LazyColumn {
        items(
            items = DataObject.allApplicationData,
            key = { it.packageName }
        ) { applicationData ->
            AppItem(
                applicationData = applicationData,
                picked = applicationData.packageName == picked?.packageName
            ) {
                onPick(
                    CircleMenuAction(
                        type = CircleMenuActionTypes.OpenApp,
                        data = OpenApp(packageName = applicationData.packageName)
                    )
                )
            }
        }
    }
}

@Composable
private fun PickCircleMenuAction(
    picked: OpenCircleMenu?,
    mainAppViewModel: MainAppViewModel,
    onPick: (CircleMenuAction) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2)
    ) {
        items(
            items = mainAppViewModel.allCircleMenu.value ?: emptyList()
        ) { circleMenu ->
            MiniCircleMenuItem(
                picked = circleMenu.id == picked?.id,
                size = (Integer.min(
                    LocalConfiguration.current.screenWidthDp,
                    LocalConfiguration.current.screenHeightDp
                ) - 20f) / 3,
                circleMenu = circleMenu
            ) {
                onPick(
                    CircleMenuAction(
                        type = CircleMenuActionTypes.OpenCircleMenu,
                        data = OpenCircleMenu(id = circleMenu.id)
                    )
                )
            }
        }
    }
}

@Composable
private fun PickOpenSettingsAction(
    picked: Boolean,
    onPick: (CircleMenuAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            enabled = !picked,
            onClick = {
                onPick(
                    CircleMenuAction(type = CircleMenuActionTypes.OpenSettings)
                )
            }
        ) {
            Text(
                text = if (picked) "Already picked" else "OpenSetting"
            )
        }
    }
}

@Composable
private fun DialogTabs(
    selectedTab: CircleMenuActionTypes,
    onSelectTab: (CircleMenuActionTypes) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = DataObject.actionDialogTabs.map { it.type }.indexOf(selectedTab),
        edgePadding = 0.dp
    ) {
        DataObject.actionDialogTabs.forEach { actionTab ->
            Tab(
                selected = selectedTab == actionTab.type,
                onClick = {
                    if (selectedTab != actionTab.type) onSelectTab(actionTab.type)
                },
                text = {
                    Text(text = actionTab.name)
                }
            )
        }
    }
}