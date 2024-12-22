package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
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
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.domain.utils.formatPhoneNumber
import com.kindeev.swipelauncher.domain.utils.getContactName
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.EnterNumberDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenUrlActionData

@Composable
fun EditCircleMenuAction(
    action: CircleMenuAction,
    getApplicationInfo: (String) -> ApplicationInfo,
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
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
                getItemImage = getItemImage,
                size = size,
                action = action,
                changeAction = { showActionDialog = true }
            )
        }

        is OpenSettingsAction -> {
            OpenSettingsDataItem(
                getItemImage = getItemImage,
                size = size,
                changeAction = { showActionDialog = true }
            )
        }

        is OpenAppAction -> {
            OpenAppDataItem(
                getApplicationInfo = getApplicationInfo,
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
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    size: Float,
    action: OpenCircleMenuAction,
    changeAction: () -> Unit
) {
    val circleMenu =
        LauncherData.allCircleMenus.value?.find { it.id == action.id }

    circleMenu?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(size.dp)
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
                    getItemImage = getItemImage,
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
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    size: Float,
    changeAction: () -> Unit
) {
    getItemImage(DefaultImage(DefaultImages.Settings))?.let { bitmap ->
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
    getApplicationInfo: (String) -> ApplicationInfo,
    action: OpenAppAction,
    size: Float,
    changeAction: () -> Unit
) {
    val applicationData = getApplicationInfo(action.packageName)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(size.dp)
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
            .fillMaxWidth()
            .height(size.dp)
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
            .fillMaxWidth()
            .height(size.dp)
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
            .fillMaxWidth()
            .height(size.dp)
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