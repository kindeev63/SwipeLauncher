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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.NoneImage
import com.kindeev.swipelauncher.presentation.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickDefaultImageDialog
private data class CircleMenuItem(
    val direction: CircleMenuDirection,
    val action: CircleMenuAction,
    val image: CircleMenuImage
)

@Composable
fun EditCircleMenuScreen(mainAppViewModel: MainAppViewModel) {
    val context = LocalContext.current
    val menuSize = context.resources.configuration.screenWidthDp / 3f * 2f
    var circleMenu by remember {
        mutableStateOf(RootCircleMenu.rootCircleMenu)
    }
    var selectedDirection by remember {
        mutableStateOf(CircleMenuDirection.Down)
    }
    mainAppViewModel.allCircleMenu.observe(LocalLifecycleOwner.current) { circleMenus ->
        circleMenus.find { it.id == circleMenu.id }?.let {
            circleMenu = it
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CircleMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            circleMenu = circleMenu,
            menuSize = menuSize,
            selectedDirection = selectedDirection
        ) { circleMenuDirection ->
            selectedDirection = circleMenuDirection
        }
        EditItemBox(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f),
            circleMenuItem = getCircleMenuItemByDirection(selectedDirection, circleMenu),
        ) { changedItem ->
            val newCircleMenu = updateCircleMenuItem(
                circleMenu = circleMenu,
                circleMenuItem = changedItem
            )
            mainAppViewModel.insertCircleMenu(newCircleMenu)
            circleMenu = newCircleMenu
        }
    }
}

private fun getCircleMenuItemByDirection(direction: CircleMenuDirection, circleMenu: CircleMenu) =
    when (direction) {
        CircleMenuDirection.Up -> {
            CircleMenuItem(
                direction = direction,
                action = circleMenu.menuActions.upAction,
                image = circleMenu.menuImages.upImage
            )
        }

        CircleMenuDirection.Down -> {
            CircleMenuItem(
                direction = direction,
                action = circleMenu.menuActions.downAction,
                image = circleMenu.menuImages.downImage
            )
        }

        CircleMenuDirection.Right -> {
            CircleMenuItem(
                direction = direction,
                action = circleMenu.menuActions.rightAction,
                image = circleMenu.menuImages.rightImage
            )
        }

        CircleMenuDirection.Left -> {
            CircleMenuItem(
                direction = direction,
                action = circleMenu.menuActions.leftAction,
                image = circleMenu.menuImages.leftImage
            )
        }
    }

private fun updateCircleMenuItem(
    circleMenu: CircleMenu,
    circleMenuItem: CircleMenuItem
): CircleMenu {
    val menuImages = circleMenu.menuImages
    val menuActions = circleMenu.menuActions
    return when (circleMenuItem.direction) {
        CircleMenuDirection.Up -> {
            circleMenu.copy(
                menuImages = menuImages.copy(
                    upImage = circleMenuItem.image
                ),
                menuActions = menuActions.copy(
                    upAction = circleMenuItem.action
                )
            )
        }

        CircleMenuDirection.Down -> {
            circleMenu.copy(
                menuImages = menuImages.copy(
                    downImage = circleMenuItem.image
                ),
                menuActions = menuActions.copy(
                    downAction = circleMenuItem.action
                )
            )
        }

        CircleMenuDirection.Right -> {
            circleMenu.copy(
                menuImages = menuImages.copy(
                    rightImage = circleMenuItem.image
                ),
                menuActions = menuActions.copy(
                    rightAction = circleMenuItem.action
                )
            )
        }

        CircleMenuDirection.Left -> {
            circleMenu.copy(
                menuImages = menuImages.copy(
                    leftImage = circleMenuItem.image
                ),
                menuActions = menuActions.copy(
                    leftAction = circleMenuItem.action
                )
            )
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
        CircleMenuImageTypes.NoneImage -> {
            onChangeImage(
                CircleMenuImage(
                    type = CircleMenuImageTypes.NoneImage,
                    data = NoneImage
                )
            )
        }

        CircleMenuImageTypes.AppImage -> {}
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
            painter = getItemImage(circleMenuImage = circleMenuImage),
            contentDescription = null
        )
    }
}

@Composable
private fun EditActionBox(
    modifier: Modifier = Modifier.fillMaxWidth(),
    circleMenuAction: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(text = "Action")
    }
}

@Composable
private fun getItemImage(circleMenuImage: CircleMenuImage): Painter {
    return when (circleMenuImage.type) {
        CircleMenuImageTypes.NoneImage -> {
            painterResource(id = R.drawable.ic_settings)
        }

        CircleMenuImageTypes.DefaultImage -> {
            val defaultImage = circleMenuImage.data as DefaultImage
            painterResource(id = defaultImage.id)
        }

        CircleMenuImageTypes.AppImage -> {
            painterResource(id = R.drawable.ic_settings)
        }
    }
}