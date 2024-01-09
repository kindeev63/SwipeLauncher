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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.CircleMenuFunctions
import com.kindeev.swipelauncher.data.CircleMenuItem
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.presentation.viewModels.EditCircleMenuScreenViewModel
import com.kindeev.swipelauncher.presentation.viewModels.factories.EditCircleMenuScreenViewModelFactory
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.CircleMenuForEditUI
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickAppDialog
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickCircleMenuDialog
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickDefaultImageDialog

@Composable
fun EditCircleMenuScreen(
    mainAppViewModel: MainAppViewModel,
    circleMenuId: Int,
    onBackPressed: () -> Unit
) {
    // ViewModel
    val viewModel: EditCircleMenuScreenViewModel = viewModel(
        factory = EditCircleMenuScreenViewModelFactory(mainAppViewModel, circleMenuId)
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
        modifier = Modifier.fillMaxSize()
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
            mainAppViewModel.allCircleMenu.value?.let { allCircleMenus ->
                EditItemBox(
                    allCircleMenus = allCircleMenus,
                    circleMenuItem = circleMenuItem,
                ) { changedItem ->
                    viewModel.updateCircleMenuItem(changedItem)
                }
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
            .background(Color.Green),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val circleMenu = viewModel.circleMenu.observeAsState()
        var title by remember {
            mutableStateOf(TextFieldValue(text = circleMenu.value?.title ?: ""))
        }
        var error by remember {
            mutableStateOf(false)
        }
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
            PlaceholderTextField(
                value = title,
                onValueChange = { newTitle ->
                    val allTitles = viewModel.mainAppViewModel.allCircleMenu.value?.map { it.title }
                        ?: emptyList()
                    if (newTitle.text == menu.title || newTitle.text.isEmpty()) {
                        title = newTitle
                    } else {
                        if (newTitle.text in allTitles) {
                            title = newTitle
                            error = true
                        } else {
                            viewModel.mainAppViewModel.insertCircleMenu(menu.copy(title = newTitle.text))
                            title = newTitle
                            if (error) error = false
                        }
                    }
                },
                error = error
            )
        }
    }
}

