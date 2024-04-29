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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.CircleMenuDirection
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.getItemImage
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuForEditUI
import com.kindeev.swipelauncher.presentation.ui.dialogs.PickImageDialog
import com.kindeev.swipelauncher.presentation.ui.elements.ActionDataItem

@Composable
fun EditCircleMenuScreen(
    circleMenuId: Int?,
    onBackPressed: () -> Unit
) {
    // ViewModel
    val viewModel: EditCircleMenuScreenVM = viewModel(
        factory = EditCircleMenuScreenVMFactory(circleMenuId)
    )

    // Checking for update circle menus
    LauncherData.allCircleMenus.observe(LocalLifecycleOwner.current) {
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
                circleMenuItem = circleMenuItem
            ) { changedItem ->
                viewModel.updateCircleMenuItem(changedItem)
            }
        }
    }
}

@Composable
fun EditCircleMenuToolbar(
    viewModel: EditCircleMenuScreenVM,
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
                        viewModel.insertCircleMenu(menu.copy(title = newTitle.text))
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
        ActionDataItem(
            action = circleMenuItem.action,
            onChange = { onEdit(circleMenuItem.copy(action = it)) }
        )
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
        val imageBitmap = circleMenuImage.getItemImage(LocalContext.current)
        if (imageBitmap != null) {
            Image(
                modifier = Modifier
                    .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                    .clickable { openDialog = true },
                bitmap = imageBitmap,
                colorFilter = if (circleMenuImage.type == CircleMenuImageTypes.DefaultImage) ColorFilter.tint(
                    MaterialTheme.colorScheme.onBackground
                ) else null,
                contentDescription = null,
            )
        } else {
            Button(onClick = { openDialog = true }) {
                Text(text = stringResource(id = R.string.pick_image))
            }
        }
    }
}