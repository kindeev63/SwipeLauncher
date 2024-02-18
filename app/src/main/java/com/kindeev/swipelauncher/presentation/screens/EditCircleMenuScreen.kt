package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.CircleMenuItem
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.presentation.viewModels.EditCircleMenuScreenViewModel
import com.kindeev.swipelauncher.presentation.viewModels.factories.EditCircleMenuScreenViewModelFactory
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.CircleMenuForEditUI
import com.kindeev.swipelauncher.presentation.uiElements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickActionDialog
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickImageDialog

@Composable
fun EditCircleMenuScreen(
    mainAppViewModel: MainAppViewModel,
    circleMenuId: Int?,
    onBackPressed: () -> Unit
) {
    // ViewModel
    val viewModel: EditCircleMenuScreenViewModel = viewModel(
        factory = EditCircleMenuScreenViewModelFactory(
            mainAppViewModel, circleMenuId, stringResource(
                id = R.string.new_circle_menu_title
            )
        )
    )

    // Checking for update circle menus
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) {
        viewModel.updateCircleMenusEvent(it)
    }

    // States
    val circleMenu = viewModel.circleMenu.observeAsState()
    val direction = viewModel.direction.observeAsState(initial = CircleMenuDirection.Up)
    val selectedCircleMenuItem = viewModel.selectedCircleMenuItem.observeAsState()

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // Toolbar
        EditCircleMenuToolbar(
            viewModel = viewModel,
            onBackPressed = onBackPressed
        )

        // CircleMenu UI
        circleMenu.value?.let { notNullCircleMenu ->
            CircleMenuBox(
                circleMenu = notNullCircleMenu,
                menuSize = viewModel.getMenuSize(LocalConfiguration.current),
                direction = direction.value
            ) { circleMenuDirection ->
                viewModel.setDirection(circleMenuDirection)
            }
        }

        // CircleMenu image and action panel
        selectedCircleMenuItem.value?.let { circleMenuItem ->
            EditItemBox(
                circleMenuItem = circleMenuItem,
                viewModel = viewModel
            ) { changedItem ->
                viewModel.updateCircleMenuItem(changedItem)
            }
        }
    }
}

@Composable
fun EditCircleMenuToolbar(
    viewModel: EditCircleMenuScreenViewModel,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.secondary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val circleMenu = viewModel.circleMenu.observeAsState()
        IconButton(
            onClick = {
                onBackPressed()
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = Color.White
            )
        }
        circleMenu.value?.let { menu ->
            if (menu.id == 0) {
                Text(
                    text = menu.title,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                var title by remember {
                    mutableStateOf(TextFieldValue())
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onPrimary),
                    value = title.copy(text = circleMenu.value?.title ?: ""),
                    onValueChange = { newTitle ->
                        viewModel.mainAppViewModel.insertCircleMenu(menu.copy(title = newTitle.text))
                        title = newTitle
                    }
                )
            }

        }
    }
}

@Composable
private fun CircleMenuBox(
    circleMenu: CircleMenu,
    menuSize: Float,
    direction: CircleMenuDirection?,
    onSelectAction: (CircleMenuDirection) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f),
        contentAlignment = Alignment.Center
    ) {
        CircleMenuForEditUI(
            menuSize = menuSize,
            menuImages = circleMenu.menuImages,
            upImageClick = { onSelectAction(CircleMenuDirection.Up) },
            downImageClick = { onSelectAction(CircleMenuDirection.Down) },
            rightImageClick = { onSelectAction(CircleMenuDirection.Right) },
            leftImageClick = { onSelectAction(CircleMenuDirection.Left) },
            selectedDirection = direction
        )
    }
}

@Composable
private fun EditItemBox(
    circleMenuItem: CircleMenuItem,
    viewModel: EditCircleMenuScreenViewModel,
    onEdit: (circleMenuItem: CircleMenuItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Image
        EditImageBox(
            circleMenuImage = circleMenuItem.image,
        ) { changedImage ->
            onEdit(circleMenuItem.copy(image = changedImage))
        }

        // Action
        EditActionBox(
            circleMenuAction = circleMenuItem.action,
            viewModel = viewModel,
        ) { changedAction ->
            onEdit(circleMenuItem.copy(action = changedAction))
        }
    }
}

