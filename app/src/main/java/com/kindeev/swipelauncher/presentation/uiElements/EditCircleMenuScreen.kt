package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.CircleMenuFunctions
import com.kindeev.swipelauncher.data.CircleMenuItem
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.presentation.EditCircleMenuScreenViewModel
import com.kindeev.swipelauncher.presentation.EditCircleMenuScreenViewModelFactory
import com.kindeev.swipelauncher.presentation.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickAppDialog
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickCircleMenuDialog
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickDefaultImageDialog

@Composable
fun EditCircleMenuScreen(mainAppViewModel: MainAppViewModel) {
    val viewModel: EditCircleMenuScreenViewModel = viewModel(
        factory = EditCircleMenuScreenViewModelFactory(
            context = LocalContext.current,
            mainAppViewModel = mainAppViewModel
        )
    )
    val context = LocalContext.current
    val menuSize = context.resources.configuration.screenWidthDp / 3f * 2f
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { circleMenus ->
        viewModel.updateCircleMenusEvent(circleMenus)
    }
    val circleMenu = viewModel.circleMenu.observeAsState(initial = RootCircleMenu.rootCircleMenu)
    val direction = viewModel.direction.observeAsState(initial = CircleMenuDirection.Up)
    val selectedCircleMenuItem = viewModel.selectedCircleMenuItem.observeAsState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CircleMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            circleMenu = circleMenu.value,
            menuSize = menuSize,
            selectedDirection = direction.value
        ) { circleMenuDirection ->
            viewModel.setDirection(circleMenuDirection)
        }
        selectedCircleMenuItem.value?.let {
            EditItemBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                mainAppViewModel = mainAppViewModel,
                circleMenuItem = it,
            ) { changedItem ->
                viewModel.updateCircleMenuItem(changedItem)
            }
        }
    }
}

@Composable
private fun CircleMenuBox(
    modifier: Modifier = Modifier.fillMaxWidth(),
    circleMenu: CircleMenu,
    menuSize: Float,
    selectedDirection: CircleMenuDirection?,
    onSelectAction: (CircleMenuDirection) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircleMenuForEditUI(
            menuSize = menuSize,
            menuImages = circleMenu.menuImages,
            upImageClick = {
                onSelectAction(CircleMenuDirection.Up)
            },
            downImageClick = {
                onSelectAction(CircleMenuDirection.Down)
            },
            rightImageClick = {
                onSelectAction(CircleMenuDirection.Right)
            },
            leftImageClick = {
                onSelectAction(CircleMenuDirection.Left)
            },
            selectedDirection = selectedDirection
        )
    }
}

@Composable
private fun EditItemBox(
    modifier: Modifier = Modifier.fillMaxWidth(),
    mainAppViewModel: MainAppViewModel,
    circleMenuItem: CircleMenuItem,
    onEdit: (circleMenuItem: CircleMenuItem) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        EditImageBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            circleMenuImage = circleMenuItem.image
        ) { changedImage ->
            onEdit(circleMenuItem.copy(image = changedImage))
        }
        EditActionBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            mainAppViewModel = mainAppViewModel,
            circleMenuAction = circleMenuItem.action
        ) { changedAction ->
            onEdit(circleMenuItem.copy(action = changedAction))
        }
    }
}

