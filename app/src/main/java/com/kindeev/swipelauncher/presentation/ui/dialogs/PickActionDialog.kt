package com.kindeev.swipelauncher.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
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
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.dialogTabs.DialogTab
import com.kindeev.swipelauncher.domain.getAs

@Composable
fun PickActionDialog(
    onDismissRequest: () -> Unit,
    picked: CircleMenuAction,
    onPick: (CircleMenuAction) -> Unit
) {
    var selectedAction by remember {
        mutableStateOf(picked)
    }
    var selectedTab by remember {
        mutableStateOf(
            when (picked.type) {
                CircleMenuActionTypes.OpenApp -> DialogTab(R.string.open_app_tab)
                CircleMenuActionTypes.OpenCircleMenu -> DialogTab(R.string.open_circle_menu_tab)
                else -> DialogTab(R.string.other_tab)
            }
        )
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
                DialogTabs(
                    tabs = Constants.actionDialogTabs,
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
                when (selectedTab.nameResourceId) {
                    R.string.open_app_tab -> {
                        PickAppTabContent(
                            pickedPackageName =
                            if (selectedAction.type == CircleMenuActionTypes.OpenApp) {
                                selectedAction.data.getAs(OpenApp::class.java).packageName
                            } else null,
                            onPick = {
                                selectedAction = CircleMenuAction(
                                    type = CircleMenuActionTypes.OpenApp,
                                    data = OpenApp(packageName = it)
                                )
                            }
                        )
                    }

                    R.string.open_circle_menu_tab -> {
                        PickCircleMenuTabContent(
                            pickedId =
                            if (selectedAction.type == CircleMenuActionTypes.OpenCircleMenu) {
                                selectedAction.data.getAs(OpenCircleMenu::class.java).id
                            } else null,
                            onPick = {
                                selectedAction = CircleMenuAction(
                                    type = CircleMenuActionTypes.OpenCircleMenu,
                                    data = it
                                )
                            }
                        )
                    }

                    R.string.other_tab -> {
                        PickOtherActionTabContent(
                            picked = selectedAction.type,
                            onPick = {
                                selectedAction = CircleMenuAction(
                                    type = it.type
                                )
                            }
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
fun PickActionDialogWithoutOpenCircleMenu(
    onDismissRequest: () -> Unit,
    picked: CircleMenuAction,
    onPick: (CircleMenuAction) -> Unit
) {
    var selectedAction by remember {
        mutableStateOf(picked)
    }
    var selectedTab by remember {
        mutableStateOf(
            when (picked.type) {
                CircleMenuActionTypes.OpenApp -> DialogTab(R.string.open_app_tab)
                else -> DialogTab(R.string.other_tab)
            }
        )
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
                DialogTabs(
                    tabs = Constants.actionDialogTabs.toMutableList().apply { remove(DialogTab(R.string.open_circle_menu_tab)) },
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
                when (selectedTab.nameResourceId) {
                    R.string.open_app_tab -> {
                        PickAppTabContent(
                            pickedPackageName =
                            if (selectedAction.type == CircleMenuActionTypes.OpenApp) {
                                selectedAction.data.getAs(OpenApp::class.java).packageName
                            } else null,
                            onPick = {
                                selectedAction = CircleMenuAction(
                                    type = CircleMenuActionTypes.OpenApp,
                                    data = it
                                )
                            }
                        )
                    }

                    R.string.other_tab -> {
                        PickOtherActionTabContent(
                            picked = selectedAction.type,
                            onPick = {
                                selectedAction = CircleMenuAction(
                                    type = it.type
                                )
                            }
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