@Composable
private fun EditImageBox(
    circleMenuImage: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    if (openDialog) {
        PickImageDialog(
            onDismissRequest = { openDialog = false },
            picked = circleMenuImage,
            onPick = {
                onChangeImage(it)
                openDialog = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = stringResource(id = R.string.image),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(5.dp))
        val painter = DataObject.getItemImage(circleMenuImage = circleMenuImage)
        if (painter == null) {
            Button(onClick = { openDialog = true }) {
                Text(text = stringResource(id = R.string.pick_image))
            }
        } else {
            Image(
                modifier = Modifier
                    .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                    .clickable { openDialog = true },
                painter = painter,
                colorFilter = if (circleMenuImage.type == CircleMenuImageTypes.DefaultImage) ColorFilter.tint(MaterialTheme.colorScheme.onBackground) else null,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun EditActionBox(
    circleMenuAction: CircleMenuAction,
    viewModel: EditCircleMenuScreenViewModel,
    onChangeAction: (CircleMenuAction) -> Unit,
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    val allApplicationData = DataObject.allApplicationData.observeAsState(emptyList())
    if (openDialog) {
        PickActionDialog(
            onDismissRequest = { openDialog = false },
            mainAppViewModel = viewModel.mainAppViewModel,
            picked = circleMenuAction,
            onPick = {
                onChangeAction(it)
                openDialog = false
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = stringResource(id = R.string.action),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(5.dp))
        when (circleMenuAction.type) {

            CircleMenuActionTypes.OpenCircleMenu -> {
                val openCircleMenu = circleMenuAction.data as OpenCircleMenu
                val circleMenu =
                    viewModel.mainAppViewModel.allCircleMenu.value?.find { it.id == openCircleMenu.id }
                circleMenu?.let {
                    MiniCircleMenuItem(
                        size = LocalConfiguration.current.screenWidthDp / 6f,
                        circleMenu = it
                    ) {
                        openDialog = true
                    }
                    GoToCircleMenu {
                        viewModel.goToCircleMenu(circleMenuId = openCircleMenu.id)
                    }
                }

            }

            CircleMenuActionTypes.OpenApp -> {
                val openApp = circleMenuAction.data as OpenApp
                val painter = when (val applicationData =
                    allApplicationData.value.find { it.packageName == openApp.packageName }) {
                    null -> {
                        val context = LocalContext.current
                        val applicationInfo =
                            context.packageManager.getApplicationInfo(openApp.packageName, 0)
                        val imageBitmap =
                            applicationInfo.loadIcon(context.packageManager).toBitmap()
                                .asImageBitmap()
                        remember(imageBitmap) {
                            BitmapPainter(
                                imageBitmap,
                                filterQuality = DrawScope.DefaultFilterQuality
                            )
                        }
                    }

                    else -> {
                        val imageBitmap = applicationData.icon
                        remember(imageBitmap) {
                            BitmapPainter(
                                imageBitmap,
                                filterQuality = DrawScope.DefaultFilterQuality
                            )
                        }
                    }
                }
                Image(
                    modifier = Modifier
                        .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                        .clickable {
                            openDialog = true
                        },
                    painter = painter,
                    contentDescription = null
                )

            }

            CircleMenuActionTypes.OpenSettings -> {
                Image(
                    modifier = Modifier
                        .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                        .clickable {
                            openDialog = true
                        },
                    painter = painterResource(id = R.drawable.ic_settings),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    contentDescription = null
                )
            }

            else -> {
                Image(
                    modifier = Modifier
                        .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                        .clickable {
                            openDialog = true
                        },
                    painter = painterResource(
                        id =
                        DataObject.defaultImages[DataObject.otherActionsList.find { it.type == circleMenuAction.type }?.image]
                            ?: R.drawable.ic_error
                    ),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    contentDescription = null
                )
            }
        }

    }
}


@Composable
private fun GoToCircleMenu(goToCircleMenu: () -> Unit) {
    Row {
        Spacer(modifier = Modifier.width(5.dp))
        TextButton(
            onClick = goToCircleMenu
        ) {
            Text(text = stringResource(id = R.string.go_to_circle_menu))
        }
    }
}