@Composable
private fun EditImageBox(
    modifier: Modifier = Modifier.fillMaxWidth(),
    circleMenuImage: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var openDialog by remember {
        mutableStateOf<CircleMenuImageTypes?>(null)
    }
    when (openDialog) {

        CircleMenuImageTypes.AppImage -> {
            PickAppDialog(
                pickedPackageName = when (val data = circleMenuImage.data) {
                    is AppImage -> {
                        data.packageName
                    }

                    else -> null
                },
                onPick = {
                    onChangeImage(
                        CircleMenuImage(
                            type = CircleMenuImageTypes.AppImage,
                            data = AppImage(packageName = it.packageName)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = {
                    openDialog = null
                }
            )
        }

        CircleMenuImageTypes.DefaultImage -> {
            PickDefaultImageDialog(
                pickedId = when (val data = circleMenuImage.data) {
                    is DefaultImage -> {
                        data.id
                    }

                    else -> null
                },
                onPick = { newId ->
                    onChangeImage(
                        CircleMenuImage(
                            type = CircleMenuImageTypes.DefaultImage,
                            data = DefaultImage(id = newId)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = {
                    openDialog = null
                })
        }

        else -> {}
    }
    Column(
        modifier = modifier.padding(5.dp)
    ) {
        Text(text = "Image")
        Spacer(modifier = Modifier.height(5.dp))
        ImageType(
            modifier = Modifier
                .fillMaxWidth(),
            circleMenuImage = circleMenuImage.type
        ) {
            if (circleMenuImage.type != it) openDialog = it
        }
        Spacer(modifier = Modifier.height(5.dp))
        ImageValue(
            circleMenuImage = circleMenuImage
        ) {
            openDialog = circleMenuImage.type
        }
    }
}

@Composable
private fun ImageType(
    modifier: Modifier = Modifier.fillMaxWidth(),
    circleMenuImage: CircleMenuImageTypes,
    onPick: (CircleMenuImageTypes) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
        Text(text = "Type:")
        DropdownMenuItem(
            text = {
                Text(text = circleMenuImage.name)
            },
            onClick = {
                expanded = !expanded
            })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CircleMenuImageTypes.values().forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.name)
                    },
                    onClick = {
                        onPick(it)
                        expanded = false
                    })
            }
        }
    }
}

@Composable
private fun ImageValue(
    circleMenuImage: CircleMenuImage,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Value:")
        Spacer(modifier = Modifier.width(5.dp))
        Image(
            modifier = Modifier
                .size(25.dp)
                .clickable(onClick = onClick),
            painter = CircleMenuFunctions.getItemImage(circleMenuImage = circleMenuImage),
            contentDescription = null
        )
    }
}

@Composable
private fun EditActionBox(
    modifier: Modifier = Modifier.fillMaxWidth(),
    mainAppViewModel: MainAppViewModel,
    circleMenuAction: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var openDialog by remember {
        mutableStateOf<CircleMenuActionTypes?>(null)
    }
    when (openDialog) {

        CircleMenuActionTypes.OpenCircleMenu -> {
            mainAppViewModel.allCircleMenu.value?.let { allCircleMenus ->
                PickCircleMenuDialog(
                    allCircleMenus = allCircleMenus,
                    pickedId = when (val data = circleMenuAction.data) {
                        is OpenCircleMenu -> {
                            data.id
                        }

                        else -> null
                    },
                    onPick = { newId ->
                        onChangeAction(
                            CircleMenuAction(
                                type = CircleMenuActionTypes.OpenCircleMenu,
                                data = OpenCircleMenu(id = newId)
                            )
                        )
                        openDialog = null
                    },
                    onDismissRequest = {
                        openDialog = null
                    }
                )
            }
        }
        CircleMenuActionTypes.NoneAction -> {
            onChangeAction(
                CircleMenuAction(
                    type = CircleMenuActionTypes.NoneAction
                )
            )
        }
        CircleMenuActionTypes.OpenSettings -> {
            onChangeAction(
                CircleMenuAction(
                    type = CircleMenuActionTypes.OpenSettings
                )
            )
        }

        else -> {}
    }
    Column(
        modifier = modifier.padding(5.dp)
    ) {
        Text(text = "Action")
        Spacer(modifier = Modifier.height(5.dp))
        ActionType(
            modifier = Modifier
                .fillMaxWidth(),
            circleMenuAction = circleMenuAction.type
        ) {
            if (circleMenuAction.type != it) openDialog = it
        }
        Spacer(modifier = Modifier.height(5.dp))
        ActionValue(
            circleMenuAction = circleMenuAction
        ) {
            openDialog = circleMenuAction.type
        }
    }
}

@Composable
private fun ActionType(
    modifier: Modifier = Modifier.fillMaxWidth(),
    circleMenuAction: CircleMenuActionTypes,
    onPick: (CircleMenuActionTypes) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
        Text(text = "Type:")
        DropdownMenuItem(
            text = {
                Text(text = circleMenuAction.name)
            },
            onClick = {
                expanded = !expanded
            })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CircleMenuActionTypes.values().forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.name)
                    },
                    onClick = {
                        onPick(it)
                        expanded = false
                    })
            }
        }
    }
}

@Composable
private fun ActionValue(
    circleMenuAction: CircleMenuAction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Value:")
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            modifier = Modifier.clickable(onClick = onClick),
            text = "Change action"
        )
    }
}