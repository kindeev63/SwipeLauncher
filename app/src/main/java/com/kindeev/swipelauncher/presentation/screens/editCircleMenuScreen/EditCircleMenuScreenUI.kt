package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.ActionItemDataType
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.SelectedItemBoxData
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.EditCircleMenuVMFactory
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.elements.EditCircleMenuAction

@Composable
fun EditCircleMenuScreenUI(
    circleMenuId: Int?,
    onBackPressed: () -> Unit
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT && Constants.minScreenLength / 6 * 5f * 1.5f < LocalConfiguration.current.screenHeightDp) {
        val menuSize = Constants.minScreenLength / 6 * 5f
        val viewModel: EditCircleMenuScreenVM = viewModel(
            factory = EditCircleMenuVMFactory(circleMenuId, menuSize, LocalContext.current)
        )
        PortraitUI(
            viewModel = viewModel,
            onBackPressed = onBackPressed
        )
    } else {
        val menuSize = (Constants.minScreenLength - 80) / 3 * 2f
        val viewModel: EditCircleMenuScreenVM = viewModel(
            factory = EditCircleMenuVMFactory(circleMenuId, menuSize, LocalContext.current)
        )
        LandscapeUI(
            viewModel = viewModel,
            onBackPressed = onBackPressed
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LandscapeUI(
    viewModel: EditCircleMenuScreenVM,
    onBackPressed: () -> Unit
) {
    val actionItemData by viewModel.actionItemData.observeAsState()
    val circleMenu by viewModel.circleMenu.observeAsState()
    val selectedBoxData by viewModel.selectedBoxData.observeAsState()

    // UI
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditCircleMenuToolbarUI(
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CircleMenu and Title
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // CircleMenu
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD3D3D3))
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(viewModel.size.dp)
                            .pointerInteropFilter(
                                onTouchEvent = viewModel.onSwipe()
                            )
                    ) {
                        val ghostItem by viewModel.ghostItem.observeAsState()
                        actionItemData?.let {
                            when (it.action) {
                                ActionItemDataType.Add -> AddCircleMenuItemUI(
                                    viewModel.size,
                                    it.size
                                )

                                ActionItemDataType.Delete -> DeleteCircleMenuItemUI(
                                    viewModel.size,
                                    it
                                )
                            }
                        }
                        ghostItem?.let { item ->
                            GhostCircleMenuItemUI(item = item)
                        }
                        if (ghostItem == null) {
                            selectedBoxData?.let { SelectedItemBox(data = it) }
                        }
                        circleMenu?.let { menu ->
                            val itemsOffset = viewModel.getItemsOffsets()
                            menu.items.forEachIndexed { index, item ->
                                if (index != ghostItem?.index) {
                                    viewModel.getItemImage(item.image)?.let { imageBitmap ->
                                        Image(
                                            modifier = Modifier
                                                .offset(
                                                    x = itemsOffset[index].x,
                                                    y = itemsOffset[index].y
                                                )
                                                .size(viewModel.itemSize.dp),
                                            bitmap = imageBitmap,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                CircleMenuTitle(viewModel)
            }
            selectedBoxData?.let { data ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    circleMenu?.items?.get(data.index)?.let { item ->
                        ImageAndActionEdit(
                            viewModel = viewModel,
                            item,
                            onChangeImage = {
                                viewModel.updateImage(item.copy(image = it), data.index)
                            },
                            onChangeAction = {
                                viewModel.updateCircleMenuItem(item.copy(action = it), data.index)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PortraitUI(
    viewModel: EditCircleMenuScreenVM,
    onBackPressed: () -> Unit
) {
    val actionItemData by viewModel.actionItemData.observeAsState()
    val circleMenu by viewModel.circleMenu.observeAsState()
    val selectedBoxData by viewModel.selectedBoxData.observeAsState()

    // UI
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditCircleMenuToolbarUI(
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // CircleMenu
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFD3D3D3))
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(viewModel.size.dp)
                        .pointerInteropFilter(
                            onTouchEvent = viewModel.onSwipe()
                        )
                ) {
                    val ghostItem by viewModel.ghostItem.observeAsState()
                    actionItemData?.let {
                        when (it.action) {
                            ActionItemDataType.Add -> AddCircleMenuItemUI(viewModel.size, it.size)
                            ActionItemDataType.Delete -> DeleteCircleMenuItemUI(viewModel.size, it)
                        }
                    }
                    ghostItem?.let { item ->
                        GhostCircleMenuItemUI(item = item)
                    }
                    if (ghostItem == null) {
                        selectedBoxData?.let { SelectedItemBox(data = it) }
                    }
                    circleMenu?.let { menu ->
                        val itemsOffset = viewModel.getItemsOffsets()
                        menu.items.forEachIndexed { index, item ->
                            if (index != ghostItem?.index) {
                                viewModel.getItemImage(item.image)?.let { imageBitmap ->
                                    Image(
                                        modifier = Modifier
                                            .offset(
                                                x = itemsOffset[index].x,
                                                y = itemsOffset[index].y
                                            )
                                            .size(viewModel.itemSize.dp),
                                        bitmap = imageBitmap,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            CircleMenuTitle(viewModel)

            // Item edit
            selectedBoxData?.let { data ->

                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    circleMenu?.items?.get(data.index)?.let { item ->
                        ImageAndActionEdit(
                            viewModel = viewModel,
                            item,
                            onChangeImage = {
                                viewModel.updateImage(item.copy(image = it), data.index)
                            },
                            onChangeAction = {
                                viewModel.updateCircleMenuItem(item.copy(action = it), data.index)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditCircleMenuToolbarUI(onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.primary)
            .shadow(elevation = 1.dp)
            .statusBarsPadding(),
    ) {
        IconButton(
            onClick = onBackPressed
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CircleMenuTitle(
    viewModel: EditCircleMenuScreenVM
) {
    val circleMenu by viewModel.circleMenu.observeAsState()
    circleMenu?.let { menu ->
        val fontSize = 24.sp
        Box(
            modifier = Modifier
                .width(viewModel.size.dp)
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            if (menu.title.isEmpty()) {
                Text(
                    text = stringResource(R.string.title),
                    color = Color.Gray,
                    fontSize = fontSize
                )
            }
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                textStyle = TextStyle(
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black
                ),
                value = menu.title,
                onValueChange = viewModel::changeTitle,
                singleLine = true
            )
        }
    }
}

@Composable
private fun SelectedItemBox(
    data: SelectedItemBoxData
) {
    Box(
        modifier = Modifier
            .offset(
                x = data.offset.x.dp,
                y = data.offset.y.dp
            )
            .size(data.size.dp)
            .background(
                color = Color(0xFF8F8F8F),
                shape = RoundedCornerShape(16.dp),
            )
    )
}

@Composable
private fun ImageAndActionEdit(
    viewModel: EditCircleMenuScreenVM,
    circleMenuItem: CircleMenuItem,
    onChangeImage: (CircleMenuImage) -> Unit,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    Row(
        modifier = Modifier
            .width(viewModel.size.dp + 20.dp)
            .height((viewModel.size.dp + 20.dp) / 3)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD3D3D3))
            .padding(10.dp)
    ) {

        // Image

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.image))
            ItemImage(
                image = circleMenuItem.image,
                size = viewModel.size / 5,
                viewModel = viewModel,
                onChangeImage = onChangeImage
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        VerticalDivider(
            color = Color(0xFF848484)
        )
        Spacer(modifier = Modifier.width(10.dp))

        // Action

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.action))
            EditCircleMenuAction(
                action = circleMenuItem.action,
                getAllApplicationsData = viewModel::getAllApplicationsData,
                getApplicationInfo = viewModel::getApplicationInfo,
                getItemImage = viewModel::getItemImage,
                size = viewModel.size / 5,
                onChangeAction = onChangeAction
            )
        }
    }
}

@Composable
fun ItemImage(
    image: CircleMenuImage,
    size: Float,
    viewModel: EditCircleMenuScreenVM,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var showImageDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showImageDialog) {
        ImageDialog(
            addUserImage = viewModel::addUserImage,
            getItemImage = viewModel::getItemImage,
            getAllApplicationsData = viewModel::getAllApplicationsData,
            onDismissRequest = { showImageDialog = false },
            onPick = onChangeImage
        )
    }

    viewModel.getItemImage(image)?.let { bitmap ->
        Image(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { showImageDialog = true }
                .padding(5.dp),
            bitmap = bitmap,
            contentDescription = null
        )
    }
}