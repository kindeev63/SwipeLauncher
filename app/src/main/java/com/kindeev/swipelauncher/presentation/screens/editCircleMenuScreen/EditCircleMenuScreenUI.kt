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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.ActionItemState
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.SelectedItemBoxData
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.elements.EditCircleMenuAction
import com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen.EditCircleMenuScreenVM

@Composable
fun EditCircleMenuScreenUI(
    viewModel: EditCircleMenuScreenVM,
    onBackPressed: () -> Unit
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT && Constants.minScreenLength / 6 * 5f * 1.5f < LocalConfiguration.current.screenHeightDp) {
        viewModel.updateMenuSize(Constants.minScreenLength / 6 * 5f)
        PortraitUI(
            viewModel = viewModel,
            onBackPressed = onBackPressed
        )
    } else {
        viewModel.updateMenuSize((Constants.minScreenLength - 80) / 3 * 2f)
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
    val actionItemData by viewModel.actionItemData.collectAsState()
    val selectedBoxData by viewModel.selectedBoxData.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val circleMenuItems by viewModel.circleMenuItems.collectAsState()
    val drawItemsData by viewModel.drawItemsData.collectAsState()
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()

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
                            .size(menuSize.dp)
                            .pointerInteropFilter(
                                onTouchEvent = viewModel.onSwipe()
                            )
                    ) {
                        val ghostItem by viewModel.ghostItem.collectAsState()
                        when (actionItemData.state) {

                            ActionItemState.Add -> AddCircleMenuItemUI(
                                menuSize,
                                actionItemData.size
                            )

                            ActionItemState.Delete -> DeleteCircleMenuItemUI(
                                menuSize,
                                actionItemData.size,
                                false
                            )

                            ActionItemState.DeleteActive -> DeleteCircleMenuItemUI(
                                menuSize,
                                actionItemData.size,
                                true
                            )
                        }
                        ghostItem?.let { item ->
                            GhostCircleMenuItemUI(item = item)
                        }
                        if (ghostItem == null) {
                            selectedBoxData?.let { SelectedItemBox(data = it) }
                        }
                        CircleMenuItems(
                            itemsOffset = drawItemsData.offsets,
                            ghostIndex = ghostItem?.index,
                            items = circleMenuItems,
                            getImage = viewModel::getImage,
                            itemSize = drawItemsData.itemSize,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                CircleMenuTitle(viewModel)
            }
            selectedItem?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    ImageAndActionEdit(
                        viewModel = viewModel,
                        circleMenuItem = item,
                        onChangeImage = viewModel::updateImage,
                        onChangeAction = viewModel::updateAction
                    )
                }
            }
        }
    }
}

@Composable
fun CircleMenuItems(
    itemsOffset: List<Offset>,
    ghostIndex: Int?,
    items: List<CircleMenuItem>,
    getImage: (CircleMenuImage) -> ImageBitmap?,
    itemSize: Float
) {
    items.forEachIndexed { index, item ->
        if (index != ghostIndex) {
            getImage(item.image)?.let { imageBitmap ->
                Image(
                    bitmap = imageBitmap,
                    modifier = Modifier
                        .offset(
                            x = itemsOffset[index].x.dp,
                            y = itemsOffset[index].y.dp
                        )
                        .size(itemSize.dp),
                    contentDescription = null
                )
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
    val actionItemData by viewModel.actionItemData.collectAsState()
    val circleMenuItems by viewModel.circleMenuItems.collectAsState()
    val selectedBoxData by viewModel.selectedBoxData.collectAsState()
    val selectedItem by viewModel.selectedItem.collectAsState()
    val drawItemsData by viewModel.drawItemsData.collectAsState()
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()

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
                        .size(menuSize.dp)
                        .pointerInteropFilter(
                            onTouchEvent = viewModel.onSwipe()
                        )
                ) {
                    val ghostItem by viewModel.ghostItem.collectAsState()
                    when (actionItemData.state) {

                        ActionItemState.Add -> AddCircleMenuItemUI(
                            menuSize,
                            actionItemData.size
                        )

                        ActionItemState.Delete -> DeleteCircleMenuItemUI(
                            menuSize,
                            actionItemData.size,
                            false
                        )

                        ActionItemState.DeleteActive -> DeleteCircleMenuItemUI(
                            menuSize,
                            actionItemData.size,
                            true
                        )
                    }
                    ghostItem?.let { item ->
                        GhostCircleMenuItemUI(item = item)
                    }
                    if (ghostItem == null) {
                        selectedBoxData?.let { SelectedItemBox(data = it) }
                    }
                    CircleMenuItems(
                        itemsOffset = drawItemsData.offsets,
                        ghostIndex = ghostItem?.index,
                        items = circleMenuItems,
                        getImage = viewModel::getImage,
                        itemSize = drawItemsData.itemSize
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            CircleMenuTitle(viewModel)

            // Item edit
            selectedItem?.let { item ->

                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageAndActionEdit(
                        viewModel = viewModel,
                        circleMenuItem = item,
                        onChangeImage = viewModel::updateImage,
                        onChangeAction = viewModel::updateAction
                    )
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
    val title by viewModel.circleMenuTitle.collectAsState()
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()
    val fontSize = 24.sp
    Box(
        modifier = Modifier
            .width(menuSize.dp)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        if (title.text.isEmpty()) {
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
            value = title,
            onValueChange = viewModel::changeTitle,
            singleLine = true
        )
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
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
            .width(menuSize.dp + 20.dp)
            .height((menuSize.dp + 20.dp) / 3)
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
                size = menuSize / 5,
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
                getApplicationInfo = viewModel::getApplicationInfo,
                size = menuSize / 5,
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
            onDismissRequest = { showImageDialog = false },
            onPick = onChangeImage
        )
    }
    viewModel.getImageBitmap(image)?.let {
        Image(
            bitmap = it,
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { showImageDialog = true }
                .padding(5.dp),
            contentDescription = null
        )
    }
}