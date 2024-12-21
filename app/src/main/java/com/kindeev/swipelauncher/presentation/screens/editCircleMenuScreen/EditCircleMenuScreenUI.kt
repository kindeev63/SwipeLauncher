package com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.domain.utils.formatPhoneNumber
import com.kindeev.swipelauncher.domain.utils.getContactName
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.ActionItemDataType
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.SelectedItemBoxData
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.EditCircleMenuVMFactory
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.EnterNumberDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenUrlActionData
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuItems

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditCircleMenuScreenUI(
    circleMenuId: Int?,
    onBackPressed: () -> Unit
) {
    val menuSize = LocalConfiguration.current.screenWidthDp / 6 * 5f
    val viewModel: EditCircleMenuScreenVM = viewModel(
        factory = EditCircleMenuVMFactory(circleMenuId, menuSize, LocalContext.current)
    )
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
                        .size(menuSize.dp)
                        .pointerInteropFilter(
                            onTouchEvent = viewModel.onSwipe()
                        )
                ) {
                    val ghostItem by viewModel.ghostItem.observeAsState()
                    actionItemData?.let {
                        when (it.action) {
                            ActionItemDataType.Add -> AddCircleMenuItemUI(menuSize, it.size)
                            ActionItemDataType.Delete -> DeleteCircleMenuItemUI(menuSize, it)
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
            .background(Color(0xFFD3D3D3))
            .shadow(elevation = 1.dp)
            .statusBarsPadding(),
    ) {
        IconButton(
            onClick = onBackPressed
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
            Text(text = "Image")
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
            Text(text = "Action")
            ItemAction(
                action = circleMenuItem.action,
                size = viewModel.size / 5,
                viewModel = viewModel,
                onChangeAction = onChangeAction
            )
        }
    }
}

@Composable
fun ItemAction(
    action: CircleMenuAction,
    viewModel: EditCircleMenuScreenVM,
    size: Float,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showActionDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showActionDialog) {
        ActionDialog(
            onDismissRequest = { showActionDialog = false },
            onPick = onChangeAction
        )
    }
    when (action) {
        is OpenCircleMenuAction -> {
            OpenCircleMenuDataItem(
                viewModel = viewModel,
                size = size,
                action = action,
                changeAction = { showActionDialog = true }
            )
        }

        is OpenSettingsAction -> {
            OpenSettingsDataItem(
                viewModel = viewModel,
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is OpenAppAction -> {
            OpenAppDataItem(
                viewModel = viewModel,
                action = action,
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is FlashLightOnAction -> {
            FlashLightOnDataItem(
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is FlashLightOffAction -> {
            FlashLightOffDataItem(
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is ChangeFlashLightConditionAction -> {
            ChangeFlashlightConditionDataItem(
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is CallAction -> {
            CallDataItem(
                size = size,
                action = action,
                onChangeAction = onChangeAction,
                changeAction = { showActionDialog = true }
            )
        }

        is DialAction -> {
            DialDataItem(
                size = size,
                action = action,
                onChangeAction = onChangeAction,
                changeAction = { showActionDialog = true }
            )
        }

        is OpenUrlAction -> {
            OpenUrlDataItem(
                size = size,
                action = action,
                onChangeAction = onChangeAction,
                changeAction = { showActionDialog = true }
            )
        }
    }
}

@Composable
private fun OpenCircleMenuDataItem(
    viewModel: EditCircleMenuScreenVM,
    size: Float,
    action: OpenCircleMenuAction,
    changeAction: () -> Unit
) {
    val circleMenu =
        LauncherData.allCircleMenus.value?.find { it.id == action.id }

    circleMenu?.let {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = changeAction)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(size.dp),
                contentAlignment = Alignment.Center
            ) {
                CircleMenuItems(
                    getItemImage = viewModel::getItemImage,
                    items = it.items,
                    menuSize = size - 10,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = it.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun OpenSettingsDataItem(
    viewModel: EditCircleMenuScreenVM,
    size: Float,
    changeAction: () -> Unit
) {
    viewModel.getItemImage(DefaultImage(DefaultImages.Settings))?.let { bitmap ->
        Image(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = changeAction)
                .padding(5.dp),
            bitmap = bitmap,
            contentDescription = null
        )
    }
}

@Composable
private fun OpenAppDataItem(
    viewModel: EditCircleMenuScreenVM,
    action: OpenAppAction,
    size: Float,
    changeAction: () -> Unit
) {
    val applicationData = viewModel.getApplicationInfo(action.packageName)
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = changeAction)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(size.dp - 20.dp),
            bitmap = applicationData.icon,
            contentDescription = "App image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = applicationData.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FlashLightOnDataItem(
    size: Float,
    changeAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = changeAction)
            .padding(5.dp),
        painter = painterResource(id = R.drawable.on_flashlight_image),
        contentDescription = null
    )
}

@Composable
private fun FlashLightOffDataItem(
    size: Float,
    changeAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = changeAction)
            .padding(5.dp),
        painter = painterResource(id = R.drawable.off_flashlight_image),
        contentDescription = null
    )
}

@Composable
private fun ChangeFlashlightConditionDataItem(
    size: Float,
    changeAction: () -> Unit
) {
    Image(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = changeAction)
            .padding(5.dp),
        painter = painterResource(id = R.drawable.change_condition_flashlight_image),
        contentDescription = null
    )
}

@Composable
private fun CallDataItem(
    size: Float,
    action: CallAction,
    changeAction: () -> Unit,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    val context = LocalContext.current
    var data by rememberSaveable {
        mutableStateOf(action)
    }
    var contactName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    if (action != data) {
        data = action
        if (hasReadContactsPermission == true) {
            contactName = context.getContactName(data.phoneNumber)
        }
    }
    if (hasReadContactsPermission == null) {
        ReadContactsPermission {
            if (it) {
                contactName = context.getContactName(data.phoneNumber)
            }
            hasReadContactsPermission = it
        }
    }
    var showEnterNumberDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showEnterNumberDialog) {
        EnterNumberDialog(
            defNumber = data.phoneNumber,
            onEnter = { onChangeAction(CallAction(it)) },
            onDismissRequest = { showEnterNumberDialog = false }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size.dp / 3 * 2)
                .clip(CircleShape)
                .clickable(onClick = changeAction),
            painter = painterResource(id = R.drawable.call_telephone_image),
            contentDescription = "Call image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        if (contactName == null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { showEnterNumberDialog = true }
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.phoneNumber.formatPhoneNumber(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 30,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { showEnterNumberDialog = true }
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(size.dp / 5 * 3)
                        .clip(RoundedCornerShape(16.dp)),
                    painter = painterResource(id = R.drawable.contact_image),
                    contentDescription = "Contact image"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = contactName
                        ?: stringResource(id = R.string.error),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 25,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun DialDataItem(
    size: Float,
    action: DialAction,
    changeAction: () -> Unit,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    val context = LocalContext.current
    var data by rememberSaveable {
        mutableStateOf(action)
    }
    var contactName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    if (action != data) {
        data = action
        if (hasReadContactsPermission == true) {
            contactName = context.getContactName(data.phoneNumber)
        }
    }
    if (hasReadContactsPermission == null) {
        ReadContactsPermission {
            if (it) {
                contactName = context.getContactName(data.phoneNumber)
            }
            hasReadContactsPermission = it
        }
    }
    var showEnterNumberDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showEnterNumberDialog) {
        EnterNumberDialog(
            defNumber = data.phoneNumber,
            onEnter = { onChangeAction(DialAction(it)) },
            onDismissRequest = { showEnterNumberDialog = false }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size.dp / 3 * 2)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = changeAction)
                .padding(2.dp),
            painter = painterResource(id = R.drawable.dial_telephone_image),
            contentDescription = "Dial image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        if (contactName == null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { showEnterNumberDialog = true }
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = data.phoneNumber.formatPhoneNumber(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 30,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { showEnterNumberDialog = true }
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(size.dp / 5 * 3)
                        .clip(RoundedCornerShape(16.dp)),
                    painter = painterResource(id = R.drawable.contact_image),
                    contentDescription = "Contact image"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = contactName
                        ?: stringResource(id = R.string.error),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 25,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun OpenUrlDataItem(
    size: Float,
    action: OpenUrlAction,
    changeAction: () -> Unit,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showOpenUrlDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showOpenUrlDialog) {
        OpenUrlActionData(
            defUrl = action.url,
            onPick = onChangeAction,
            onDismissRequest = { showOpenUrlDialog = false }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(size.dp / 3 * 2)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = changeAction)
                .padding(4.dp),
            painter = painterResource(id = R.drawable.open_url_image),
            contentDescription = "Open url image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    showOpenUrlDialog = true
                }
                .padding(2.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = action.url,
            fontSize = Constants.minScreenLength.sp / 30,
            color = MaterialTheme.colorScheme.onBackground
        )
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