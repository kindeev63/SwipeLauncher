package com.kindeev.swipelauncher.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem

@Composable
fun ActionDialog(
    onDismissRequest: () -> Unit,
    onPick: (CircleMenuAction) -> Unit
) {
    var actionType by rememberSaveable {
        mutableStateOf<CircleMenuActionTypes?>(null)
    }
    AllActionTypes(
        onPick = { actionType = it },
        onDismissRequest = onDismissRequest
    )
    when (actionType) {
        CircleMenuActionTypes.OpenCircleMenu -> {
            OpenCircleMenuActionData(
                onPick = onPick,
                onDismissRequest = { actionType = null }
            )
        }

        CircleMenuActionTypes.OpenSettings -> onPick(CircleMenuAction(type = CircleMenuActionTypes.OpenSettings))
        CircleMenuActionTypes.OpenApp -> {
            OpenAppActionData(
                onPick = onPick,
                onDismissRequest = { actionType = null }
            )
        }

        CircleMenuActionTypes.FlashLightOn -> onPick(CircleMenuAction(type = CircleMenuActionTypes.FlashLightOn))
        CircleMenuActionTypes.FlashLightOff -> onPick(CircleMenuAction(type = CircleMenuActionTypes.FlashLightOff))
        CircleMenuActionTypes.ChangeFlashLightCondition -> onPick(CircleMenuAction(type = CircleMenuActionTypes.ChangeFlashLightCondition))
        CircleMenuActionTypes.Call -> TODO()
        null -> {}
    }
}

@Composable
private fun AllActionTypes(
    onPick: (CircleMenuActionTypes) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = Constants.actionTypes) { actionType ->
                    ActionTypeElement(
                        actionType = actionType,
                        onClick = { onPick(actionType.type) }
                    )
                }
            }
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
private fun SearchElement(searchText: String, onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2196F3))
                .padding(horizontal = 15.dp, vertical = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchText.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search),
                    color = Color.White
                )
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                textStyle = TextStyle(
                    color = Color.White,
                ),
                value = searchText,
                onValueChange = onTextChange
            )
        }
    }

}

@Composable
private fun ActionTypeElement(
    actionType: ActionType,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1976D2))
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = actionType.imageResId),
                contentDescription = "Action type image"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(id = actionType.nameResId),
                color = Color.White
            )
        }
    }
}

@Composable
private fun OpenCircleMenuActionData(
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(
                    items = LauncherData.allCircleMenus.value?.filter {
                        it.title.lowercase().contains(searchText.lowercase())
                    } ?: emptyList()
                ) { circleMenu ->
                    MiniCircleMenuItem(
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
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
private fun OpenAppActionData(
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            val allApplicationData = LauncherData.allApplicationData.observeAsState(emptyList())
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = allApplicationData.value.filter {
                        it.name.lowercase().contains(searchText.lowercase())
                    },
                    key = { it.packageName }
                ) { applicationData ->
                    AppItem(
                        applicationData = applicationData
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
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}