@Composable
private fun PlaceholderTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    error: Boolean
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            if (value.text.isEmpty()) {
                Text(
                    text = "Title",
                    style = TextStyle(color = Color.Gray),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        if (error) {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                tint = Color.Red
            )
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
    allCircleMenus: List<CircleMenu>,
    circleMenuItem: CircleMenuItem,
    onEdit: (circleMenuItem: CircleMenuItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // Image
        EditImageBox(
            circleMenuImage = circleMenuItem.image
        ) { changedImage ->
            onEdit(circleMenuItem.copy(image = changedImage))
        }

        // Action
        EditActionBox(
            allCircleMenus = allCircleMenus,
            circleMenuAction = circleMenuItem.action
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
        mutableStateOf<CircleMenuImageTypes?>(null)
    }

    // Pick image dialogs
    when (openDialog) {

        // App Image
        CircleMenuImageTypes.AppImage -> {
            PickAppDialog(
                pickedPackageName = if (circleMenuImage.data is AppImage) circleMenuImage.data.packageName else null,
                onPick = {
                    onChangeImage(
                        CircleMenuImage(
                            type = CircleMenuImageTypes.AppImage,
                            data = AppImage(packageName = it.packageName)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = { openDialog = null }
            )
        }

        // Default Image
        CircleMenuImageTypes.DefaultImage -> {
            PickDefaultImageDialog(
                pickedId = if (circleMenuImage.data is DefaultImage) circleMenuImage.data.id else null,
                onPick = { newId ->
                    onChangeImage(
                        CircleMenuImage(
                            type = CircleMenuImageTypes.DefaultImage,
                            data = DefaultImage(id = newId)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = { openDialog = null }
            )
        }

        else -> {}
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(5.dp)
    ) {
        Text(text = "Image")
        Spacer(modifier = Modifier.height(5.dp))

        // Type of image
        ImageType(
            selectedType = circleMenuImage.type
        ) {
            if (circleMenuImage.type != it) openDialog = it
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Image value
        ImageValue(
            circleMenuImage = circleMenuImage
        ) {
            openDialog = circleMenuImage.type
        }
    }
}

@Composable
private fun ImageType(
    selectedType: CircleMenuImageTypes,
    onPick: (CircleMenuImageTypes) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
        Text(text = "Type:")
        DropdownMenuItem(
            text = { Text(text = selectedType.name) },
            onClick = { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CircleMenuImageTypes.values().forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        onPick(it)
                        expanded = false
                    }
                )
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
        CircleMenuFunctions.getItemImage(
            circleMenuImage = circleMenuImage
        )?.let { painter ->
            Text(text = "Value:")
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                modifier = Modifier
                    .size(25.dp)
                    .clickable(onClick = onClick),
                painter = painter,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun EditActionBox(
    allCircleMenus: List<CircleMenu>,
    circleMenuAction: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var openDialog by remember {
        mutableStateOf<CircleMenuActionTypes?>(null)
    }

    // Pick action dialogs
    when (openDialog) {

        // Open Circle Menu
        CircleMenuActionTypes.OpenCircleMenu -> {
            PickCircleMenuDialog(
                allCircleMenus = allCircleMenus,
                pickedId = if (circleMenuAction.data is OpenCircleMenu) circleMenuAction.data.id else null,
                onPick = { newId ->
                    onChangeAction(
                        CircleMenuAction(
                            type = CircleMenuActionTypes.OpenCircleMenu,
                            data = OpenCircleMenu(id = newId)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = { openDialog = null }
            )
        }

        // Open App
        CircleMenuActionTypes.OpenApp -> {
            PickAppDialog(
                pickedPackageName = if (circleMenuAction.data is OpenApp) circleMenuAction.data.packageName else null,
                onPick = { appData ->
                    onChangeAction(
                        CircleMenuAction(
                            type = CircleMenuActionTypes.OpenApp,
                            data = OpenApp(packageName = appData.packageName)
                        )
                    )
                    openDialog = null
                },
                onDismissRequest = { openDialog = null }
            )
        }

        // None Action
        CircleMenuActionTypes.NoneAction -> {
            onChangeAction(
                CircleMenuAction(
                    type = CircleMenuActionTypes.NoneAction
                )
            )
        }

        // Open Settings
        CircleMenuActionTypes.OpenSettings -> {
            onChangeAction(
                CircleMenuAction(
                    type = CircleMenuActionTypes.OpenSettings
                )
            )
        }

        else -> {}
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .padding(5.dp)
    ) {
        Text(text = "Action")
        Spacer(modifier = Modifier.height(5.dp))

        // Type of action
        ActionType(
            circleMenuAction = circleMenuAction.type
        ) {
            if (circleMenuAction.type != it) openDialog = it
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Action Value
        ActionValue(
            circleMenuAction = circleMenuAction,
            allCircleMenus = allCircleMenus
        ) {
            openDialog = circleMenuAction.type
        }
    }
}

@Composable
private fun ActionType(
    circleMenuAction: CircleMenuActionTypes,
    onPick: (CircleMenuActionTypes) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember {
            mutableStateOf(false)
        }
        Text(text = "Type:")
        DropdownMenuItem(
            text = { Text(text = circleMenuAction.name) },
            onClick = { expanded = !expanded }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CircleMenuActionTypes.values().forEach {
                DropdownMenuItem(
                    text = { Text(text = it.name) },
                    onClick = {
                        onPick(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionValue(
    circleMenuAction: CircleMenuAction,
    allCircleMenus: List<CircleMenu>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (circleMenuAction.type) {
            CircleMenuActionTypes.NoneAction -> {}

            CircleMenuActionTypes.OpenCircleMenu -> {
                val openCircleMenu = circleMenuAction.data as OpenCircleMenu
                allCircleMenus.find { it.id == openCircleMenu.id }?.let { circleMenu ->
                    Text(text = "Value:")
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        modifier = Modifier
                            .clickable(onClick = onClick),
                        text = circleMenu.title
                    )
                }

            }

            CircleMenuActionTypes.OpenSettings -> {}
            CircleMenuActionTypes.OpenApp -> {
                val currentApp = circleMenuAction.data as OpenApp
                val context = LocalContext.current
                val applicationInfo =
                    context.packageManager.getApplicationInfo(currentApp.packageName, 0)
                val imageBitmap =
                    applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                Text(text = "Value:")
                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    modifier = Modifier
                        .size(25.dp)
                        .clickable(onClick = onClick),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            }
        }
    }
}