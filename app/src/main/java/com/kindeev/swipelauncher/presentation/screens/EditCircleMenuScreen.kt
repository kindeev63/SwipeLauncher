package com.kindeev.swipelauncher.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.getSelectedBoxOffset
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuForEditUI
import com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction.ImageAndAction
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
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
    val item by viewModel.item.observeAsState()

    var deleteItemDialog by remember {
        mutableStateOf<CircleMenuItem?>(null)
    }
    deleteItemDialog?.let { thisItem ->
        QuestionDialog(
            text = stringResource(id = R.string.delete_item_question),
            onDismissRequest = { deleteItemDialog = null },
            onClickYes = {
                viewModel.deleteItem(thisItem)
                deleteItemDialog = null
            }
        )
    }

    // UI
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditCircleMenuToolbar(
                viewModel = viewModel,
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->

        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // Tablet

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CircleMenu UI
                Box(
                    modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2),
                    contentAlignment = Alignment.Center
                ) {
                    CircleMenuBox(
                        viewModel = viewModel,
                        menuSize = max(
                            LocalConfiguration.current.screenWidthDp,
                            LocalConfiguration.current.screenHeightDp
                        ) / 3f
                    )
                }
                // CircleMenu image and action panel
                item?.let { circleMenuItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        ImageAndAction(
                            width = LocalConfiguration.current.screenWidthDp.dp / 2 - 10.dp,
                            circleMenuItem = circleMenuItem,
                            onChangeAction = {
                                viewModel.updateCircleMenuItem((circleMenuItem.copy(action = it)))
                            },
                            onChangeImage = {
                                viewModel.updateImage(circleMenuItem.copy(image = it))
                            }
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red)
                                .clickable { deleteItemDialog = item }
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                imageVector = Icons.Rounded.Delete,
                                tint = Color.White,
                                contentDescription = null
                            )
                            Text(
                                text = stringResource(id = R.string.delete),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                }
            }
        } else {

            // Phone

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                // CircleMenu UI
                CircleMenuBox(
                    viewModel = viewModel,
                    menuSize = Constants.minScreenLength / 3 * 2
                )
                Spacer(modifier = Modifier.height(30.dp))
                // CircleMenu image and action panel
                item?.let { circleMenuItem ->
                    ImageAndAction(
                        circleMenuItem = circleMenuItem,
                        onChangeAction = {
                            viewModel.updateCircleMenuItem((circleMenuItem.copy(action = it)))
                        },
                        onChangeImage = {
                            viewModel.updateImage(circleMenuItem.copy(image = it))
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Red)
                            .clickable { deleteItemDialog = item }
                            .padding(horizontal = 10.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = Icons.Rounded.Delete,
                            tint = Color.White,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.delete),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun EditCircleMenuToolbar(
    viewModel: EditCircleMenuScreenVM,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val circleMenu = viewModel.circleMenu.observeAsState()
        IconButton(
            onClick = {
                onBackPressed()
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        circleMenu.value?.let { menu ->
            if (menu.id == 0) {
                Text(
                    text = menu.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp
                )
            } else {
                var title by remember {
                    mutableStateOf(TextFieldValue())
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    ),
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

private data class CircleMenuItemToAdd(
    val offset: Offset? = null,
    val image: CircleMenuImage? = null
)

@Composable
private fun CircleMenuBox(
    viewModel: EditCircleMenuScreenVM,
    menuSize: Float
) {
    val circleMenu by viewModel.circleMenu.observeAsState()
    val item by viewModel.item.observeAsState()
    var circleMenuItemToAdd by remember {
        mutableStateOf(CircleMenuItemToAdd())
    }
    if (circleMenuItemToAdd.offset != null) {
        if (circleMenuItemToAdd.image == null) {
            ImageDialog(
                onDismissRequest = {
                    if (circleMenuItemToAdd.image == null) {
                        circleMenuItemToAdd = CircleMenuItemToAdd()
                    }
                },
                onPick = { circleMenuItemToAdd = circleMenuItemToAdd.copy(image = it) }
            )
        } else {
            if (circleMenuItemToAdd.image is AppImage && LauncherData.settings.value?.getValueOf(
                    SettingNames.PickAppActionWithImage,
                    PickAppActionWithImage::class.java
                )?.enabled == true
            ) {
                viewModel.insertItem(
                    CircleMenuItem(
                        offset = circleMenuItemToAdd.offset
                            ?: throw IllegalArgumentException("Illegal offset"),
                        image = circleMenuItemToAdd.image
                            ?: throw IllegalArgumentException("Illegal image"),
                        action = OpenAppAction(packageName = (circleMenuItemToAdd.image as AppImage).packageName)
                    )
                )
                circleMenuItemToAdd = CircleMenuItemToAdd()
            } else {
                ActionDialog(
                    onDismissRequest = { circleMenuItemToAdd = CircleMenuItemToAdd() },
                    onPick = {
                        viewModel.insertItem(
                            CircleMenuItem(
                                offset = circleMenuItemToAdd.offset
                                    ?: throw IllegalArgumentException("Illegal offset"),
                                image = circleMenuItemToAdd.image
                                    ?: throw IllegalArgumentException("Illegal image"),
                                action = it
                            )
                        )
                    }
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp)
    ) {
        CircleMenuForEditUI(
            items = circleMenu?.items ?: emptyList(),
            selectedBoxOffset = item?.offset?.getSelectedBoxOffset(menuSize),
            menuSize = menuSize,
            onSelectItem = { viewModel.setItem(it) },
            onAdd = { circleMenuItemToAdd = CircleMenuItemToAdd(offset = it) }
        )
    }
}